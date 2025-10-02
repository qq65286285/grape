//package com.grape.grape;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONUtil;
//import jakarta.annotation.Resource;
//import org.junit.jupiter.api.Test;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
//import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
//import org.springframework.ai.chat.memory.InMemoryChatMemory;
//import org.springframework.ai.chat.messages.UserMessage;
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.ai.ollama.api.OllamaOptions;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.parameters.P;
//import v2.io.swagger.models.*;
//import v2.io.swagger.models.properties.RefProperty;
//import v2.io.swagger.models.refs.GenericRef;
//import v2.io.swagger.parser.SwaggerParser;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//class GrapeApplicationTests {
//
//	@Test
//	void contextLoads() {
//	}
//
//	@Test
//	void test() throws IOException {
//
//		Swagger swagger = new SwaggerParser().
//				read("http://192.168.1.200:30565/api/openapi/v2/api-docs?group=openapi");
////		System.out.println(JSONUtil.toJsonStr(swagger));
//		Map<String, Path> paths = swagger.getPaths();
//
//		for(String key : paths.keySet()){
//			Path path = paths.get(key);
//			System.out.println("==================================");
//			System.out.println("请求地址: " + key);
//			System.out.println("入参: " +JSONUtil.toJsonStr( path.getParameters() ) );
//			if(path.getGet() != null){
//				System.out.println("请求方式: GET" );
//				System.out.println("出参: " +JSONUtil.toJsonStr( path.getGet().getResponses() ) );
//				Map<String, Response> returnList = path.getGet().getResponses();
//				for(String key2 : returnList.keySet()){
//					Response response = returnList.get(key2);
//					if(key2.equals("200")){
//						if(response.getSchema().getType().equals("ref")){
//							RefProperty ref = (RefProperty) response.getSchema();
//							String dtoName = StrUtil.subAfter(ref.get$ref(),  "#/definitions/", false);
////							dtoName = StrUtil.replace(dtoName,"«","<");
////							dtoName = StrUtil.replace(dtoName,"»",">");
//							System.out.println("返回对象类型： " + dtoName);
//							System.out.println("返回对象类型结构： " + swagger.getDefinitions().get(dtoName));
//						}
//					}
//				}
//
//			} else if (path.getPost() != null) {
//				System.out.println("请求方式: POST" );
//				System.out.println("出参: " +JSONUtil.toJsonStr( path.getPost().getResponses() ) );
//			}else if(path.getPut() != null){
//				System.out.println("请求方式: PUT" );
//				System.out.println("出参: " +JSONUtil.toJsonStr( path.getPut().getResponses() ) );
//			} else if (path.getDelete() != null) {
//				System.out.println("请求方式: DELETE" );
//				System.out.println("出参: " +JSONUtil.toJsonStr( path.getDelete().getResponses() ) );
//			}
//			System.out.println("==================================");
//		}
//	}
//
//
//	@Resource
//	private OllamaChatModel chatModel;
//
////	/vms-backend-production/logs/hysp_vms/backup/2025-05/hysp_vms-2025-05-06_15.log.zip
//	@Test
//	void aiCallTest(){
//		OllamaOptions customOptions = OllamaOptions.builder()
//				.topP(Double.valueOf(0.7))
//				.model("DeepSeek-R1-Distill-SRE-Qwen-7B:latest")
//				.temperature(Double.valueOf(0.8))
//				.build();
//		String message = "请用shell语言写一个脚本，改脚本为校正当前系统时间，当前系统为centos 7";
//		Prompt prompt = new Prompt(message, customOptions);
//		ChatResponse result = chatModel.call(prompt);
//		System.out.println(result.getResult().toString());
////		ChatClient ollamaiChatClient = ChatClient.builder(chatModel)
////				// 实现 Chat Memory 的 Advisor
////				// 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
////				.defaultAdvisors(
////						new MessageChatMemoryAdvisor(new InMemoryChatMemory())
////				)
////				// 实现 Logger 的 Advisor
////				.defaultAdvisors(
////						new SimpleLoggerAdvisor()
////				)
////				// 设置 ChatClient 中 ChatModel 的 Options 参数
////				.defaultOptions(
////						OllamaOptions.builder()
////								.topP(0.7)
////								.build()
////				)
////				.build();
//	}
//}
