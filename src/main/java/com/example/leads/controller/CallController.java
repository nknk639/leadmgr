package com.example.leads.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.leads.service.CallService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
public class CallController {

    private final CallService callSvc;

    /* ====== 架電登録 (Ajax or 通常 POST) ====== */
    @PostMapping("/leads/{id}/calls")
    public String registerCall(@PathVariable Long id,
                               @RequestParam Long resultId,
                               @RequestParam(required = false) String memo,
                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nextCall) {

        callSvc.registerCall(id, resultId, memo, nextCall);
        // 連続ナビのためリダイレクト先を判断するロジックは後で実装
        return "redirect:/leads/" + id;
    }
}