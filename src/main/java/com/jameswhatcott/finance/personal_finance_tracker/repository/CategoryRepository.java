package com.jameswhatcott.finance.personal_finance_tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jameswhatcott.finance.personal_finance_tracker.entity.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Add this method
    Optional<Category> findByName(String name);
}
