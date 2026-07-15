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
import jakarta.servlet.http.HttpSession;
import com.inhouse.marketplace.exception.ForbiddenOperationException;

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
    public ResponseEntity<User> register(@Valid @RequestBody User user, HttpSession session) {
        User saved = userService.addUser(user);
        session.setAttribute("AUTH_USER_ID", saved.getUserId());
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** POST /api/users/login — Authenticate with userId and password */
    @PostMapping("/login")
    @Operation(summary = "Login with credentials")
    public ResponseEntity<User> login(@RequestBody User user, HttpSession session) {
        User authenticated = userService.login(user);
        session.setAttribute("AUTH_USER_ID", authenticated.getUserId());
        authenticated.setPassword(null);
        return ResponseEntity.ok(authenticated);
    }

    /** POST /api/users/logout — Logout (stateless acknowledgement) */
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<Void> delete(@PathVariable String userId, HttpSession session) {
        Object authenticatedUser = session.getAttribute("AUTH_USER_ID");
        if (authenticatedUser == null || !userId.equals(authenticatedUser.toString())) {
            throw new ForbiddenOperationException("You can only delete your own account.");
        }
        userService.removeCurrentUser(userId);
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/users/me - Delete the authenticated user's account and owned data. */
    @DeleteMapping("/me")
    @Operation(summary = "Delete current user account")
    public ResponseEntity<Void> deleteCurrentUser(HttpSession session) {
        Object authenticatedUser = session.getAttribute("AUTH_USER_ID");
        if (authenticatedUser == null) {
            throw new ForbiddenOperationException("An authenticated session is required.");
        }
        userService.removeCurrentUser(authenticatedUser.toString());
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
