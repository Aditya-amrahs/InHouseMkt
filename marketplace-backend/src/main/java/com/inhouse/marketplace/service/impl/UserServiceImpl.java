package com.inhouse.marketplace.service.impl;

import com.inhouse.marketplace.entity.User;
import com.inhouse.marketplace.exception.DuplicateResourceException;
import com.inhouse.marketplace.exception.InvalidCredentialsException;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IUserRepository;
import com.inhouse.marketplace.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of IUserService — handles registration, login, and user management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    @Override
    public User addUser(User user) {
        // Check for duplicate userId before saving
        if (userRepository.existsById(user.getUserId())) {
            throw new DuplicateResourceException(
                    "User with userId '" + user.getUserId() + "' already exists.");
        }
        return userRepository.save(user);
    }

    @Override
    public User login(User user) {
        return userRepository
                .findByUserIdAndPassword(user.getUserId(), user.getPassword())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid userId or password."));
    }

    @Override
    public User logout(User user) {
        // Stateless REST — logout is a client-side operation.
        // Validate the user exists and return it for acknowledgement.
        return userRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", user.getUserId()));
    }

    @Override
    public User editUser(User user) {
        if (!userRepository.existsById(user.getUserId())) {
            throw new ResourceNotFoundException("User", user.getUserId());
        }
        return userRepository.save(user);
    }

    @Override
    public void removeUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        userRepository.deleteById(userId);
    }
}
