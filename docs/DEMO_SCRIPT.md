# Intelligent Medical Inventory Tracking System (MITS)
## 5-Minute Live Examination Demo Script & Viva Presentation Runbook

> **Course**: B.Tech CSE-AI — Object Oriented Techniques using Java  
> **Evaluation**: Capstone PBL Assessment 2  
> **Project**: Intelligent Medical Inventory Tracking System (MITS)  
> **Target Duration**: Exactly 5 Minutes (300 Seconds)  
> **Base Directory**: `c:\Users\devis\Downloads\java`  
> **Primary Interfaces**: Swing Desktop GUI (`run_gui.bat`), Multi-threaded TCP Server (`run_server.bat`), Headless CLI (`ConsoleApp`), Standalone Harness (`TestRunner`)

---

## 1. Demo Timeline & Pacing Overview

| Time Window | Step # | Action / Feature Highlight | Primary Syllabus Unit |
|---|---|---|---|
| **0:00 - 0:25** | **Step 1 & 2** | Start TCP Server (`run_server.bat`) & Launch GUI (`run_gui.bat`) | **Unit 4 & 5**: Sockets, Threads, GUI EDT |
| **0:25 - 0:45** | **Step 3** | Authenticate as `admin` / `admin123` via Login Dialog | **Unit 2, 3 & 5**: RBAC, Custom Exceptions, Swing |
| **0:45 - 1:15** | **Step 4** | Dashboard KPI Cards, Bar Chart & Background Live Alerts | **Unit 4 & 5**: AWT Graphics2D, Daemon Threads |
| **1:15 - 1:45** | **Step 5** | Add New Medicine `MED-029 Metoclopramide 10mg` via CardLayout | **Unit 1 & 2**: Inheritance, Regex, Layouts |
| **1:45 - 2:20** | **Step 6 & 7** | Issue Stock for `MED-001`, Trigger Alert & Verify Insufficient Exception | **Unit 3 & 4**: Synchronized Methods, Checked Exceptions |
| **2:20 - 2:50** | **Step 8** | Search Inventory (`Amox`) & Explain Custom Table Cell Renderer | **Unit 1 & 5**: JTable, String Matching, TableCellRenderer |
| **2:50 - 3:25** | **Step 9** | Async Full Inventory Report Generation & File Export | **Unit 3 & 4**: Worker Threads, JProgressBar, File I/O |
| **3:25 - 4:00** | **Step 10** | TCP Remote Socket Query (`SEARCH Paracetamol` over port 5000) | **Unit 4 & 5**: TCP Sockets, ClientHandler Protocol |
| **4:00 - 4:35** | **Step 11** | Backup Serialization, Concurrency Stress Test & Exception Test | **Unit 3 & 4**: Serialization, Race Conditions, Multi-catch |
| **4:35 - 4:50** | **Step 12** | Headless CLI Demo (`ConsoleApp --help` & `--report`) | **Unit 1**: CLI Arguments, Stream Formatting |
| **4:50 - 5:00** | **Step 13** | Standalone Automated Test Runner (34 PASSED, 0 FAILED) | **Unit 1 - 5**: Full Integration Verification |

---

## 2. Pre-Demo Checklist (30 Seconds Before Calling the Examiner)

Before inviting the examiner to view your screen, ensure the following environment state:

1. **Working Directory**: Ensure you are in `c:\Users\devis\Downloads\java`.
2. **Pre-compiled Classes**: Run `build.bat` once to ensure all classes exist in `out/`.
3. **Open 3 Terminal Windows (PowerShell or Command Prompt)**:
   - **Terminal 1**: Ready to start the server (`run_server.bat`).
   - **Terminal 2**: Ready to launch the Swing GUI (`run_gui.bat`).
   - **Terminal 3**: Ready for CLI commands and TestRunner.
4. **Resolution & Audio**: Set display scaling so Swing fonts and console outputs are crisp and readable.

---

## 3. Step-by-Step Live Demo Script

---

### Step 1: Start the TCP Inventory Server
* **Duration**: 0:00 - 0:12 (12 seconds)
* **Location**: Terminal 1

