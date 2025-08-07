package com.jameswhatcott.finance.personal_finance_tracker.service;

import com.jameswhatcott.finance.personal_finance_tracker.entity.BudgetPlannedAmount;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Category;
import com.jameswhatcott.finance.personal_finance_tracker.entity.User;
import com.jameswhatcott.finance.personal_finance_tracker.repository.BudgetPlannedAmountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetPlannedAmountService {
    
    @Autowired
    private BudgetPlannedAmountRepository budgetPlannedAmountRepository;
    
    public BudgetPlannedAmount savePlannedAmount(User user, Category category, BigDecimal plannedAmount, YearMonth budgetMonth) {
        String budgetMonthStr = budgetMonth.toString();
        
        // Check if planned amount already exists for this user, category, and month
        Optional<BudgetPlannedAmount> existing = budgetPlannedAmountRepository
            .findByUserIdAndCategoryIdAndBudgetMonth(user.getId(), category.getId(), budgetMonthStr);
        
        if (existing.isPresent()) {
            // Update existing
            BudgetPlannedAmount existingAmount = existing.get();
            existingAmount.setPlannedAmount(plannedAmount);
            return budgetPlannedAmountRepository.save(existingAmount);
        } else {
            // Create new
            BudgetPlannedAmount newAmount = BudgetPlannedAmount.builder()
                .user(user)
                .category(category)
                .plannedAmount(plannedAmount)
                .budgetMonth(budgetMonthStr)
                .build();
            return budgetPlannedAmountRepository.save(newAmount);
        }
    }
    
    public List<BudgetPlannedAmount> getPlannedAmountsForUserAndMonth(Long userId, YearMonth budgetMonth) {
        return budgetPlannedAmountRepository.findByUserIdAndBudgetMonth(userId, budgetMonth.toString());
    }
}
