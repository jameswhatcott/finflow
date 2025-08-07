package com.jameswhatcott.finance.personal_finance_tracker.entity.enums;

public enum CategoryType {
    INCOME("Income"),
    EXPENSE("Expense");
    
    private final String displayName;
    
    CategoryType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 