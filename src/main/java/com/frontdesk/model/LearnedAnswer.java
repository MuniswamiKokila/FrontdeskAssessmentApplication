package com.frontdesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearnedAnswer {
    @Id
    @GeneratedValue
    private Long id;
    private String question;
    private String answer;
}