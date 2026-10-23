# 🏥 Intelligent Medical Inventory Tracking System (MITS)
## 📌 Master Project Guide: Execution, Syllabus Topics & Team Role Defense

---

## 1. ✅ Phase 2 Completion Status: 100% COMPLETE

| Category | Requirement | Actual Status in MITS | Verified |
|---|---|---|---|
| **Language & Environment** | Pure Java 17+ (No Maven, No Gradle, No SQLite) | Built using standard JDK 17 (`java.net`, `java.io`, `javax.swing`, `java.util`) | ✅ Verified |
| **Packages Structure** | 10 Organized Logical Packages | `com.mits.model`, `service`, `exception`, `util`, `io`, `concurrent`, `network`, `gui`, `report`, `app` (55 source files) | ✅ Verified |
| **Persistence** | File-based persistence in `./data/` | Object Serialization (`inventory.ser`), CSV export (`medicines.csv`), automated timestamped backups in `./data/backup/` | ✅ Verified |
| **Multithreading** | Background monitors, Daemon threads, Synchronization | `ExpiryMonitorThread` (daemon), `LowStockMonitor` (Runnable), `AutoBackupThread` (daemon), `AlertNotificationQueue` (wait/notifyAll) | ✅ Verified |
| **Socket Networking** | Client-Server over TCP | `InventoryServer` (port 5000, thread pool) & `InventoryClient` | ✅ Verified |
| **User Interface** | Swing/AWT Desktop GUI | `DashboardFrame` (7 role-based tabs, CardLayout, GridBagLayout, custom Java 2D `CategoryBarChartPanel`) | ✅ Verified |
| **Testing** | Pure Java Automated Test Suite | `TestRunner.java`: **34/34 Test Suites Passed, 0 Failures (100% Pass Rate)** | ✅ Verified |
| **Academic Reports** | PBL Report 1 & 2 in exact university format | `Report 1 (2).docx`, `Report 2.docx`, `Report 1_backup.docx` | ✅ Verified |

---

## 2. 🚀 HOW TO RUN THIS PROJECT (Step-by-Step)

The project is compiled into `./out`. You can run it in multiple ways depending on what you want to show:

### 🌟 Option A: Launch the Swing Desktop GUI (Recommended for Main Demo)
1. Open **Command Prompt** (CMD) or **PowerShell**.
2. Navigate to your project folder:
   ```cmd
   cd C:\Users\devis\Downloads\java
   ```
3. Run the GUI launcher batch script:
   ```cmd
   run_gui.bat
   ```
   *(Or run directly via Java: `java -cp out com.mits.app.GuiApp`)*
4. **What happens on screen:**
   - The **Login Window** appears.
   - Click the **"Quick Fill (Admin)"** button at the bottom (or enter username: `admin`, password: `admin123`).
   - Click **"Sign In"**.
   - The main **Dashboard Window** opens with 7 tabs:
     * **Dashboard Tab:** 5 live KPI status cards, custom Java 2D category bar chart, live scrolling alerts panel.
     * **Add/Edit Medicine Tab:** Form with dynamic field switching (e.g. selecting Syrup shows Volume field; selecting Injection shows Vial Type field).
     * **Stock Operations Tab:** Dispense stock (automated FEFO batch deduction) and Inward Restock.
     * **Search Tab:** Search by name/code with category filter, column-sortable table with health color coding (Red = Expired, Amber = Near Expiry, Pink = Low Stock).
     * **Reports Tab:** Generate analytical reports with progress bar; export to file.
     * **Remote Server Tab:** Connect to TCP socket server and query live inventory.
     * **Backup & Tools Tab:** Trigger immediate backup, restore backup, run Concurrency Demo, run Exception Demo.

---

### 💻 Option B: Run the Console Menu-Driven Application (Unit 1 CLI)
If the examiner asks: *"Can this system be operated without a GUI via terminal?"*
1. In Command Prompt:
   ```cmd
   run_console.bat
   ```
   *(Or with arguments: `java -cp out com.mits.app.ConsoleApp --user admin`)*
2. Enter password `admin123`.
3. Choose menu options (1: View All, 2: Search with method overloading, 3: Add Medicine, 4: Issue Stock, 5: Restock, 6: View Alerts, 7: Reports, 8: Backup, 9: Restore).

---

### 🌐 Option C: Run the TCP Client-Server System (Unit 4 Networking)
To demonstrate doctor remote consultation querying:
1. **Terminal 1 (Start Server):**
   ```cmd
   run_server.bat
   ```
   *(Output: `MITS Server running on port 5000. Press Enter to stop.`)*
2. **Terminal 2 (Client Terminal or GUI Remote Tab):**
   - In the GUI **"Remote Server"** tab, click **Connect**.
   - Type command: `SEARCH Paracetamol` or `STOCK MED-001` or `ALERTS`.
   - Click **Send** to see instant multi-line responses from the server socket.

