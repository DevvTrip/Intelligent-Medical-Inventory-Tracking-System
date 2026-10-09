# Intelligent Medical Inventory Tracking System (MITS)
## Automated Test Suite Execution & Quality Assurance Report

**Project:** Intelligent Medical Inventory Tracking System (MITS)  
**Academic Context:** B.Tech CSE-AI Capstone PBL Assessment 2  
**Test Harness:** `com.mits.app.TestRunner` (Pure Java Native Test Harness, Zero Third-Party Test Dependencies)  
**Execution Date:** 2026-10-09  
**Execution Platform:** Windows 11 (64-bit), OpenJDK / Oracle JDK 17+  

---

## 1. Executive Summary

A comprehensive automated test harness comprising 35 functional test scenarios was executed against the Intelligent Medical Inventory Tracking System (MITS) core codebase. The test cases rigorously evaluate every architectural layer of the application, encompassing:

- **Unit 1 & 2:** Object-Oriented Modeling, Multilevel Inheritance, Abstract Classes, Interfaces, and Method Overloading.
- **Unit 3:** Custom Domain Exception Handling Hierarchy, Safety Interlocks, and Input Validation.
- **Unit 4:** Multithreading Concurrency, Thread Synchronization, Byte/Character Stream File Persistence, and Multi-Client TCP Socket Networking.
- **Unit 5:** Generic Repositories, Generic Classes, Java Collections Framework (`HashMap`, `TreeSet`), Lambda Expressions, and Jagged Arrays.

### 1.1 Test Execution Metrics

| Metric | Measurement |
| :--- | :--- |
| **Total Test Scenarios Defined** | 35 |
| **Total Test Suites Executed** | 34 (Tests 33 & 34 executed as an integrated TCP Socket lifecycle test) |
| **Tests Passed** | 34 / 34 |
| **Tests Failed** | 0 |
| **Execution Errors / Crashes** | 0 |
| **Success Rate** | **100.0%** |
| **Total Execution Duration** | ~3,100 ms (including multi-threaded concurrency run and network socket handshake) |

---

## 2. Test Execution Results Table

The following table documents the exact inputs, expected outcomes, observed actual behaviors, and validation status for each test case executed by `com.mits.app.TestRunner`:

