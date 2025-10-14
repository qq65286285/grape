package com.grape.grape;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.grape.grape.config.jwttools.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
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
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NjA0MzQ4OTAsImlhdCI6MTc2MDQyNDA5MCwidXNlcm5hbWUiOiJxcTY1Mjg2Mjg1In0.MaCm45G1F93yTNqF28CL2qG2NTNA0Gsqad3EFVxCazU";
        boolean result = JwtUtils.verify(token);
        System.out.println(result);
	}

	@Test
	void test() throws IOException {

	}



}
