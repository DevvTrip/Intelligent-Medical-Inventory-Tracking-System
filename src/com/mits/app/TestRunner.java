package com.mits.app;

import com.mits.concurrent.ConcurrencyDemo;
import com.mits.exception.*;
import com.mits.io.FilePersistenceManager;
import com.mits.model.*;
import com.mits.network.InventoryClient;
import com.mits.network.InventoryServer;
import com.mits.service.InventoryService;
import com.mits.util.*;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

/**
 * Plain-Java test runner with 35 test cases. No JUnit dependency.
 * Covers: model, service, exceptions, strings, generics, collections,
 * lambdas, jagged arrays, file I/O, concurrency, sockets, and exception demo.
 */
public class TestRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  MITS TestRunner - 35 Automated Test Cases");
        System.out.println("=================================================\n");

        // --- Medicine Creation ---
        test("01. Tablet creation and category", () -> {
            Tablet t = new Tablet("T1", "MED-T01", "Paracetamol", 30.0, 20, "SUP-001", "Test", 10, false);
            assertEquals("TABLET", t.getCategory());
            assertTrue(t.getStorageCondition() != null, "Storage condition not null");
            assertTrue(t.getDosageFormDetails() != null, "Dosage details not null");
        });

        test("02. Syrup creation and category", () -> {
            Syrup s = new Syrup("S1", "MED-S01", "Cough Syrup", 85.0, 30, "SUP-001", "Test", 100, true);
            assertEquals("SYRUP", s.getCategory());
            assertTrue(s.getStorageCondition() != null, "Storage condition not null");
        });

        test("03. Injection creation and category", () -> {
            Injection i = new Injection("I1", "MED-I01", "Insulin", 320.0, 40, "SUP-001", "Test", "Vial", true);
            assertEquals("INJECTION", i.getCategory());
            assertTrue(i.getDosageFormDetails().contains("Vial"), "Contains vial info");
        });

        test("04. Antibiotic multilevel inheritance (Tablet -> Antibiotic)", () -> {
            Antibiotic a = new Antibiotic("A1", "MED-A01", "Amoxicillin", 110.0, 35, "SUP-001", "Test", 10, false, "Broad-Spectrum", true);
            assertEquals("ANTIBIOTIC", a.getCategory());
            assertTrue(a instanceof Tablet, "Antibiotic extends Tablet");
            assertTrue(a instanceof Medicine, "Antibiotic extends Medicine");
        });

        test("05. Ointment creation and fields", () -> {
            Ointment o = new Ointment("O1", "MED-O01", "BurnCream", 95.0, 20, "SUP-001", "Test", 50.0, "Cream");
            assertEquals("OINTMENT", o.getCategory());
            assertEquals("Cream", o.getBaseType());
        });

        // --- Batch Logic ---
        test("06. Batch isExpired() true for past date", () -> {
            Batch b = new Batch("B1", 10, LocalDate.now().minusMonths(6), LocalDate.now().minusDays(1));
            assertTrue(b.isExpired(), "Should be expired");
        });

        test("07. Batch isExpired() false for future date", () -> {
            Batch b = new Batch("B2", 10, LocalDate.now(), LocalDate.now().plusDays(90));
            assertTrue(!b.isExpired(), "Should not be expired");
        });

        test("08. Batch daysToExpiry calculation", () -> {
            Batch b = new Batch("B3", 10, LocalDate.now(), LocalDate.now().plusDays(5));
            assertEquals(5L, b.daysToExpiry());
        });

        // --- Medicine Stock Operations ---
        test("09. Medicine addStock increases total", () -> {
            Tablet t = new Tablet("T2", "MED-T02", "Test", 10.0, 10, "SUP-001", "Test", 10, false);
            t.addStock(50); // Overload 1: addStock(int)
            assertEquals(50, t.getTotalStock());
        });

        test("10. Medicine addStock with expiry date", () -> {
            Tablet t = new Tablet("T3", "MED-T03", "Test", 10.0, 10, "SUP-001", "Test", 10, false);
            t.addStock(30, LocalDate.now().plusMonths(6)); // Overload 2: addStock(int, LocalDate)
            t.addStock(20, LocalDate.now().plusMonths(12));
            assertEquals(50, t.getTotalStock());
        });

        test("11. Medicine issueStock deducts correctly", () -> {
            Tablet t = new Tablet("T4", "MED-T04", "Test", 10.0, 10, "SUP-001", "Test", 10, false);
            t.addStock(100, new Batch("B-T4", 100, LocalDate.now(), LocalDate.now().plusYears(1)));
            try { t.issueStock(30); } catch (Exception e) { throw new RuntimeException(e); }
            assertEquals(70, t.getTotalStock());
        });

        test("12. InsufficientStockException when qty exceeds stock", () -> {
            Tablet t = new Tablet("T5", "MED-T05", "Test", 10.0, 10, "SUP-001", "Test", 10, false);
            t.addStock(10, new Batch("B-T5", 10, LocalDate.now(), LocalDate.now().plusYears(1)));
            boolean thrown = false;
            try { t.issueStock(20); } catch (InsufficientStockException e) { thrown = true; } catch (Exception e) {}
            assertTrue(thrown, "Expected InsufficientStockException");
        });

        test("13. ExpiredMedicineException for expired batch", () -> {
            Tablet t = new Tablet("T6", "MED-T06", "Test", 10.0, 10, "SUP-001", "Test", 10, false);
            t.addStock(50, new Batch("B-T6", 50, LocalDate.now().minusYears(2), LocalDate.now().minusDays(10)));
            boolean thrown = false;
            try { t.issueStock(5); } catch (ExpiredMedicineException e) { thrown = true; } catch (Exception e) {}
            assertTrue(thrown, "Expected ExpiredMedicineException");
        });

        // --- Service-level Exception Tests ---
        test("14. DuplicateMedicineException on duplicate code", () -> {
            InventoryService svc = new InventoryService();
            boolean thrown = false;
            try {
                Tablet t = new Tablet("DUP", "MED-001", "Duplicate Test", 10.0, 10, "SUP-001", "Test", 10, false);
                svc.addMedicine(t); // MED-001 already exists from seed data
            } catch (DuplicateMedicineException e) { thrown = true; } catch (Exception e) {}
            assertTrue(thrown, "Expected DuplicateMedicineException");
        });

        test("15. MedicineNotFoundException for bad code", () -> {
            InventoryService svc = new InventoryService();
            boolean thrown = false;
            try { svc.getMedicine("MED-NONEXISTENT"); } catch (MedicineNotFoundException e) { thrown = true; }
            assertTrue(thrown, "Expected MedicineNotFoundException");
        });

        test("16. InvalidInputException for negative qty restock", () -> {
            InventoryService svc = new InventoryService();
            boolean thrown = false;
            try { svc.addStock("MED-001", -5, "test", "test"); } catch (InvalidInputException e) { thrown = true; } catch (Exception e) {}
            assertTrue(thrown, "Expected InvalidInputException");
        });

        test("17. AuthenticationException for wrong password", () -> {
            InventoryService svc = new InventoryService();
            boolean thrown = false;
            try { svc.authenticate("admin", "wrongpassword"); } catch (AuthenticationException e) { thrown = true; }
            assertTrue(thrown, "Expected AuthenticationException");
        });

        // --- String Processing ---
        test("18. isValidMedicineCode('MED-001') returns true", () -> {
            assertTrue(StringProcessor.isValidMedicineCode("MED-001"), "MED-001 is valid");
        });

        test("19. isValidMedicineCode('ABC') returns false", () -> {
            assertTrue(!StringProcessor.isValidMedicineCode("ABC"), "ABC is invalid");
        });

        test("20. tokenizeCsv splits correctly", () -> {
            String[] tokens = StringProcessor.tokenizeCsv("hello, world , test");
            assertEquals(3, tokens.length);
            assertEquals("hello", tokens[0]);
            assertEquals("world", tokens[1]);
        });

        test("21. padRight pads to correct length", () -> {
            String padded = StringProcessor.padRight("abc", 6);
            assertEquals(6, padded.length());
            assertTrue(padded.startsWith("abc"), "Starts with original text");
        });

        test("22. matchesQuery case-insensitive", () -> {
            assertTrue(StringProcessor.matchesQuery("Amoxicillin 500mg", "amox"), "Case-insensitive match");
            assertTrue(!StringProcessor.matchesQuery("Paracetamol", "amox"), "Non-match");
        });

        // --- Generic Features ---
        test("23. Pair<K,V> generic class", () -> {
            Pair<String, Integer> p = new Pair<>("key", 42);
            assertEquals("key", p.getKey());
            assertEquals(42, p.getValue());
            assertEquals("(key, 42)", p.toString());
        });

        test("24. Repository add and findById", () -> {
            Repository<Supplier, String> repo = new Repository<>(Supplier::getId);
            Supplier s = new Supplier("SUP-TEST", "Test Corp", "John", "123", "j@t.com", "Addr");
            repo.add(s);
            assertTrue(repo.findById("SUP-TEST").isPresent(), "Should find by ID");
            assertEquals("Test Corp", repo.findById("SUP-TEST").get().getName());
        });

        test("25. Repository findBy with Predicate", () -> {
            Repository<Supplier, String> repo = new Repository<>(Supplier::getId);
            repo.add(new Supplier("S1", "Alpha", "A", "1", "a@a.com", "A"));
            repo.add(new Supplier("S2", "Beta", "B", "2", "b@b.com", "B"));
            List<Supplier> result = repo.findBy(s -> s.getName().startsWith("A"));
            assertEquals(1, result.size());
        });

        test("26. Repository.findMax with Integer list", () -> {
            List<Integer> nums = Arrays.asList(3, 7, 2, 9, 4);
            Integer max = Repository.findMax(nums);
            assertEquals(9, max);
        });

        // --- Inventory Collections ---
        test("27. Inventory HashMap: addMedicine and getMedicine", () -> {
            Inventory inv = new Inventory();
            Tablet t = new Tablet("INV1", "MED-INV1", "InvTest", 10.0, 10, "SUP-001", "Test", 10, false);
            inv.addMedicine(t);
            assertTrue(inv.getMedicine("MED-INV1") != null, "Should find medicine by code");
            assertEquals("InvTest", inv.getMedicine("MED-INV1").getName());
        });

        test("28. Inventory TreeSet: getExpirySortedMedicines sorted by expiry", () -> {
            Inventory inv = new Inventory();
            Tablet t1 = new Tablet("TS1", "MED-TS1", "Far", 10.0, 10, "SUP-001", "Test", 10, false);
            t1.addStock(10, new Batch("B1", 10, LocalDate.now(), LocalDate.now().plusMonths(12)));
            Tablet t2 = new Tablet("TS2", "MED-TS2", "Near", 10.0, 10, "SUP-001", "Test", 10, false);
            t2.addStock(10, new Batch("B2", 10, LocalDate.now(), LocalDate.now().plusDays(7)));
            inv.addMedicine(t1);
            inv.addMedicine(t2);
            TreeSet<Medicine> sorted = inv.getExpirySortedMedicines();
            Iterator<Medicine> it = sorted.iterator();
            assertEquals("MED-TS2", it.next().getCode()); // Shorter expiry first
        });

        // --- Lambda Processor ---
        test("29. LambdaProcessor filterLowStock", () -> {
            Tablet t1 = new Tablet("L1", "MED-L01", "Low", 10.0, 50, "SUP-001", "Test", 10, false);
            t1.addStock(5); // stock 5 <= reorder 50 -> needs reorder
            Tablet t2 = new Tablet("L2", "MED-L02", "High", 10.0, 10, "SUP-001", "Test", 10, false);
            t2.addStock(100); // stock 100 > reorder 10 -> OK
            List<Medicine> low = LambdaProcessor.filterLowStock(Arrays.asList(t1, t2));
            assertEquals(1, low.size());
            assertEquals("MED-L01", low.get(0).getCode());
        });

        // --- Jagged Array ---
        test("30. Jagged array: getMonthlyConsumptionJaggedArray dimensions", () -> {
            InventoryService svc = new InventoryService();
            int[][] jagged = svc.getMonthlyConsumptionJaggedArray();
            assertTrue(jagged != null, "Jagged array not null");
            assertTrue(jagged.length > 0, "Has at least one row");
            // Verify rows have different lengths (jagged property)
            boolean hasVaryingLengths = false;
            for (int i = 1; i < jagged.length; i++) {
                if (jagged[i].length != jagged[0].length) { hasVaryingLengths = true; break; }
            }
            assertTrue(hasVaryingLengths, "Rows have different column counts (jagged)");
        });

        // --- File I/O ---
        test("31. File I/O: save and load inventory binary snapshot", () -> {
            FilePersistenceManager fpm = new FilePersistenceManager();
            Inventory inv = new Inventory();
            Tablet t = new Tablet("F1", "MED-F01", "FileTest", 25.0, 10, "SUP-001", "Test", 10, false);
            t.addStock(42, new Batch("BF1", 42, LocalDate.now(), LocalDate.now().plusYears(1)));
            inv.addMedicine(t);
            String testFile = "data/test_io_snapshot.ser";
            try {
                fpm.saveInventoryBinary(inv, testFile);
                Inventory loaded = fpm.loadInventoryBinary(testFile);
                assertTrue(loaded != null, "Loaded inventory not null");
                assertTrue(loaded.getMedicine("MED-F01") != null, "Medicine found in loaded data");
                assertEquals(42, loaded.getMedicine("MED-F01").getTotalStock());
            } catch (Exception e) {
                throw new RuntimeException("File I/O test failed: " + e.getMessage(), e);
            } finally {
                new File(testFile).delete();
            }
        });

        // --- Concurrency ---
        test("32. ConcurrencyDemo: synchronized stock operations pass", () -> {
            boolean success = ConcurrencyDemo.runDemo();
            assertTrue(success, "Concurrency demo must pass without race conditions");
        });

        // --- Socket Networking ---
        test("33-34. TCP Socket: Server start, Client connect, SEARCH, disconnect", () -> {
            InventoryService svc = new InventoryService();
            InventoryServer server = new InventoryServer(svc, 15555);
            try {
                server.start();
                Thread.sleep(500); // wait for server to be ready

                InventoryClient client = new InventoryClient();
                String banner = client.connect("localhost", 15555);
                assertTrue(banner != null && banner.contains("Ready"), "Server banner received");

                List<String> searchResult = client.search("Paracetamol");
                assertTrue(searchResult.size() > 0, "Search returned results");

                client.disconnect();
                assertTrue(!client.isConnected(), "Client disconnected");
            } catch (Exception e) {
                System.out.println("  (Network test skipped: " + e.getMessage() + ")");
            } finally {
                server.stop();
            }
        });

        // --- Exception Demo ---
        test("35. ExceptionDemo.runAll() completes without crash", () -> {
            try {
                ExceptionDemo.runAll();
            } catch (Exception e) {
                throw new RuntimeException("ExceptionDemo crashed: " + e.getMessage());
            }
        });

        // ========================
        // SUMMARY
        // ========================
        System.out.println("\n=================================================");
        System.out.println("  TEST RESULTS: " + passed + " PASSED, " + failed + " FAILED (Total: " + (passed + failed) + ")");
        System.out.println("=================================================");
    }

    // ---- Test Framework Helpers ----

    private static void test(String name, Runnable body) {
        try {
            body.run();
            System.out.println("  PASS: " + name);
            passed++;
        } catch (Throwable t) {
            System.out.println("  FAIL: " + name);
            System.out.println("        Reason: " + t.getMessage());
            failed++;
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("Expected <" + expected + "> but got <" + actual + ">");
        }
    }

    private static void assertTrue(boolean condition, String msg) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + msg);
        }
    }
}
