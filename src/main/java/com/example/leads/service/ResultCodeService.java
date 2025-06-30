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

    public ResultCode save(ResultCode code) {
        return repo.save(code);
    }
}
