package com.frontdesk.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OpenAIServiceTest {

    @InjectMocks
    private OpenAIService openAIService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Manually set API key since @Value injection won't work in unit tests
        openAIService.openAiApiKey = "test-api-key";
    }

    @Test
    public void testGetAIAnswer_Success() {
        // Prepare mocked OpenAI API response body
        Map<String, Object> message = Map.of("role", "assistant", "content", "Test AI response");
        Map<String, Object> choice = Map.of("message", message);
        List<Map> choices = List.of(choice);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("choices", choices);

        ResponseEntity<Map> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Mock RestTemplate to return successful response
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        // Call the method under test
        String answer = openAIService.getAIAnswer("Hello?");

        // Verify results
        assertEquals("Test AI response", answer);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    public void testGetAIAnswer_HttpClientErrorException() {
        // Mock RestTemplate to throw error
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        // Call the method under test
        String answer = openAIService.getAIAnswer("Hello?");

        // Verify that method returns null on error
        assertNull(answer);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(Map.class));
    }
}
