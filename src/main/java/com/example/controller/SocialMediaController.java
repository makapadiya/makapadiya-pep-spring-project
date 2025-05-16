package com.example.controller;  // package where this controller is located

import com.example.entity.Account;  // Account is user class
import com.example.entity.Message;  // Message is message/post class
import com.example.service.AccountService;  // import service layer class, where business logic lives
import com.example.service.MessageService;  // import service layer class, where business logic lives

import com.example.repository.AccountRepository;

import java.util.List;  // This Java interface is used to represent an ordered collection (also known as a sequence) of objects
import java.util.Optional;  // This is a container object that may or may not contain a non-null value. It's primarily used to represent the absence of a value, providing a more explicit way to handle situations where a result might be null.

import org.springframework.beans.factory.annotation.Autowired;  // Tells Spring to automatically provide dependecies
import org.springframework.http.HttpStatus;  // This is used to indicate the result of an HTTP request
import org.springframework.http.ResponseEntity;  // This is Spring class to build HTTP responses with status codes & bodies
import org.springframework.web.bind.annotation.PostMapping;  // This is used to map HTTP POST requests to specific handler methods in a controller
import org.springframework.web.bind.annotation.GetMapping;  // This is used to map HTTP GET requests
import org.springframework.web.bind.annotation.DeleteMapping;  // This is used to map HTTP DELETE requests
import org.springframework.web.bind.annotation.PatchMapping;  // This is used to map HTTP PATCH requests
import org.springframework.web.bind.annotation.PathVariable;  // This is used to extract values directly from the URI path.
import org.springframework.web.bind.annotation.RequestBody;  // This is used to bind the HTTP request body to a method parameter
import org.springframework.web.bind.annotation.RestController;  // This is a specialized Spring annotation that simplifies the creation of RESTful web services. It combines the functionalities of @Controller and @ResponseBody.

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

// This annotation tells Spring that this class is a REST controller.
// REST controller handles HTTP requests and returns data (usually JSON) as responses. 
@RestController // Tells spring this class will handle HTTP requests (like a web controller) return JSON 
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired // Spring will automatically inject AccountRepository instance here
    private AccountRepository accountRepository;

    @Autowired // Spring will automatically inject SocialMediaController instance here
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    // 1: Our API should be able to process new User registrations.
    @PostMapping("/register") // This annotation is used to map HTTP POST request.
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        
        // Account existingAccount = accountService.getAccountByUsername(account.getUsername());
        Account existingAccount = accountRepository.findByUsername(account.getUsername());
        if (existingAccount != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Return status 409
        }

        Account savedAccount = accountService.insertAccount(account);
        if (savedAccount == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Return status 409
        }

        return new ResponseEntity<>(savedAccount, HttpStatus.OK);
    }

    // 2: Our API should be able to process User logins.
    @PostMapping("/login") // This annotation is used to map HTTP POST request.
    public ResponseEntity<Object> login(@RequestBody Account account) {
        try {
            ResponseEntity<Account> response = accountService.login(account);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 3: Our API should be able to process the creation of new messages.
    @PostMapping("/messages") // This annotation is used to map HTTP POST request.
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        
        Message savedMessage = messageService.createMessage(message);

        if (savedMessage == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return status 400
        }

        return new ResponseEntity<>(savedMessage, HttpStatus.OK);

    }

    // 4: Our API should be able to retrieve all messages.
    @GetMapping("/messages") // This annotation is used to map HTTP GET request.
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    // 5: Our API should be able to retrieve a message by its ID.
    @GetMapping("/messages/{messageId}") // This annotation is used to map HTTP GET request.
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        
        Optional<Message> optionalMessage = messageService.getMessageById(messageId);
        
        if (optionalMessage.isPresent()) {
            return ResponseEntity.ok(optionalMessage.get());
        } else {
            return ResponseEntity.ok().build();
        }

    }

    // 6: Our API should be able to delete a message identified by a message ID.
    @DeleteMapping("messages/{messageId}") // This annotation is used to map HTTP DELETE request.
    public ResponseEntity<Object> deleteMessage(@PathVariable Integer messageId) {
        
        Boolean isDeleted = messageService.deleteMessageById(messageId);
        if (isDeleted) {
            return ResponseEntity.ok().body("1");
        } else {
            return ResponseEntity.ok().build();
        }

    }

    // 7: Our API should be able to update a message text identified by a message ID.
    @PatchMapping("messages/{messageId}") // This annotation is used to map HTTP PATCH request.
    public ResponseEntity<Object> updateMessageText(@PathVariable Integer messageId, @RequestBody Message newMessageText) {
        
        try {
            ResponseEntity<Object> response = messageService.updateMessageText(messageId, newMessageText);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        
    }

    // 8: Our API should be able to retrieve all messages written by a particular user.
    @GetMapping("accounts/{accountId}/messages") // This annotation is used to map HTTP GET request.
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        
        List<Message> messages = messageService.getMessagesByAccountId(accountId);

        return ResponseEntity.ok(messages);
    }
}
