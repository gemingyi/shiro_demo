package com.example.shiro;

import org.apache.shiro.session.mgt.SimpleSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiroApplicationTests {

	@Autowired
	RedisTemplate redisTemplate;

	@Test
	public void contextLoads() {
		SimpleSession session = (SimpleSession) redisTemplate.opsForValue().get("shiro_redis_session:78fe7b51-97fe-4a8f-92da-8e5a5d5224e9");
		System.out.println(session.getAttribute("currentUser"));
	}

}