---

### 🧪 Option D: Run the 34 Automated Tests (Show This to the Teacher First!)
```cmd
java -cp out com.mits.app.TestRunner
```
* **Output:** 34 PASS results covering Units 1 to 5, live multithreading concurrency verification (6 threads, 900 transactions, 0 race conditions), and live exception demonstration.

---

### 🔨 If You Make Any Code Changes, Recompile with:
```cmd
build.bat
```
*(Compiles all 55 classes into `./out` with zero errors in ~2 seconds).*

---

## 3. 📚 COMPLETE SYLLABUS TOPICS USED (Unit 1 to Unit 5)

### 🔹 UNIT 1: Java Basics, Classes & OOP Relationships
* **Classes & Encapsulation:** `Medicine.java`, `Batch.java`, `Supplier.java`, `User.java`. Private fields with validated public getters/setters (e.g., preventing negative prices).
* **Association ("Uses-a"):** `Medicine` references `Supplier` via `supplierId`. If a supplier is removed, medicine records remain valid.
* **Aggregation ("Has-a"):** `Inventory` aggregates `Medicine` objects inside a `HashMap`.
* **Composition ("Owns-a"):** `Medicine` composes a `List<Batch>`. A batch lot cannot exist in the hospital warehouse without its medicine parent. Deleting a medicine cascades to destroy its batches.
* **Generalization ("Is-a"):** `Tablet`, `Syrup`, `Injection`, and `Ointment` inherit from `Medicine`.
* **Console I/O & CLI Args:** `ConsoleApp.java` handles `--seed`, `--report`, `--user <name>`, `--help` using `Scanner` and `switch`.

### 🔹 UNIT 2: Abstraction, Polymorphism, Arrays & Lambdas
* **Abstract Class & Abstract Methods:** `Medicine.java` is abstract. Enforces concrete implementation of `getStorageCondition()`, `calculateDiscountedPrice()`, and `getDosageFormDetails()`.
* **Interfaces:** `Expirable` (expiry checking), `Reorderable` (stock thresholds), `Reportable` (summary text). Both `Medicine` and `Batch` implement `Expirable`.
* **Multilevel Inheritance:** `Medicine` ➔ `Tablet` ➔ `Antibiotic`. Adds antibiotic clinical spectrum and prescription enforcement.
* **Method Overloading:**
  * In `Medicine.java`: `addStock(int)`, `addStock(int, LocalDate)`, `addStock(int, Batch)`.
  * In `InventoryService.java`: `search(String)`, `search(String, String)`, `search(double, double)`.
* **Method Overriding:** Subclasses override `getStorageCondition()` (Tablet = room temp; Injection = cold-chain 2-8°C).
* **Jagged Arrays:** `InventoryService.getMonthlyConsumptionJaggedArray()` allocates non-uniform 2D matrix (`int[][]`) to store varying historical consumption months per medicine without memory waste.
* **Lambda Expressions:** `Predicate<Medicine>` (filter low stock), `Comparator<Medicine>` (sort by expiry), `Consumer<Alert>` (GUI alert dispatch), custom `@FunctionalInterface StockEvaluator`.

### 🔹 UNIT 3: Exception Handling & Strings
* **Custom Checked Exceptions:** `InsufficientStockException`, `ExpiredMedicineException`, `MedicineNotFoundException`, `DuplicateMedicineException`, `InvalidInputException`, `AuthenticationException`.
* **Unchecked Exception:** `InventoryOperationException` (extends `RuntimeException` for storage crashes).
* **Exception Constructs:** `ExceptionDemo.java` proves try-catch, multiple catch (specific before general), nested try blocks, guaranteed `finally` cleanup, `throw`, and `throws`.
* **String Processing (`StringProcessor.java`):**
  * `String`: Immutable identifiers and regex validation for `MED-XXX`.
  * `StringBuilder`: High-speed single-threaded report assembly in `ReportService`.
  * `StringBuffer`: Synchronized thread-safe buffer (`AUDIT_LOG_BUFFER`) for concurrent audit logging without corruption.

### 🔹 UNIT 4: Multithreading, I/O & Sockets
* **Extending Thread:** `ExpiryMonitorThread` (daemon, checks expiry every 30s), `AutoBackupThread` (daemon, auto-saves every 5m).
* **Implementing Runnable:** `LowStockMonitor` (daemon, monitors reorder points).
* **Daemon Threads (`setDaemon(true)`):** Ensures background monitor loops terminate cleanly when user closes GUI.
* **Synchronization & Race Conditions:** `Medicine.issueStock()` and `addStock()` use `synchronized` monitor locks. Proved via `ConcurrencyDemo` (6 threads, 900 units deducted, final stock mathematically exact at 100 units).
* **Producer-Consumer:** `AlertNotificationQueue` coordinates background producers and GUI consumers using `wait()` and `notifyAll()`.
* **Dual File I/O:**
  * Byte Streams (`ObjectOutputStream`/`ObjectInputStream`): Binary serialization to `./data/inventory.ser`.
  * Character Streams (`BufferedReader`/`PrintWriter`): CSV exports (`./data/medicines.csv`) and text reports.
