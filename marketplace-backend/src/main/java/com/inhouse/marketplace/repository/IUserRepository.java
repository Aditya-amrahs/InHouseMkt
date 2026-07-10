package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity operations.
 * Extends JpaRepository to inherit standard CRUD methods.
 */
@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    /**
     * Validates credentials for login — finds a user matching both userId and password.
     */
    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.password = :password")
    Optional<User> findByUserIdAndPassword(@Param("userId") String userId,
                                            @Param("password") String password);
}
