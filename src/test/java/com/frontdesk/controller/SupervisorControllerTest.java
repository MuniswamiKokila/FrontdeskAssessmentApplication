package com.frontdesk.controller;

import com.frontdesk.model.HelpRequest;
import com.frontdesk.repository.HelpRequestRepository;
import com.frontdesk.service.AIService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SupervisorControllerTest {

    @Mock
    private HelpRequestRepository hrRepo;

    @Mock
    private AIService aiService;

    @InjectMocks
    private SupervisorController supervisorController;

    public SupervisorControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPending() {
        HelpRequest request1 = new HelpRequest();
        HelpRequest request2 = new HelpRequest();
        List<HelpRequest> pendingList = Arrays.asList(request1, request2);

        when(hrRepo.findByStatus("PENDING")).thenReturn(pendingList);

        List<HelpRequest> result = supervisorController.getPending();

        assertEquals(2, result.size());
        verify(hrRepo, times(1)).findByStatus("PENDING");
    }

    @Test
    public void testAnswer() {
        Long id = 1L;
        String answer = "This is the answer";

        doNothing().when(aiService).receiveSupervisorAnswer(id, answer);

        ResponseEntity<String> response = supervisorController.answer(id, answer);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Supervisor responded and AI updated.", response.getBody());
        verify(aiService, times(1)).receiveSupervisorAnswer(id, answer);
    }

    @Test
    public void testGetAllHelpRequests() {
        HelpRequest request1 = new HelpRequest();
        HelpRequest request2 = new HelpRequest();
        List<HelpRequest> allRequests = Arrays.asList(request1, request2);

        when(hrRepo.findAll()).thenReturn(allRequests);
        List<HelpRequest> result = supervisorController.getAllHelpRequests();

        assertEquals(2, result.size());
        verify(hrRepo, times(1)).findAll();
    }
}