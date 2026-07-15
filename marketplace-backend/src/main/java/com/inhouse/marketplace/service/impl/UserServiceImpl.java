package com.inhouse.marketplace.service.impl;

import com.inhouse.marketplace.entity.Employee;
import com.inhouse.marketplace.entity.Proposal;
import com.inhouse.marketplace.entity.Resource;
import com.inhouse.marketplace.entity.User;
import com.inhouse.marketplace.exception.DuplicateResourceException;
import com.inhouse.marketplace.exception.InvalidCredentialsException;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IUserRepository;
import com.inhouse.marketplace.repository.IEmployeeRepository;
import com.inhouse.marketplace.repository.IProposalRepository;
import com.inhouse.marketplace.repository.IResourceRepository;
import com.inhouse.marketplace.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementation of IUserService — handles registration, login, and user management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IEmployeeRepository employeeRepository;
    private final IProposalRepository proposalRepository;
    private final IResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User addUser(User user) {
        // Check for duplicate userId before saving
        if (userRepository.existsById(user.getUserId())) {
            throw new DuplicateResourceException(
                    "User with userId '" + user.getUserId() + "' already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(User user) {
        // Preserve compatibility with the existing repository contract and
        // previously stored plaintext records during the hash migration.
        User directMatch = userRepository.findByUserIdAndPassword(user.getUserId(), user.getPassword())
                .orElse(null);
        if (directMatch != null) {
            return directMatch;
        }
        User stored = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid userId or password."));
        // The fallback keeps existing local data usable during the migration from
        // plaintext passwords; all newly registered passwords are BCrypt hashes.
        boolean matches = passwordEncoder.matches(user.getPassword(), stored.getPassword())
                || stored.getPassword().equals(user.getPassword());
        if (!matches) {
            throw new InvalidCredentialsException("Invalid userId or password.");
        }
        return stored;
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

    @Override
    public void removeCurrentUser(String userId) {
        removeUserData(userId);
    }

    private void removeUserData(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Employee employee = employeeRepository.findByUserId(userId).orElse(null);

        if (employee == null) {
            userRepository.delete(user);
            return;
        }

        java.util.List<Proposal> proposalsToDelete = new java.util.ArrayList<>(proposalRepository.findAllByEmpId(employee.getEmpId()));
        java.util.List<Resource> resources = resourceRepository.findAllByEmpId(employee.getEmpId());
        for (Resource resource : resources) {
            proposalsToDelete.addAll(proposalRepository.findAllByResourceId(resource.getResId()));
        }
        proposalRepository.deleteAll(new java.util.HashSet<>(proposalsToDelete));
        resourceRepository.deleteAll(resources);
        employeeRepository.delete(employee);
    }
}
