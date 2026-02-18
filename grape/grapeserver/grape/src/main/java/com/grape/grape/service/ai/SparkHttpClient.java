package com.grape.grape.service.ai;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;

public class SparkHttpClient {
    private static final String API_URL = "https://spark-api-open.xf-yun.com/v2/chat/completions";
    @Value("${xfyun.spark.api-key}")
    private String apiKey;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SparkHttpClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 发送带上下文的实体/关系抽取请求
    public List<ExtractedEntity> extractEntities(String text) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "spark-x"); // 指定使用X系列模型
        requestBody.put("user", "document-processor");
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 4096);
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", buildPrompt(text)); // 关键！构造精准提示词
        requestBody.put("messages", Collections.singletonList(message));

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(
                        objectMapper.writeValueAsString(requestBody),
                        MediaType.get("application/json")
                ))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code: " + response);
            String jsonBody = response.body().string();
            return parseResponse(jsonBody);
        }
    }

    private String buildPrompt(String text) {
        return """
            请从以下文本中严格提取所有命名实体及其关系，仅返回JSON数组，不要添加任何解释：
            规则：
            1. 实体类型只能是 [人物PERSON|地点LOCATION|机构ORGANIZATION|时间DATE]
            2. 关系类型只能是 [出生于BORN_AT|位于LOCATED_IN|工作于WORKS_AT|成立于FOUNDED_ON]
            3. 如果找不到明确关系则忽略该实体对
            
            文本内容：
            %s
            
            期望输出格式示例：
            [{"entity1":"张三","type":"PERSON","relation":"WORKS_AT","entity2":"中科院","type":"ORGANIZATION"}]
            """.formatted(text.substring(0, Math.min(text.length(), 3000))); // 截断防超长
    }

    private List<ExtractedEntity> parseResponse(String jsonBody) throws IOException {
        // 使用泛型类型引用避免类型转换警告
        Map<String, Object> root = objectMapper.readValue(jsonBody, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        
        // 安全获取 choices 字段
        Object choicesObj = root.get("choices");
        if (!(choicesObj instanceof List)) return Collections.emptyList();
        
        List<?> choices = (List<?>) choicesObj;
        if (choices.isEmpty()) return Collections.emptyList();
        
        // 安全获取 message 字段
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map)) return Collections.emptyList();
        
        Map<?, ?> choiceMap = (Map<?, ?>) firstChoice;
        Object messageObj = choiceMap.get("message");
        if (!(messageObj instanceof Map)) return Collections.emptyList();
        
        Map<?, ?> messageMap = (Map<?, ?>) messageObj;
        Object contentObj = messageMap.get("content");
        if (contentObj == null) return Collections.emptyList();
        
        String content = contentObj.toString();
        // 使用更简洁的类型引用方式
        return objectMapper.readValue(content, new com.fasterxml.jackson.core.type.TypeReference<List<ExtractedEntity>>() {});
    }

    // 定义抽取结果实体类
    public static class ExtractedEntity {
        public String entity1;
        public String type1;
        public String relation;
        public String entity2;
        public String type2;
        // Getters & Setters...
    }
}
