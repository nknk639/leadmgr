package com.example.leads.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.leads.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashSvc;
	
    @GetMapping("/")
    public String dashboard(@RequestParam(name = "period", defaultValue = "month") String period,
            Model model) {

YearMonth ym = YearMonth.now();
LocalDate today = LocalDate.now();
long calls;
long appoints;
if ("day".equals(period)) {
calls = dashSvc.countCallsInDay(today);
appoints = dashSvc.countAppointmentsInDay(today);
} else {
calls = dashSvc.countCallsInMonth(ym);
appoints = dashSvc.countAppointmentsInMonth(ym);
period = "month";
}

model.addAttribute("period", period);
model.addAttribute("kpiCall", calls);
model.addAttribute("kpiApp", appoints);
model.addAttribute("kpiRate", dashSvc.appointmentRate(calls, appoints));

model.addAttribute("rateTable", dashSvc.monthlyAppointmentRates());
model.addAttribute("callTable", dashSvc.monthlyCallCounts());
model.addAttribute("appTable", dashSvc.monthlyAppointmentCounts());

List<DashboardService.DailyValue> daily = dashSvc.dailyCalls(ym);
model.addAttribute("dailyLabels",
daily.stream().map(v -> v.date().getDayOfMonth()).toList());
model.addAttribute("dailyCalls",
daily.stream().map(DashboardService.DailyValue::count).toList());

return "dashboard";
    }
}
