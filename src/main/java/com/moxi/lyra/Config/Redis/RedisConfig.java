package com.moxi.lyra.Config.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
@Autowired
private ObjectMapper objectMapper;
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
	RedisTemplate<String, Object> template = new RedisTemplate<>();
	template.setConnectionFactory(factory);
	GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
	template.setKeySerializer(new StringRedisSerializer());
	template.setValueSerializer(serializer);
	return template;
}
}