#### 🖱️ What to Do
1. Switch to **Terminal 1**.
2. Type and run:
   ```cmd
   .\run_server.bat
   ```
3. Observe terminal confirmation:
   ```
   Starting MITS Inventory Server...
   [2026-10-09 ...] InventoryServer started on port: 5000
   ```

#### 🗣️ What to Say to the Examiner
> "Good morning, respected examiner. I am demonstrating the Intelligent Medical Inventory Tracking System (MITS). To demonstrate distributed enterprise architecture, I first start our background TCP Inventory Server on port 5000. This server uses `ServerSocket` and an `ExecutorService` cached thread pool to asynchronously handle remote hospital wards and branches without blocking."

#### 🧠 Concept Demonstrated
* **Unit 5 (Networking)**: TCP Socket Programming (`ServerSocket`, `Socket`).
* **Unit 4 (Multithreading)**: Concurrent client session handling using thread pool executors.

#### 🛡️ Examiner Defense / Quick Q&A
* **Q**: *Why use a cached thread pool instead of creating raw threads?*
* **A**: *Cached thread pools reuse idle threads, mitigating the overhead of frequent thread instantiation during burst socket queries from hospital wards.*

---

### Step 2: Launch the Swing Desktop GUI
* **Duration**: 0:12 - 0:25 (13 seconds)
* **Location**: Terminal 2

#### 🖱️ What to Do
1. Switch to **Terminal 2**.
2. Type and run:
   ```cmd
   .\run_gui.bat
   ```
3. The graphical **Sign In** window (`LoginFrame`) appears centered on screen.

#### 🗣️ What to Say to the Examiner
> "Next, in a separate terminal, I launch the Swing desktop client. The application bootstraps the `InventoryService`, verifies our binary serialized persistence store in `./data/inventory.ser`, and spawns two background daemon threads: `ExpiryMonitorThread` and `AutoBackupThread`."

#### 🧠 Concept Demonstrated
* **Unit 5 (GUI)**: Swing Event Dispatch Thread (EDT) safety using `SwingUtilities.invokeLater()`.
* **Unit 4 (Concurrency)**: Daemon background thread initialization (`setDaemon(true)`).
* **Unit 3 (I/O)**: File existence validation and persistence bootstrapping.

---

### Step 3: Login as Administrator
* **Duration**: 0:25 - 0:45 (20 seconds)
* **Location**: Login Dialog Window

#### 🖱️ What to Do
1. In the login dialog:
   - Username is pre-filled as `admin` (or click the quick-demo button **"Admin"**).
   - Password is pre-filled as `admin123`.
2. Click **"Sign In"** (or press `ENTER`).
3. The Login dialog disposes smoothly and the main **DashboardFrame** window opens.

#### 🗣️ What to Say to the Examiner
> "I am authenticating as the System Administrator using role-based credentials. MITS implements strict Role-Based Access Control (RBAC) supporting Admin, Pharmacist, and Doctor roles. Logging in as Admin grants full administrative privileges, unlocking data entry, stock operations, and system maintenance tabs."

#### 🧠 Concept Demonstrated
* **Unit 2 (OOP & Architecture)**: Encapsulation and Role enum mapping (`Role.ADMIN`, `Role.PHARMACIST`, `Role.DOCTOR`).
* **Unit 3 (Exception Handling)**: Authentication boundary protected by custom checked `AuthenticationException`.
* **Unit 5 (GUI)**: Clean transition from `JFrame` dispose to main dashboard view.

---

### Step 4: Dashboard Tab — Summary Cards, Category Bar Chart & Live Alerts
* **Duration**: 0:45 - 1:15 (30 seconds)
* **Location**: Tab 1 ("Dashboard")

#### 🖱️ What to Do
1. Direct the examiner's attention to the 5 top status cards:
   - **Total Medicines**: `28` (registered SKUs).
   - **Stock Value**: Total inventory valuation formatted in Indian Rupees (e.g., `₹146,525.00`).
   - **Low Stock**: Count of medicines whose total stock is below their reorder threshold.
   - **Expiring Soon**: Count of batches expiring within 30 days.
   - **Expired**: Count of expired batches quarantined for safety.
