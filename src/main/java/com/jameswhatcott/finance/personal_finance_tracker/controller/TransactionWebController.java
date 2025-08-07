package com.jameswhatcott.finance.personal_finance_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Category;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Account;
import com.jameswhatcott.finance.personal_finance_tracker.service.TransactionService;
import com.jameswhatcott.finance.personal_finance_tracker.service.CategoryService;
import com.jameswhatcott.finance.personal_finance_tracker.service.AccountService;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.TransactionType;
import com.jameswhatcott.finance.personal_finance_tracker.entity.User;
import com.jameswhatcott.finance.personal_finance_tracker.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.CategoryType;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TransactionWebController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Simple test endpoint to check if controller is working
     */
    @GetMapping("/transactions-test")
    public String testTransactionPage(Model model) {
        model.addAttribute("message", "Transaction controller is working!");
        return "pages/error"; // Use error page as a simple test
    }
    
    @GetMapping("/test-simple")
    public String testSimple(Model model) {
        model.addAttribute("message", "Simple test is working!");
        return "pages/error";
    }

    /**
     * Display the transaction list page with monthly filtering
     * Learning: @GetMapping maps HTTP GET requests to this method
     */
    @GetMapping("/transactions")
    public String showTransactionList(Model model) {
        
        System.out.println("=== TRANSACTION CONTROLLER DEBUG ===");
        System.out.println("1. Method showTransactionList() called");
        
        try {
            // Get the authenticated user
            System.out.println("2. Getting authenticated user...");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            System.out.println("3. Username: " + username);
            
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Long userId = user.getId();
            System.out.println("4. User ID: " + userId);
            
            // Use current month for now
            YearMonth selectedMonth = YearMonth.now();
            System.out.println("5. Selected month: " + selectedMonth);
            
            // Get transactions for the selected month
            System.out.println("6. Getting transactions...");
            List<Transaction> transactions;
            try {
                transactions = transactionService.getTransactionsByUserIdAndMonth(userId, selectedMonth);
                System.out.println("7. Found " + transactions.size() + " transactions");
            } catch (Exception e) {
                System.err.println("7. Error getting transactions: " + e.getMessage());
                e.printStackTrace();
                transactions = new ArrayList<>();
            }
            
            // Get all categories for the transaction form
            System.out.println("8. Getting categories...");
            List<Category> categories;
            try {
                categories = categoryService.getAllCategories();
                System.out.println("9. Found " + categories.size() + " categories");
            } catch (Exception e) {
                System.err.println("9. Error getting categories: " + e.getMessage());
                e.printStackTrace();
                categories = new ArrayList<>();
            }
            
            // Get all accounts for the transaction form
            System.out.println("10. Getting accounts...");
            List<Account> accounts;
            try {
                accounts = accountService.getAllAccounts();
                System.out.println("11. Found " + accounts.size() + " accounts");
            } catch (Exception e) {
                System.err.println("11. Error getting accounts: " + e.getMessage());
                e.printStackTrace();
                accounts = new ArrayList<>();
            }
            
            // Calculate summary statistics
            System.out.println("12. Calculating summary statistics...");
            BigDecimal totalIncome = transactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            BigDecimal totalExpenses = transactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            System.out.println("13. Total Income: " + totalIncome);
            System.out.println("14. Total Expenses: " + totalExpenses);
            
            // Add data to the model
            System.out.println("15. Adding data to model...");
            model.addAttribute("transactions", transactions);
            model.addAttribute("categories", categories);
            model.addAttribute("accounts", accounts);
            model.addAttribute("selectedMonth", selectedMonth);
            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("netAmount", totalIncome.subtract(totalExpenses));
            
            // Add available months for the dropdown (last 12 months)
            List<YearMonth> availableMonths = generateAvailableMonths();
            model.addAttribute("availableMonths", availableMonths);
            System.out.println("16. Added " + availableMonths.size() + " available months");
            System.out.println("16a. Available months: " + availableMonths); // Add this line
            
            System.out.println("17. Returning transaction-list page");
            return "pages/transaction-list";
            
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("ERROR in transaction controller: " + e.getMessage());
            e.printStackTrace();
            
            // Return a simple error page or redirect
            model.addAttribute("error", "Error loading transactions: " + e.getMessage());
            return "pages/error";
        }
    }
    
    @GetMapping("/transactions-full")
    public String showTransactionListFull(Model model) {
        
        try {
            // For development, use hardcoded user ID (since auth is disabled)
            Long userId = 1L;
            
            // Use current month for now
            YearMonth selectedMonth = YearMonth.now();
            
            // Get transactions for the selected month (handle case where user doesn't exist)
            List<Transaction> transactions = new ArrayList<>();
            
            // Get all categories for the transaction form
            List<Category> categories = new ArrayList<>();
            
            // Get all accounts for the transaction form
            List<Account> accounts = new ArrayList<>();
            
            // Calculate summary statistics
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpenses = BigDecimal.ZERO;
            
            // Add data to the model (this is how we pass data to Thymeleaf templates)
            model.addAttribute("transactions", transactions);
            model.addAttribute("categories", categories);
            model.addAttribute("accounts", accounts);
            model.addAttribute("selectedMonth", selectedMonth);
            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("netAmount", totalIncome.subtract(totalExpenses));
            
            // Add available months for the dropdown (last 12 months)
            List<YearMonth> availableMonths = generateAvailableMonths();
            model.addAttribute("availableMonths", availableMonths);
            
            return "pages/transaction-list";
            
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error in transaction controller: " + e.getMessage());
            e.printStackTrace();
            
            // Return a simple error page or redirect
            model.addAttribute("error", "Error loading transactions: " + e.getMessage());
            return "pages/error";
        }
    }
    
    /**
     * Handle form submission to create a new transaction
     * Learning: @PostMapping handles HTTP POST requests
     */
    @PostMapping("/transactions")
    public String createTransaction(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("description") String description,
            @RequestParam("transactionDate") LocalDate transactionDate,
            @RequestParam("transactionType") TransactionType transactionType,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam("accountId") Long accountId,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== TRANSACTION POST DEBUG ===");
        System.out.println("1. Creating transaction with amount: " + amount);
        System.out.println("2. Description: " + description);
        System.out.println("3. Date: " + transactionDate);
        System.out.println("4. Type: " + transactionType);
        System.out.println("5. Category ID: " + categoryId);
        System.out.println("6. Account ID: " + accountId);
        
        try {
            // Get the authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Long userId = user.getId();
            
            System.out.println("7. User ID: " + userId);
            
            // Create transaction object
            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .description(description)
                    .transactionDate(transactionDate)
                    .transactionType(transactionType)
                    .build();
            
            System.out.println("8. Created transaction object");
            
            // Save the transaction using the service layer
            Transaction savedTransaction = transactionService.createTransaction(transaction, accountId, userId, categoryId);
            
            System.out.println("9. Transaction saved with ID: " + savedTransaction.getId());
            
            // Add success message
            redirectAttributes.addFlashAttribute("success", "Transaction created successfully!");
            
        } catch (Exception e) {
            System.err.println("ERROR creating transaction: " + e.getMessage());
            e.printStackTrace();
            
            // Add error message
            redirectAttributes.addFlashAttribute("error", "Error creating transaction: " + e.getMessage());
        }
        
        // Redirect back to the transaction list
        return "redirect:/transactions";
    }

    /**
     * Helper method to generate available months for the dropdown
     * Learning: Private methods help organize code within the controller
     */
    private List<YearMonth> generateAvailableMonths() {
        List<YearMonth> months = new java.util.ArrayList<>();
        YearMonth current = YearMonth.now();
        
        // Generate last 12 months
        for (int i = 11; i >= 0; i--) {
            months.add(current.minusMonths(i));
        }
        
        return months;
    }
}