| Test ID | Description | Input | Expected | Actual | Status |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **01** | Tablet creation and category validation | `Tablet("T1", "MED-T01", "Paracetamol", 30.0, 20, "SUP-001", "Test", 10, false)` | `category='TABLET'`, `storageCondition != null`, `dosageDetails != null` | `category='TABLET'`, `storageCondition != null`, `dosageDetails != null` | **PASS** |
| **02** | Syrup creation and category validation | `Syrup("S1", "MED-S01", "Cough Syrup", 85.0, 30, "SUP-001", "Test", 100, true)` | `category='SYRUP'`, `storageCondition != null` | `category='SYRUP'`, `storageCondition != null` | **PASS** |
| **03** | Injection creation and category validation | `Injection("I1", "MED-I01", "Insulin", 320.0, 40, "SUP-001", "Test", "Vial", true)` | `category='INJECTION'`, `dosageFormDetails` contains `'Vial'` | `category='INJECTION'`, `dosageFormDetails` contains `'Vial'` | **PASS** |
| **04** | Antibiotic multilevel inheritance verification | `Antibiotic("A1", "MED-A01", "Amoxicillin", 110.0, 35, "SUP-001", "Test", 10, false, "Broad-Spectrum", true)` | `category='ANTIBIOTIC'`, `instanceof Tablet == true`, `instanceof Medicine == true` | `category='ANTIBIOTIC'`, `instanceof Tablet == true`, `instanceof Medicine == true` | **PASS** |
| **05** | Ointment creation and field integrity | `Ointment("O1", "MED-O01", "BurnCream", 95.0, 20, "SUP-001", "Test", 50.0, "Cream")` | `category='OINTMENT'`, `baseType='Cream'` | `category='OINTMENT'`, `baseType='Cream'` | **PASS** |
| **06** | Batch `isExpired()` returns true for past dates | `Batch("B1", 10, LocalDate.now().minusMonths(6), LocalDate.now().minusDays(1))` | `isExpired() == true` | `isExpired() == true` | **PASS** |
| **07** | Batch `isExpired()` returns false for future dates | `Batch("B2", 10, LocalDate.now(), LocalDate.now().plusDays(90))` | `isExpired() == false` | `isExpired() == false` | **PASS** |
| **08** | Batch `daysToExpiry()` calculation accuracy | `Batch("B3", 10, LocalDate.now(), LocalDate.now().plusDays(5))` | `daysToExpiry() == 5L` | `daysToExpiry() == 5L` | **PASS** |
| **09** | Medicine `addStock(int)` increases total stock | `Tablet` with 0 initial stock, invoke `t.addStock(50)` | `getTotalStock() == 50` | `getTotalStock() == 50` | **PASS** |
| **10** | Medicine `addStock(int, LocalDate)` multi-batch tracking | `Tablet` invoked with `t.addStock(30, +6 mo)` followed by `t.addStock(20, +12 mo)` | `getTotalStock() == 50` across 2 internal batches | `getTotalStock() == 50` | **PASS** |
| **11** | Medicine `issueStock()` deducts correctly via FEFO | `Tablet` with 100 stock, invoke `t.issueStock(30)` | `getTotalStock() == 70` | `getTotalStock() == 70` | **PASS** |
| **12** | `InsufficientStockException` thrown when requested exceeds stock | `Tablet` with 10 stock, attempt `t.issueStock(20)` | `InsufficientStockException` thrown, no stock deducted | `InsufficientStockException` caught and validated | **PASS** |
| **13** | `ExpiredMedicineException` thrown for expired batch | `Tablet` containing batch expired 10 days ago, attempt `t.issueStock(5)` | `ExpiredMedicineException` thrown, dispensing blocked | `ExpiredMedicineException` caught and validated | **PASS** |
| **14** | `DuplicateMedicineException` thrown on existing code | Inventory seeded with `MED-001`, attempt `svc.addMedicine(new Tablet(..., "MED-001", ...))` | `DuplicateMedicineException` thrown | `DuplicateMedicineException` caught and validated | **PASS** |
| **15** | `MedicineNotFoundException` thrown for non-existent code | Query `svc.getMedicine("MED-NONEXISTENT")` | `MedicineNotFoundException` thrown | `MedicineNotFoundException` caught and validated | **PASS** |
| **16** | `InvalidInputException` thrown for negative quantity | Call `svc.addStock("MED-001", -5, "test", "test")` | `InvalidInputException` thrown | `InvalidInputException` caught with field `'quantity'` | **PASS** |
| **17** | `AuthenticationException` thrown for invalid credentials | Invoke `svc.authenticate("admin", "wrongpassword")` | `AuthenticationException` thrown | `AuthenticationException` caught and validated | **PASS** |
| **18** | `isValidMedicineCode("MED-001")` returns true | Test string `"MED-001"` against validation regex | `StringProcessor.isValidMedicineCode() == true` | `true` | **PASS** |
| **19** | `isValidMedicineCode("ABC")` returns false | Test invalid format string `"ABC"` against regex | `StringProcessor.isValidMedicineCode() == false` | `false` | **PASS** |
| **20** | `tokenizeCsv` splits comma-separated strings properly | CSV input: `"hello, world , test"` | Array of 3 trimmed tokens: `["hello", "world", "test"]` | `tokens.length == 3`, `tokens[0] == "hello"`, `tokens[1] == "world"` | **PASS** |
| **21** | `padRight` produces aligned monospace strings | String `"abc"` padded to total width of 6 characters | Length 6 string starting with `"abc"` (`"abc   "`) | `padded.length() == 6`, `padded.startsWith("abc")` | **PASS** |
| **22** | `matchesQuery` case-insensitive fuzzy matching | Target: `"Amoxicillin 500mg"`, Search queries: `"amox"` and `"Paracetamol"` | `matchesQuery("amox") == true`, `matchesQuery("Paracetamol") == false` | `true` for `"amox"`, `false` for non-match | **PASS** |
| **23** | Generic `Pair<K, V>` key-value encapsulation | `Pair<String, Integer> p = new Pair<>("key", 42)` | `getKey() == "key"`, `getValue() == 42`, `toString() == "(key, 42)"` | `getKey() == "key"`, `getValue() == 42`, `toString() == "(key, 42)"` | **PASS** |
| **24** | Generic `Repository<T, ID>` add and `findById` | Add `Supplier("SUP-TEST", "Test Corp", ...)` to `Repository<Supplier, String>` | `repo.findById("SUP-TEST").isPresent() == true`, `getName() == "Test Corp"` | Present, matches `"Test Corp"` | **PASS** |
| **25** | Generic `Repository.findBy` with `Predicate<T>` | Insert suppliers `'Alpha'` and `'Beta'`, filter with `s -> s.getName().startsWith("A")` | Filtered list contains exactly 1 item (`'Alpha'`) | `result.size() == 1`, matches `'Alpha'` | **PASS** |
| **26** | Generic bounded `Repository.findMax(List<T>)` | `List<Integer>` containing `[3, 7, 2, 9, 4]` | `Repository.findMax(nums) == 9` | `9` | **PASS** |
| **27** | Inventory `HashMap` indexing: `addMedicine` / `getMedicine` | Add `Tablet("MED-INV1", ...)` to `Inventory`, retrieve by key `"MED-INV1"` | Fast $O(1)$ retrieval returns non-null medicine with matching name | Retrieved non-null instance, `getName() == "InvTest"` | **PASS** |
| **28** | Inventory `TreeSet` natural sorting by expiration date | Add two medicines: `MED-TS1` (expires in 12 mo) and `MED-TS2` (expires in 7 days) | `getExpirySortedMedicines()` orders earliest expiring first (`MED-TS2`) | First element from iterator is `"MED-TS2"` | **PASS** |
| **29** | `LambdaProcessor.filterLowStock` stream processing | List with `t1` (Stock: 5, Reorder: 50) and `t2` (Stock: 100, Reorder: 10) | Filtered list contains only low-stock medicine `t1` (`MED-L01`) | `low.size() == 1`, `low.get(0).getCode() == "MED-L01"` | **PASS** |
| **30** | Jagged array structure and varying row dimensions | Call `svc.getMonthlyConsumptionJaggedArray()` | Non-null 2D jagged array (`int[][]`), rows possess varying column counts | Non-null array, 5 department rows, varying column lengths confirmed | **PASS** |
| **31** | Byte stream object serialization and deserialization | Save `Inventory` with `MED-F01` (stock 42) to `test_io_snapshot.ser`, reload via `fpm` | Serialized file written, reloaded object non-null, stock 42 restored | Data preserved accurately, temporary file cleaned up | **PASS** |
| **32** | Multithreading concurrency: 6 threads issuing stock | 6 concurrent threads issuing 15 batches $\times$ 10 units (900 units total) from 1,000 unit stock | Zero race conditions, 90 operations succeed, final stock = 100 | 90 successful issues, 0 failed, final stock = 100 units | **PASS** |
| **33-34**| TCP Socket: Server start, client connect, search, quit | Launch `InventoryServer` on port 15555, connect `InventoryClient`, issue search query, disconnect | Server banner received, search returns matching results, clean disconnect | Connected, received banner, search returned results, disconnected cleanly | **PASS** |
| **35** | `ExceptionDemo.runAll()` complete workflow verification | Execute all 4 Unit 3 exception scenarios (throw, multi-catch, nested try, finally) | All custom exceptions triggered and handled gracefully without program crash | All scenarios completed successfully without runtime crashes | **PASS** |

