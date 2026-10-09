package com.mits.model;

import java.time.LocalDate;

/**
 * Interface representing items that have an expiration date.
 * Part of Unit 2: Interfaces and Polymorphism.
 */
public interface Expirable {
    /**
     * Checks if the item has passed its expiry date.
     * @return true if expired, false otherwise
     */
    boolean isExpired();

    /**
     * Calculates the number of days remaining until the item expires.
     * Negative value means already expired.
     * @return number of days
     */
    long daysToExpiry();

    /**
     * Gets the expiration date of the item.
     * @return LocalDate of expiry
     */
    LocalDate getExpiryDate();
}