2. Point out the bottom-left **Category Distribution Bar Chart**.
3. Point out the bottom-right **Live Alerts Panel** displaying background alerts.

#### 🗣️ What to Say to the Examiner
> "Here on the main Dashboard, the top KPI cards aggregate real-time inventory statistics across all 28 seed medicines. In the lower half: on the left is a custom AWT `Graphics2D` bar chart that dynamically renders stock counts across Tablet, Antibiotic, Syrup, Injection, and Ointment categories. On the right is our Live Alerts panel. This panel receives asynchronous alerts pushed from our daemon monitor threads using an Observer callback pattern, without freezing the GUI."

#### 🧠 Concept Demonstrated
* **Unit 5 (AWT & 2D Graphics)**: Custom painting in `CategoryBarChartPanel` overriding `paintComponent(Graphics g)` with `Graphics2D` anti-aliasing and gradient rendering.
* **Unit 5 (Layout Management)**: Composite layouts (`BorderLayout`, `GridLayout(1, 5)`, `EmptyBorder`).
* **Unit 4 (Concurrency)**: Non-blocking inter-thread UI updates dispatched safely via `SwingUtilities.invokeLater()`.

---

### Step 5: Add/Edit Medicine Tab — Polymorphic Medicine Registration
* **Duration**: 1:15 - 1:45 (30 seconds)
* **Location**: Tab 2 ("Add/Edit Medicine")

#### 🖱️ What to Do
1. Click the **"Add/Edit Medicine"** tab.
2. Fill out the form fields with the following details:
   - **Type**: Select `Tablet` (dropdown).
   - **Code (MED-XXX)**: Type `MED-029`
   - **Name**: Type `Metoclopramide 10mg`
   - **Description**: Type `Antiemetic gastroprokinetic`
   - **Unit Price (₹)**: Set to `35.0`
   - **Reorder Level**: Set to `30`
   - **Supplier**: Select `SUP-001 - Apollo Health Supplies`
   - **Type-Specific Fields (CardLayout)**: Strip Size `10`, leave Chewable unchecked.
3. Click the **"Add Medicine"** button.
4. A popup dialog appears: `"Medicine added successfully!"`. Click **OK**.

#### 🗣️ What to Say to the Examiner
> "In the Add/Edit Medicine screen, I will register a new formulation: MED-029, Metoclopramide 10mg Tablet, priced at ₹35 with reorder threshold 30 and strip size 10. Notice that selecting 'Tablet' dynamically updates the type-specific form fields using `CardLayout`. When I click Add, MITS validates the code format using regular expressions, instantiates a `Tablet` subclass that polymorphically extends abstract class `Medicine`, and stores it into our inventory repository."

#### 🧠 Concept Demonstrated
* **Unit 2 (Inheritance & Polymorphism)**: Subclass `Tablet` extending abstract class `Medicine`, constructor chaining using `super(...)`.
* **Unit 1 (Java Basics)**: String manipulation and regular expression validation (`MED-\\d{3,4}`).
* **Unit 5 (Swing Layouts)**: Dynamic container switching using `CardLayout`.
* **Unit 3 (Exception Handling)**: Data integrity guarded by custom `InvalidInputException` and `DuplicateMedicineException`.

---

### Step 6: Stock Operations Tab — Dispensation & Low-Stock Trigger
* **Duration**: 1:45 - 2:20 (35 seconds)
* **Location**: Tab 3 ("Stock Operations")

#### 🖱️ What to Do
1. Click the **"Stock Operations"** tab.
2. Focus on the upper **Issue Stock (Dispense)** panel:
   - **Medicine Code**: Type `MED-001` (Paracetamol 650mg, initial stock = 120, reorder = 50).
   - **Quantity**: Set spinner/field to `100`.
   - **Remarks**: Type `Ward 4 Emergency`.
3. Click **"Issue Stock"**.
   - Confirmation dialog pops up: `"Issued 100 units of Paracetamol 650mg | Remaining: 20"`. Click **OK**.
4. Now, attempt to issue **`100`** more units of `MED-001`:
   - Keep Medicine Code `MED-001`, Quantity `100`.
   - Click **"Issue Stock"** again.
   - An error dialog appears: `"Insufficient stock for Paracetamol (MED-001). Requested: 100, Available: 20"`. Click **OK**.

