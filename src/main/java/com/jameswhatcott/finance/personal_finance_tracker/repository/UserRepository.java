package com.jameswhatcott.finance.personal_finance_tracker.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jameswhatcott.finance.personal_finance_tracker.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom methods can go here
    // Spring will automatically implement them based on method names
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
