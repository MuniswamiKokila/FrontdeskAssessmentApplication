package com.frontdesk.controller;

import com.frontdesk.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    @Autowired
    private AIService aiService;

    @PostMapping("/call")
    public ResponseEntity<String> receiveCall(@RequestParam String callerId, @RequestParam String question) {
        return ResponseEntity.ok(aiService.handleIncomingCall(callerId, question));
    }
}