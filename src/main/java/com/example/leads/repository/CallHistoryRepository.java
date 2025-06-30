package com.example.leads.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.leads.model.CallHistory;
import com.example.leads.model.Lead;

public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {
    List<CallHistory> findByLeadOrderByCallDatetimeDesc(Lead lead);
}
