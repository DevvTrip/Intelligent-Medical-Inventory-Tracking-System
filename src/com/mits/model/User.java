package com.mits.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an authenticated system user.
 * Part of Unit 1: Classes and Encapsulation.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String fullName;
    private Role role;

    /** Default constructor */
    public User() {
        this.username = "guest";
        this.password = "guest123";
        this.fullName = "Guest User";
        this.role = Role.DOCTOR;
    }

    /** Parameterized constructor */
    public User(String username, String password, String fullName, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role != null ? role : Role.DOCTOR;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Checks if user has admin privileges.
     */
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    /**
     * Checks if user has pharmacist or higher privileges.
     */
    public boolean canManageStock() {
        return role == Role.ADMIN || role == Role.PHARMACIST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [%s]", fullName, username, role);
    }
}
