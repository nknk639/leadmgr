package com.example.leads.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.leads.model.ResultCode;
import com.example.leads.repository.ResultCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResultCodeService {

    private final ResultCodeRepository repo;

    public List<ResultCode> getActiveCodes() {
        return repo.findByActiveTrue();
    }
    
    /** Get all result codes */
    public List<ResultCode> getAllCodes() {
        return repo.findAll();
    }

    /** Get a code by id or null if not found */
    public ResultCode getCode(Long id) {
        return repo.findById(id).orElse(null);
    }
    
    /** Toggle active flag */
    public void toggleActive(Long id, boolean active) {
        repo.findById(id).ifPresent(code -> {
            code.setActive(active);
            repo.save(code);
        });
    }

    public ResultCode save(ResultCode code) {
        return repo.save(code);
    }
}
