package com.jameswhatcott.finance.personal_finance_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Budget;


@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Add this method
    List<Budget> findByUserId(Long userId);
}

