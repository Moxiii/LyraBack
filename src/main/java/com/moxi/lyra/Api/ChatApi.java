package com.moxi.lyra.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.moxi.lyra.Config.CustomAnnotation.RequireAuthorization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RequireAuthorization
@RequestMapping("/api/chat")
@RestController
public class ChatApi {
@Value("${OLLAMA_URL}")
private String BASE_URL;
private final RestTemplate restTemplate;
private final ObjectMapper objectMapper;

public ChatApi(RestTemplate restTemplate, ObjectMapper objectMapper) {
	this.restTemplate = restTemplate;
	this.objectMapper = objectMapper;
}
@PostMapping("/ollama")
public ResponseEntity<String> getChat(@RequestBody String jsonMessage) {
	try{
		JsonNode jsonNode = objectMapper.readTree(jsonMessage);
		String message = jsonNode.get("message").asText();
		message = message.replace("\r", "").replace("\n", " ").trim();
		ObjectNode jsonRequest = objectMapper.createObjectNode();
		jsonRequest.put("model", "stable-code:3b");
		jsonRequest.put("prompt", message);
		jsonRequest.put("stream", false);
		String jsonBody = objectMapper.writeValueAsString(jsonRequest);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				BASE_URL+"generate",
				HttpMethod.POST,
				entity,
				String.class
		);
		return ResponseEntity.ok(response.getBody());
	}
	catch(Exception e){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting api : "+ e.getMessage());
	}

}
@PostMapping("/pull/model")
public ResponseEntity<String> pullModel(@RequestBody String model) {
	try {
		ObjectNode jsonRequest = objectMapper.createObjectNode();
		jsonRequest.put("name", model);
		String jsonBody = objectMapper.writeValueAsString(jsonRequest);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL+"pull", entity, String.class);
		return ResponseEntity.ok(response.getBody());
	} catch (Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error pulling model : "+ e.getMessage());
	}
}
}
