package com.example.repository;  // package where this repository interface is located

import com.example.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// This interface gives you built-in CRUD methods for the account entity
// It is managed by Spring Data JPA, so you don't need to write any SQL manually
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Custom method to check an account by username
    boolean existsByUsername(String username);

    // Custom method to find an account by username
    Account findByUsername(String username);

    // Custom method to find an account by username and password
    Account findByUsernameAndPassword(String username, String password);

    // Custom method to find an account by accountId
    Account findByAccountId(Integer accountId);
}