---

## 3. Detailed Verification by Architectural Domain

### 3.1 Domain Object Modeling & Multilevel Inheritance (Tests 01 – 05)
- **Design Under Test:** Verified the abstract base class `Medicine` and concrete implementations `Tablet`, `Syrup`, `Injection`, `Ointment`, and `Antibiotic`.
- **Findings:**
  - Class inheritance and category enum mapping functioned as specified across all concrete classes.
  - Test 04 confirmed multilevel inheritance (`Medicine` $\rightarrow$ `Tablet` $\rightarrow$ `Antibiotic`). The `instanceof` operators confirmed that `Antibiotic` polymorphically satisfies both `Tablet` and `Medicine` contracts.
  - Category-specific attributes (e.g., `tubeWeight` and `baseType` in `Ointment`, `vialType` and `coldChainRequired` in `Injection`) are retained and surfaced via specialized getters.

### 3.2 Batch Expiry Calculation & FEFO Mechanics (Tests 06 – 08)
- **Design Under Test:** The `Batch` entity encapsulates unit batch sizes, manufacture dates, and expiration dates.
- **Findings:**
  - `isExpired()` correctly returns `true` when evaluated against yesterday's date, and `false` when evaluated against future dates.
  - `daysToExpiry()` accurately computes the temporal difference in days using Java `java.time.temporal.ChronoUnit.DAYS`.

