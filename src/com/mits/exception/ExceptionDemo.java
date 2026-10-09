package com.mits.exception;

import java.time.LocalDate;

/**
 * Demonstrates all exception handling concepts required by the Unit 3 syllabus:
 * - try-catch block
 * - multiple catch blocks
 * - nested try blocks
 * - finally block
 * - throw keyword
 * - throws keyword
 * - user-defined checked exceptions:
 *     * InsufficientStockException
 *     * ExpiredMedicineException
 *     * MedicineNotFoundException
 *     * DuplicateMedicineException
 *     * InvalidInputException
 *     * AuthenticationException
 * - user-defined unchecked exception:
 *     * InventoryOperationException
 */
public class ExceptionDemo {

    /**
     * Demonstrates: throw, throws, try-catch
     */
    public static void demonstrateBasicTryCatch() {
        System.out.println("\n--- [1] Demonstrating basic try-catch with throw & throws ---");
        try {
            validateQuantity(-5);
        } catch (InvalidInputException ex) {
            System.out.println("CAUGHT InvalidInputException successfully!");
            System.out.println("Field: " + ex.getFieldName() + " | Reason: " + ex.getReason());
        }
    }

    private static void validateQuantity(int qty) throws InvalidInputException {
        if (qty <= 0) {
            throw new InvalidInputException("quantity", "Must be strictly positive, received: " + qty);
        }
    }

    /**
     * Demonstrates: Multiple catch blocks catching distinct custom exceptions.
     */
    public static void demonstrateMultipleCatch(int mode) {
        System.out.println("\n--- [2] Demonstrating Multiple Catch Blocks (mode=" + mode + ") ---");
        try {
            if (mode == 1) {
                throw new InsufficientStockException("MED-001", "Paracetamol", 500, 40);
            } else if (mode == 2) {
                throw new ExpiredMedicineException("MED-002", "Amoxicillin", "B-2022-01", LocalDate.now().minusDays(15));
            } else if (mode == 3) {
                throw new MedicineNotFoundException("MED-999");
            } else {
                // Triggers built-in exception
                int div = 10 / 0;
            }
        } catch (InsufficientStockException e) {
            System.out.println("Caught InsufficientStockException: " + e.getMessage());
        } catch (ExpiredMedicineException e) {
            System.out.println("Caught ExpiredMedicineException: " + e.getMessage());
        } catch (MedicineNotFoundException e) {
            System.out.println("Caught MedicineNotFoundException: " + e.getMessage());
        } catch (ArithmeticException e) {
            System.out.println("Caught standard ArithmeticException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Caught general Exception fallback: " + e.getMessage());
        }
    }

    /**
     * Demonstrates: Nested try blocks.
     */
    public static void demonstrateNestedTry() {
        System.out.println("\n--- [3] Demonstrating Nested try-catch Blocks ---");
        try {
            System.out.println("Outer try: Checking user authentication...");
            authenticateUser("admin", "admin123");

            try {
                System.out.println("Inner try: Checking duplicate medicine code registration...");
                checkDuplicateCode("MED-001", "MED-001");
            } catch (DuplicateMedicineException innerEx) {
                System.out.println("INNER CATCH: Caught DuplicateMedicineException: " + innerEx.getMessage());
            }

            System.out.println("Outer try resumed safely after handling inner exception.");
        } catch (AuthenticationException outerEx) {
            System.out.println("OUTER CATCH: Authentication failed: " + outerEx.getMessage());
        }
    }

    private static void authenticateUser(String user, String pass) throws AuthenticationException {
        if (!"admin".equals(user) || !"admin123".equals(pass)) {
            throw new AuthenticationException(user);
        }
    }

    private static void checkDuplicateCode(String existing, String proposed) throws DuplicateMedicineException {
        if (existing.equalsIgnoreCase(proposed)) {
            throw new DuplicateMedicineException(proposed);
        }
    }

    /**
     * Demonstrates: finally block execution guarantee.
     */
    public static void demonstrateFinallyBlock() {
        System.out.println("\n--- [4] Demonstrating finally block execution ---");
        boolean connectionOpen = false;
        try {
            connectionOpen = true;
            System.out.println("Connection opened: " + connectionOpen);
            System.out.println("Simulating error during file/socket I/O...");
            throw new InventoryOperationException("Simulated I/O pipeline failure");
        } catch (InventoryOperationException ex) {
            System.out.println("Caught InventoryOperationException (unchecked): " + ex.getMessage());
        } finally {
            connectionOpen = false;
            System.out.println("FINALLY BLOCK: Cleanup completed. Connection closed: " + (!connectionOpen));
        }
    }

    /**
     * Demonstrates all cases sequentially.
     */
    public static void runAll() {
        System.out.println("=================================================");
        System.out.println("       MITS UNIT 3: EXCEPTION HANDLING DEMO      ");
        System.out.println("=================================================");
        demonstrateBasicTryCatch();
        demonstrateMultipleCatch(1);
        demonstrateMultipleCatch(2);
        demonstrateMultipleCatch(3);
        demonstrateMultipleCatch(4);
        demonstrateNestedTry();
        demonstrateFinallyBlock();
        System.out.println("\n[SUCCESS] All Unit 3 exception scenarios demonstrated successfully!\n");
    }

    public static void main(String[] args) {
        runAll();
    }
}
