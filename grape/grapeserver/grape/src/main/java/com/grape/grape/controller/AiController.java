package com.grape.grape.controller;

import com.grape.grape.entity.dto.RoleContent;
import com.grape.grape.service.ai.B_WsXModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * AI控制器
 * 用于处理前端对AI模型的调用请求
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private B_WsXModel bWsXModel;

    /**
     * 调用科大讯飞Spark API
     * @param request 请求参数，包含用户的问题
     * @return 响应结果
     */
    @PostMapping("/spark")
    public Map<String, Object> callSpark(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String question = request.get("question");
            if (question == null || question.isEmpty()) {
                response.put("code", 400);
                response.put("message", "问题不能为空");
                return response;
            }

            // 创建角色内容对象
            RoleContent roleContent = new RoleContent();
            roleContent.setRole("user");
            roleContent.setContent(question);

            // 调用WebSocket初始化方法
            bWsXModel.initWebSocket(roleContent);

            // 注意：由于WebSocket是异步通信，这里无法直接返回结果
            // 实际应用中，需要通过WebSocket或其他方式将结果推送给前端
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", "已发送请求到科大讯飞Spark API，请等待结果");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "调用失败：" + e.getMessage());
        }
        return response;
    }
}
