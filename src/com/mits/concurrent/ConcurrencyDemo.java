package com.mits.concurrent;

import com.mits.exception.ExpiredMedicineException;
import com.mits.exception.InsufficientStockException;
import com.mits.model.Batch;
import com.mits.model.Tablet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates thread synchronization, thread priorities, and join() under high concurrency.
 * Fulfills Unit 4 requirement:
 * - 5+ threads issue stock simultaneously
 * - Method synchronization prevents race conditions
 * - Final stock count is mathematically exact
 * - Demonstrates setPriority() and join()
 */
public class ConcurrencyDemo {

    public static boolean runDemo() {
        System.out.println("=================================================");
        System.out.println("     MITS UNIT 4: MULTITHREADING CONCURRENCY DEMO ");
        System.out.println("=================================================");

        // Create a dedicated medicine for concurrent test
        Tablet testMedicine = new Tablet("TEST-01", "MED-RACE", "ConcurrenCure 500mg", 50.0, 20, "SUP-001", "Test", 10, false);
        int initialStock = 1000;
        testMedicine.addStock(initialStock, new Batch("B-CONC-01", initialStock, LocalDate.now(), LocalDate.now().plusYears(2)));

        int threadCount = 6;              // 6 concurrent threads (exceeds requirement of 5+)
        int issuesPerThread = 15;         // Each thread issues 15 times
        int unitsPerIssue = 10;           // 10 units per issue
        // Expected total deducted: 6 * 15 * 10 = 900 units
        int expectedFinalStock = initialStock - (threadCount * issuesPerThread * unitsPerIssue); // 100 units

        System.out.println("Initial Stock: " + testMedicine.getTotalStock() + " units");
        System.out.println("Launching " + threadCount + " concurrent threads...");
        System.out.println("Each thread issues " + issuesPerThread + " batches of " + unitsPerIssue + " units (" + (issuesPerThread * unitsPerIssue) + " units/thread)");
        System.out.println("Expected total issued: " + (threadCount * issuesPerThread * unitsPerIssue) + " units");
        System.out.println("Expected final stock: " + expectedFinalStock + " units");

        List<Thread> workers = new ArrayList<>();
        AtomicInteger successfulIssues = new AtomicInteger(0);
        AtomicInteger failedIssues = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            final int threadId = i;
            Thread worker = new Thread(() -> {
                for (int op = 1; op <= issuesPerThread; op++) {
                    try {
                        // Calling synchronized issueStock method on Medicine
                        testMedicine.issueStock(unitsPerIssue);
                        successfulIssues.incrementAndGet();
                    } catch (InsufficientStockException | ExpiredMedicineException e) {
                        failedIssues.incrementAndGet();
                    } catch (Exception e) {
                        System.err.println("Unexpected error: " + e.getMessage());
                    }
                }
            }, "Dispenser-Thread-" + threadId);

            // Demonstrate Thread Priorities (Unit 4 syllabus)
            int priority = Thread.MIN_PRIORITY + (i % 5);
            worker.setPriority(priority);
            workers.add(worker);
        }

        // Start all threads simultaneously
        long startTime = System.currentTimeMillis();
        for (Thread t : workers) {
            System.out.println("Starting " + t.getName() + " with Priority: " + t.getPriority());
            t.start();
        }

        // Demonstrate Thread join() - main thread waits for all workers to finish
        System.out.println("\nMain thread invoking join() on all workers...");
        for (Thread t : workers) {
            try {
                t.join(); // Blocks until thread t terminates
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted during join: " + e.getMessage());
            }
        }
        long duration = System.currentTimeMillis() - startTime;

        int finalStock = testMedicine.getTotalStock();
        System.out.println("\nAll worker threads terminated. Duration: " + duration + " ms");
        System.out.println("Total successful issue operations: " + successfulIssues.get());
        System.out.println("Total failed issue operations: " + failedIssues.get());
        System.out.println("Final Stock in Medicine: " + finalStock + " units");

        boolean passed = (finalStock == expectedFinalStock);
        if (passed) {
            System.out.println("\n>>> [PASSED] Concurrency test succeeded without race conditions!");
            System.out.println(">>> Synchronized method guaranteed atomicity across all threads.");
        } else {
            System.err.println("\n>>> [FAILED] Race condition detected! Expected " + expectedFinalStock + " but got " + finalStock);
        }
        System.out.println("=================================================\n");
        return passed;
    }

    public static void main(String[] args) {
        runDemo();
    }
}
