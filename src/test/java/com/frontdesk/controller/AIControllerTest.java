package com.frontdesk.controller;

import com.frontdesk.service.AIService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AIController.class)
public class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIService aiService;

    @Test
    public void testReceiveCall() throws Exception {
        String callerId = "12345";
        String question = "What is AI?";
        String mockAnswer = "AI stands for Artificial Intelligence";

        Mockito.when(aiService.handleIncomingCall(callerId, question)).thenReturn(mockAnswer);

        mockMvc.perform(post("/api/ai/call")
                        .param("callerId", callerId)
                        .param("question", question)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mockAnswer));
    }
}
