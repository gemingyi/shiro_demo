package com.example.shiro_jwt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.shiro_jwt.dao")
public class ShiroJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShiroJwtApplication.class, args);
	}

}

