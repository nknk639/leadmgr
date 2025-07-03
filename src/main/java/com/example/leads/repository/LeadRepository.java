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
    
 // 前後ナビ用: 指定IDより小さい(古い)レコードのうち最大ID
    Optional<Lead> findFirstByIdLessThanOrderByIdDesc(Long id);

    // 前後ナビ用: 指定IDより大きい(新しい)レコードのうち最小ID
    Optional<Lead> findFirstByIdGreaterThanOrderByIdAsc(Long id);
}
