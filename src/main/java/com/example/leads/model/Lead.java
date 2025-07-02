// Lead.java
package com.example.leads.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lead_id")
    private Long id;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "phone_number", nullable = false, length = 45, unique = true)
    private String phoneNumber;

    @Column(name = "job_title", length = 255)
    private String jobTitle;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "posting_date")
    private LocalDate postingDate;

    @Column(name = "total_employees")
    private Integer totalEmployees;

    @Column(name = "local_employees")
    private Integer localEmployees;

    @Column(name = "trial_employment")
    private Boolean trialEmployment;

    @Column(name = "last_call_date")
    private LocalDateTime lastCallDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "last_call_result")
    private ResultCode lastCallResult;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "next_call_date")
    private LocalDateTime nextCallDate;
    
    @Column(name = "handler", length = 50)
    private String handler;

    @Column(name = "handler_role", length = 50)
    private String handlerRole;

    @OneToMany(mappedBy = "lead", fetch = FetchType.LAZY)
    private List<CallHistory> callHistories;
}
