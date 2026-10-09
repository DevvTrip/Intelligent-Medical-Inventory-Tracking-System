package com.mits.service;

import com.mits.model.User;
import com.mits.util.Repository;

/**
 * Concrete generic repository for User entities.
 * Demonstrates:
 * - Unit 5: Generic class inheritance (extends Repository<User, String>)
 */
public class UserRepository extends Repository<User, String> {
    private static final long serialVersionUID = 1L;

    public UserRepository() {
        super(User::getUsername);
    }
}
