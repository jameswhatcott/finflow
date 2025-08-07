package com.jameswhatcott.finance.personal_finance_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Account;
import com.jameswhatcott.finance.personal_finance_tracker.entity.User;
import com.jameswhatcott.finance.personal_finance_tracker.repository.AccountRepository;
import com.jameswhatcott.finance.personal_finance_tracker.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Create a new account for a user
    public Account createAccount(Account account, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Business logic
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());
        account.setIsActive(true);
        
        return accountRepository.save(account);
    }
    
    // Get all accounts for a user
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }
    
    // Get account by ID
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    // Update account
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.setAccountName(accountDetails.getAccountName());
        account.setAccountType(accountDetails.getAccountType());
        account.setUpdatedAt(LocalDateTime.now());
        
        return accountRepository.save(account);
    }
    
    // Delete account
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
    
    // Calculate total balance for a user
    public BigDecimal calculateTotalBalance(Long userId) {
        List<Account> accounts = getAccountsByUserId(userId);
        
        return accounts.stream()
            .filter(Account::getIsActive)
            .map(account -> account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Update account balance (called by TransactionService)
    public void updateAccountBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Handle null balance
        BigDecimal currentBalance = account.getBalance();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        
        account.setBalance(currentBalance.add(amount));
        account.setUpdatedAt(LocalDateTime.now());
        
        accountRepository.save(account);
    }

    // Get all accounts (for admin purposes)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
