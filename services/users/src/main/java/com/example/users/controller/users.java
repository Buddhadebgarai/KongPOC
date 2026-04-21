package com.example.users.controller;

import com.example.users.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping(path = "api/v1/users")
public class users {

    private static final Logger log = LoggerFactory.getLogger(users.class);
    private static final AtomicLong counter = new AtomicLong(1);
    private static final Map<Long, User> userStore = Collections.synchronizedMap(new LinkedHashMap<>());

    static {
        // Initialize with sample data
        userStore.put(1L, new User(1L, "John Doe", "john@example.com"));
        userStore.put(2L, new User(2L, "Jane Smith", "jane@example.com"));
        counter.set(3L);
    }

    @GetMapping(path = "/")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users");
        return ResponseEntity.ok(new ArrayList<>(userStore.values()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userStore.get(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Creating new user: {}", user);
        Long newId = counter.getAndIncrement();
        user.setId(newId);
        userStore.put(newId, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("Updating user with id: {}", id);
        if (!userStore.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
        user.setId(id);
        userStore.put(id, user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userStore.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found"));
        }
        userStore.remove(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
