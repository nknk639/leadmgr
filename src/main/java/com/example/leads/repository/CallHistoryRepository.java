package com.example.leads.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.leads.model.CallHistory;
import com.example.leads.model.Lead;

public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {
	
	@EntityGraph(attributePaths = {"resultCode", "prevResultCode"})
    List<CallHistory> findByLeadOrderByCallDatetimeDesc(Lead lead);
	
    @Query(value = "SELECT COUNT(*) FROM call_history " +
	            "WHERE call_datetime >= :start AND call_datetime < :end",
	    nativeQuery = true)
	long countCallsBetween(@Param("start") LocalDateTime start,
	                    @Param("end") LocalDateTime end);
	
	@Query(value = "SELECT COUNT(*) FROM call_history ch " +
	            "JOIN result_codes rc ON ch.result_code = rc.result_code_id " +
	            "WHERE rc.code = :code " +
	            "AND ch.call_datetime >= :start AND ch.call_datetime < :end",
	    nativeQuery = true)
	long countByResultCodeBetween(@Param("code") String code,
	                           @Param("start") LocalDateTime start,
	                           @Param("end") LocalDateTime end);
	
	@Query(value = "SELECT DATE(call_datetime) AS d, COUNT(*) AS cnt " +
	            "FROM call_history " +
	            "WHERE call_datetime >= :start AND call_datetime < :end " +
	            "GROUP BY d ORDER BY d",
	    nativeQuery = true)
	List<Object[]> countDailyCalls(@Param("start") LocalDateTime start,
	                            @Param("end") LocalDateTime end);
	
	@Query(value = "SELECT DATE_FORMAT(call_datetime, '%Y-%m') AS ym, COUNT(*) AS cnt " +
	            "FROM call_history GROUP BY ym ORDER BY ym",
	    nativeQuery = true)
	List<Object[]> countMonthlyCalls();
	
	@Query(value = "SELECT DATE_FORMAT(ch.call_datetime, '%Y-%m') AS ym, COUNT(*) AS cnt " +
	            "FROM call_history ch JOIN result_codes rc ON ch.result_code = rc.result_code_id " +
	            "WHERE rc.code = :code GROUP BY ym ORDER BY ym",
	    nativeQuery = true)
	List<Object[]> countMonthlyAppointments(@Param("code") String code);
}
