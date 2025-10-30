package com.frontdesk.repository;

import com.frontdesk.model.LearnedAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LearnedAnswerRepository extends JpaRepository<LearnedAnswer, Long> {
    Optional<LearnedAnswer> findByQuestion(String question);
}