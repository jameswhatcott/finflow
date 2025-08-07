package com.jameswhatcott.finance.personal_finance_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.jameswhatcott.finance.personal_finance_tracker.service.UserService;
import com.jameswhatcott.finance.personal_finance_tracker.service.AccountService;
import com.jameswhatcott.finance.personal_finance_tracker.service.TransactionService;
import com.jameswhatcott.finance.personal_finance_tracker.service.AnalyticsService;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Account;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import com.jameswhatcott.finance.personal_finance_tracker.entity.User;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class DashboardController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    // Dashboard home page
    @GetMapping("/")
    public String showDashboard(Model model) {
        try {
            // Get the authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Long userId = user.getId();
            
            // Get accounts and transactions
            List<Account> accounts = accountService.getAccountsByUserId(userId);
            List<Transaction> recentTransactions = transactionService.getTransactionsByUserId(userId);
            
            // Calculate totals
            BigDecimal totalBalance = accountService.calculateTotalBalance(userId);
            
            // Get current month analytics
            YearMonth currentMonth = YearMonth.now();
            BigDecimal monthlyIncome = analyticsService.getMonthlyIncome(userId, currentMonth);
            BigDecimal monthlyExpenses = analyticsService.getMonthlyExpenses(userId, currentMonth);
            
            // Ensure values are not null, default to zero
            monthlyIncome = monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO;
            monthlyExpenses = monthlyExpenses != null ? monthlyExpenses : BigDecimal.ZERO;
            
            // Calculate net worth change (income - expenses)
            BigDecimal netWorthChange = monthlyIncome.subtract(monthlyExpenses);
            
            model.addAttribute("user", user);
            model.addAttribute("accounts", accounts);
            model.addAttribute("recentTransactions", recentTransactions);
            model.addAttribute("totalBalance", totalBalance);
            model.addAttribute("monthlyIncome", monthlyIncome);
            model.addAttribute("monthlyExpenses", monthlyExpenses);
            model.addAttribute("netWorthChange", netWorthChange);
            
            return "pages/dashboard";
            
        } catch (Exception e) {
            // If there's an error, redirect to login
            return "redirect:/login";
        }
    }
    
    // Dashboard endpoint
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        return getDashboardForCurrentUser(model);
    }
    
    private String getDashboardForCurrentUser(Model model) {
        // For development, use default user ID
        Long userId = 1L;
        
        // Get user data
        User user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            // Create a dummy user for development
            user = new User();
            user.setId(1L);
            user.setName("Development User");
        }
        
        model.addAttribute("user", user);
        
        // Get accounts
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        model.addAttribute("accounts", accounts);
        
        // Get total balance
        BigDecimal totalBalance = accountService.calculateTotalBalance(userId);
        model.addAttribute("totalBalance", totalBalance);
        
        // Get recent transactions
        List<Transaction> recentTransactions = transactionService.getTransactionsByUserId(userId);
        model.addAttribute("recentTransactions", recentTransactions);
        
        // Get current month analytics
        YearMonth currentMonth = YearMonth.now();
        BigDecimal monthlyIncome = analyticsService.getMonthlyIncome(userId, currentMonth);
        BigDecimal monthlyExpenses = analyticsService.getMonthlyExpenses(userId, currentMonth);
        
        // Ensure values are not null, default to zero
        monthlyIncome = monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO;
        monthlyExpenses = monthlyExpenses != null ? monthlyExpenses : BigDecimal.ZERO;
        
        // Calculate net worth change (income - expenses)
        BigDecimal netWorthChange = monthlyIncome.subtract(monthlyExpenses);
        
        model.addAttribute("monthlyIncome", monthlyIncome);
        model.addAttribute("monthlyExpenses", monthlyExpenses);
        model.addAttribute("netWorthChange", netWorthChange);
        model.addAttribute("currentMonth", currentMonth);
        
        return "pages/dashboard";
    }
    
    // User profile page
    @GetMapping("/user/{userId}")
    public String userProfile(@PathVariable Long userId, Model model) {
        // For development, use default user ID
        Long developmentUserId = 1L;
        
        // Get user data
        User user = userService.getUserById(developmentUserId).orElse(null);
        if (user == null) {
            // Create a dummy user for development
            user = new User();
            user.setId(1L);
            user.setName("Development User");
        }
        
        // Get user's accounts
        List<Account> accounts = accountService.getAccountsByUserId(developmentUserId);
        
        // Get user's transactions
        List<Transaction> transactions = transactionService.getTransactionsByUserId(developmentUserId);
        
        // Calculate total balance
        BigDecimal totalBalance = accountService.calculateTotalBalance(developmentUserId);
        
        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("transactions", transactions);
        model.addAttribute("totalBalance", totalBalance);
        
        return "pages/user-profile";
    }
}