* **TCP Socket Programming:** `InventoryServer` (port 5000, `ServerSocket`, multi-threaded client pool) and `InventoryClient`.

### 🔹 UNIT 5: GUI, Generics & Collections
* **Swing & AWT GUI:** `DashboardFrame` (7 tabs, `CardLayout`, `GridBagLayout`, `JTable` with `TableRowSorter` and `MedicineTableCellRenderer`).
* **Custom Java 2D Graphics:** `CategoryBarChartPanel` overrides `paintComponent(Graphics g)` to render real-time colored bar charts.
* **Swing Thread Safety:** `SwingUtilities.invokeLater()` marshals background alerts onto Event Dispatch Thread (EDT).
* **Collections Framework:**
  * `HashMap<String, Medicine>`: $O(1)$ instant code lookup.
  * `TreeSet<Medicine>`: Self-sorting collection by earliest expiry date.
  * `LinkedList<Alert>`: Bounded FIFO alert history.
  * `PriorityQueue<Medicine>`: Automatic triage of low-stock medicines needing reorder.
  * `ArrayList<StockTransaction>`: Fast append-only audit trail.
* **Generics:** Generic `Repository<T, ID>`, bounded type parameter `<E extends Comparable<E>> E findMax(List<E>)`, wildcards `Collection<? extends T>`, and generic utility `Pair<K, V>`.

---

## 4. 👥 WHO DID WHAT? (Team Member Contribution & Viva Defense)

| Student Name | Roll No. | Exact Module Owned | Files Created | What to Say in Viva |
|---|---|---|---|---|
| **Devvrat Tripathi** *(Team Lead)* | 0251CSAI231 | Service Layer, Business Logic & CLI | `InventoryService.java`<br>`ConsoleApp.java`<br>`Constants.java` | *"I built the central Facade service layer that coordinates GUI, CLI, and Sockets with persistence. I implemented the 3 overloaded search methods, the jagged array consumption analytics, and the 28 pre-seeded medicine dataset."* |
| **Manas Srivastava** | 0251CSAI144 | OOP Domain Models & FEFO Batch Engine | `Medicine.java`<br>`Tablet/Syrup/Injection/Ointment.java`<br>`Antibiotic.java`<br>`Batch.java`, `Supplier.java` | *"I architected the inheritance hierarchy where abstract Medicine forces subclasses to define custom storage and discount rules. I built the multilevel inheritance chain (Medicine->Tablet->Antibiotic) and the FEFO automated batch lot deduction algorithm."* |
| **Pranjal Sharma** | 0251CSAI141 | Multithreading & Concurrency Engine | `ExpiryMonitorThread.java`<br>`LowStockMonitor.java`<br>`AutoBackupThread.java`<br>`AlertNotificationQueue.java`<br>`ConcurrencyDemo.java` | *"I designed the multithreading engine. I implemented daemon threads with priority scheduling, the producer-consumer alert queue with wait() and notifyAll(), and built ConcurrencyDemo proving mathematically that our synchronized locks eliminate race conditions across 6 threads."* |
| **Hardik Kumar** | 0251CSAI195 | Swing GUI, Sockets & File Persistence | `DashboardFrame.java`<br>`LoginFrame.java`<br>`CategoryBarChartPanel.java`<br>`InventoryServer.java`, `InventoryClient.java`<br>`FilePersistenceManager.java` | *"I engineered the 7-tab Swing GUI with CardLayout dynamic swapping and Java 2D bar charts. I built the multi-client TCP socket server on port 5000 for remote doctor querying, and implemented dual persistence (binary object serialization + CSV character streams)."* |
| **Mritunjay Gupta** | 0251CSAI132 | Exceptions, Collections & Automated Tests | `InsufficientStockException.java` (+6 custom exceptions)<br>`ExceptionDemo.java`<br>`Inventory.java`<br>`TestRunner.java` | *"I built the custom exception framework and ExceptionDemo demonstrating all 7 exception keywords. I configured the collections in Inventory.java (HashMap for O(1) lookups, TreeSet for expiry sorting), and created the standalone test suite with 34/34 passing tests."* |

---

## 5. 🔑 Default Login Credentials
* **Admin:** `admin` / `admin123` (Full access to all 7 tabs, backups, and demos)
* **Pharmacist:** `pharmacist` / `pharma123` (Stock operations, dispensing, restocking, reports)
* **Doctor:** `doctor` / `doctor123` (Read-only medicine search and availability reports)

---
*Generated for Group 24 | B.Tech CSE-AI | Capstone Project PBL Assessment 2*
