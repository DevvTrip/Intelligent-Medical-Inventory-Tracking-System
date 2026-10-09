package com.mits.service;

import com.mits.model.Medicine;
import com.mits.util.Repository;
import com.mits.util.Searchable;

import java.util.List;

/**
 * Concrete generic repository for Medicine entities.
 * Demonstrates:
 * - Unit 5: Generic class inheritance (extends Repository<Medicine, String>)
 * - Unit 5: Generic interface implementation (implements Searchable<Medicine>)
 */
public class MedicineRepository extends Repository<Medicine, String> implements Searchable<Medicine> {
    private static final long serialVersionUID = 1L;

    public MedicineRepository() {
        super(Medicine::getCode);
    }

    @Override
    public List<Medicine> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String q = query.trim().toLowerCase();
        return findBy(m -> m.getCode().toLowerCase().contains(q)
                || m.getName().toLowerCase().contains(q)
                || m.getCategory().toLowerCase().contains(q));
    }
}
