package com.frontdesk.service;

import com.frontdesk.model.HelpRequest;
import com.frontdesk.model.LearnedAnswer;
import com.frontdesk.repository.HelpRequestRepository;
import com.frontdesk.repository.LearnedAnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AIServiceTest {

    @Mock
    private LearnedAnswerRepository answerRepo;

    @Mock
    private HelpRequestRepository helpRequestRepo;

    @Mock
    private OpenAIService openAIService;

    @InjectMocks
    private AIService aiService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleIncomingCall_KnownAnswer() {
        String question = "What is AI?";
        LearnedAnswer learnedAnswer = new LearnedAnswer();
        learnedAnswer.setAnswer("Artificial Intelligence");

        when(answerRepo.findByQuestion(question)).thenReturn(Optional.of(learnedAnswer));

        String response = aiService.handleIncomingCall("caller1", question);

        assertEquals("Artificial Intelligence", response);
        verify(answerRepo, times(1)).findByQuestion(question);
        verifyNoInteractions(openAIService);
        verifyNoInteractions(helpRequestRepo);
    }

    @Test
    public void testHandleIncomingCall_UnknownAnswer_OpenAIResponds() {
        String question = "What is AI?";
        when(answerRepo.findByQuestion(question)).thenReturn(Optional.empty());
        when(openAIService.getAIAnswer(question)).thenReturn("OpenAI Answer");

        String response = aiService.handleIncomingCall("caller1", question);

        assertEquals("OpenAI Answer", response);
        verify(answerRepo, times(1)).findByQuestion(question);
        verify(openAIService, times(1)).getAIAnswer(question);
        verifyNoInteractions(helpRequestRepo);
    }

    @Test
    public void testHandleIncomingCall_UnknownAnswer_OpenAINoResponse() {
        String question = "Unknown question";
        when(answerRepo.findByQuestion(question)).thenReturn(Optional.empty());
        when(openAIService.getAIAnswer(question)).thenReturn("");

        ArgumentCaptor<HelpRequest> captor = ArgumentCaptor.forClass(HelpRequest.class);

        String response = aiService.handleIncomingCall("caller1", question);

        assertEquals("Let me check with my supervisor and get back.", response);
        verify(helpRequestRepo, times(1)).save(captor.capture());
        HelpRequest savedRequest = captor.getValue();
        assertEquals("caller1", savedRequest.getCallerId());
        assertEquals(question, savedRequest.getQuestion());
        assertEquals("PENDING", savedRequest.getStatus());
    }

    @Test
    public void testReceiveSupervisorAnswer() {
        Long requestId = 1L;
        String answer = "This is an answer";

        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setId(requestId);
        helpRequest.setQuestion("Sample Question");
        when(helpRequestRepo.findById(requestId)).thenReturn(Optional.of(helpRequest));

        aiService.receiveSupervisorAnswer(requestId, answer);

        verify(helpRequestRepo, times(1)).findById(requestId);
        verify(helpRequestRepo, times(1)).save(helpRequest);
        verify(answerRepo, times(1)).save(any(LearnedAnswer.class));

        assertEquals("RESOLVED", helpRequest.getStatus());
        assertEquals(answer, helpRequest.getSupervisorAnswer());
        assertNotNull(helpRequest.getResolvedAt());
    }
}
