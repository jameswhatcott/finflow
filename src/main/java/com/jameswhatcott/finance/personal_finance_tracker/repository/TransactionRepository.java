package com.jameswhatcott.finance.personal_finance_tracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Add this method
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);
    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
