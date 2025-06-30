package com.example.leads.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.leads.model.Lead;
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
    public ImportResult importCsv(MultipartFile file) {
        // 1. CSV 解析 → DTO
        // 2. 電話番号で重複チェック
        // 3. 新規のみ saveAll
        // 4. 取り込み件数を返却
        throw new UnsupportedOperationException("importCsv 未実装");
    }

    /* ========= 更新 ========= */
    @Transactional
    public Lead saveLead(Lead lead) {  // 会社情報の編集など
        return leadRepo.save(lead);
    }

    /* ========== DTO ========== */
    public record ImportResult(int totalRows, int inserted, int skipped) {}
}
