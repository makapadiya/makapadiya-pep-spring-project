// This class is a part of service package
package com.example.service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service // Marks this class as a service component so Spring can manage it
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired // Spring will automatically inject MessageService instance here
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    // Create (Insert) a new message into the Message table
    public ResponseEntity<Message> createMessage(Message message) {
        // Validate messageText and postedBy
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty() ||
                message.getMessageText().length() > 255 || message.getPostedBy() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Check if an Account exists with the given username (postedBy)
        String username = getUsernameByAccountId(message.getPostedBy());
        if (username == null || !accountRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Save the new message to the database
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    // Method to retrieve username based on accountId (postedBy)
    public String getUsernameByAccountId(Integer accountId) {
        // Fetch the Account entity from repository
        Account account = accountRepository.findByAccountId(accountId);

        // Return username if account is found, otherwise null
        return account != null ? account.getUsername() : null;
    }

    // Method to retrieve all messages
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Method to retrieve message based on messageId
    public ResponseEntity<Message> getMessageById(Integer messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            return ResponseEntity.ok(optionalMessage.get());
        } else {
            return ResponseEntity.ok().build();
        }
    }

    // Method to delete a message based on messageId
    public ResponseEntity<Object> deleteMessageById(Integer messageId) {
        // Check if the message with the given ID exists
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return ResponseEntity.ok().body("1");
        } else {
            return ResponseEntity.ok().build();
        }
    }

    // Method to update a message based on messageId
    public ResponseEntity<Object> updateMessageText(Integer messageId, Message newMessageText) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
    
        if (optionalMessage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message not found");
        }
    
        Message message = optionalMessage.get();
    
        // Check if newMessageText is null or empty after trimming
        if (newMessageText.getMessageText() == null || newMessageText.getMessageText().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Message text cannot be empty");
        }
    
        // Check if newMessageText exceeds 255 characters
        if (newMessageText.getMessageText().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Message too long: it must have a length of at most 255 characters");
        }
    
        // Update message text and save
        message.setMessageText(newMessageText.getMessageText());
        messageRepository.save(message);
    
        // Return success response
        return ResponseEntity.ok().body("1"); // Assuming "1" represents the number of rows updated
    }

    // Method to retrieve messages based on accountId (postedBy)
    public ResponseEntity<List<Message>> getMessagesByAccountId(Integer accountId) {
        List<Message> messages = messageRepository.findByPostedBy(accountId);

        return ResponseEntity.ok(messages);
    }
}