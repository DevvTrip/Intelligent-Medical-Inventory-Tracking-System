package com.mits.service;

import com.mits.model.Supplier;
import com.mits.util.Repository;
import com.mits.util.Searchable;

import java.util.List;

/**
 * Concrete generic repository for Supplier entities.
 * Demonstrates:
 * - Unit 5: Generic class inheritance (extends Repository<Supplier, String>)
 * - Unit 5: Generic interface implementation (implements Searchable<Supplier>)
 */
public class SupplierRepository extends Repository<Supplier, String> implements Searchable<Supplier> {
    private static final long serialVersionUID = 1L;

    public SupplierRepository() {
        super(Supplier::getId);
    }

    @Override
    public List<Supplier> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.trim().toLowerCase();
        return findBy(s -> s.getId().toLowerCase().contains(q)
                || s.getName().toLowerCase().contains(q)
                || s.getContactPerson().toLowerCase().contains(q));
    }
}
