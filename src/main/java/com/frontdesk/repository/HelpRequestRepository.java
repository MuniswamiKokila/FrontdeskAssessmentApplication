package com.frontdesk.repository;

import com.frontdesk.model.HelpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HelpRequestRepository extends JpaRepository<HelpRequest, Long> {
    List<HelpRequest> findByStatus(String status);
}