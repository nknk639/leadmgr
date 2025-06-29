// CallHistory.java
package com.example.leads.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "call_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CallHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "call_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Column(name = "call_datetime", nullable = false)
    private LocalDateTime callDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_code")
    private ResultCode resultCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_result_code")
    private ResultCode prevResultCode;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