#### 🗣️ What to Say to the Examiner
> "Now I navigate to Stock Operations. Paracetamol MED-001 originally has 120 units in stock with a reorder threshold of 50. I issue 100 units. The operation executes atomically inside a `synchronized` method, deducting inventory via First-In-First-Out (FIFO) batch scheduling. Stock drops to 20 units. Because 20 is below the threshold of 50, a low-stock alert is immediately dispatched. Furthermore, when I attempt to issue another 100 units, the system catches the deficit and raises our custom checked `InsufficientStockException`, preventing negative inventory."

#### 🧠 Concept Demonstrated
* **Unit 4 (Synchronization)**: Thread-safe stock modification preventing race conditions via `synchronized issueStock(...)`.
* **Unit 3 (Custom Checked Exceptions)**: Throwing and catching `InsufficientStockException` with state payload (`requested` vs `available`).
* **Unit 2 (Data Structures & FIFO)**: Batch deduction logic iterating batches in chronological order.

---

### Step 7: Dashboard Tab — Verification of Live Alert & KPI Increment
* **Duration**: 2:20 - 2:35 (15 seconds)
* **Location**: Tab 1 ("Dashboard")

#### 🖱️ What to Do
1. Click back to **Tab 1 ("Dashboard")**.
2. Point out:
   - The **Low Stock** counter card has incremented by 1.
   - The **Stock Value** card has updated to reflect the 100 dispensed units.
   - In the **Live Alerts Panel**, point to the newly generated alert line:
     ```
     [WARNING] LOW_STOCK: MED-001 - Paracetamol 650mg | Stock dropped to 20 units (Reorder Threshold: 50)
     ```

#### 🗣️ What to Say to the Examiner
> "Switching back to the Dashboard, we can verify that the Low Stock counter has incremented, the valuation recalculated, and the Live Alerts panel reflects the new warning for MED-001. This demonstrates decoupling between transactional business logic and the Swing UI using an event-driven notification listener."

#### 🧠 Concept Demonstrated
* **Unit 4 (Inter-Thread Communication)**: Alert notification dispatch from business services to UI listeners.
* **Unit 5 (Swing Components)**: Dynamic text appending and automatic scroll caret repositioning in `JTextArea`.

---

### Step 8: Search Tab — Query Filtering & Custom Table Cell Renderer
* **Duration**: 2:35 - 3:00 (25 seconds)
* **Location**: Tab 4 ("Search")

#### 🖱️ What to Do
1. Click the **"Search"** tab.
2. In the **Query** field, type: `Amox`.
3. Click **"Search"** (or press `ENTER`).
   - The table filters to show `Amoxicillin 500mg (MED-009)`.
4. Now clear the query box and click **"Search"** again to show all medicines.
5. Point out the distinct row color coding:
   - **Light Red rows**: Expired medicines (e.g., `MED-103` Cephalexin 500mg, expired batch).
   - **Soft Amber / Orange rows**: Near-expiry medicines within 30 days (e.g., `MED-102` Doxycycline 100mg, `MED-007` Vitamin C).
   - **Soft Pink rows**: Low-stock medicines (e.g., `MED-001` Paracetamol, `MED-003` Cetirizine, `MED-100` Azithromycin).

#### 🗣️ What to Say to the Examiner
> "In the Search tab, searching for 'Amox' demonstrates case-insensitive String matching across medicine names and codes. When viewing all records, notice the color-coded rows managed by our custom `MedicineTableCellRenderer`: Soft Red denotes Expired drugs that must be quarantined immediately; Soft Amber warns of items nearing expiry within 30 days; and Soft Pink highlights low stock needing reorder. Column headers also support interactive sorting via `TableRowSorter`."

#### 🧠 Concept Demonstrated
* **Unit 5 (JTable Customization)**: Extending `DefaultTableCellRenderer` to alter row background and foreground based on domain entity rules (`daysToExpiry()` and `needsReorder()`).
* **Unit 1 (String Manipulation)**: Case-insensitive search using `toLowerCase().contains()`.
* **Unit 2 (Collections Sorting)**: Dynamic multi-column sorting using `TableRowSorter<MedicineTableModel>`.

