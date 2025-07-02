package com.example.leads.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.leads.model.Lead;
import com.example.leads.model.ResultCode;
import com.example.leads.repository.LeadRepository;
import com.example.leads.repository.ResultCodeRepository;
import com.example.leads.spec.LeadSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepo;
    private final ResultCodeRepository codeRepo;

    /* ========= 検索・取得 ========= */

    @Transactional(readOnly = true)
    public Page<Lead> searchLeads(String keyword,
                                  Boolean recallOnly,
                                  List<Long> resultCodeIds,
                                  Pageable pageable) {

        Specification<Lead> spec = Specification.where(null);

        // キーワード
        spec = spec.and(LeadSpecifications.keyword(keyword));

        // 要再コール
        if (Boolean.TRUE.equals(recallOnly)) {
            spec = spec.and(LeadSpecifications.recall(LocalDateTime.now()));
        }

        // 電話結果フィルタ
        spec = spec.and(LeadSpecifications.lastResultIn(resultCodeIds));

        // 何も条件が付かなかった場合は全件になる
        return leadRepo.findAll(spec, pageable);
    }



    public Optional<Lead> getLead(Long id) {
        return leadRepo.findById(id);
    }

    public List<Lead> findRecalls(LocalDateTime asOf) {
        // nextCallDate <= asOf で抽出
        return leadRepo.findAll(/* TODO: 実装 */);
    }

    /* ========= CSV 取り込み ========= */
    @Transactional
    public ImportResult importCsv(InputStream stream) {
        int total = 0;
        int inserted = 0;
        ResultCode defaultCode = codeRepo.findById(1L).orElse(null);
        var newLeads = new java.util.ArrayList<Lead>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // header
            if (line == null) {
                return new ImportResult(0, 0, 0);
            }
            while ((line = reader.readLine()) != null) {
                total++;
                String[] cols = line.split(",", -1);
                if (cols.length < 5) continue;
                String phone = cols[4].trim();
                if (phone.isEmpty() || leadRepo.findByPhoneNumber(phone).isPresent()) {
                    continue;
                }
                Lead lead = Lead.builder()
                        .handler(emptyToNull(cols[0]))
                        .handlerRole(emptyToNull(cols[1]))
                        .companyName(emptyToNull(cols[2]))
                        .address(emptyToNull(cols[3]))
                        .phoneNumber(phone)
                        .jobTitle(emptyToNull(cols[5]))
                        .jobDescription(emptyToNull(cols[6]))
                        .postingDate(parseDate(cols[7]))
                        .totalEmployees(parseInt(cols[8]))
                        .localEmployees(parseInt(cols[9]))
                        .trialEmployment(parseBool(cols[10]))
                        .lastCallDate(parseDateTime(cols[11]))
                        .lastCallResult(defaultCode)
                        .memo(emptyToNull(cols[13]))
                        .nextCallDate(parseDateTime(cols[14]))
                        .build();
                newLeads.add(lead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!newLeads.isEmpty()) {
            leadRepo.saveAll(newLeads);
            inserted = newLeads.size();
        }
        return new ImportResult(total, inserted, total - inserted);
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty() || "N/A".equalsIgnoreCase(s)) return null;
        return s;
    }

    private static Integer parseInt(String s) {
        try {
            s = emptyToNull(s);
            return s != null ? Integer.valueOf(s) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean parseBool(String s) {
        s = emptyToNull(s);
        if (s == null) return null;
        if (s.equals("1") || s.equalsIgnoreCase("true")) return Boolean.TRUE;
        if (s.equals("0") || s.equalsIgnoreCase("false")) return Boolean.FALSE;
        return null;
    }

    private static LocalDate parseDate(String s) {
        s = emptyToNull(s);
        if (s == null) return null;
        try {
            DateTimeFormatter jp = DateTimeFormatter.ofPattern("yyyy年M月d日");
            return LocalDate.parse(s, jp);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(s);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private static LocalDateTime parseDateTime(String s) {
        s = emptyToNull(s);
        if (s == null) return null;
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(s, f);
        } catch (DateTimeParseException e) {
            LocalDate d = parseDate(s);
            return d != null ? d.atStartOfDay() : null;
        }
    }

    /* ========= 更新 ========= */
    @Transactional
    public Lead saveLead(Lead lead) {  // 会社情報の編集など
        return leadRepo.save(lead);
    }

    /* ========== DTO ========== */
    public record ImportResult(int totalRows, int inserted, int skipped) {}
}
