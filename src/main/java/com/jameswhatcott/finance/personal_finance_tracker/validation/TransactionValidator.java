package com.jameswhatcott.finance.personal_finance_tracker.validation;

import org.springframework.stereotype.Component;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import java.math.BigDecimal;

@Component
public class TransactionValidator {
    
    public void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() == null) {
            throw new IllegalArgumentException("Transaction amount is required");
        }
        
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction description is required");
        }
        
        if (transaction.getTransactionDate() == null) {
            throw new IllegalArgumentException("Transaction date is required");
        }
        
        if (transaction.getTransactionType() == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
    }
    
    public void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Amount cannot be zero");
        }
    }
}
