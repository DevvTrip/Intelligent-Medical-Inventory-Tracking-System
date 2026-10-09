package com.mits.model;

/**
 * User roles defining role-based authorization levels.
 * Part of Unit 1 and Unit 5 (RBAC in GUI & Security).
 */
public enum Role {
    ADMIN("Administrator - Full access to inventory, users, and backups"),
    PHARMACIST("Pharmacist - Stock operations, issuing, restock, reports"),
    DOCTOR("Doctor - Read-only inventory and medicine search");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