---

### Step 9: Reports Tab — Background Worker Thread & File Export
* **Duration**: 3:00 - 3:30 (30 seconds)
* **Location**: Tab 5 ("Reports")

#### 🖱️ What to Do
1. Click the **"Reports"** tab.
2. In the **Report** combo box, select `FULL_INVENTORY`.
3. Click **"Generate Report"**.
4. Observe the **JProgressBar** animating smoothly (20% -> 50% -> 100%) while the report generates in the background.
5. Once populated in the text area, click **"Export to File"**.
6. A `JFileChooser` dialog opens:
   - Type filename `inventory_report.txt` and click **Save**.
7. Success dialog pops up: `"Report exported successfully!"`. Click **OK**.

#### 🗣️ What to Say to the Examiner
> "In the Reports tab, I select 'Full Inventory' and click 'Generate Report'. Notice that the progress bar advances smoothly without freezing the UI. This is because report synthesis runs asynchronously on a dedicated worker thread extending `Thread` (`ReportGeneratorThread`). Once generated, clicking 'Export to File' activates a `JFileChooser` and writes the audit report to disk using character stream I/O."

#### 🧠 Concept Demonstrated
* **Unit 4 (Worker Threads)**: Multithreaded execution using `ReportGeneratorThread` preventing Swing Event Dispatch Thread (EDT) freezes.
* **Unit 5 (Swing Controls)**: `JProgressBar` progress reporting and `JFileChooser` system file dialog.
* **Unit 3 (File I/O)**: Character stream writing using `PrintWriter` and `FileWriter`.

---

### Step 10: Remote Server Tab — Live TCP Socket Query
* **Duration**: 3:30 - 4:00 (30 seconds)
* **Location**: Tab 6 ("Remote Server")

#### 🖱️ What to Do
1. Click the **"Remote Server"** tab.
2. Verify Host is `localhost` and Port is `5000`.
3. Click **"Connect"**.
   - The response area receives the server banner:
     ```
     200 MITS Inventory Server Ready. Available commands: LOGIN, SEARCH, STOCK, ISSUE, ALERTS, QUIT
     ```
4. In the **"Cmd:"** text field, type:
   ```
   SEARCH Paracetamol
   ```
5. Click **"Send"**.
   - The server response appears in the console area:
     ```
     > SEARCH Paracetamol
       COUNT 2
       DATA MED-001 | Paracetamol 650mg | TABLET | Stock: 20 | ₹30.00
       DATA MED-105 | Paracetamol Paediatric 120mg | SYRUP | Stock: 60 | ₹45.00
       OK END_SEARCH
     ```

#### 🗣️ What to Say to the Examiner
> "Under the Remote Server tab, our GUI acts as a TCP client communicating over network sockets with our InventoryServer running on port 5000. When I connect, a two-way Socket stream is established. Sending the protocol command 'SEARCH Paracetamol' queries the central synchronized inventory across the network and returns the current stock levels in real time—confirming that MED-001 Paracetamol now reflects 20 units after our earlier dispensation."

#### 🧠 Concept Demonstrated
* **Unit 5 (Socket Networking)**: TCP client-side socket handling via `Socket`, `BufferedReader`, and `PrintWriter`.
* **Unit 4 (Network Concurrency)**: Inter-process TCP communication querying synchronized inventory state.

---

### Step 11: Backup & Tools Tab — Serialization, Concurrency Stress Test & Exception Suite
* **Duration**: 4:00 - 4:35 (35 seconds)
* **Location**: Tab 7 ("Backup & Tools")

#### 🖱️ What to Do
1. Click the **"Backup & Tools"** tab.
2. Click **"Backup Now"**:
   - Output area displays: `Backup created: data\backup\backup_<timestamp>.ser`
