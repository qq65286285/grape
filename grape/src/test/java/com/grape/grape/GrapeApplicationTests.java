package com.grape.grape;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import v2.io.swagger.models.*;
import v2.io.swagger.models.properties.RefProperty;
import v2.io.swagger.models.refs.GenericRef;
import v2.io.swagger.parser.SwaggerParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GrapeApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void test() throws IOException {

		Swagger swagger = new SwaggerParser().
				read("http://192.168.1.200:30565/api/openapi/v2/api-docs?group=openapi");
//		System.out.println(JSONUtil.toJsonStr(swagger));
		Map<String, Path> paths = swagger.getPaths();

		for(String key : paths.keySet()){
			Path path = paths.get(key);
			System.out.println("==================================");
			System.out.println("请求地址: " + key);
			System.out.println("入参: " +JSONUtil.toJsonStr( path.getParameters() ) );
			if(path.getGet() != null){
				System.out.println("请求方式: GET" );
				System.out.println("出参: " +JSONUtil.toJsonStr( path.getGet().getResponses() ) );
				Map<String, Response> returnList = path.getGet().getResponses();
				for(String key2 : returnList.keySet()){
					Response response = returnList.get(key2);
					if(key2.equals("200")){
						if(response.getSchema().getType().equals("ref")){
							RefProperty ref = (RefProperty) response.getSchema();
							String dtoName = StrUtil.subAfter(ref.get$ref(),  "#/definitions/", false);
//							dtoName = StrUtil.replace(dtoName,"«","<");
//							dtoName = StrUtil.replace(dtoName,"»",">");
							System.out.println("返回对象类型： " + dtoName);
							System.out.println("返回对象类型结构： " + swagger.getDefinitions().get(dtoName));
						}
					}
				}

			} else if (path.getPost() != null) {
				System.out.println("请求方式: POST" );
				System.out.println("出参: " +JSONUtil.toJsonStr( path.getPost().getResponses() ) );
			}else if(path.getPut() != null){
				System.out.println("请求方式: PUT" );
				System.out.println("出参: " +JSONUtil.toJsonStr( path.getPut().getResponses() ) );
			} else if (path.getDelete() != null) {
				System.out.println("请求方式: DELETE" );
				System.out.println("出参: " +JSONUtil.toJsonStr( path.getDelete().getResponses() ) );
			}
			System.out.println("==================================");
		}
	}
}
