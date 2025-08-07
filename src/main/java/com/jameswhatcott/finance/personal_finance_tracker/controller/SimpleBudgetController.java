package com.jameswhatcott.finance.personal_finance_tracker.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Category;
import com.jameswhatcott.finance.personal_finance_tracker.entity.Transaction;
import com.jameswhatcott.finance.personal_finance_tracker.entity.User;
import com.jameswhatcott.finance.personal_finance_tracker.entity.UserBudgetSettings;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.CategoryType;
import com.jameswhatcott.finance.personal_finance_tracker.service.CategoryService;
import com.jameswhatcott.finance.personal_finance_tracker.service.TransactionService;
import com.jameswhatcott.finance.personal_finance_tracker.service.UserBudgetSettingsService;
import com.jameswhatcott.finance.personal_finance_tracker.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.TransactionType;
import com.jameswhatcott.finance.personal_finance_tracker.entity.BudgetPlannedAmount;
import com.jameswhatcott.finance.personal_finance_tracker.repository.BudgetPlannedAmountRepository;
import com.jameswhatcott.finance.personal_finance_tracker.service.BudgetPlannedAmountService;

@Controller
public class SimpleBudgetController {
    
    @Autowired
    private UserBudgetSettingsService budgetSettingsService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BudgetPlannedAmountService budgetPlannedAmountService;
    
    public static class BudgetCategoryData {
        private String categoryName;
        private BigDecimal plannedAmount;
        private BigDecimal actualAmount;
        private BigDecimal difference;
        private String categoryColor;
        
        public BudgetCategoryData(String categoryName, BigDecimal plannedAmount, BigDecimal actualAmount, String categoryColor) {
            this.categoryName = categoryName;
            this.plannedAmount = plannedAmount != null ? plannedAmount : BigDecimal.ZERO;
            this.actualAmount = actualAmount != null ? actualAmount : BigDecimal.ZERO;
            this.difference = this.actualAmount.subtract(this.plannedAmount);
            this.categoryColor = categoryColor;
        }
        
        // Getters and setters
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public BigDecimal getPlannedAmount() { return plannedAmount; }
        public void setPlannedAmount(BigDecimal plannedAmount) { 
            this.plannedAmount = plannedAmount != null ? plannedAmount : BigDecimal.ZERO;
            this.difference = this.actualAmount.subtract(this.plannedAmount);
        }
        
        public BigDecimal getActualAmount() { return actualAmount; }
        public void setActualAmount(BigDecimal actualAmount) { 
            this.actualAmount = actualAmount != null ? actualAmount : BigDecimal.ZERO;
            this.difference = this.actualAmount.subtract(this.plannedAmount);
        }
        
        public BigDecimal getDifference() { return difference; }
        public void setDifference(BigDecimal difference) { this.difference = difference; }
        
        public String getCategoryColor() { return categoryColor; }
        public void setCategoryColor(String categoryColor) { this.categoryColor = categoryColor; }
    }

    @GetMapping("/budget")
    public String showBudgetDashboard(Model model) {
        try {
            System.out.println("=== BUDGET DASHBOARD DEBUG ===");
            
            // Get the authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            Long userId = user.getId();
            
            System.out.println("1. User ID: " + userId);
            
            // Get all categories from database
            List<Category> allCategories = categoryService.getAllCategories();
            System.out.println("2. Found " + allCategories.size() + " total categories");
            
            // Separate income and expense categories
            List<Category> incomeCategories = allCategories.stream()
                .filter(cat -> cat.getType() == CategoryType.INCOME)
                .collect(Collectors.toList());
                
            List<Category> expenseCategories = allCategories.stream()
                .filter(cat -> cat.getType() == CategoryType.EXPENSE)
                .collect(Collectors.toList());
            
            System.out.println("3. Income categories: " + incomeCategories.size());
            System.out.println("4. Expense categories: " + expenseCategories.size());
            
            // If no categories exist, initialize with defaults
            if (incomeCategories.isEmpty() && expenseCategories.isEmpty()) {
                System.out.println("5. No categories found, initializing test data");
                initializeTestData(userId);
                allCategories = categoryService.getAllCategories();
                incomeCategories = allCategories.stream()
                    .filter(cat -> cat.getType() == CategoryType.INCOME)
                    .collect(Collectors.toList());
                expenseCategories = allCategories.stream()
                    .filter(cat -> cat.getType() == CategoryType.EXPENSE)
                    .collect(Collectors.toList());
                System.out.println("6. After initialization - Income: " + incomeCategories.size() + ", Expense: " + expenseCategories.size());
            }
            
            // Get current month for transactions
            YearMonth currentMonth = YearMonth.now();
            System.out.println("7. Current month: " + currentMonth);
            
            // Get transactions for current month
            List<Transaction> currentMonthTransactions = transactionService.getTransactionsByUserIdAndMonth(userId, currentMonth);
            System.out.println("8. Found " + currentMonthTransactions.size() + " transactions for current month");
            
            // Convert to BudgetCategoryData with actual amounts
            List<BudgetCategoryData> incomeBudgetData = incomeCategories.stream()
                .map(cat -> {
                    // Calculate actual amount for this category
                    BigDecimal actualAmount = currentMonthTransactions.stream()
                        .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(cat.getId()))
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    System.out.println("9. Category " + cat.getName() + " actual amount: " + actualAmount);
                    
                    return new BudgetCategoryData(cat.getName(), BigDecimal.ZERO, actualAmount, cat.getColor());
                })
                .collect(Collectors.toList());
                
            List<BudgetCategoryData> expenseBudgetData = expenseCategories.stream()
                .map(cat -> {
                    // Calculate actual amount for this category
                    BigDecimal actualAmount = currentMonthTransactions.stream()
                        .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(cat.getId()))
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    System.out.println("10. Category " + cat.getName() + " actual amount: " + actualAmount);
                    
                    return new BudgetCategoryData(cat.getName(), BigDecimal.ZERO, actualAmount, cat.getColor());
                })
                .collect(Collectors.toList());
            
