package com.inhouse.marketplace.controller;

import com.inhouse.marketplace.entity.User;
import com.inhouse.marketplace.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for User registration, login, and management.
 * Base path: /api/users
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User registration and authentication")
public class UserController {

    private final IUserService userService;

    /** POST /api/users/register — Register a new user */
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user));
    }

    /** POST /api/users/login — Authenticate with userId and password */
    @PostMapping("/login")
    @Operation(summary = "Login with credentials")
    public ResponseEntity<User> login(@RequestBody User user) {
        return ResponseEntity.ok(userService.login(user));
    }

    /** POST /api/users/logout — Logout (stateless acknowledgement) */
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<User> logout(@RequestBody User user) {
        return ResponseEntity.ok(userService.logout(user));
    }

    /** PUT /api/users/{userId} — Update user details */
    @PutMapping("/{userId}")
    @Operation(summary = "Update user")
    public ResponseEntity<User> update(@PathVariable String userId, @RequestBody User user) {
        user.setUserId(userId);
        return ResponseEntity.ok(userService.editUser(user));
    }

    /** DELETE /api/users/{userId} — Delete a user */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        userService.removeUser(userId);
        return ResponseEntity.noContent().build();
    }
}
