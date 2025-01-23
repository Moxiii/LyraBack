package com.moxi.lyra.Config.Object;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
@Bean
public ObjectMapper objectMapper() {
	ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().json().build();
	objectMapper.registerModule(new JavaTimeModule());
	return objectMapper;
}
}
