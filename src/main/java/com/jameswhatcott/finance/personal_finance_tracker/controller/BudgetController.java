package com.jameswhatcott.finance.personal_finance_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Budget;
import com.jameswhatcott.finance.personal_finance_tracker.service.BudgetService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    
    @Autowired
    private BudgetService budgetService;
    
    // GET /api/budgets - Get all budgets
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }
    
    // GET /api/budgets/{id} - Get budget by ID
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Optional<Budget> budget = budgetService.getBudgetById(id);
        if (budget.isPresent()) {
            return ResponseEntity.ok(budget.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /api/budgets/user/{userId} - Get budgets for specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable Long userId) {
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }
    
    // POST /api/budgets - Create new budget
    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget, @RequestParam Long userId) {
        try {
            Budget createdBudget = budgetService.createBudget(budget, userId);
            return ResponseEntity.ok(createdBudget);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PUT /api/budgets/{id} - Update budget
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails) {
        try {
            Budget updatedBudget = budgetService.updateBudget(id, budgetDetails);
            return ResponseEntity.ok(updatedBudget);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE /api/budgets/{id} - Delete budget
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        try {
            budgetService.deleteBudget(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /api/budgets/{id}/progress - Get budget progress
    @GetMapping("/{id}/progress")
    public ResponseEntity<BigDecimal> getBudgetProgress(@PathVariable Long id) {
        BigDecimal progress = budgetService.getBudgetProgress(id);
        return ResponseEntity.ok(progress);
    }
    
    // GET /api/budgets/{id}/exceeded - Check if budget is exceeded
    @GetMapping("/{id}/exceeded")
    public ResponseEntity<Boolean> isBudgetExceeded(@PathVariable Long id) {
        boolean exceeded = budgetService.isBudgetExceeded(id);
        return ResponseEntity.ok(exceeded);
    }
}
