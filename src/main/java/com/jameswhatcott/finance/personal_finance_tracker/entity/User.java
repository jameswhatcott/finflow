package com.jameswhatcott.finance.personal_finance_tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.UserRole;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.BudgetPeriod;
import com.jameswhatcott.finance.personal_finance_tracker.entity.enums.IncomeFrequency;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.OneToOne;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;

    @Embedded
    private Address address;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER; // ADMIN, USER

    @Column(name = "spouse_id")
    private Long spouseId; // Reference to wife's user ID

    @Column(name = "household_id")
    private Long householdId; // For shared accounts/budgets

    @Column(name = "notification_preferences")
    private String notificationPreferences; // JSON for email/SMS preferences

    // User preferences:
    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "America/Denver"; // Utah timezone

    @Column(name = "date_format")
    @Builder.Default
    private String dateFormat = "MM/dd/yyyy";

    @Column(name = "language")
    @Builder.Default
    private String language = "en";

    // Security features:
    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    // Budget-related preferences:
    @Column(name = "budget_period")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BudgetPeriod budgetPeriod = BudgetPeriod.MONTHLY; // MONTHLY, YEARLY

    @Column(name = "budget_notifications")
    private Boolean budgetNotifications = true;

    // Useful for spending analysis:
    @Column(name = "income_frequency")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IncomeFrequency incomeFrequency = IncomeFrequency.MONTHLY; // MONTHLY, BIWEEKLY, etc.

    @Column(name = "default_currency")
    @Builder.Default
    private String defaultCurrency = "USD";

    @Column(name = "tax_bracket")
    private String taxBracket; // For tax calculations

    // User will have multiple accounts, so we might want:
    @Column(name = "primary_account_id")
    private Long primaryAccountId; // Which account to show first

    @Column(name = "account_preferences")
    private String accountPreferences; // JSON for display preferences

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-accounts")
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-transactions")
    private List<Transaction> transactions = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-budget-settings")
    private UserBudgetSettings budgetSettings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserBudgetSettings getBudgetSettings() {
        return budgetSettings;
    }

    public void setBudgetSettings(UserBudgetSettings budgetSettings) {
        this.budgetSettings = budgetSettings;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    // Builder method since Lombok isn't working properly
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String username;
        private String password;
        private String name;
        private String email;
        private UserRole role;
        private boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder role(UserRole role) {
            this.role = role;
            return this;
        }

        public UserBuilder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setEmail(email);
            user.setRole(role);
            user.setIsActive(isActive);
            user.setCreatedAt(createdAt);
            user.setUpdatedAt(updatedAt);
            return user;
        }
    }
}
