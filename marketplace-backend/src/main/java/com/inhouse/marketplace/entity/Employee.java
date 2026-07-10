package com.inhouse.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents an employee in the organisation.
 * Each Employee has exactly one User login (1:1).
 * An Employee can own multiple Requirements and Offers.
 */
@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private int empId;

    @NotBlank(message = "Employee name must not be blank")
    @Column(name = "emp_name", nullable = false, length = 150)
    private String empName;

    @Column(name = "dept_name", length = 100)
    private String deptName;

    @Column(name = "location", length = 100)
    private String location;

    /**
     * 1:1 relationship — each employee maps to exactly one login user.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
