package com.frontdesk.controller;

import com.frontdesk.model.HelpRequest;
import com.frontdesk.repository.HelpRequestRepository;
import com.frontdesk.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
public class SupervisorController {
    private final HelpRequestRepository hrRepo;
    private final AIService aiService;

    @GetMapping("/help-requests")
    public List<HelpRequest> getPending() { return hrRepo.findByStatus("PENDING"); }

    @PostMapping("/answer")
    public ResponseEntity<String> answer(@RequestParam Long id, @RequestParam String answer) {
        aiService.receiveSupervisorAnswer(id, answer);
        return ResponseEntity.ok("Supervisor responded and AI updated.");
    }

    @GetMapping("/history")
    public List<HelpRequest> getAllHelpRequests() {
        return hrRepo.findAll();
    }
}