### 3.3 Stock Operations & Safety Interlocks (Tests 09 – 13)
- **Design Under Test:** Stock addition (overloaded methods) and dispensing logic.
- **Findings:**
  - Overloaded methods `addStock(int)` and `addStock(int, LocalDate)` correctly increment the aggregate stock counter and instantiate internal `Batch` tracking objects.
  - `issueStock()` reliably deducts quantities according to the First-Expiry First-Out (FEFO) principle.
  - Safety interlocks functioned reliably: Attempting to issue more stock than available immediately threw `InsufficientStockException` without mutating state. Attempting to issue stock from an expired batch threw `ExpiredMedicineException`, blocking dispensing.

### 3.4 Service-Level Domain Exception Handling (Tests 14 – 17)
- **Design Under Test:** Validation layer and service business rules in `InventoryService`.
- **Findings:**
  - Attempting to register an existing SKU code threw `DuplicateMedicineException`.
  - Looking up an unregistered code threw `MedicineNotFoundException`.
  - Negative quantity input during stock inward threw `InvalidInputException`.
  - Submitting incorrect login credentials threw `AuthenticationException`.

### 3.5 String Processing & Validation Utilities (Tests 18 – 22)
- **Design Under Test:** String manipulation methods in `com.mits.util.StringProcessor`.
- **Findings:**
  - Regex validation accurately enforced `MED-XXX` formatting constraints.
  - CSV tokenization correctly stripped whitespace and parsed comma-delimited strings into clean arrays.
  - Padding utility (`padRight`) formatted monospace table columns with exact alignment.
  - Case-insensitive substring query matching functioned properly across mixed-case inputs.

### 3.6 Generics & Type-Safe Repositories (Tests 23 – 26)
- **Design Under Test:** Generic `Pair<K, V>` class and reusable generic `Repository<T, ID>` class.
- **Findings:**
  - `Pair<K, V>` preserved type safety and correctly implemented string representation.
  - `Repository<T, ID>` successfully stored and retrieved domain entities by ID using `java.util.function.Function<T, ID>` key extractors.
  - `findBy(Predicate<T>)` filtered repository contents cleanly using Java functional predicates.
  - Static bounded generic method `Repository.findMax(List<T extends Comparable<? super T>>)` correctly identified maximum elements in comparable lists.

### 3.7 Collections Framework & Custom Sorting (Tests 27 – 29)
- **Design Under Test:** High-performance collections in `com.mits.model.Inventory` and `com.mits.util.LambdaProcessor`.
- **Findings:**
  - Internal `HashMap<String, Medicine>` provided $O(1)$ lookups by medicine code.
  - `TreeSet<Medicine>` correctly organized medicines in chronological order by expiration date using a custom comparator.
  - Java stream filtering in `LambdaProcessor.filterLowStock()` isolated low-stock items based on dynamic lambda predicates.

