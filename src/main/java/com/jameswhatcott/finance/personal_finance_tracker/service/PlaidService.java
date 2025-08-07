package com.jameswhatcott.finance.personal_finance_tracker.service;

// Temporarily disabled until Plaid API keys are available
/*
import com.plaid.client.ApiClient;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Account;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Category;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.TransactionType;
import com.jameswhatcott.finance.personal_finance_tracker.service.TransactionService;
import com.jameswhatcott.finance.personal_finance_tracker.service.CategoryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PlaidService {
    
    // Create link token for frontend
    public String createLinkToken(String userId) {
        try {
            // PlaidApi plaidApi = plaidApiClient.createService(PlaidApi.class); // This line is commented out
            
            // LinkTokenCreateRequest request = new LinkTokenCreateRequest() // This line is commented out
            //     .user(new LinkTokenCreateRequestUser().clientUserId(userId)) // This line is commented out
            //     .clientName("Personal Finance Tracker") // This line is commented out
            //     .products(List.of("transactions")) // This line is commented out
            //     .countryCodes(List.of("US")) // This line is commented out
            //     .language("en"); // This line is commented out
            
            // LinkTokenCreateResponse response = plaidApi.linkTokenCreate(request).execute(); // This line is commented out
            return "Link token creation is temporarily disabled."; // This line is commented out
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create link token: " + e.getMessage());
        }
    }
    
    // Exchange public token for access token
    public String exchangePublicToken(String publicToken) {
        try {
            // PlaidApi plaidApi = plaidApiClient.createService(PlaidApi.class); // This line is commented out
            
            // ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest() // This line is commented out
            //     .publicToken(publicToken); // This line is commented out
            
            // ItemPublicTokenExchangeResponse response = plaidApi.itemPublicTokenExchange(request).execute(); // This line is commented out
            return "Public token exchange is temporarily disabled."; // This line is commented out
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange public token: " + e.getMessage());
        }
    }
    
    // Sync transactions from Plaid
    public void syncTransactions(String accessToken, Long userId, Long accountId) {
        try {
            // PlaidApi plaidApi = plaidApiClient.createService(PlaidApi.class); // This line is commented out
            
            // // Get transactions for the last 30 days
            // LocalDate endDate = LocalDate.now();
            // LocalDate startDate = endDate.minusDays(30);
            
            // TransactionsGetRequest request = new TransactionsGetRequest() // This line is commented out
            //     .accessToken(accessToken) // This line is commented out
            //     .startDate(startDate) // This line is commented out
            //     .endDate(endDate); // This line is commented out
            
            // TransactionsGetResponse response = plaidApi.transactionsGet(request).execute(); // This line is commented out
            
            // // Process each transaction
            // for (com.plaid.client.model.Transaction plaidTransaction : response.getTransactions()) { // This line is commented out
            //     // Check if transaction already exists
            //     if (!transactionExists(plaidTransaction.getTransactionId())) { // This line is commented out
            //         createTransactionFromPlaid(plaidTransaction, userId, accountId); // This line is commented out
            //     }
            // }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync transactions: " + e.getMessage());
        }
    }
    
    private boolean transactionExists(String plaidTransactionId) {
        // This would check your database for existing transactions
        // For now, return false to allow all transactions
        return false;
    }
    
    private void createTransactionFromPlaid(com.plaid.client.model.Transaction plaidTransaction, 
                                          Long userId, Long accountId) {
        try {
            // Determine transaction type based on amount
            TransactionType transactionType = plaidTransaction.getAmount() >= 0 ? 
                TransactionType.INCOME : TransactionType.EXPENSE;
            
            // Find or create category based on Plaid category
            Long categoryId = findOrCreateCategory(plaidTransaction.getCategory());
            
            // Create transaction
            Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(plaidTransaction.getAmount()))
                .description(plaidTransaction.getName())
                .transactionDate(LocalDate.parse(plaidTransaction.getDate()))
                .transactionType(transactionType)
                .merchantName(plaidTransaction.getMerchantName())
                .plaidTransactionId(plaidTransaction.getTransactionId())
                .plaidCategory(String.join(", ", plaidTransaction.getCategory()))
                .build();
            
            // transactionService.createTransaction(transaction, accountId, userId, categoryId); // This line is commented out
            
        } catch (Exception e) {
            // Log error but continue processing other transactions
            System.err.println("Error creating transaction from Plaid: " + e.getMessage());
        }
    }
    
    private Long findOrCreateCategory(List<String> plaidCategories) {
        if (plaidCategories == null || plaidCategories.isEmpty()) {
            return null;
        }
        
        // Use the primary category
        String primaryCategory = plaidCategories.get(0);
        
        // Try to find existing category
        // Optional<Category> existingCategory = categoryService.getCategoryByName(primaryCategory); // This line is commented out
        // if (existingCategory.isPresent()) { // This line is commented out
        //     return existingCategory.get().getId(); // This line is commented out
        // }
        
        // Create new category
        // Category newCategory = Category.builder() // This line is commented out
        //     .name(primaryCategory) // This line is commented out
        //     .description("Auto-created from Plaid") // This line is commented out
        //     .color("#6c757d") // This line is commented out
        //     .build(); // This line is commented out
        
        // Category savedCategory = categoryService.createCategory(newCategory); // This line is commented out
        return null; // This line is commented out
    }
}
*/
