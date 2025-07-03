// ResultCode.java
package com.example.leads.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "result_codes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResultCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_code_id")
    private Long id;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "label", length = 255)
    private String label;

    @Column(name = "active", nullable = false, columnDefinition = "TINYINT(1) default 1")
    private Boolean active;
    
    public boolean isEnabled() {
    	return Boolean.TRUE.equals(this.active);
    }
    
}
