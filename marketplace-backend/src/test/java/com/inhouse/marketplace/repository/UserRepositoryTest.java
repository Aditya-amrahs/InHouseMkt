package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for IUserRepository using the H2 in-memory database.
 */
@DataJpaTest
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    @Test
    @DisplayName("testUserRegistration_ValidData_SavesUser")
    void testUserRegistration_ValidData_SavesUser() {
        User user = User.builder().userId("newuser@company.com").password("Secret@1").build();

        User saved = userRepository.save(user);

        assertThat(saved.getUserId()).isEqualTo("newuser@company.com");
        assertThat(userRepository.findById("newuser@company.com")).isPresent();
    }

    @Test
    @DisplayName("testLogin_ValidCredentials_ReturnsUser")
    void testLogin_ValidCredentials_ReturnsUser() {
        userRepository.save(User.builder().userId("login@company.com").password("Secret@1").build());

        Optional<User> result = userRepository.findByUserIdAndPassword("login@company.com", "Secret@1");

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("login@company.com");
    }

    @Test
    @DisplayName("testLogin_InvalidCredentials_ReturnsEmpty")
    void testLogin_InvalidCredentials_ReturnsEmpty() {
        userRepository.save(User.builder().userId("login2@company.com").password("Secret@1").build());

        Optional<User> result = userRepository.findByUserIdAndPassword("login2@company.com", "wrong");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("testRemoveUser_ExistingUserId_DeletesUser")
    void testRemoveUser_ExistingUserId_DeletesUser() {
        userRepository.save(User.builder().userId("gone@company.com").password("pass").build());

        userRepository.deleteById("gone@company.com");

        assertThat(userRepository.findById("gone@company.com")).isEmpty();
    }
}
