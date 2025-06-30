package com.example.leads.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.leads.model.CallHistory;
import com.example.leads.model.Lead;
import com.example.leads.model.ResultCode;
import com.example.leads.repository.CallHistoryRepository;
import com.example.leads.repository.LeadRepository;
import com.example.leads.repository.ResultCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallService {

    private final LeadRepository leadRepo;
    private final ResultCodeRepository codeRepo;
    private final CallHistoryRepository callRepo;

    @Transactional
    public CallHistory registerCall(Long leadId,
    		Long resultCodeId,
            String memo,
            LocalDateTime nextCallDate) {

        Lead lead   = leadRepo.findById(leadId)
                              .orElseThrow(() -> new IllegalArgumentException("Lead not found"));
        ResultCode rc = codeRepo.findById(resultCodeId)
                                .orElseThrow(() -> new IllegalArgumentException("ResultCode not found"));

        // CallHistory 作成
        CallHistory ch = CallHistory.builder()
                                    .lead(lead)
                                    .callDatetime(LocalDateTime.now())
                                    .resultCode(rc)
                                    .prevResultCode(lead.getLastCallResult())
                                    .note(memo)
                                    .build();
        callRepo.save(ch);

        // Lead 更新
        lead.setLastCallDate(ch.getCallDatetime());
        lead.setLastCallResult(rc);
        lead.setNextCallDate(nextCallDate);
        leadRepo.save(lead);

        return ch;
    }
    

    @Transactional(readOnly = true)
    public List<CallHistory> getHistories(Lead lead) {
        return callRepo.findByLeadOrderByCallDatetimeDesc(lead);
    }
    
}
