package com.inhouse.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Represents a login credential for an employee.
 * userId is the employee's unique login identifier (e.g., email or employee code).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    @NotBlank(message = "User ID must not be blank")
    private String userId;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password must not be blank")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
