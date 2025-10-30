package com.frontdesk.service;

import com.frontdesk.model.HelpRequest;
import com.frontdesk.model.LearnedAnswer;
import com.frontdesk.repository.HelpRequestRepository;
import com.frontdesk.repository.LearnedAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AIService {

    @Autowired
    private LearnedAnswerRepository answerRepo;

    @Autowired
    private HelpRequestRepository helpRequestRepo;

    @Autowired
    private OpenAIService openAIService;

    public String handleIncomingCall(String callerId, String question) {
        Optional<LearnedAnswer> known = answerRepo.findByQuestion(question);
        if (known.isPresent()) {
            System.out.println("AI: " + known.get().getAnswer());
            return known.get().getAnswer();
        } else {
            String aiAnswer = openAIService.getAIAnswer(question);
            if (aiAnswer != null && aiAnswer.length() > 0) {
                System.out.println("AI (OpenAI): " + aiAnswer);
                return aiAnswer;
            }

            HelpRequest req = new HelpRequest(null, callerId, question, "PENDING", null, LocalDateTime.now(), null);
            helpRequestRepo.save(req);
            System.out.println("Escalated to supervisor, id: " + req.getId());
            return "Let me check with my supervisor and get back.";
        }
    }

    public void receiveSupervisorAnswer(Long requestId, String answer) {
        HelpRequest req = helpRequestRepo.findById(requestId).orElseThrow();
        req.setSupervisorAnswer(answer);
        req.setStatus("RESOLVED");
        req.setResolvedAt(LocalDateTime.now());
        helpRequestRepo.save(req);
        answerRepo.save(new LearnedAnswer(null, req.getQuestion(), answer));
        System.out.println("Supervisor answered, AI updated knowledge base.");
    }
}