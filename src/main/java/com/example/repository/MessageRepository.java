package com.example.repository;  // package where this repository interface is located

import com.example.entity.Message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// This interface gives you built-in CRUD methods for the message entity
// It is managed by Spring Data JPA, so you don't need to write any SQL manually
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Custom query method: find all messages posted by a specific user (by Account Id)
    // Spring will automatically generate SQl statement like:
    // SELECT * FROM message WHERE postedBy = ?
    List<Message> findByPostedBy(Integer accountId);
}