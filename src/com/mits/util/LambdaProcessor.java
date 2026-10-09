package com.mits.util;

import com.mits.model.Medicine;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Demonstrates functional programming and lambda expressions in Java.
 * Part of Unit 2: Lambdas and Functional Interfaces.
 *
 * Demonstrates:
 * - Predicate<T>: filtering medicines (low stock, expiring soon)
 * - Comparator<T>: multi-criteria sorting (expiry, name, stock quantity)
 * - Function<T, R>: mapping medicines to valuations, names, summaries
 * - Consumer<T>: forEach processing and printing
 * - Custom @FunctionalInterface: StockEvaluator for custom business logic
 */
public class LambdaProcessor {

    /**
     * Custom functional interface demonstrating @FunctionalInterface annotation.
     */
    @FunctionalInterface
    public interface StockEvaluator {
        /**
         * Evaluates a medicine and returns a score or status flag.
         * @param medicine medicine to evaluate
         * @return evaluated numeric score
         */
        double evaluate(Medicine medicine);
    }

    /**
     * Filters medicines matching a given Predicate.
     * Demonstrates: Predicate<Medicine> lambda
     */
    public static List<Medicine> filter(List<Medicine> medicines, Predicate<Medicine> predicate) {
        List<Medicine> result = new ArrayList<>();
        for (Medicine m : medicines) {
            if (predicate.test(m)) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Predicate lambda: filters medicines that need reordering.
     */
    public static List<Medicine> filterLowStock(List<Medicine> medicines) {
        Predicate<Medicine> lowStockPredicate = Medicine::needsReorder;
        return filter(medicines, lowStockPredicate);
    }

    /**
     * Predicate lambda: filters medicines expiring within the specified days threshold.
     */
    public static List<Medicine> filterExpiringWithinDays(List<Medicine> medicines, int days) {
        Predicate<Medicine> expiringPredicate = m -> m.daysToExpiry() >= 0 && m.daysToExpiry() <= days;
        return filter(medicines, expiringPredicate);
    }

    /**
     * Comparator lambda: sorts medicines by expiry date ascending.
     */
    public static void sortByExpiryDate(List<Medicine> medicines) {
        Comparator<Medicine> expiryComparator = (m1, m2) -> m1.getExpiryDate().compareTo(m2.getExpiryDate());
        medicines.sort(expiryComparator);
    }

    /**
     * Comparator lambda: sorts medicines by name alphabetically.
     */
    public static void sortByName(List<Medicine> medicines) {
        Comparator<Medicine> nameComparator = (m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName());
        medicines.sort(nameComparator);
    }

    /**
     * Comparator lambda: sorts medicines by total stock quantity descending.
     */
    public static void sortByStockQuantityDesc(List<Medicine> medicines) {
        Comparator<Medicine> stockComparator = (m1, m2) -> Integer.compare(m2.getTotalStock(), m1.getTotalStock());
        medicines.sort(stockComparator);
    }

    /**
     * Function lambda: maps medicines to a transformed list of results.
     */
    public static <R> List<R> map(List<Medicine> medicines, Function<Medicine, R> mapper) {
        List<R> list = new ArrayList<>();
        for (Medicine m : medicines) {
            list.add(mapper.apply(m));
        }
        return list;
    }

    /**
     * Computes the total inventory monetary valuation using Function lambda.
     */
    public static double computeTotalValuation(List<Medicine> medicines) {
        Function<Medicine, Double> valueMapper = m -> m.getTotalStock() * m.getUnitPrice();
        double sum = 0.0;
        for (Medicine m : medicines) {
            sum += valueMapper.apply(m);
        }
        return sum;
    }

    /**
     * Demonstrates Consumer<T> lambda with forEach iteration.
     */
    public static void processEach(List<Medicine> medicines, Consumer<Medicine> consumer) {
        medicines.forEach(consumer);
    }

    /**
     * Demonstrates custom @FunctionalInterface StockEvaluator.
     */
    public static double evaluateInventoryRisk(List<Medicine> medicines, StockEvaluator evaluator) {
        double totalScore = 0.0;
        for (Medicine m : medicines) {
            totalScore += evaluator.evaluate(m);
        }
        return totalScore;
    }
}
