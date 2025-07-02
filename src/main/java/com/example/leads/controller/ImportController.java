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
        try (var reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(file.getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return "import";
            }
            var headers = java.util.Arrays.asList(headerLine.split(",", -1));
            var rows = new java.util.ArrayList<java.util.List<String>>();
            for (int i = 0; i < 10; i++) {
                String line = reader.readLine();
                if (line == null) break;
                rows.add(java.util.Arrays.asList(line.split(",", -1)));
            }
            session.setAttribute("IMPORT_FILE_BYTES", file.getBytes());
            model.addAttribute("headers", headers);
            model.addAttribute("previewRows", rows);
        } catch (java.io.IOException e) {
            log.error("CSV preview error", e);
        }
        return "import";
    }

    /* ===== 取込実行 ===== */
    @PostMapping("/import/execute")
    public String execute(HttpSession session, Model model) {
    	byte[] bytes = (byte[]) session.getAttribute("IMPORT_FILE_BYTES");
        if (bytes == null) {
            return "redirect:/import";
        }
        var result = leadSvc.importCsv(new java.io.ByteArrayInputStream(bytes));
        session.removeAttribute("IMPORT_FILE_BYTES");
        model.addAttribute("progress", 100);
        model.addAttribute("importResult", result);
        // プレビューを再表示しなくても良いならリダイレクト
        return "import";
    }
}
