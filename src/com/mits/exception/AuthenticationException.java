package com.mits.exception;

/**
 * Checked exception thrown when user credentials fail verification.
 * Part of Unit 3: Exception Handling.
 */
public class AuthenticationException extends Exception {
    private final String username;

    public AuthenticationException(String username) {
        super(String.format("Authentication failed for username: '%s'. Invalid credentials.", username));
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
