package com.mits.model;

/**
 * Interface for entities capable of generating textual report summaries.
 * Part of Unit 2: Interfaces and Polymorphism.
 */
public interface Reportable {
    /**
     * Generates a concise single-line or block summary of the entity.
     * @return formatted report summary string
     */
    String generateSummary();
}
