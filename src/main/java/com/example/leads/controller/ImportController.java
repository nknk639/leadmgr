package com.example.leads.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.leads.service.LeadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
public class ImportController {

    private final LeadService leadSvc;

    /* ===== フォーム表示 ===== */
    @GetMapping("/import")
    public String form() {
        return "import";
    }

    /* ===== プレビュー生成 ===== */
    @PostMapping("/import")
    public String preview(@RequestParam MultipartFile file,
                          Model model,
                          HttpSession session) {

        // TODO: CSV を先頭10行パース → model に headers / previewRows を載せる
        // ファイルを Session に一時保持して後続の execute で利用
        session.setAttribute("IMPORT_FILE", file);
        model.addAttribute("headers", /* List<String> */ null);
        model.addAttribute("previewRows", /* List<List<String>> */ null);
        return "import";
    }

    /* ===== 取込実行 ===== */
    @PostMapping("/import/execute")
    public String execute(HttpSession session, Model model) {
        MultipartFile file = (MultipartFile) session.getAttribute("IMPORT_FILE");
        if (file == null) {
            return "redirect:/import";
        }
        var result = leadSvc.importCsv(file);
        model.addAttribute("progress", 100);
        model.addAttribute("importResult", result);
        // プレビューを再表示しなくても良いならリダイレクト
        return "import";
    }
}
