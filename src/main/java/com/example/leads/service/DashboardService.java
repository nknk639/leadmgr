package com.example.leads.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.leads.repository.CallHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final CallHistoryRepository callRepo;
    private static final String APPOINT_CODE = "APPOINT";

    public long countCallsInMonth(YearMonth ym) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
        return callRepo.countCallsBetween(start, end);
    }

    public long countAppointmentsInMonth(YearMonth ym) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
        return callRepo.countByResultCodeBetween(APPOINT_CODE, start, end);
    }

    public long countCallsInDay(LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.plusDays(1).atStartOfDay();
        return callRepo.countCallsBetween(start, end);
    }

    public long countAppointmentsInDay(LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.plusDays(1).atStartOfDay();
        return callRepo.countByResultCodeBetween(APPOINT_CODE, start, end);
    }

    public double appointmentRate(long calls, long apps) {
        if (calls == 0) return 0d;
        return (double) apps * 100d / calls;
    }

    public List<MonthlyValue<Long>> monthlyCallCounts() {
        return callRepo.countMonthlyCalls().stream()
                .map(r -> new MonthlyValue<>(YearMonth.parse((String) r[0]), ((Number) r[1]).longValue()))
                .toList();
    }

    public List<MonthlyValue<Long>> monthlyAppointmentCounts() {
        return callRepo.countMonthlyAppointments(APPOINT_CODE).stream()
                .map(r -> new MonthlyValue<>(YearMonth.parse((String) r[0]), ((Number) r[1]).longValue()))
                .toList();
    }

    public List<MonthlyValue<Double>> monthlyAppointmentRates() {
        Map<YearMonth, Long> calls = monthlyCallCounts().stream()
                .collect(Collectors.toMap(MonthlyValue::month, MonthlyValue::value));
        Map<YearMonth, Long> apps = monthlyAppointmentCounts().stream()
                .collect(Collectors.toMap(MonthlyValue::month, MonthlyValue::value));
        var months = new TreeMap<YearMonth, Double>();
        for (YearMonth m : calls.keySet()) {
            long call = calls.getOrDefault(m, 0L);
            long app = apps.getOrDefault(m, 0L);
            months.put(m, appointmentRate(call, app));
        }
        for (YearMonth m : apps.keySet()) {
            months.putIfAbsent(m, appointmentRate(calls.getOrDefault(m, 0L), apps.get(m)));
        }
        return months.entrySet().stream()
                .map(e -> new MonthlyValue<>(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(MonthlyValue::month))
                .toList();
    }

    public List<DailyValue> dailyCalls(YearMonth ym) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
        return callRepo.countDailyCalls(start, end).stream()
                .map(r -> new DailyValue(LocalDate.parse(r[0].toString()), ((Number) r[1]).longValue()))
                .toList();
    }

    public record MonthlyValue<T>(YearMonth month, T value) {
        public String formattedMonth() {
            return month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
    }

    public record DailyValue(LocalDate date, long count) {
        public String formattedDate() {
            return date.format(DateTimeFormatter.ofPattern("MM/dd"));
        }
    }
}