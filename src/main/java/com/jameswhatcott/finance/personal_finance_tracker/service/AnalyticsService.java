package com.jameswhatcott.finance.personal_finance_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.TransactionType;
import com.jameswhatcott.finance.personal_finance_tracker.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    // Get total income for a user in a specific month
    public BigDecimal getMonthlyIncome(Long userId, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        return transactionRepository.findByUserId(userId)
            .stream()
            .filter(t -> t.getTransactionType() == TransactionType.INCOME)
            .filter(t -> !t.getTransactionDate().isBefore(startDate) && !t.getTransactionDate().isAfter(endDate))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Get total expenses for a user in a specific month
    public BigDecimal getMonthlyExpenses(Long userId, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        return transactionRepository.findByUserId(userId)
            .stream()
            .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
            .filter(t -> !t.getTransactionDate().isBefore(startDate) && !t.getTransactionDate().isAfter(endDate))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .abs(); // Convert negative to positive
    }
    
    // Get spending by category for a user in a specific month
    public Map<String, BigDecimal> getSpendingByCategory(Long userId, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        return transactionRepository.findByUserId(userId)
            .stream()
            .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
            .filter(t -> !t.getTransactionDate().isBefore(startDate) && !t.getTransactionDate().isAfter(endDate))
            .filter(t -> t.getCategory() != null)
            .collect(Collectors.groupingBy(
                t -> t.getCategory().getName(),
                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
            ));
    }
    
    // Get net worth change over time
    public BigDecimal getNetWorthChange(Long userId, YearMonth yearMonth) {
        BigDecimal income = getMonthlyIncome(userId, yearMonth);
        BigDecimal expenses = getMonthlyExpenses(userId, yearMonth);
        return income.subtract(expenses);
    }
}
