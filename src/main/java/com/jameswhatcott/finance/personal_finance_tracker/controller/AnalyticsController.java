package com.jameswhatcott.finance.personal_finance_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jameswhatcott.finance.personal_finance_tracker.service.AnalyticsService;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    // GET /api/analytics/user/{userId}/income?year=2024&month=1
    @GetMapping("/user/{userId}/income")
    public ResponseEntity<BigDecimal> getMonthlyIncome(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        BigDecimal income = analyticsService.getMonthlyIncome(userId, yearMonth);
        return ResponseEntity.ok(income);
    }
    
    // GET /api/analytics/user/{userId}/expenses?year=2024&month=1
    @GetMapping("/user/{userId}/expenses")
    public ResponseEntity<BigDecimal> getMonthlyExpenses(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        BigDecimal expenses = analyticsService.getMonthlyExpenses(userId, yearMonth);
        return ResponseEntity.ok(expenses);
    }
    
    // GET /api/analytics/user/{userId}/spending-by-category?year=2024&month=1
    @GetMapping("/user/{userId}/spending-by-category")
    public ResponseEntity<Map<String, BigDecimal>> getSpendingByCategory(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        Map<String, BigDecimal> spendingByCategory = analyticsService.getSpendingByCategory(userId, yearMonth);
        return ResponseEntity.ok(spendingByCategory);
    }
    
    // GET /api/analytics/user/{userId}/net-worth-change?year=2024&month=1
    @GetMapping("/user/{userId}/net-worth-change")
    public ResponseEntity<BigDecimal> getNetWorthChange(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        BigDecimal netWorthChange = analyticsService.getNetWorthChange(userId, yearMonth);
        return ResponseEntity.ok(netWorthChange);
    }
}