3. Click **"Run Concurrency Demo"**:
   - Output area populates with the multithreaded test log:
     ```
     =================================================
          MITS UNIT 4: MULTITHREADING CONCURRENCY DEMO 
     =================================================
     Initial Stock: 1000 units
     Launching 6 concurrent threads...
     Each thread issues 15 batches of 10 units (150 units/thread)
     Expected total issued: 900 units
     Expected final stock: 100 units
     ...
     Final Stock in Medicine: 100 units
     >>> [PASSED] Concurrency test succeeded without race conditions!
     >>> Synchronized method guaranteed atomicity across all threads.
     ```
4. Click **"Run Exception Demo"**:
   - Output area displays the complete Unit 3 exception harness verifying:
     - Basic try-catch with throw & throws (`InvalidInputException`)
     - Multiple catch blocks (`InsufficientStockException`, `ExpiredMedicineException`, `MedicineNotFoundException`, `ArithmeticException`)
     - Nested try-catch (`DuplicateMedicineException`)
     - Guaranteed `finally` block resource cleanup.

#### 🗣️ What to Say to the Examiner
> "In the Backup & Tools tab, three critical core Java capabilities are demonstrated:
> First, 'Backup Now' serializes the complete live object graph into a timestamped binary file using Java Object Serialization.
> Second, 'Run Concurrency Demo' spawns 6 concurrent worker threads with varying thread priorities, simulating simultaneous high-volume dispensations totaling 900 units from an initial 1000 units. Because our stock methods are synchronized, the final stock is exactly 100 units with zero race conditions.
> Third, 'Run Exception Demo' demonstrates our comprehensive Unit 3 exception architecture, executing multi-catch blocks, nested try-catch blocks, and guaranteed resource cleanup using finally."

#### 🧠 Concept Demonstrated
* **Unit 3 (Object Serialization)**: Object persistence via `ObjectOutputStream` and `Serializable` interface.
* **Unit 4 (Multithreading & Race Conditions)**: Race condition prevention using `synchronized` methods, thread priorities (`setPriority`), and `Thread.join()`.
* **Unit 3 (Exception Handling Hierarchy)**: Custom checked exceptions, unchecked exceptions, multi-catch syntax, nested try-catches, and `finally` execution guarantees.

---

### Step 12: Console Application — CLI Arguments & Headless Reporting
* **Duration**: 4:35 - 4:50 (15 seconds)
* **Location**: Terminal 3

#### 🖱️ What to Do
1. Switch to **Terminal 3**.
2. Run the help argument:
   ```cmd
   java -cp out com.mits.app.ConsoleApp --help
   ```
   - Shows CLI usage flags: `--seed`, `--report`, `--user <name>`, `--help`.
3. Run the headless report argument:
   ```cmd
   java -cp out com.mits.app.ConsoleApp --report
   ```
   - Prints the full ASCII-formatted inventory valuation report directly into the terminal.

#### 🗣️ What to Say to the Examiner
> "Moving to the command line, MITS provides a headless interface via ConsoleApp. Demonstrating Unit 1 command-line argument processing: running with '--help' displays CLI usage options, and running with '--report' instantly compiles a complete inventory valuation report directly to standard output. This allows hospital IT systems to schedule headless batch jobs and nightly automated audits."

#### 🧠 Concept Demonstrated
* **Unit 1 (Command-Line Arguments & Control Structures)**: Processing `String[] args`, switch-case statement handling, and formatted text output via `System.out.printf`.
* **Unit 2 (Code Reusability)**: Business logic in `ReportService` shared seamlessly across GUI and CLI clients.

---

### Step 13: Standalone Automated Test Runner — 34 Automated Tests
* **Duration**: 4:50 - 5:00 (10 seconds)
* **Location**: Terminal 3

#### 🖱️ What to Do
1. In **Terminal 3**, execute the automated test runner:
   ```cmd
   java -cp out com.mits.app.TestRunner
   ```
2. Scroll to the bottom of the execution output and highlight the clean test verdict:
   ```
   =================================================
     TEST RESULTS: 34 PASSED, 0 FAILED (Total: 34)
   =================================================
   ```

#### 🗣️ What to Say to the Examiner
> "Finally, I execute our standalone automated TestRunner. Without relying on third-party frameworks like JUnit, our custom harness executes 34 automated unit and integration tests covering object models, multi-level inheritance, all 6 custom checked exceptions, generic repositories, jagged consumption arrays, multithreading concurrency stress tests, and TCP socket communication. As you can see: 34 PASSED, 0 FAILED—100% test passing rate confirming complete syllabus mastery."

