package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.User;
import com.inhouse.marketplace.exception.DuplicateResourceException;
import com.inhouse.marketplace.exception.InvalidCredentialsException;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IUserRepository;
import com.inhouse.marketplace.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl — covers TEST MATRIX: Login Module.
 * All repository calls are mocked with Mockito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
                .userId("emp001@company.com")
                .password("Secret@123")
                .build();
    }

    // -----------------------------------------------------------------------
    // Registration Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testUserRegistration_ValidData_ReturnsSuccess")
    void testUserRegistration_ValidData_ReturnsSuccess() {
        when(userRepository.existsById(validUser.getUserId())).thenReturn(false);
        when(userRepository.save(validUser)).thenReturn(validUser);

        User result = userService.addUser(validUser);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(validUser.getUserId());
        verify(userRepository).save(validUser);
    }

    @Test
    @DisplayName("testUserRegistration_DuplicateUserId_ThrowsException")
    void testUserRegistration_DuplicateUserId_ThrowsException() {
        when(userRepository.existsById(validUser.getUserId())).thenReturn(true);

        assertThatThrownBy(() -> userService.addUser(validUser))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // Login Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testLogin_ValidCredentials_ReturnsUser")
    void testLogin_ValidCredentials_ReturnsUser() {
        when(userRepository.findByUserIdAndPassword(validUser.getUserId(), validUser.getPassword()))
                .thenReturn(Optional.of(validUser));

        User result = userService.login(validUser);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(validUser.getUserId());
    }

    @Test
    @DisplayName("testLogin_InvalidCredentials_ThrowsException")
    void testLogin_InvalidCredentials_ThrowsException() {
        when(userRepository.findByUserIdAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(validUser))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid");
    }

    // -----------------------------------------------------------------------
    // Logout Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testLogout_ValidUser_ReturnsSuccess")
    void testLogout_ValidUser_ReturnsSuccess() {
        when(userRepository.findById(validUser.getUserId())).thenReturn(Optional.of(validUser));

        User result = userService.logout(validUser);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(validUser.getUserId());
    }

    // -----------------------------------------------------------------------
    // Edit Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testEditUser_ValidData_UpdatesUser")
    void testEditUser_ValidData_UpdatesUser() {
        User updated = User.builder().userId(validUser.getUserId()).password("NewPass@456").build();
        when(userRepository.existsById(updated.getUserId())).thenReturn(true);
        when(userRepository.save(updated)).thenReturn(updated);

        User result = userService.editUser(updated);

        assertThat(result.getPassword()).isEqualTo("NewPass@456");
    }

    // -----------------------------------------------------------------------
    // Delete Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testRemoveUser_ExistingUserId_DeletesUser")
    void testRemoveUser_ExistingUserId_DeletesUser() {
        when(userRepository.existsById(validUser.getUserId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(validUser.getUserId());

        assertThatCode(() -> userService.removeUser(validUser.getUserId()))
                .doesNotThrowAnyException();

        verify(userRepository).deleteById(validUser.getUserId());
    }

    @Test
    @DisplayName("testRemoveUser_NonExistingUserId_ThrowsException")
    void testRemoveUser_NonExistingUserId_ThrowsException() {
        when(userRepository.existsById("ghost@company.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.removeUser("ghost@company.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
