package com.example.leads.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.leads.model.Lead;
import com.example.leads.service.CallService;
import com.example.leads.service.LeadService;
import com.example.leads.service.ResultCodeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
public class LeadController {

    private final LeadService leadSvc;
    private final ResultCodeService codeSvc;
    private final CallService callSvc;

    /* ======= 一覧 ======= */
    @GetMapping("/leads")
    public String list(@RequestParam(required = false) String q,
    				   @RequestParam(defaultValue="0") int recall,
                       @RequestParam(required = false) List<Long> resultCodes,
                       @PageableDefault(size = 20) Pageable pageable,
                       Model model) {

        Page<Lead> leads = leadSvc.searchLeads(q, recall == 1, resultCodes, pageable);
        model.addAttribute("leads", leads);
        model.addAttribute("codes", codeSvc.getActiveCodes());
        model.addAttribute("recall", recall);
        // ★ ここを追加 – null のときは空リストを渡す
        model.addAttribute("resultCodes",
                resultCodes != null ? resultCodes : List.of());
        return "leads/list";
    }

    /* ======= 詳細 ======= */
    @GetMapping("/leads/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Lead lead = leadSvc.getLead(id)
                           .orElseThrow(() -> new IllegalArgumentException("Lead not found"));

        // 前後ナビ用の ID（最小限の実装：ID±1 試行。実運用でページング対応なら追加実装）
        model.addAttribute("prevLeadId", id > 1 ? id - 1 : null);
        model.addAttribute("nextLeadId", id + 1);   // 存在チェックは省略

        model.addAttribute("lead", lead);
        model.addAttribute("callHistories", callSvc.getHistories(lead));
        model.addAttribute("resultCodes", codeSvc.getActiveCodes());
        return "leads/detail";
    }

    /* ======= 要再コール一覧 ======= */
    @GetMapping("/recalls")
    public String recalls(Model model) {
        model.addAttribute("recalls", leadSvc.findRecalls(LocalDateTime.now()));
        return "recall";
    }
}
