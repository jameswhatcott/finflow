package com.jameswhatcott.finance.personal_finance_tracker.repository;

import com.jameswhatcott.finance.personal_finance_tracker.entity.BudgetPlannedAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetPlannedAmountRepository extends JpaRepository<BudgetPlannedAmount, Long> {
    
    List<BudgetPlannedAmount> findByUserIdAndBudgetMonth(Long userId, String budgetMonth);
    
    Optional<BudgetPlannedAmount> findByUserIdAndCategoryIdAndBudgetMonth(Long userId, Long categoryId, String budgetMonth);
}
