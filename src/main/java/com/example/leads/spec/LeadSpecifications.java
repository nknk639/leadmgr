package com.example.leads.spec;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.example.leads.model.Lead;

/** Lead 検索条件生成ユーティリティ */
public final class LeadSpecifications {

    private LeadSpecifications() {}

    /** キーワード（会社名 or 電話番号 部分一致） */
    public static Specification<Lead> keyword(String kw) {
        if (kw == null || kw.isBlank()) return null;

        String like = "%" + kw.trim().toLowerCase() + "%";
        return (root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("companyName")), like),
                    cb.like(cb.lower(root.get("phoneNumber")), like)
                );
    }

    /** 要再コール (nextCallDate <= now) */
    public static Specification<Lead> recall(LocalDateTime now) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("nextCallDate"), now);
    }

    /** 最終架電結果が指定 ID 群に含まれる */
    public static Specification<Lead> lastResultIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return (root, query, cb) -> {
            var join = root.join("lastCallResult", JoinType.LEFT);
            query.distinct(true); // 同じ Lead が重複しないように
            return join.get("id").in(ids);
        };
    }
}
