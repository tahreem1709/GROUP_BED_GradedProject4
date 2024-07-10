package org.ems.employee_management_system.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "First Name is mandatory")
    @Column(name = "first_name")
    private String fname;

    @NotBlank(message = "Last Name is mandatory")
    @Column(name = "last_name")
    private String lname;

    @NotBlank(message = "Email is mandatory")
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp created_at;

    @Transient
    private boolean isNew = true;

}