#### 🧠 Concept Demonstrated
* **Unit 1 to 5 Comprehensive Synthesis**: Complete end-to-end regression validation of OOP, data structures, multithreading, socket networking, and exception handling.

---

## 4. Quick Syllabus Mapping Reference Card

Use this quick-reference table during the viva if the examiner asks to see code for a specific unit:

| Unit | Syllabus Requirement | Where Demonstrated in Demo | Key Class / Method |
|---|---|---|---|
| **Unit 1** | Command-Line Arguments, Control Flow, Strings | Step 12 (ConsoleApp args) & Step 8 (Search) | [`ConsoleApp.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/app/ConsoleApp.java#L25-L65), [`StringProcessor.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/StringProcessor.java) |
| **Unit 1** | Jagged Arrays | Step 9 (Reports -> Jagged Consumption) | [`InventoryService.java:getMonthlyConsumptionJaggedArray()`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java) |
| **Unit 2** | Multilevel & Hierarchical Inheritance | Step 5 (Add Tablet & Antibiotic) | [`Medicine.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) -> [`Tablet.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java) -> [`Antibiotic.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Antibiotic.java) |
| **Unit 2** | Generics & Collections | Step 4 & Step 13 (Repository, Pair, Map, TreeSet) | [`Repository.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/Repository.java), [`Pair.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/Pair.java), [`Inventory.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Inventory.java) |
| **Unit 3** | Custom Checked & Unchecked Exceptions | Step 6 (InsufficientStock) & Step 11 (ExceptionDemo) | [`InsufficientStockException.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/InsufficientStockException.java), [`ExceptionDemo.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/ExceptionDemo.java) |
| **Unit 3** | File I/O & Binary Serialization | Step 9 (Export to File) & Step 11 (Backup Now) | [`FilePersistenceManager.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/io/FilePersistenceManager.java), [`ReportService.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/report/ReportService.java) |
| **Unit 4** | Multithreading & Synchronization | Step 6 (Synchronized Stock) & Step 11 (ConcurrencyDemo) | [`InventoryService.java:issueStock()`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java#L213), [`ConcurrencyDemo.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ConcurrencyDemo.java) |
| **Unit 4** | Daemon Threads & Worker Threads | Step 4 (Daemon Monitors) & Step 9 (Report Worker) | [`ExpiryMonitorThread.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java), [`ReportGeneratorThread.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ReportGeneratorThread.java) |
| **Unit 5** | Swing Components, Layouts & Renderers | Step 3, 4, 5, 8 (Dashboard, Cards, Renderer) | [`DashboardFrame.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/gui/DashboardFrame.java), [`MedicineTableCellRenderer.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/gui/MedicineTableCellRenderer.java) |
| **Unit 5** | Custom 2D Graphics Painting | Step 4 (Category Bar Chart) | [`CategoryBarChartPanel.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/gui/CategoryBarChartPanel.java#L45-L110) |
| **Unit 5** | TCP Socket Programming | Step 1 (Server Start) & Step 10 (Remote Client Tab) | [`InventoryServer.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/network/InventoryServer.java), [`InventoryClient.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/network/InventoryClient.java) |

---

## 5. Emergency Troubleshooting & Fallback Cheat Sheet

| Potential Glitch during Demo | Immediate Solution |
|---|---|
| **Port 5000 Already in Use** | Run server on custom port: `run_server.bat 8080`, then set Port to `8080` in Remote Server tab. |
| **Accidentally Closed GUI** | Relaunch instantly in Terminal 2 with `.\run_gui.bat`. Seed data and modifications persist in `./data/inventory.ser`. |
| **Want to Reset to Clean 28 Seed Items** | Delete the `data` folder or run `java -cp out com.mits.app.ConsoleApp --seed`. System regenerates the 28 standard seed items automatically. |
| **Examiner Asks to See Test Details** | Open and show [`TestRunner.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/app/TestRunner.java) lines 45-190 showing each unit assertion. |
