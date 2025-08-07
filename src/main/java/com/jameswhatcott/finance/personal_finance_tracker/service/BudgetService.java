package com.jameswhatcott.finance.personal_finance_tracker.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Budget;
import com.jameswhatcott.finance.personal_finance_tracker.entity.User;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.TransactionType;
import com.jameswhatcott.finance.personal_finance_tracker.repository.BudgetRepository;
import com.jameswhatcott.finance.personal_finance_tracker.repository.CategoryRepository;
import com.jameswhatcott.finance.personal_finance_tracker.repository.UserRepository;
import com.jameswhatcott.finance.personal_finance_tracker.repository.TransactionRepository;

import java.math.RoundingMode;

@Service
public class BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    // Create a new budget
    public Budget createBudget(Budget budget, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Business validation
        validateBudget(budget);
        
        // Set user and timestamps
        budget.setUser(user);
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());
        budget.setIsActive(true);
        
        return budgetRepository.save(budget);
    }
    
    // Get all budgets for a user
    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }
    
    // Get budget by ID
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }
    
    // Update budget
    public Budget updateBudget(Long id, Budget budgetDetails) {
        Budget budget = budgetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        budget.setName(budgetDetails.getName());
        budget.setAmount(budgetDetails.getAmount());
        budget.setPeriod(budgetDetails.getPeriod());
        budget.setStartDate(budgetDetails.getStartDate());
        budget.setEndDate(budgetDetails.getEndDate());
        budget.setUpdatedAt(LocalDateTime.now());
        
        return budgetRepository.save(budget);
    }
    
    // Delete budget
    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
    
    // Check if budget is exceeded (business logic!)
    public boolean isBudgetExceeded(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        BigDecimal spentAmount = calculateSpentAmount(budget);
        return spentAmount.compareTo(budget.getAmount()) > 0;
    }
    
    // Get budget progress (business logic!)
    public BigDecimal getBudgetProgress(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        BigDecimal spentAmount = calculateSpentAmount(budget);
        if (budget.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return spentAmount.divide(budget.getAmount(), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSpentAmount(Budget budget) {
        // If budget is category-specific, calculate spending for that category
        if (budget.getCategory() != null) {
            return transactionRepository.findByUserIdAndCategoryId(
                budget.getUser().getId(), 
                budget.getCategory().getId()
            ).stream()
            .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .abs(); // Convert negative amounts to positive
        }
        
        // If budget is overall, calculate all expenses
        return transactionRepository.findByUserId(budget.getUser().getId())
            .stream()
            .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .abs(); // Convert negative amounts to positive
    }
    
    // Business validation
    private void validateBudget(Budget budget) {
        if (budget.getName() == null || budget.getName().trim().isEmpty()) {
            throw new RuntimeException("Budget name is required");
        }
        if (budget.getAmount() == null || budget.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Budget amount must be greater than zero");
        }
        if (budget.getStartDate() == null) {
            throw new RuntimeException("Budget start date is required");
        }
    }
}
