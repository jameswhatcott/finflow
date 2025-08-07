package com.jameswhatcott.finance.personal_finance_tracker.controller;

// Temporarily disabled until Plaid API keys are available
/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jameswhatcott.finance.personal_finance_tracker.service.PlaidService;

import java.util.Map;

@RestController
@RequestMapping("/api/plaid")
public class PlaidController {
    
    // Create link token for frontend
    @PostMapping("/create-link-token")
    public ResponseEntity<Map<String, String>> createLinkToken(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String linkToken = plaidService.createLinkToken(userId);
        return ResponseEntity.ok(Map.of("linkToken", linkToken));
    }
    
    // Exchange public token for access token
    @PostMapping("/exchange-token")
    public ResponseEntity<Map<String, String>> exchangeToken(@RequestBody Map<String, String> request) {
        String publicToken = request.get("publicToken");
        String accessToken = plaidService.exchangePublicToken(publicToken);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }
    
    // Sync transactions
    @PostMapping("/sync-transactions")
    public ResponseEntity<Map<String, String>> syncTransactions(@RequestBody Map<String, String> request) {
        String accessToken = request.get("accessToken");
        Long userId = Long.parseLong(request.get("userId"));
        Long accountId = Long.parseLong(request.get("accountId"));
        
        plaidService.syncTransactions(accessToken, userId, accountId);
        return ResponseEntity.ok(Map.of("message", "Transactions synced successfully"));
    }
}
*/
