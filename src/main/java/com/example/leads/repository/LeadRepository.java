package com.example.leads.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.leads.model.Lead;

public interface LeadRepository
        extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {   // ★追加

    Optional<Lead> findByPhoneNumber(String phoneNumber);

    Page<Lead> findByCompanyNameContainingIgnoreCaseOrPhoneNumberContaining(
            String companyName, String phoneNumber, Pageable pageable);
}
