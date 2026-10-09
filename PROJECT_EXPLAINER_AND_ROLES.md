# Intelligent Medical Inventory Tracking System (MITS)
## 🎓 Complete Beginner-Friendly Guide, System Workflow & Team Member Role Defense
*Specially crafted for B.Tech CSE-AI Capstone / PBL Assessment 2 (Object Oriented Techniques using Java)*

---

## 📌 TABLE OF CONTENTS
1. [What is this Project in 2 Minutes? (Explain Like I'm 5)](#1-what-is-this-project-in-2-minutes-explain-like-im-5)
2. [The Real-World Problem vs Our Solution](#2-the-real-world-problem-vs-our-solution)
3. [End-to-End System Workflow (How It Actually Works)](#3-end-to-end-system-workflow-how-it-actually-works)
4. [Every Single Java Concept Explained in Simple Words (Units 1 to 5)](#4-every-single-java-concept-explained-in-simple-words-units-1-to-5)
5. [👥 WHO DID WHAT? Complete Team Member Roles & Viva Defense](#5--who-did-what-complete-team-member-roles--viva-defense)
   - [Student 1: Devvrat Tripathi (Team Lead & Business Logic)](#student-1-devvrat-tripathi-0251csai231---team-lead--business-logic)
   - [Student 2: Manas Srivastava (System Architect & OOP Models)](#student-2-manas-srivastava-0251csai144---system-architect--oop-models)
   - [Student 3: Pranjal Sharma (Concurrency & Background Monitors)](#student-3-pranjal-sharma-0251csai141---concurrency--background-monitors)
   - [Student 4: Hardik Kumar (GUI, Sockets & File Persistence)](#student-4-hardik-kumar-0251csai195---gui-sockets--file-persistence)
   - [Student 5: Mritunjay Gupta (Exceptions, Collections & Test Framework)](#student-5-mritunjay-gupta-0251csai132---exceptions-collections--test-framework)
6. [🔥 Teacher's Trap Questions & Exact Answers to Speak in Viva](#6--teachers-trap-questions--exact-answers-to-speak-in-viva)
7. [How to Run and Demo the Project](#7-how-to-run-and-demo-the-project)

---

## 1. What is this Project in 2 Minutes? (Explain Like I'm 5)

Imagine a busy hospital pharmacy:
* Doctors prescribe medicines.
* Patients line up at the pharmacy counter.
* Pharmacists need to hand out medicines fast.

**What goes wrong in real life?**
1. **Stockouts:** A patient needs insulin, but the pharmacy realizes it's out of stock *right when the patient is at the counter*.
2. **Expired Medicines:** Medicines sit in the back of the drawer. By the time someone checks, they expired 3 months ago. Giving expired medicine to a patient can be fatal.
3. **Manual Chaos:** Writing stock counts in paper registers or spreadsheets leads to human errors and double-entry mistakes.

**What does our system do?**
Our application is an **intelligent, automated desktop software** written in **Pure Java 17** (zero external libraries, zero databases). 
* It tracks every single tablet, syrup, injection, and ointment.
* It tracks **batches and expiry dates** (always dispensing the batch that expires earliest first — called **FEFO: First-Expiry First-Out**).
* It has **silent background robot threads** that constantly scan the warehouse. If a medicine is running low or expiring in 30 days, it automatically sounds the alarm on the dashboard!
* It allows doctors to search medicines over a **hospital network (TCP socket)** from their clinic room.
* It auto-saves everything to disk and creates automatic backup copies.

---

## 2. The Real-World Problem vs Our Solution

| The Hospital Problem | How MITS Solves It | Technical Java Feature Used |
|---|---|---|
| Pharmacist gives medicine that expired last week | Automated **FEFO algorithm** picks the batch expiring soonest; blocks dispensing if expired | OOP Composition (`Medicine` owns `Batches`), Custom `ExpiredMedicineException` |
| Multiple nurses/pharmacists dispensing at the exact same second causing negative stock | **Thread Synchronization** blocks simultaneous writes and makes updates 100% atomic | `synchronized` keyword, Monitor locks |
| Pharmacist forgets to check expiry dates daily | An automatic daemon thread silently scans inventory every 30 seconds | Multithreading (`Thread`, `setDaemon(true)`) |
| Network/Power failure corrupts database | Dual-mode persistence: Binary serialization (`.ser`) + human-readable CSV + auto backups | Java Byte Streams (`ObjectOutputStream`) & Character Streams (`PrintWriter`) |
| Doctors don't know if a medicine is in stock without calling pharmacy | Dedicated TCP Server allows doctor's terminal to query stock remotely | Java Socket Programming (`ServerSocket`, `Socket`) |
| Unqualified staff modifying medicine prices | Role-Based Access Control (RBAC): Admin has full rights, Pharmacist manages stock, Doctor has read-only search | Java Enums (`Role`), GUI Tab visibility control |

---

## 3. End-to-End System Workflow (How It Actually Works)

Here is the exact lifecycle of how data flows in the system from the moment you click `run_gui.bat`:

```
+-----------------------------------------------------------------------------------+
| 1. APPLICATION BOOTSTRAP (GuiApp.java)                                            |
|    - Checks ./data/ folder. If empty, seeds 28 medicines, 4 suppliers, 3 users.   |
|    - Spawns Background Daemon Threads: ExpiryMonitor, LowStockMonitor, AutoBackup.|
+-----------------------------------------------------------------------------------+
                                       |
                                       v
+-----------------------------------------------------------------------------------+
| 2. USER AUTHENTICATION (LoginFrame.java)                                          |
|    - User types username & password (e.g. admin / admin123).                      |
|    - InventoryService checks UserRepository.                                      |
|    - If invalid -> Throws AuthenticationException -> Shows error popup.           |
|    - If valid -> Loads DashboardFrame tailored to user's Role.                    |
+-----------------------------------------------------------------------------------+
                                       |
                                       v
+-----------------------------------------------------------------------------------+
| 3. DASHBOARD LAUNCH (DashboardFrame.java)                                         |
|    - Displays 5 live KPI Status Cards (Total Meds, Stock Value, Low, Near, Expired)|
|    - Renders CategoryBarChartPanel (Custom Java 2D Graphics bar chart).           |
|    - Subscribes to AlertNotificationQueue via SwingUtilities.invokeLater.         |
+-----------------------------------------------------------------------------------+
       |                               |                                |
       v                               v                                v
+------------------+         +------------------+         +-------------------+
| DISPENSE / ISSUE |         | RESTOCK INWARD   |         | REMOTE QUERY      |
| Pharmacist enters|         | Enters code,     |         | Doctor on remote  |
| code & quantity. |         | batch number,    |         | PC sends TCP      |
| Calls:           |         | expiry date.     |         | "SEARCH Paracet"  |
| Medicine.issue() |         | Calls:           |         | Server returns    |
| (FEFO deduction) |         | addStock(Batch)  |         | live stock count. |
+------------------+         +------------------+         +-------------------+
       |                               |                                |
       +-------------------------------+--------------------------------+
                                       |
                                       v
+-----------------------------------------------------------------------------------+
| 4. THREAD-SAFE INVENTORY PERSISTENCE & AUDIT                                      |
|    - All stock mutators run under 'synchronized' locks to eliminate race errors.  |
|    - Transaction logged into ArrayList<StockTransaction>.                         |
|    - FilePersistenceManager saves binary snapshot to ./data/inventory.ser.        |
|    - AutoBackupThread creates timestamped snapshot in ./data/backup/.             |
+-----------------------------------------------------------------------------------+
```

---

## 4. Every Single Java Concept Explained in Simple Words (Units 1 to 5)

When the teacher asks, *"Explain how you applied Unit X in your code?"*, here is exactly what to say:

### 🔹 UNIT 1: Classes, Objects, Constructors & Relationships

#### 1. Encapsulation
* **What it means:** Keeping variables `private` and only allowing access through `public` getters/setters with validation rules.
* **In our code:** In `Medicine.java`, fields like `unitPrice`, `reorderLevel`, and `batches` are `private`. You cannot set a negative price because `setUnitPrice()` throws `InvalidInputException`.

#### 2. The 4 Object Relationships (VERY POPULAR TEACHER QUESTION!):
1. **Association ("Uses-a"):**
   * *Real-world:* A Teacher teaches a Student, but both exist independently.
   * *In our code:* `Medicine` has a `supplierId` string linking it to `Supplier`. If a Supplier goes out of business, the medicine still exists.
2. **Aggregation ("Has-a" / Loose Whole-Part):**
   * *Real-world:* A Library has Books. If the library closes, the books still exist.
   * *In our code:* `Inventory` aggregates `Medicine` objects inside a `HashMap`. Medicines can exist even if removed from this specific inventory instance.
3. **Composition ("Owns-a" / Strict Whole-Part):**
   * *Real-world:* A Human has a Heart. If the human dies, the heart cannot survive independently.
   * *In our code:* `Medicine` composes `Batch`. A `Batch` cannot exist in the warehouse without belonging to a specific `Medicine`. If the medicine is deleted, all its batches are destroyed with it.
4. **Generalization ("Is-a" / Inheritance):**
   * *Real-world:* A Dog is an Animal.
   * *In our code:* `Tablet`, `Syrup`, `Injection`, and `Ointment` extend `Medicine`.

---

### 🔹 UNIT 2: OOP Features, Interfaces, Arrays & Lambdas

#### 1. Abstract Class vs Interface
* **Abstract Class (`Medicine.java`):**
  * *Why abstract?* You cannot buy a generic "Medicine" at a store; you buy a specific *Tablet* or *Syrup*.
  * Provides shared fields (`code`, `name`, `unitPrice`) and shared concrete code (`addStock()`, `getTotalStock()`), but forces subclasses to implement abstract methods:
    * `getStorageCondition()` -> Tablets need room temp; Injections need cold-chain (2-8°C).
    * `calculateDiscountedPrice(qty)` -> Syrups offer different volume discounts than Injections.
    * `getDosageFormDetails()` -> Strip size for tablets; volume in ml for syrups.
* **Interfaces (`Expirable`, `Reorderable`, `Reportable`):**
  * Pure contracts with zero instance state.
  * `Expirable` defines `isExpired()` and `daysToExpiry()`. Implemented by both `Medicine` AND `Batch`.
  * Allows polymorphic handling: background monitors only care if an object is `Expirable`, without caring whether it is a Tablet or an Injection.

#### 2. Multilevel Inheritance
* Hierarchy: `Medicine` (Level 1) ➔ `Tablet` (Level 2) ➔ `Antibiotic` (Level 3).
* An `Antibiotic` inherits everything a `Tablet` has (strip size, chewability), plus adds antibiotic-specific properties: `spectrum` ("Broad-Spectrum"), `requiresPrescription` (Schedule-H compliance), and custom price discounting.

#### 3. Method Overloading vs Overriding
* **Overloading (Same name, different parameters in SAME class - Compile-Time):**
  * In `Medicine.java`:
    1. `addStock(int qty)` -> Quick stock increment.
    2. `addStock(int qty, LocalDate expiry)` -> Creates batch with default name.
    3. `addStock(int qty, Batch batch)` -> Detailed inward lot registration.
  * In `InventoryService.java`:
    1. `search(String query)` -> Search by name/code.
    2. `search(String query, String category)` -> Search with category filter.
    3. `search(double minPrice, double maxPrice)` -> Search by price range.
* **Overriding (Same name, same parameters in SUBCLASS - Runtime Dynamic Binding):**
  * `Tablet.getStorageCondition()` returns `"Store in a cool, dry place below 25°C"`.
  * `Injection.getStorageCondition()` returns `"Store in refrigerator at 2°C to 8°C. Do NOT freeze."`.

#### 4. Jagged Arrays (Ragged Arrays)
* **What is it?** A 2D array where each row has a different number of columns (`int[][]`).
* **Why we used it:** In `InventoryService.getMonthlyConsumptionJaggedArray()`, different medicines have different historical tracking durations:
  * Fast-moving Paracetamol has 12 months of consumption data.
  * Newly launched Antibiotic only has 3 months of data.
  * A regular rectangular matrix would waste memory with empty zeros. Jagged array allocates exact columns per medicine.

#### 5. Lambda Expressions & Functional Interfaces
* Clean, 1-line anonymous functions replacing bulky inner classes.
* Built-in interfaces used:
  * `Predicate<Medicine>`: `m -> m.needsReorder()` (Used in `LambdaProcessor.filterLowStock`).
  * `Comparator<Medicine>`: `(m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName())`.
  * `Consumer<Alert>`: Registered alert listener for live GUI updates.
  * Custom `@FunctionalInterface StockEvaluator`: Custom risk score calculator `(stock, reorder) -> stock <= reorder ? 1.0 : 0.0`.

---

### 🔹 UNIT 3: Exception Handling & String Handling

#### 1. Custom Checked vs Unchecked Exceptions
* **Checked Exceptions (Extend `Exception`):** The compiler forces you to handle them with `try-catch` or declare with `throws`. Used for predictable business errors:
  * `InsufficientStockException`: Attempted to dispense 50 units when only 20 are in stock.
  * `ExpiredMedicineException`: Attempted to dispense a batch past its expiry date.
  * `MedicineNotFoundException`: Requested code does not exist.
  * `DuplicateMedicineException`: Adding code `MED-001` when it's already registered.
  * `InvalidInputException`: Entering negative price or blank name.
  * `AuthenticationException`: Wrong password.
* **Unchecked Exception (Extends `RuntimeException`):**
  * `InventoryOperationException`: Fatal internal failure (e.g. storage file corrupted or disk read failure).

#### 2. Exception Constructs Showcase (`ExceptionDemo.java`)
* Demonstrates all 7 keywords:
  1. `try-catch`: Normal catching of errors.
  2. `multiple catch`: Catching specific custom exceptions first, then general `Exception`.
  3. `nested try`: Checking login credentials in outer try, then checking duplicate medicine code in inner try.
  4. `finally`: Guaranteed resource cleanup (closing simulated file handles even if an exception occurs).
  5. `throw`: Deliberately triggering an exception (`throw new InsufficientStockException(...)`).
  6. `throws`: Declaring on method signatures that caller must handle it.

#### 3. String vs StringBuilder vs StringBuffer
* **`String`:** Immutable (cannot be changed). Used for medicine codes, names, and regex matching.
* **`StringBuilder`:** Mutable, high performance, single-threaded. Used in `ReportService` to assemble large 1,000-line analytical reports in microseconds.
* **`StringBuffer`:** Mutable and **thread-safe** (methods are synchronized). Used in `StringProcessor.AUDIT_LOG_BUFFER` so multiple concurrent worker threads can append audit trail logs safely without memory corruption.

---

### 🔹 UNIT 4: Multithreading, Concurrency, I/O & Sockets

#### 1. How Multithreading is Implemented
* **Pattern 1: Extending `Thread`:**
  * `ExpiryMonitorThread` (daemon): Runs infinite loop sleeping 30 seconds; scans all medicines and posts alerts if daysToExpiry <= 30.
  * `AutoBackupThread` (daemon): Runs infinite loop sleeping 5 minutes; writes timestamped `.ser` backup files.
* **Pattern 2: Implementing `Runnable`:**
  * `LowStockMonitor`: Implements `Runnable`, passed into `new Thread(task)`. Scans for medicines where `totalStock <= reorderLevel`.
* **Daemon Threads (`setDaemon(true)`):**
  * Background helper threads that automatically terminate when the main user closes the GUI window. Without daemon mode, the Java program would keep running in Task Manager forever!

#### 2. Synchronization & Eliminating Race Conditions
* **The Problem:** If Thread A and Thread B both read stock = 50, and both deduct 30 at the exact same millisecond, stock becomes 20 instead of the correct -10 (or 50 - 60 = error).
* **The Solution:** In `Medicine.java`, `issueStock()` and `addStock()` have the `synchronized` modifier. Only one thread can enter the method monitor at a time.
* **Proof:** `ConcurrencyDemo.java` launches **6 concurrent threads** issuing stock 90 times simultaneously (deducting 900 units from 1,000 units). Final stock is mathematically verified to be **exactly 100 units** with zero race conditions!

#### 3. Producer-Consumer Pattern (`AlertNotificationQueue.java`)
* Uses Java's core monitor methods: `wait()` and `notifyAll()`.
* **Producers:** Background monitor threads detect low stock/expiry and `enqueue()` alerts. If queue is full (capacity 100), producers `wait()`.
* **Consumers:** GUI worker threads `dequeue()` alerts and display them on screen. When an item is read, `notifyAll()` wakes waiting threads.

#### 4. File I/O: Byte Streams vs Character Streams
* **Byte Streams (`ObjectOutputStream` / `ObjectInputStream`):**
  * Read/write raw binary data.
  * Used in `FilePersistenceManager.saveInventoryBinary()` to serialize the entire `Inventory` object graph directly to `./data/inventory.ser`. Fast, compact, exact state restoration.
* **Character Streams (`FileReader`, `FileWriter`, `BufferedReader`, `PrintWriter`):**
  * Read/write human-readable text characters.
  * Used to export `./data/medicines.csv`, audit log files, and text reports.

#### 5. TCP Client-Server Socket Programming
* **Server (`InventoryServer.java`):**
  * Listens on port 5000 using `ServerSocket`.
  * Uses `ExecutorService` (cached thread pool) so multiple doctor terminals can connect simultaneously.
  * Understands custom text protocol: `LOGIN <user> <pass>`, `SEARCH <query>`, `STOCK <code>`, `ISSUE <code> <qty>`, `ALERTS`, `QUIT`.
* **Client (`InventoryClient.java`):**
  * Connects over `Socket("localhost", 5000)`. Sends requests and reads server responses via `BufferedReader` and `PrintWriter`.

---

### 🔹 UNIT 5: GUI Programming, Generics & Collections Framework

#### 1. Swing GUI (`DashboardFrame.java`)
* Built using pure Java Swing and AWT.
* **Layout Managers Used:**
  * `BorderLayout`: Main frame division (North header, Center workspace, South buttons).
  * `GridLayout`: Dashboard KPI cards (1 row, 5 equal columns).
  * `GridBagLayout`: Flexible form layout with labels and input fields.
  * `CardLayout`: Dynamic sub-panel that swaps fields based on medicine type (selecting "Syrup" shows volume field; selecting "Injection" shows vial type field).
* **Components:** `JTabbedPane`, `JTable`, `TableRowSorter`, `JSpinner`, `JComboBox`, `JProgressBar`, `JFileChooser`.
* **Custom Graphics2D:** `CategoryBarChartPanel.java` overrides `paintComponent(Graphics g)` to draw a dynamic colored bar chart showing stock distribution across Tablet, Syrup, Injection, Ointment, Antibiotic.
* **EDT Thread Safety:** All background thread alerts call `SwingUtilities.invokeLater(() -> ...)` to ensure GUI rendering never collides with background threads.

#### 2. Collections Framework (Why We Chose Each):
| Collection | Where Used | Why This Specific Collection? |
|---|---|---|
| `HashMap<String, Medicine>` | `Inventory.java` | **O(1) instant lookup** of medicines by unique code (`MED-001`). |
| `TreeSet<Medicine>` | `Inventory.getExpirySortedMedicines()` | Automatically keeps medicines sorted by **earliest expiry date** using custom `Comparator`. |
| `LinkedList<Alert>` | `Inventory.java` alert history | Efficient $O(1)$ insertion at head for new alerts and removal of old alerts (FIFO bounded queue). |
| `PriorityQueue<Medicine>` | `Inventory.java` | Priority heap that automatically surfaces medicines with the lowest stock ratio first for reordering. |
| `ArrayList<StockTransaction>` | `Inventory.java` audit trail | Fast random access, efficient append-only transaction history. |
| `HashSet<String>` | `Inventory.java` categories | Guarantees list of unique medicine categories without duplicates. |

#### 3. Generics
* **Generic Class (`Repository<T, ID>`):** In-memory generic repository used for medicines, suppliers, and users.
* **Generic Method with Bounded Type Parameter:**
  ```java
  public static <E extends Comparable<E>> E findMax(List<E> list)
  ```
  Ensures only objects that can be compared can be passed in.
* **Wildcard Generics:** `printAll(Collection<? extends T> items)` accepts collections of any subtype.
* **Generic Utility (`Pair<K, V>`):** Generic key-value wrapper.

---

## 5. 👥 WHO DID WHAT? Complete Team Member Roles & Viva Defense

> ⚠️ **TEACHER'S FAVORITE QUESTION:** *"Who in the team wrote which part? Don't tell me everyone did everything."*
> 
> **Here is the clear, distinct, and defensible role division.** Each student has 100% ownership of their specific module!

---

### 👨‍💻 Student 1: Devvrat Tripathi (0251CSAI231) - Team Lead & Business Logic

#### What Devvrat Did:
* **Primary Role:** System Architecture, Business Logic Coordination, and Command-Line Interface.
* **Specific Source Files Created/Owned:**
  * `src/com/mits/service/InventoryService.java` (The heart of the entire project)
  * `src/com/mits/app/ConsoleApp.java` (Menu-driven console application with CLI args)
  * `src/com/mits/util/Constants.java`
* **Key Achievements:**
  * Designed the business logic connecting all layers.
  * Implemented the 3 overloaded `search()` methods and the `getMonthlyConsumptionJaggedArray()` matrix.
  * Built CLI argument parsing (`--seed`, `--report`, `--user <name>`, `--help`).
  * Created the 28 pre-seeded realistic medicine records across 5 categories.

#### Questions Teacher Will Ask Devvrat:
1. *Q: "Devvrat, how does your service layer coordinate between the GUI and the persistence layer?"*
   * **Answer:** *"Ma'am, `InventoryService` acts as a Facade. Whether the request comes from `ConsoleApp`, `DashboardFrame` (GUI), or `InventoryServer` (TCP), it calls `InventoryService`. It enforces synchronization, triggers audit logging, checks safety thresholds, and delegates binary snapshot saving to `FilePersistenceManager`."*
2. *Q: "Why did you implement jagged arrays in `InventoryService`?"*
   * **Answer:** *"Because in pharmacy logistics, different medicines have tracked consumption histories of different lengths (e.g. Paracetamol has 12 months, while a newly introduced antibiotic only has 3 months). A rectangular 2D array would waste memory with empty zero padding. A jagged array `int[][]` allocates rows with exact column counts."*

---

### 👨‍💻 Student 2: Manas Srivastava (0251CSAI144) - System Architect & OOP Models

#### What Manas Did:
* **Primary Role:** Domain Object Modeling, Class Hierarchies, and OOP Contracts.
* **Specific Source Files Created/Owned:**
  * `src/com/mits/model/Medicine.java` (Core abstract class)
  * `src/com/mits/model/Tablet.java`, `Syrup.java`, `Injection.java`, `Ointment.java`
  * `src/com/mits/model/Antibiotic.java` (Multilevel inheritance)
  * `src/com/mits/model/Batch.java` (Composition)
  * `src/com/mits/model/Supplier.java` (Association)
  * `src/com/mits/model/Expirable.java`, `Reorderable.java`, `Reportable.java` (Interfaces)
  * `src/com/mits/model/Role.java`, `User.java`

#### Key Achievements:
* Architected the inheritance hierarchy where `Medicine` is abstract and forces subclasses to define unique storage conditions, discount calculations, and dosage forms.
* Built the **multilevel inheritance** chain: `Medicine ➔ Tablet ➔ Antibiotic`.
* Implemented **Composition**: `Medicine` owns a `List<Batch>`. Built the automated FEFO (First-Expiry First-Out) deduction algorithm inside `Medicine.issueStock()`.

#### Questions Teacher Will Ask Manas:
1. *Q: "Manas, explain why `Medicine` is an abstract class and not an interface or concrete class?"*
   * **Answer:** *"Sir, `Medicine` contains concrete shared state and behaviour (like `code`, `name`, `unitPrice`, synchronized `addStock()`, and batch management) which an interface cannot hold. But it also has behaviors that are physically impossible to generalize (like storage condition: tablets stay in room temp, while injections require 2-8°C cold chain). Making it abstract forces every medicine category to provide its own concrete implementation."*
2. *Q: "Explain how Batch and Medicine demonstrate Composition?"*
   * **Answer:** *"In our design, a `Batch` cannot exist independently in the warehouse without being tied to a specific `Medicine`. `Medicine` manages the lifecycle of its `List<Batch>`. If a medicine is deleted, all its batch lots are destroyed with it."*

---

### 👨‍💻 Student 3: Pranjal Sharma (0251CSAI141) - Concurrency & Background Monitors

#### What Pranjal Did:
* **Primary Role:** Multithreading, Thread Safety, Concurrency Verification, and Asynchronous Alerts.
* **Specific Source Files Created/Owned:**
  * `src/com/mits/concurrent/ExpiryMonitorThread.java` (Extends `Thread`, daemon)
  * `src/com/mits/concurrent/AutoBackupThread.java` (Extends `Thread`, daemon)
  * `src/com/mits/concurrent/LowStockMonitor.java` (Implements `Runnable`)
  * `src/com/mits/concurrent/AlertNotificationQueue.java` (Producer-Consumer with `wait`/`notifyAll`)
  * `src/com/mits/concurrent/ReportGeneratorThread.java` (Background worker with progress callback)
  * `src/com/mits/concurrent/ConcurrencyDemo.java` (Mathematical proof of synchronization)

#### Key Achievements:
* Created background daemon threads that silently monitor stock levels without freezing the application.
* Designed the thread-safe `AlertNotificationQueue` using Java's intrinsic monitor methods (`wait()` and `notifyAll()`).
* Built `ConcurrencyDemo` which launches 6 concurrent threads to mathematically prove zero race conditions.

#### Questions Teacher Will Ask Pranjal:
1. *Q: "Pranjal, what is a daemon thread and why did you use `setDaemon(true)`?"*
   * **Answer:** *"Ma'am, a daemon thread is a low-priority background thread that provides services to user threads. The JVM terminates automatically when all non-daemon user threads finish. If `ExpiryMonitorThread` and `AutoBackupThread` were normal user threads, closing the GUI window would leave their infinite loops running forever in the background as zombie processes. `setDaemon(true)` ensures they terminate cleanly when the GUI closes."*
2. *Q: "How does your `ConcurrencyDemo` prove that you eliminated race conditions?"*
   * **Answer:** *"We initialize a medicine with 1,000 units. We spawn 6 threads, each attempting to issue 15 batches of 10 units (150 units per thread, total 900 units). Without the `synchronized` keyword on `issueStock()`, concurrent read-modify-write collisions would cause lost updates. With our synchronization, all 6 threads complete, and final stock is mathematically verified to be exactly 100 units every single run."*

---

### 👨‍💻 Student 4: Hardik Kumar (0251CSAI195) - GUI, Sockets & File Persistence

#### What Hardik Did:
* **Primary Role:** Swing/AWT User Interface, Client-Server TCP Networking, and Dual File Persistence.
* **Specific Source Files Created/Owned:**
  * `src/com/mits/gui/LoginFrame.java` & `DashboardFrame.java` (7-tab interactive GUI)
  * `src/com/mits/gui/CategoryBarChartPanel.java` (Custom Java 2D Graphics bar chart)
  * `src/com/mits/gui/StatusCardPanel.java` & `MedicineTableCellRenderer.java`
  * `src/com/mits/network/InventoryServer.java` & `InventoryClient.java` (TCP Sockets)
  * `src/com/mits/io/FilePersistenceManager.java` (Byte & Character streams)
  * `src/com/mits/app/GuiApp.java` & `ServerApp.java`

#### Key Achievements:
* Created the complete 7-tab GUI with CardLayout dynamic field swapping and role-based tab access.
* Programmed the custom Java 2D Graphics bar chart using `Graphics2D` rendering.
* Built the multi-client TCP socket server on port 5000 with a custom wire command protocol.
* Implemented dual-channel persistence: Java Object Serialization (`.ser`) and CSV character streams with automatic backup rotation.

#### Questions Teacher Will Ask Hardik:
1. *Q: "Hardik, why did you use both Byte Streams and Character Streams?"*
   * **Answer:** *"Sir, Byte Streams (`ObjectOutputStream`/`ObjectInputStream`) serialize the entire object graph directly to `./data/inventory.ser`, preserving deep relationships and batch structures instantaneously. Character Streams (`BufferedReader`/`PrintWriter`) are used for `./data/medicines.csv` and exported reports so that hospital management can open the data in Microsoft Excel or Notepad."*
2. *Q: "How did you prevent the Swing GUI from freezing during background updates?"*
   * **Answer:** *"Swing is single-threaded; all UI rendering occurs on the Event Dispatch Thread (EDT). When background monitor threads detect an alert, they do not update Swing components directly. Instead, they use `SwingUtilities.invokeLater(() -> alertsArea.append(...))` to place the update task safely onto the EDT event queue."*

---

### 👨‍💻 Student 5: Mritunjay Gupta (0251CSAI132) - Exceptions, Collections & Test Framework

#### What Mritunjay Did:
* **Primary Role:** Custom Exception Hierarchy, Collections Data Structures, and Standalone Test Suite.
* **Specific Source Files Created/Owned:**
  * `src/com/mits/exception/InsufficientStockException.java`
  * `src/com/mits/exception/ExpiredMedicineException.java`
  * `src/com/mits/exception/MedicineNotFoundException.java`
  * `src/com/mits/exception/DuplicateMedicineException.java`
  * `src/com/mits/exception/InvalidInputException.java`
  * `src/com/mits/exception/AuthenticationException.java`
  * `src/com/mits/exception/InventoryOperationException.java` (Unchecked runtime)
  * `src/com/mits/exception/ExceptionDemo.java`
  * `src/com/mits/model/Inventory.java` (HashMap, TreeSet, LinkedList, PriorityQueue)
  * `src/com/mits/util/Repository.java` (Generics), `StringProcessor.java`, `LambdaProcessor.java`
  * `src/com/mits/app/TestRunner.java` (34 test suites, 35 assertions, 0 external libraries)

#### Key Achievements:
* Designed the complete custom exception hierarchy and built `ExceptionDemo` showing try-catch, multi-catch, nested try, and finally blocks.
* Configured the optimal collections inside `Inventory.java` (`HashMap` for $O(1)$ lookups, `TreeSet` for expiry date sorting, `PriorityQueue` for reorder triage).
* Built the pure Java automated test harness (`TestRunner.java`) achieving **34/34 passing tests (100% pass rate)**.

#### Questions Teacher Will Ask Mritunjay:
1. *Q: "Mritunjay, explain the difference between checked and unchecked exceptions in your module?"*
   * **Answer:** *"Ma'am, checked exceptions (like `InsufficientStockException` and `ExpiredMedicineException`) inherit from `Exception`. They represent recoverable business scenarios that the calling code MUST anticipate and handle (e.g. prompt the pharmacist to order more stock). Unchecked exceptions (like `InventoryOperationException`) inherit from `RuntimeException` and represent unrecoverable technical failures like disk pipeline crashes."*
2. *Q: "Why did you use both a HashMap and a TreeSet for medicines in `Inventory.java`?"*
   * **Answer:** *"A `HashMap` gives us $O(1)$ instantaneous retrieval when a user searches for a medicine by its unique code (`MED-001`). However, a HashMap has no ordering. To display medicines sorted by which one expires first, we use a `TreeSet` backed by a custom Comparator that sorts by earliest batch expiry."*

---

## 6. 🔥 Teacher's Trap Questions & Exact Answers to Speak in Viva

### Trap Question 1: *"Did you use external libraries like Lombok, Maven, Gson, or SQLite?"*
* **Say this:** *"No sir! Our project is built on **Pure JDK 17 Standard Library**. We deliberately used pure `java.net` for sockets, pure `javax.swing` and `java.awt` for the GUI, pure `java.io` for serialization/CSV, and core Java Collections. This demonstrates fundamental mastery of Object-Oriented Java without relying on third-party frameworks."*

### Trap Question 2: *"Why did you use `synchronized` keyword instead of just letting the threads run?"*
* **Say this:** *"Ma'am, if two threads invoke `issueStock()` simultaneously on the same medicine without synchronization, they read the same initial quantity and overwrite each other's decrements — known as a Race Condition or Lost Update anomaly. The `synchronized` keyword acquires the object's intrinsic monitor lock, ensuring mutual exclusion so that stock deduction is strictly atomic."*

### Trap Question 3: *"Can an abstract class have constructors?"*
* **Say this:** *"Yes! `Medicine.java` has both a default constructor and a parameterized constructor. Although you cannot instantiate an abstract class directly using `new Medicine()`, its constructors are invoked by subclass constructors using `super(...)` to initialize inherited fields like `code`, `name`, and `unitPrice`."*

### Trap Question 4: *"Why do you have both String, StringBuilder, and StringBuffer in your project?"*
* **Say this:** *"Sir, `String` is immutable and safe for identifiers. `StringBuilder` is mutable and fast for assembling large text reports in a single thread. `StringBuffer` is synchronized and thread-safe, which we specifically used for our shared concurrent `AUDIT_LOG_BUFFER` so multiple threads writing audit entries do not corrupt the log."*

---

## 7. How to Run and Demo the Project

### Step 1: Open Terminal and Compile
```batch
cd c:\Users\devis\Downloads\java
build.bat
```
*(Result: "Build completed successfully! Output directory: .\out")*

### Step 2: Run the Automated Test Suite (To impress the teacher first!)
```batch
java -cp out com.mits.app.TestRunner
```
*(Result: "TEST RESULTS: 34 PASSED, 0 FAILED (Total: 34)")*

### Step 3: Run the Swing GUI Application
```batch
run_gui.bat
```
1. Click the **"Quick Fill (Admin)"** button at the bottom of the login screen.
2. Click **"Sign In"**.
3. **Show Dashboard Tab:** Point out the 5 KPI Cards (Total Meds: 28, Low Stock, Expired), the Category Bar Chart, and the Live Alerts scroll pane.
4. **Show Search Tab:** Type `"Amox"` in the search box, click Search. Show how near-expiry and expired rows are color-coded (Amber & Red).
5. **Show Stock Operations Tab:** Issue 100 units of `MED-001`. Show the success popup. Then issue 100 more to demonstrate `InsufficientStockException` handling!
6. **Show Backup & Tools Tab:** Click **"Run Concurrency Demo"** to show 6 threads executing live on screen with 0 race conditions!

---

### 📄 Summary Table for Quick Reference

| Team Member | Roll No. | Sub-System Owned | Core Java Tech Demonstrated |
|---|---|---|---|
| **Devvrat Tripathi** | 0251CSAI231 | Service Layer, CLI, Jagged Arrays | Facade Pattern, Jagged Arrays, Overloading, CLI args |
| **Manas Srivastava** | 0251CSAI144 | Domain Models, FEFO Batch Logic | Abstraction, Inheritance, Interfaces, Composition |
| **Pranjal Sharma** | 0251CSAI141 | Multithreading & Monitors | Daemon Threads, Runnable, Synchronized, wait/notify |
| **Hardik Kumar** | 0251CSAI195 | Swing GUI, Sockets, I/O Persistence | Java 2D Graphics, TCP Sockets, Byte/Char Streams |
| **Mritunjay Gupta** | 0251CSAI132 | Exceptions, Collections, TestRunner | Checked Exceptions, Generics, Collections, Unit Testing |

---
*Created for Group 24 | Department of Computer Science & Engineering (AI) | Academic Year 2026*