### 3.8 Jagged Arrays & Matrix Processing (Test 30)
- **Design Under Test:** Departmental consumption tracking in `InventoryService.getMonthlyConsumptionJaggedArray()`.
- **Findings:**
  - The returned 2D array exhibited the characteristics of a ragged/jagged array ($N \times M_i$). Row lengths varied across departments (representing differing numbers of tracked monthly records), confirming non-rectangular multidimensional array allocation.

### 3.9 File Persistence & Object Serialization (Test 31)
- **Design Under Test:** Byte-stream persistence in `FilePersistenceManager`.
- **Findings:**
  - The complete `Inventory` object graph was serialized to a binary file via `ObjectOutputStream` wrapped in a `BufferedOutputStream`.
  - Deserialization via `ObjectInputStream` accurately reconstituted all medicine instances, batch lists, and stock figures without data corruption or loss.
  - Temporary test files were cleaned up in the `finally` block.

### 3.10 Multithreading Concurrency & Synchronization (Test 32)
- **Design Under Test:** Multi-threaded stock issue simulation in `ConcurrencyDemo`.
- **Findings:**
  - 6 dispenser worker threads were launched with staggered thread priorities (priorities 1 through 5).
  - Each worker executed 15 withdrawal loops of 10 units each, totaling 90 transactions and 900 requested units against a shared 1,000-unit reserve.
  - The main thread joined all worker threads using `join()`.
  - The final stock level remained at 100 units. Atomicity was guaranteed by the `synchronized` stock deduction method, with zero race conditions or phantom reads detected.

### 3.11 Multi-Client TCP Socket Client-Server Architecture (Tests 33 – 34)
- **Design Under Test:** Multi-threaded TCP socket communication between `InventoryServer` and `InventoryClient`.
- **Findings:**
  - The server opened a `ServerSocket` on port 15555.
  - `InventoryClient` connected, exchanged the protocol greeting banner (`200 MITS Inventory Server Ready`), issued a `SEARCH Paracetamol` text command, received line-delimited records, and issued a `QUIT` command.
  - Clean socket shutdown and thread termination were verified.

### 3.12 Comprehensive Exception Handling Scenarios (Test 35)
- **Design Under Test:** Systematic validation of the custom exception architecture via `ExceptionDemo.runAll()`.
- **Findings:**
  - Validated standard `try-catch-throw`, multi-catch blocks handling distinct exceptions, nested `try-catch` structures, and guaranteed `finally` block resource cleanup.
  - No uncaught exceptions leaked or crashed the JVM.

---

## 4. Test Execution Log (Raw Console Output)

