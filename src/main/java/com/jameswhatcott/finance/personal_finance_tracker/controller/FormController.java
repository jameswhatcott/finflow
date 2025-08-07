package com.jameswhatcott.finance.personal_finance_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jameswhatcott.finance.personal_finance_tracker.service.AccountService;
import com.jameswhatcott.finance.personal_finance_tracker.service.TransactionService;
import com.jameswhatcott.finance.personal_finance_tracker.service.CategoryService;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Account;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Category;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.TransactionType;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.AccountType;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class FormController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Show transaction form
    @GetMapping("/transaction/new")
    public String showTransactionForm(Model model) {
        model.addAttribute("title", "Add Transaction");
        // For now, use user ID 1
        Long userId = 1L;
        
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("accounts", accounts);
        model.addAttribute("categories", categories);
        model.addAttribute("transactionTypes", TransactionType.values());
        
        return "pages/transaction-form";
    }
    
    // Handle transaction form submission
    @PostMapping("/transaction/new")
    public String createTransaction(
            @RequestParam BigDecimal amount,
            @RequestParam String description,
            @RequestParam LocalDate transactionDate,
            @RequestParam TransactionType transactionType,
            @RequestParam Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String merchantName,
            RedirectAttributes redirectAttributes) {
        
        try {
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription(description);
            transaction.setTransactionDate(transactionDate);
            transaction.setTransactionType(transactionType);
            transaction.setMerchantName(merchantName);
            
            // For now, use user ID 1
            Long userId = 1L;
            
            transactionService.createTransaction(transaction, accountId, userId, categoryId);
            
            redirectAttributes.addFlashAttribute("success", "Transaction created successfully!");
            return "redirect:/";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating transaction: " + e.getMessage());
            return "redirect:/transaction/new";
        }
    }
    
    // Show account form
    @GetMapping("/account/new")
    public String showAccountForm(Model model) {
        model.addAttribute("title", "Add Account");
        model.addAttribute("accountTypes", AccountType.values());
        return "pages/account-form";
    }
    
    // Handle account form submission
    @PostMapping("/account/new")
    public String createAccount(
            @RequestParam String accountName,
            @RequestParam(required = false) String accountNumber,
            @RequestParam AccountType accountType,
            @RequestParam(required = false) BigDecimal balance,
            @RequestParam(defaultValue = "USD") String currency,
            RedirectAttributes redirectAttributes) {
        
        try {
            Account account = new Account();
            account.setAccountName(accountName);
            account.setAccountNumber(accountNumber);
            account.setAccountType(accountType);
            account.setBalance(balance != null ? balance : BigDecimal.ZERO);
            account.setCurrency(currency);
            
            // For now, use user ID 1
            Long userId = 1L;
            
            accountService.createAccount(account, userId);
            
            redirectAttributes.addFlashAttribute("success", "Account created successfully!");
            return "redirect:/";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating account: " + e.getMessage());
            return "redirect:/account/new";
        }
    }
    
    // Show category form
    @GetMapping("/category/new")
    public String showCategoryForm() {
        return "pages/category-form";
    }
    
    // Handle category form submission
    @PostMapping("/category/new")
    public String createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String color,
            @RequestParam CategoryType type,
            RedirectAttributes redirectAttributes) {
        
        try {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            category.setColor(color != null ? color : "#007bff");
            category.setType(type);
            
            categoryService.createCategory(category);
            
            redirectAttributes.addFlashAttribute("success", "Category created successfully!");
            return "redirect:/";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating category: " + e.getMessage());
            return "redirect:/category/new";
        }
    }
}
