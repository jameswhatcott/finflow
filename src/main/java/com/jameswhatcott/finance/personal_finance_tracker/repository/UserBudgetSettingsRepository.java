package com.jameswhatcott.finance.personal_finance_tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jameswhatcott.finance.personal_finance_tracker.entity.UserBudgetSettings;

@Repository
public interface UserBudgetSettingsRepository extends JpaRepository<UserBudgetSettings, Long> {
    Optional<UserBudgetSettings> findByUserId(Long userId);
} 