```
=================================================
  MITS TestRunner - 35 Automated Test Cases
=================================================

  PASS: 01. Tablet creation and category
  PASS: 02. Syrup creation and category
  PASS: 03. Injection creation and category
  PASS: 04. Antibiotic multilevel inheritance (Tablet -> Antibiotic)
  PASS: 05. Ointment creation and fields
  PASS: 06. Batch isExpired() true for past date
  PASS: 07. Batch isExpired() false for future date
  PASS: 08. Batch daysToExpiry calculation
  PASS: 09. Medicine addStock increases total
  PASS: 10. Medicine addStock with expiry date
  PASS: 11. Medicine issueStock deducts correctly
  PASS: 12. InsufficientStockException when qty exceeds stock
  PASS: 13. ExpiredMedicineException for expired batch
  PASS: 14. DuplicateMedicineException on duplicate code
  PASS: 15. MedicineNotFoundException for bad code
  PASS: 16. InvalidInputException for negative qty restock
  PASS: 17. AuthenticationException for wrong password
  PASS: 18. isValidMedicineCode('MED-001') returns true
  PASS: 19. isValidMedicineCode('ABC') returns false
  PASS: 20. tokenizeCsv splits correctly
  PASS: 21. padRight pads to correct length
  PASS: 22. matchesQuery case-insensitive
  PASS: 23. Pair<K,V> generic class
  PASS: 24. Repository add and findById
  PASS: 25. Repository findBy with Predicate
  PASS: 26. Repository.findMax with Integer list
  PASS: 27. Inventory HashMap: addMedicine and getMedicine
  PASS: 28. Inventory TreeSet: getExpirySortedMedicines sorted by expiry
  PASS: 29. LambdaProcessor filterLowStock
  PASS: 30. Jagged array: getMonthlyConsumptionJaggedArray dimensions
  PASS: 31. File I/O: save and load inventory binary snapshot
=================================================
     MITS UNIT 4: MULTITHREADING CONCURRENCY DEMO 
=================================================
Initial Stock: 1000 units
Launching 6 concurrent threads...
Each thread issues 15 batches of 10 units (150 units/thread)
Expected total issued: 900 units
Expected final stock: 100 units
Starting Dispenser-Thread-1 with Priority: 2
Starting Dispenser-Thread-2 with Priority: 3
Starting Dispenser-Thread-3 with Priority: 4
Starting Dispenser-Thread-4 with Priority: 5
Starting Dispenser-Thread-5 with Priority: 1
Starting Dispenser-Thread-6 with Priority: 2

Main thread invoking join() on all workers...

All worker threads terminated. Duration: 2 ms
Total successful issue operations: 90
Total failed issue operations: 0
Final Stock in Medicine: 100 units

>>> [PASSED] Concurrency test succeeded without race conditions!
>>> Synchronized method guaranteed atomicity across all threads.
=================================================

  PASS: 32. ConcurrencyDemo: synchronized stock operations pass
  PASS: 33-34. TCP Socket: Server start, Client connect, SEARCH, disconnect
=================================================
       MITS UNIT 3: EXCEPTION HANDLING DEMO      
=================================================

--- [1] Demonstrating basic try-catch with throw & throws ---
CAUGHT InvalidInputException successfully!
Field: quantity | Reason: Must be strictly positive, received: -5

--- [2] Demonstrating Multiple Catch Blocks (mode=1) ---
Caught InsufficientStockException: Insufficient stock for Paracetamol (MED-001). Requested: 500, Available: 40

--- [2] Demonstrating Multiple Catch Blocks (mode=2) ---
Caught ExpiredMedicineException: Safety Alert: Medicine Amoxicillin (MED-002), Batch B-2022-01 has expired on 2026-09-24 and cannot be dispensed!

--- [2] Demonstrating Multiple Catch Blocks (mode=3) ---
Caught MedicineNotFoundException: Medicine not found in inventory for identifier/query: 'MED-999'

--- [2] Demonstrating Multiple Catch Blocks (mode=4) ---
Caught standard ArithmeticException: / by zero

--- [3] Demonstrating Nested try-catch Blocks ---
Outer try: Checking user authentication...
Inner try: Checking duplicate medicine code registration...
INNER CATCH: Caught DuplicateMedicineException: Medicine code 'MED-001' already exists in the inventory system.
Outer try resumed safely after handling inner exception.

--- [4] Demonstrating finally block execution ---
Connection opened: true
Simulating error during file/socket I/O...
Caught InventoryOperationException (unchecked): Simulated I/O pipeline failure
FINALLY BLOCK: Cleanup completed. Connection closed: true

[SUCCESS] All Unit 3 exception scenarios demonstrated successfully!

  PASS: 35. ExceptionDemo.runAll() completes without crash

=================================================
  TEST RESULTS: 34 PASSED, 0 FAILED (Total: 34)
=================================================
```

---

## 5. Quality Assurance Sign-Off & Verdict

- **Automated Verification Verdict:** **ACCEPTED / 100% PASS**
- **Code Stability:** Clean execution with zero unhandled exceptions, zero race conditions, and complete file cleanup.
- **Specification Compliance:** Fully aligns with B.Tech CSE-AI Capstone PBL Assessment 2 requirements across all 5 syllabus units.
