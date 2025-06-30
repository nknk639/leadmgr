package com.example.leads.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.leads.model.ResultCode;

public interface ResultCodeRepository extends JpaRepository<ResultCode, Long> {
    List<ResultCode> findByActiveTrue();
}