            System.out.println("11. Created budget data - Income: " + incomeBudgetData.size() + ", Expense: " + expenseBudgetData.size());
            
            // Load planned amounts for current month
            List<BudgetPlannedAmount> plannedAmounts = budgetPlannedAmountService.getPlannedAmountsForUserAndMonth(userId, currentMonth);
            System.out.println("12. Loaded " + plannedAmounts.size() + " planned amounts for " + currentMonth);
            
            // Update income data with planned amounts
            for (BudgetCategoryData incomeData : incomeBudgetData) {
                plannedAmounts.stream()
                    .filter(pa -> pa.getCategory().getName().equals(incomeData.getCategoryName()))
                    .findFirst()
                    .ifPresent(pa -> {
                        incomeData.setPlannedAmount(pa.getPlannedAmount());
                        System.out.println("13. Set planned amount for " + incomeData.getCategoryName() + ": " + pa.getPlannedAmount());
                    });
            }
            
            // Update expense data with planned amounts
            for (BudgetCategoryData expenseData : expenseBudgetData) {
                plannedAmounts.stream()
                    .filter(pa -> pa.getCategory().getName().equals(expenseData.getCategoryName()))
                    .findFirst()
                    .ifPresent(pa -> {
                        expenseData.setPlannedAmount(pa.getPlannedAmount());
                        System.out.println("14. Set planned amount for " + expenseData.getCategoryName() + ": " + pa.getPlannedAmount());
                    });
            }
            
