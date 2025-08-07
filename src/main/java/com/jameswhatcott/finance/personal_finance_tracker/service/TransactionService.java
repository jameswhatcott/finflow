package com.jameswhatcott.finance.personal_finance_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Account;
import com.jameswhatcott.finance.personal_finance_tracker.entity.User;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Category;
import com.jameswhatcott.finance.personal_finance_tracker.repository.TransactionRepository;
import com.jameswhatcott.finance.personal_finance_tracker.repository.AccountRepository;
import com.jameswhatcott.finance.personal_finance_tracker.repository.UserRepository;
import com.jameswhatcott.finance.personal_finance_tracker.repository.CategoryRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private AccountService accountService;
    
    // Create a new transaction
    public Transaction createTransaction(Transaction transaction, Long accountId, Long userId, Long categoryId) {
        // Get the related entities
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        }
        
        // Set the relationships
        transaction.setAccount(account);
        transaction.setUser(user);
        transaction.setCategory(category);
        
        // Business validation
        validateTransaction(transaction);
        
        // Set timestamps
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        
        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Update account balance (business logic!)
        accountService.updateAccountBalance(accountId, transaction.getAmount());
        
        return savedTransaction;
    }
    
    // Get all transactions for a user
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    // Get transactions by user ID and month
    public List<Transaction> getTransactionsByUserIdAndMonth(Long userId, java.time.YearMonth month) {
        return transactionRepository.findByUserIdAndTransactionDateBetween(
            userId, 
            month.atDay(1), 
            month.atEndOfMonth()
        );
    }
    
    // Get transaction by ID
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    // Update transaction
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Calculate balance adjustment
        BigDecimal oldAmount = transaction.getAmount();
        BigDecimal newAmount = transactionDetails.getAmount();
        BigDecimal adjustment = newAmount.subtract(oldAmount);
        
        // Update transaction
        transaction.setAmount(newAmount);
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setTransactionDate(transactionDetails.getTransactionDate());
        transaction.setUpdatedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Update account balance
        accountService.updateAccountBalance(
            transaction.getAccount().getId(), 
            adjustment
        );
        
        return savedTransaction;
    }
    
    // Delete transaction
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        // Reverse the balance change
        accountService.updateAccountBalance(
            transaction.getAccount().getId(), 
            transaction.getAmount().negate()
        );
        
        transactionRepository.deleteById(id);
    }
    
    // Business validation
    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() == null) {
            throw new RuntimeException("Transaction amount is required");
        }
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Transaction description is required");
        }
        if (transaction.getAccount() == null) {
            throw new RuntimeException("Account is required");
        }
    }
}
