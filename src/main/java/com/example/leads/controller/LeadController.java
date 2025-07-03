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
     // 表示中のリードIDリストをクエリに渡すため保持
        model.addAttribute("idList",
                leads.getContent().stream().map(Lead::getId).toList());
        return "leads/list";
    }

    /* ======= 詳細 ======= */
    @GetMapping("/leads/{id}")
    public String detail(@PathVariable Long id,
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) Integer idx,
            Model model) {
        Lead lead = leadSvc.getLead(id)
                           .orElseThrow(() -> new IllegalArgumentException("Lead not found"));

     // // 表示順リストが渡されている場合はその順で前後を決定
        Long prevId = null;
        Long nextId = null;
        int currentIndex = idx != null ? idx : -1; // 1-based index for display
        int listIndex = currentIndex - 1;          // convert to 0-based for list access
        
        if (ids != null && !ids.isEmpty()) {
        	// idx パラメータが無い場合はリスト内の位置を取得
            if (currentIndex < 0) {
                listIndex = ids.indexOf(id);
                currentIndex = listIndex >= 0 ? listIndex + 1 : -1;
            }
            // 前後の ID を 0-based の位置から計算
            if (listIndex > 0) {
                prevId = ids.get(listIndex - 1);
            }
            if (listIndex >= 0 && listIndex < ids.size() - 1) {
                nextId = ids.get(listIndex + 1);
            }
        } else {
            // Fallback: 単純に ID ±1
            prevId = id > 1 ? id - 1 : null;
            nextId = id + 1;
        }

        model.addAttribute("prevLeadId", prevId);
        model.addAttribute("nextLeadId", nextId);
        model.addAttribute("idList", ids != null ? ids : List.of());
        model.addAttribute("currentIndex", currentIndex);

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
