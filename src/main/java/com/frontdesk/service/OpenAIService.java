package com.frontdesk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class OpenAIService {
    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String getAIAnswer(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + openAiApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-3.5-turbo");
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            body.put("messages", messages);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String url = "https://api.openai.com/v1/chat/completions";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            List<Map> choices = (List<Map>) response.getBody().get("choices");
            return (String) ((Map) choices.get(0).get("message")).get("content");
        } catch (HttpClientErrorException ex) {
            // Log the quota or client error
            System.out.println("OpenAI quota exceeded or API error: " + ex.getMessage());
            // Return null or a special message to escalate to supervisor
            return null;
        }
    }
}