package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.User;

/**
 * Service contract for User (login/registration) operations.
 */
public interface IUserService {

    /** Register a new user. Throws DuplicateResourceException if userId already exists. */
    User addUser(User user);

    /** Validate credentials and return the User. Throws InvalidCredentialsException on failure. */
    User login(User user);

    /** Logout — currently a no-op placeholder (stateless REST). */
    User logout(User user);

    /** Update password or other mutable fields of an existing user. */
    User editUser(User user);

    /** Remove a user by userId. */
    void removeUser(String userId);

    /** Remove the authenticated user and all marketplace data owned by its employee profile. */
    void removeCurrentUser(String userId);
}
