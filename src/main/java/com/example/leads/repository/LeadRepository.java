package com.example.leads.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.leads.model.Lead;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByPhoneNumber(String phoneNumber);
}
