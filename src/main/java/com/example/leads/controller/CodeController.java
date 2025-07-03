package com.example.leads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.leads.model.ResultCode;
import com.example.leads.service.ResultCodeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CodeController {

    private final ResultCodeService codeSvc;

    @GetMapping("/codes")
    public String list(Model model) {
        model.addAttribute("codes", codeSvc.getAllCodes());
        return "codelist";
    }

    @PostMapping("/codes")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String label) {
        ResultCode code = id != null ? codeSvc.getCode(id) : new ResultCode();
        if (code == null) {
            code = new ResultCode();
        }
        code.setLabel(label);
        if (code.getActive() == null) {
            code.setActive(Boolean.TRUE);
        }
        codeSvc.save(code);
        return "redirect:/codes";
    }

    @PutMapping("/codes/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         @RequestParam(name="enabled", defaultValue="false") boolean enabled) {
        codeSvc.toggleActive(id, enabled);
        return "redirect:/codes";
    }
}