            // Calculate totals
            BigDecimal totalIncome = incomeBudgetData.stream()
                .map(BudgetCategoryData::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal totalPlannedIncome = incomeBudgetData.stream()
                .map(BudgetCategoryData::getPlannedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal totalExpenses = expenseBudgetData.stream()
                .map(BudgetCategoryData::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal totalPlannedExpenses = expenseBudgetData.stream()
                .map(BudgetCategoryData::getPlannedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            System.out.println("15. Calculated totals:");
            System.out.println("   - totalIncome: " + totalIncome);
            System.out.println("   - totalPlannedIncome: " + totalPlannedIncome);
            System.out.println("   - totalExpenses: " + totalExpenses);
            System.out.println("   - totalPlannedExpenses: " + totalPlannedExpenses);
            
            // Add to model
            model.addAttribute("incomeBudgetData", incomeBudgetData);
            model.addAttribute("expenseBudgetData", expenseBudgetData);
            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalPlannedIncome", totalPlannedIncome);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("totalPlannedExpenses", totalPlannedExpenses);
            model.addAttribute("zero", BigDecimal.ZERO);
            
            // Get budget settings for the user
            UserBudgetSettings budgetSettings = budgetSettingsService.getBudgetSettingsByUserId(userId)
                .orElse(null);
            model.addAttribute("budgetSettings", budgetSettings);
            
            // Calculate monthly budget and remaining budget
            BigDecimal monthlyBudget = budgetSettings != null ? budgetSettings.getMonthlyBudget() : BigDecimal.ZERO;
            BigDecimal remainingBudget = monthlyBudget.subtract(totalExpenses);
            
            model.addAttribute("monthlyBudget", monthlyBudget);
            model.addAttribute("remainingBudget", remainingBudget);
            
            System.out.println("16. Added all attributes to model");
            System.out.println("17. Returning simple-budget-dashboard");
            
            return "pages/simple-budget-dashboard";
            
        } catch (Exception e) {
            System.err.println("ERROR in budget dashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading budget dashboard: " + e.getMessage());
            return "pages/error";
        }
    }
    
    private void initializeTestData(Long userId) {
        try {
            // Check if we have any categories, if not create some test categories
            List<Category> existingCategories = categoryService.getAllCategories();
            if (existingCategories.isEmpty()) {
                // Create test categories
                Category salaryCategory = new Category();
                salaryCategory.setName("Salary");
                salaryCategory.setDescription("Monthly salary income");
                salaryCategory.setColor("#28a745");
                salaryCategory.setType(CategoryType.INCOME);
                categoryService.createCategory(salaryCategory);
                
                Category foodCategory = new Category();
                foodCategory.setName("Food & Dining");
                foodCategory.setDescription("Groceries and restaurant expenses");
                foodCategory.setColor("#dc3545");
                foodCategory.setType(CategoryType.EXPENSE);
                categoryService.createCategory(foodCategory);
                
                Category transportCategory = new Category();
                transportCategory.setName("Transportation");
                transportCategory.setDescription("Gas, public transport, etc.");
                transportCategory.setColor("#ffc107");
                transportCategory.setType(CategoryType.EXPENSE);
                categoryService.createCategory(transportCategory);
            }
            
            // Don't try to create a user with a specific ID, just check if categories exist
            // The user creation is causing transaction conflicts
            
        } catch (Exception e) {
            // Log the error but don't fail the page
            System.err.println("Error initializing test data: " + e.getMessage());
        }
    }
    
    @PostMapping("/budget/settings")
    public String updateBudgetSettings(
            @RequestParam BigDecimal monthlyBudget,
            @RequestParam(required = false) LocalDate budgetStartDate,
            RedirectAttributes redirectAttributes) {
        
        // For development, use default user ID
        Long userId = 1L;
        
        try {
            budgetSettingsService.saveBudgetSettings(userId, monthlyBudget, budgetStartDate);
            redirectAttributes.addFlashAttribute("success", "Budget settings updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating budget settings: " + e.getMessage());
        }
        
        return "redirect:/budget";
    }

    @PostMapping("/budget/planned-amount")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePlannedAmount(@RequestBody Map<String, Object> request) {
        try {
            String categoryName = (String) request.get("categoryName");
            String categoryType = (String) request.get("categoryType");
            String plannedAmountStr = (String) request.get("plannedAmount");
            
            BigDecimal plannedAmount = new BigDecimal(plannedAmountStr);
            
            // For now, we'll just log the update
            // In the future, you could store this in a database
            System.out.println("Updated planned amount for " + categoryName + " (" + categoryType + "): " + plannedAmount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Planned amount updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating planned amount: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/budget/update-categories")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCategories(@RequestBody Map<String, Object> request) {
        try {
            String type = (String) request.get("type");
            List<Map<String, Object>> changes = (List<Map<String, Object>>) request.get("changes");
            
            System.out.println("=== UPDATING CATEGORIES ===");
            System.out.println("Type: " + type);
            System.out.println("Changes: " + changes);
            
            // Get the authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Use current month for budget
            YearMonth currentMonth = YearMonth.now();
            
            for (Map<String, Object> change : changes) {
                Integer index = (Integer) change.get("index");
                String categoryName = (String) change.get("categoryName");
                String plannedAmountStr = (String) change.get("plannedAmount");
                
                Category category = null;
                
                if (categoryName != null && !categoryName.trim().isEmpty()) {
                    // Create or update category
                    CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
                    
                    // Check if category already exists
                    List<Category> existingCategories = categoryService.getAllCategories();
                    Category existingCategory = existingCategories.stream()
                        .filter(cat -> cat.getName().equalsIgnoreCase(categoryName) && cat.getType() == categoryType)
                        .findFirst()
                        .orElse(null);
                    
                    if (existingCategory != null) {
                        // Update existing category
                        System.out.println("Updating existing category: " + existingCategory.getName());
                        existingCategory.setName(categoryName);
                        category = categoryService.updateCategory(existingCategory);
                    } else {
                        // Create new category
                        System.out.println("Creating new category: " + categoryName);
                        Category newCategory = Category.builder()
                                .name(categoryName)
                                .type(categoryType)
                                .color("#" + String.format("%06x", new java.util.Random().nextInt(0xffffff)))
                                .build();
                        category = categoryService.createCategory(newCategory);
                    }
                }
                
                if (plannedAmountStr != null && !plannedAmountStr.trim().isEmpty() && category != null) {
                    BigDecimal plannedAmount = new BigDecimal(plannedAmountStr);
                    System.out.println("Saving planned amount for " + category.getName() + ": " + plannedAmount);
                    
                    // Save planned amount
                    budgetPlannedAmountService.savePlannedAmount(user, category, plannedAmount, currentMonth);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categories and planned amounts updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR updating categories: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating categories: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/budget/add-category")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCategory(@RequestBody Map<String, Object> request) {
        try {
            String categoryName = (String) request.get("categoryName");
            String categoryType = (String) request.get("categoryType");
            
            System.out.println("=== ADDING CATEGORY ===");
            System.out.println("Category Name: " + categoryName);
            System.out.println("Category Type: " + categoryType);
            
            // Create the category
            Category category = Category.builder()
                    .name(categoryName)
                    .type(CategoryType.valueOf(categoryType.toUpperCase()))
                    .color("#" + String.format("%06x", new java.util.Random().nextInt(0xffffff)))
                    .build();
            
            Category savedCategory = categoryService.createCategory(category);
            
            System.out.println("Category created with ID: " + savedCategory.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category created successfully");
            response.put("categoryId", savedCategory.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR creating category: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating category: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

} 