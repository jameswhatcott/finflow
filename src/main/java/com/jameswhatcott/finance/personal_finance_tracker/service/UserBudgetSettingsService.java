package com.jameswhatcott.finance.personal_finance_tracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jameswhatcott.finance.personal_finance_tracker.entity.UserBudgetSettings;
import com.jameswhatcott.finance.personal_finance_tracker.entity.User;
import com.jameswhatcott.finance.personal_finance_tracker.repository.UserBudgetSettingsRepository;
import com.jameswhatcott.finance.personal_finance_tracker.repository.UserRepository;

@Service
public class UserBudgetSettingsService {
    
    @Autowired
    private UserBudgetSettingsRepository budgetSettingsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Get budget settings for a user
    public Optional<UserBudgetSettings> getBudgetSettingsByUserId(Long userId) {
        return budgetSettingsRepository.findByUserId(userId);
    }
    
    // Create or update budget settings
    public UserBudgetSettings saveBudgetSettings(Long userId, BigDecimal monthlyBudget, LocalDate budgetStartDate) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get existing settings or create new ones
        UserBudgetSettings settings = budgetSettingsRepository.findByUserId(userId)
            .orElse(new UserBudgetSettings());
        
        settings.setUser(user);
        settings.setMonthlyBudget(monthlyBudget);
        settings.setBudgetStartDate(budgetStartDate != null ? budgetStartDate : LocalDate.now());
        settings.setIsActive(true);
        settings.setCreatedAt(LocalDateTime.now());
        settings.setUpdatedAt(LocalDateTime.now());
        
        return budgetSettingsRepository.save(settings);
    }
    
    // Update monthly budget
    public UserBudgetSettings updateMonthlyBudget(Long userId, BigDecimal monthlyBudget) {
        UserBudgetSettings settings = budgetSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Budget settings not found"));
        
        settings.setMonthlyBudget(monthlyBudget);
        settings.setUpdatedAt(LocalDateTime.now());
        
        return budgetSettingsRepository.save(settings);
    }
    
    // Get monthly budget for a user
    public BigDecimal getMonthlyBudget(Long userId) {
        return budgetSettingsRepository.findByUserId(userId)
            .map(UserBudgetSettings::getMonthlyBudget)
            .orElse(BigDecimal.ZERO);
    }
} 