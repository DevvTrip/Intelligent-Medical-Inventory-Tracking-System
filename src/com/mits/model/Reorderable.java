package com.mits.model;

/**
 * Interface representing items that can be reordered when stock runs low.
 * Part of Unit 2: Interfaces and Polymorphism.
 */
public interface Reorderable {
    /**
     * Checks if current stock level has fallen below or reached reorder threshold.
     * @return true if restocking is needed, false otherwise
     */
    boolean needsReorder();

    /**
     * Calculates the recommended replenishment quantity to bring stock back to safe levels.
     * @return recommended quantity to reorder
     */
    int reorderQuantity();
}
