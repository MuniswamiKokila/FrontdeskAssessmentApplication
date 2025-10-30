package com.frontdesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpRequest {
    @Id
    @GeneratedValue
    private Long id;
    private String callerId;
    private String question;
    private String status; // PENDING, RESOLVED, UNRESOLVED
    private String supervisorAnswer;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;
}