# Intelligent Medical Inventory Tracking System (MITS)
## Comprehensive User Manual & Operations Guide

**Project:** Intelligent Medical Inventory Tracking System (MITS)  
**Academic Context:** B.Tech CSE-AI Capstone PBL Assessment 2  
**Version:** 2.0  
**Runtime Platform:** Java Standard Edition (JDK 17+)  

---

## Table of Contents

1. [Getting Started](#1-getting-started)
   - [System Prerequisites](#11-system-prerequisites)
   - [Directory Structure](#12-directory-structure)
   - [Compilation & Build](#13-compilation--build)
   - [Running the Applications](#14-running-the-applications)
2. [Login Screen](#2-login-screen)
   - [Authentication & Security](#21-authentication--security)
   - [Role-Based Access Control (RBAC)](#22-role-based-access-control-rbac)
   - [Pre-Configured User Credentials](#23-pre-configured-user-credentials)
   - [Quick Demo Fill Buttons](#24-quick-demo-fill-buttons)
3. [Dashboard](#3-dashboard)
   - [Executive Status Cards](#31-executive-status-cards)
   - [Category Stock Distribution Bar Chart](#32-category-stock-distribution-bar-chart)
   - [Live Alerts & Real-Time Monitoring](#33-live-alerts--real-time-monitoring)
4. [Add/Edit Medicine](#4-addedit-medicine)
   - [General Form Fields](#41-general-form-fields)
   - [Category-Specific Attributes (CardLayout)](#42-category-specific-attributes-cardlayout)
   - [Input Validation & Business Rules](#43-input-validation--business-rules)
   - [Action Controls (Add, Update, Delete, Clear)](#44-action-controls-add-update-delete-clear)
5. [Stock Operations](#5-stock-operations)
   - [Stock Issue & Dispensing (FEFO Policy)](#51-stock-issue--dispensing-fefo-policy)
   - [Restocking & Multi-Batch Inward](#52-restocking--multi-batch-inward)
   - [Safety Interlocks & Exception Protection](#53-safety-interlocks--exception-protection)
6. [Search & Catalog](#6-search--catalog)
   - [Multi-Parameter Search Query](#61-multi-parameter-search-query)
   - [Category Filtering](#62-category-filtering)
   - [Interactive Table Sorting](#63-interactive-table-sorting)
   - [Visual Health Color-Coding](#64-visual-health-color-coding)
7. [Reports Engine](#7-reports-engine)
   - [Available Analytical Reports](#71-available-analytical-reports)
   - [Asynchronous Background Generation](#72-asynchronous-background-generation)
   - [File Export Operations](#73-file-export-operations)
8. [Remote Server Client](#8-remote-server-client)
   - [Network Host & Port Configuration](#81-network-host--port-configuration)
   - [Establishing Connection](#82-establishing-connection)
   - [Issuing Commands & Viewing Responses](#83-issuing-commands--viewing-responses)
9. [Backup & Diagnostics Tools](#9-backup--diagnostics-tools)
   - [Immediate State Backup](#91-immediate-state-backup)
   - [Snapshot Recovery & Restoration](#92-snapshot-recovery--restoration)
   - [Concurrency Demonstration](#93-concurrency-demonstration)
   - [Exception Handling Architecture Demo](#94-exception-handling-architecture-demo)
10. [Console Application](#10-console-application)
    - [Command-Line Arguments & Flags](#101-command-line-arguments--flags)
    - [Interactive Console Menu Reference](#102-interactive-console-menu-reference)
    - [Search Overloading in Console](#103-search-overloading-in-console)
11. [TCP Server Daemon](#11-tcp-server-daemon)
    - [Server Architecture & Initialization](#111-server-architecture--initialization)
    - [TCP Protocol Commands & Wire Format](#112-tcp-protocol-commands--wire-format)
    - [External Client Integration Example](#113-external-client-integration-example)

---

## 1. Getting Started

### 1.1 System Prerequisites

Before running the Intelligent Medical Inventory Tracking System (MITS), ensure your workstation meets the following runtime requirements:

- **Operating System:** Windows 10/11, macOS 12+, or modern Linux distribution (Ubuntu 20.04+, RHEL 8+).
- **Java Development Kit (JDK):** JDK 17 or higher (Java 21 LTS recommended). Verify using:
  ```powershell
  java -version
  javac -version
  ```
- **Display Environment (for GUI):** Standard display resolution (1280x800 or higher recommended). A graphical desktop environment is required for Swing GUI applications.
- **Port Availability:** TCP port `5000` (or user-defined port) available for remote networking operations.

### 1.2 Directory Structure

The project follows a clean modular package structure under `src/com/mits`:

```
c:\Users\devis\Downloads\java\
├── bin/                          # Compiled bytecode (.class files)
├── data/                         # Persistent application data
│   ├── backup/                   # Automated and manual binary backups (.ser)
│   ├── audit.log                 # System event audit trailing
│   ├── inventory.ser             # Serialized inventory object graph
│   ├── medicines.csv             # Exported medicine catalog
│   ├── suppliers.csv             # Supplier directory
│   └── users.csv                 # Authorized user profiles
├── docs/                         # System documentation and manuals
│   ├── USER_MANUAL.md            # Operator and user manual
│   └── TEST_REPORT.md            # Automated test suite validation report
└── src/                          # Java source code
    └── com/mits/
        ├── app/                  # Main application entry points
        ├── concurrent/           # Multi-threading, monitors, worker threads
        ├── exception/            # Custom exception hierarchy
        ├── gui/                  # Swing GUI components, models, and renderers
        ├── io/                   # Byte and character stream file persistence
        ├── model/                # Core domain models and entities
        ├── network/              # TCP client-server networking
        ├── report/               # Analytical report generators
        ├── service/              # Business logic services and repositories
        └── util/                 # Algorithms, lambdas, generics, helpers
```

### 1.3 Compilation & Build

To compile all source modules into the target `bin` directory, run the following command from the project root (`c:\Users\devis\Downloads\java`):

#### On Windows PowerShell:
```powershell
javac -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
```

#### On Linux / macOS / Bash:
```bash
mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin @sources.txt
```

### 1.4 Running the Applications

MITS provides four dedicated entry points tailored for different operational scenarios:

| Application Target | Class Name | Description | Execution Command |
| :--- | :--- | :--- | :--- |
| **Graphical User Interface (GUI)** | `com.mits.app.GuiApp` | Primary desktop interface with dashboard, tabs, and real-time alerts. | `java -cp bin com.mits.app.GuiApp` |
| **Console CLI Application** | `com.mits.app.ConsoleApp` | Menu-driven terminal interface with CLI flag support. | `java -cp bin com.mits.app.ConsoleApp` |
| **TCP Remote Server** | `com.mits.app.ServerApp` | Multi-threaded headless socket server on port 5000. | `java -cp bin com.mits.app.ServerApp` |
| **Automated Test Suite** | `com.mits.app.TestRunner` | Comprehensive test harness executing 35 verification tests. | `java -cp bin com.mits.app.TestRunner` |

---

## 2. Login Screen

### 2.1 Authentication & Security

Upon launching the GUI application (`GuiApp`), the system presents the secure authentication dialog. The application requires credential verification before allowing access to inventory resources. 

- Credentials entered are checked against the encrypted and seeded `UserRepository`.
- Failed authentication triggers an error notification dialog powered by `AuthenticationException`.
- Invalid attempts are logged to `data/audit.log` with a high-precision timestamp.

```
+--------------------------------------------------------+
|           Medical Inventory System                     |
|      B.Tech Capstone Assessment • Sign In              |
+--------------------------------------------------------+
|  Username: [ admin                                   ] |
|  Password: [ ••••••••                                ] |
|                                                        |
|  Demo Fill: [ Admin ]   [ Pharmacist ]   [ Doctor ]    |
|                                                        |
|                   [   Sign In   ]                      |
+--------------------------------------------------------+
```

### 2.2 Role-Based Access Control (RBAC)

MITS enforces strict Role-Based Access Control across its user interface tabs and service methods:

| Role | Permissions | Visible GUI Tabs | Console App Permissions |
| :--- | :--- | :--- | :--- |
| **ADMIN** | Full administrative rights: Add/Edit/Delete medicines, issue stock, restock, view reports, execute manual backups, restore backups, run hardware/concurrency diagnostics. | All tabs visible (Dashboard, Add/Edit Medicine, Stock Operations, Search, Reports, Remote Server, Backup & Tools) | Unrestricted menu options (1 through 9) |
| **PHARMACIST** | Stock management rights: Add/Edit medicines, issue stock, restock, view live alerts, generate reports. | Dashboard, Add/Edit Medicine, Stock Operations, Search, Reports, Remote Server | Operations 1 through 7 (Backup & Restore restricted) |
| **DOCTOR** | Read-only clinical inquiry: Search medicine catalog, view current stock levels, verify batch expiry dates, view reports. | Dashboard, Search, Reports, Remote Server | Read-only search, catalog viewing, and reports |

### 2.3 Pre-Configured User Credentials

The system seeds three standard role-based accounts upon initialization:

| Username | Password | Full Name | Role | Intended Operational Use |
| :--- | :--- | :--- | :--- | :--- |
| `admin` | `admin123` | Chief Administrator | `ADMIN` | System administration, backup, system diagnostics |
| `pharmacist` | `pharma123` | Head Pharmacist | `PHARMACIST` | Medicine dispensing, restocking, catalog maintenance |
| `doctor` | `doctor123` | Dr. A. Sharma (MD) | `DOCTOR` | Clinical medicine inquiry, stock availability lookup |

### 2.4 Quick Demo Fill Buttons

For evaluation and demonstration convenience, the login screen includes three one-click autofill buttons:
1. **[ Admin ]**: Fills `admin` / `admin123`.
2. **[ Pharmacist ]**: Fills `pharmacist` / `pharma123`.
3. **[ Doctor ]**: Fills `doctor` / `doctor123`.

Clicking any button immediately populates the username and password fields, allowing instant login.

---

## 3. Dashboard

The Dashboard tab serves as the centralized mission control center for inventory monitoring.

```
+----------------------------------------------------------------------------------------------------+
| [Total Medicines]  [ Stock Value ]  [   Low Stock   ]  [ Expiring Soon ]  [     Expired     ]      |
|        28              ₹84,250.00           4 SKUs            3 SKUs             2 SKUs            |
+----------------------------------------------------------------------------------------------------+
|                                                  |                                                 |
|          CATEGORY DISTRIBUTION CHART             |                   LIVE ALERTS                   |
|                                                  |                                                 |
|   TABLET     ██████████████████ (1420 units)     | [CRITICAL] MED-008 Amoxicillin expired!         |
|   ANTIBIOTIC ██████████         (650 units)      | [WARNING]  MED-003 Cetirizine low stock (15)    |
|   SYRUP      ████████████       (820 units)      | [URGENT]   MED-012 Insulin expiring in 8 days   |
|   INJECTION  █████              (310 units)      | [INFO]     Auto-backup saved: backup_1002.ser   |
|   OINTMENT   ████████           (490 units)      |                                                 |
+----------------------------------------------------------------------------------------------------+
```

### 3.1 Executive Status Cards

The top of the dashboard displays five high-contrast KPI cards:

1. **Total Medicines (Blue Card):**
   - Shows the total number of distinct Stock Keeping Units (SKUs) registered in the system.
   - Example: `28` unique pharmaceutical items.
2. **Stock Value (Green Card):**
   - Displays real-time aggregate valuation of all stock in Indian Rupees (`₹`).
   - Formula: $\sum (\text{Total Units in Batches} \times \text{Unit Price})$.
3. **Low Stock (Orange Card):**
   - Count of medicines where current aggregate inventory is less than or equal to the defined Reorder Level.
4. **Expiring Soon (Amber Card):**
   - Count of medicines having non-expired batches due to expire within the next 30 days.
5. **Expired (Red Card):**
   - Count of medicines containing batches that have passed their expiration date, requiring immediate quarantine.

### 3.2 Category Stock Distribution Bar Chart

Rendered dynamically via `CategoryBarChartPanel` using Java 2D Graphics (`Graphics2D`):
- Features crisp anti-aliased rendering with color-coded bars:
  - **Tablet:** Royal Blue (`#2980B9`)
  - **Antibiotic:** Safety Orange (`#E67E22`)
  - **Syrup:** Emerald Green (`#27AE60`)
  - **Injection:** Crimson Red (`#C0392B`)
  - **Ointment:** Amethyst Purple (`#8E44AD`)
- Displays category names, horizontal grid markings, and total unit counts above each bar.
- Resizes smoothly when the main window dimensions change.

### 3.3 Live Alerts & Real-Time Monitoring

The Live Alerts text area is continuously fed by two background daemon threads:
- **`LowStockMonitor` (Daemon Thread):** Evaluates reorder thresholds every 30 seconds and posts reorder notifications.
- **`ExpiryMonitorThread` (Daemon Thread):** Analyzes batch expiration dates every 30 seconds, flagging items expiring in $\le 30$ days and expired items.
- Alerts stream smoothly into the UI via `SwingUtilities.invokeLater` callbacks without freezing user interactions.

---

## 4. Add/Edit Medicine

*(Visible to `ADMIN` and `PHARMACIST`)*

This tab provides complete master catalog management for pharmaceutical products.

### 4.1 General Form Fields

Every medicine item requires the following standardized catalog attributes:

| Field Name | Component Type | Constraints / Validation | Description |
| :--- | :--- | :--- | :--- |
| **Type** | JComboBox | Tablet, Syrup, Injection, Ointment, Antibiotic | Determines polymorphic class hierarchy. |
| **Code** | JTextField | Pattern: `MED-XXX` (e.g., `MED-029`) | Unique inventory SKU identifier. |
| **Name** | JTextField | 2 to 100 characters, non-empty | Brand/Generic pharmaceutical name. |
| **Description** | JTextField | Non-empty text | Clinical usage or therapeutic category. |
| **Unit Price (₹)** | JSpinner | Minimum ₹0.00, step ₹0.50 | Retail/dispensing price per unit. |
| **Reorder Level** | JSpinner | Non-negative integer (0-9999) | Minimum stock threshold triggering restock alert. |
| **Supplier** | JComboBox | Pre-loaded from `SupplierRepository` | Licensed pharmaceutical distributor ID & name. |

### 4.2 Category-Specific Attributes (CardLayout)

Depending on the chosen medicine type, the **Type-Specific Fields** panel switches automatically using Java `CardLayout`:

```
+-------------------------------------------------------------+
| Type: [ Tablet        v ]                                   |
| Code: [ MED-029         ]   Name: [ Paracetamol 500mg     ] |
+-------------------------------------------------------------+
| Type-Specific Fields:                                       |
|  * Tablet:     Strip Size: [ 10 ]     [ ] Chewable          |
|  * Syrup:      Volume (ml): [ 100 ]   [x] Measuring Cup     |
|  * Injection:  Vial Type: [ Vial ]    [x] Cold Chain Req.   |
|  * Ointment:   Tube Weight (g): [25]  Base: [ Cream ]       |
|  * Antibiotic: Spectrum: [Broad]      [x] Rx Required       |
+-------------------------------------------------------------+
```

1. **Tablet:**
   - **Strip Size:** Number of tablets per blister package (spinner).
   - **Chewable:** Checkbox indicating chewable formulation.
2. **Syrup:**
   - **Volume (ml):** Bottle liquid capacity in milliliters (e.g., 60ml, 100ml, 200ml).
   - **Measuring Cup Included:** Checkbox indicating dosing accessory inclusion.
3. **Injection:**
   - **Vial Type:** Container specification (`Vial`, `Ampoule`, `Prefilled Syringe`).
   - **Cold Chain Required:** Checkbox for strict refrigeration requirements ($2^\circ\text{C} - 8^\circ\text{C}$).
4. **Ointment:**
   - **Tube Weight (grams):** Net topical weight in grams (e.g., 15.0g, 30.0g).
   - **Base Type:** Topical formulation base (`Cream`, `Gel`, `Ointment`, `Lotion`).
5. **Antibiotic:** (Multilevel inheritance: extends `Tablet`)
   - **Spectrum:** Antimicrobial range (`Broad-Spectrum`, `Narrow-Spectrum`).
   - **Prescription Required:** Enforces strict Schedule H/H1 prescription compliance.

### 4.3 Input Validation & Business Rules

The form validates input using `Validator` and `StringProcessor` before saving:
- **Code Syntax Check:** Validates regex `^MED-[A-Z0-9]{3,}$`. Violations throw `InvalidInputException`.
- **Uniqueness Check:** Attempting to register an existing code throws `DuplicateMedicineException`.
- **Numeric Validation:** Prices and reorder limits must be strictly non-negative.

### 4.4 Action Controls (Add, Update, Delete, Clear)

- **[ Add Medicine ]:** Validates form inputs, instantiates the concrete subclass, registers it in `InventoryService`, updates disk persistence, and refreshes the Dashboard.
- **[ Update ]:** Updates the existing medicine record matching the specified code.
- **[ Delete ]:** Prompts for confirmation and removes the medicine from inventory.
- **[ Clear ]:** Resets all form fields and spinners to default values.

---

## 5. Stock Operations

*(Visible to `ADMIN` and `PHARMACIST`)*

This tab handles day-to-day inventory inflow and outflow.

### 5.1 Stock Issue & Dispensing (FEFO Policy)

Dispensing stock to patients or hospital wards is performed in the **Issue Stock** sub-panel:

```
+-------------------------------------------------------------+
| Issue Stock (Dispense)                                      |
+-------------------------------------------------------------+
| Medicine Code: [ MED-001    ]                               |
| Quantity:      [ 20         ]                               |
| Remarks:       [ General OPD Patient Dispensing           ] |
|                                                             |
|                    [   Issue Stock   ]                      |
+-------------------------------------------------------------+
```

1. Enter the target **Medicine Code** (e.g., `MED-001`).
2. Specify the **Quantity** to issue (must be positive integer).
3. Enter operational **Remarks** (e.g., Department, Ward, Prescription ID).
4. Click **[ Issue Stock ]**.

#### First-Expiry, First-Out (FEFO) Principle:
MITS manages internal batch tracking using a strict FEFO policy:
- The system automatically selects stock from the batch having the closest expiration date.
- Multi-batch deduction: If the requested quantity exceeds the nearest batch, MITS drains the earliest batch and deducts the remainder from the subsequent batch.

### 5.2 Restocking & Multi-Batch Inward

When receiving fresh inventory shipments from suppliers, use the **Restock** sub-panel:

```
+-------------------------------------------------------------+
| Restock (Add Inventory)                                     |
+-------------------------------------------------------------+
| Medicine Code:      [ MED-002     ]                         |
| Quantity:           [ 100         ]                         |
| Batch Number:       [ B-2026-OCT  ]                         |
| Expiry (YYYY-MM-DD):[ 2028-10-31  ]                         |
|                                                             |
|                    [    Restock    ]                        |
+-------------------------------------------------------------+
```

1. Enter the **Medicine Code**.
2. Enter the received **Quantity**.
3. Specify the manufacturer's **Batch Number** (e.g., `B-2026-OCT`).
4. Enter the verified **Expiry Date** in `YYYY-MM-DD` ISO format.
5. Click **[ Restock ]**. A new `Batch` record is created and attached to the medicine.

### 5.3 Safety Interlocks & Exception Protection

Stock transactions are safeguarded by custom domain exceptions:
- **`InsufficientStockException`:** Triggered when the requested issue quantity exceeds total available stock. Prevents negative inventory balances.
- **`ExpiredMedicineException`:** Triggered if a batch has crossed its expiration date. Dispensing of expired stock is physically blocked by the system to ensure patient safety.
- **`MedicineNotFoundException`:** Triggered if an unrecognized medicine code is entered.

---

## 6. Search & Catalog

*(Visible to all roles: `ADMIN`, `PHARMACIST`, `DOCTOR`)*

The Search screen offers instant lookup across all inventory records.

```
+-----------------------------------------------------------------------------------------------+
| Query: [ amox           ]   Category: [ ALL          v ]   [ Search ]                         |
+-----------------------------------------------------------------------------------------------+
| Code    | Name             | Category   | Stock | Price   | Reorder | Expiry     | Days | Supp|
|---------|------------------|------------|-------|---------|---------|------------|------|-----|
| MED-002 | Amoxicillin 500mg| ANTIBIOTIC | 15    | ₹110.00 | 30      | 2027-04-15 | 553  | S01 |
| MED-015 | Amoxicillin/Clav | ANTIBIOTIC | 0     | ₹195.00 | 25      | 2026-09-10 | -28  | S02 |
+-----------------------------------------------------------------------------------------------+
```

### 6.1 Multi-Parameter Search Query

The search query field supports fuzzy, case-insensitive matching powered by `StringProcessor.matchesQuery()`:
- Search by medicine name (e.g., `Paracetamol`, `Metformin`).
- Search by code (e.g., `MED-001`, `MED-014`).
- Search by description or clinical active compound.

### 6.2 Category Filtering

Use the category dropdown to isolate specific pharmaceutical classes:
- `ALL`: Complete catalog.
- `TABLET`: Standard oral solid dosage forms.
- `ANTIBIOTIC`: Broad and narrow spectrum antibacterials.
- `SYRUP`: Oral liquid formulations.
- `INJECTION`: Sterile parenteral solutions and vials.
- `OINTMENT`: Topical dermatological creams and ointments.

### 6.3 Interactive Table Sorting

The results table is wrapped in a `TableRowSorter`:
- Click any column header (e.g., **Stock**, **Price**, **Expiry Date**, **Days Left**) to sort in ascending or descending order.
- Numeric columns sort by numeric value rather than lexicographic strings.

### 6.4 Visual Health Color-Coding

Every table row is automatically styled by `MedicineTableCellRenderer` to highlight stock condition at a glance:

| Color Tint | Background Color | Text Color | Inventory Health Condition |
| :--- | :--- | :--- | :--- |
| **Light Red** | RGB `(255, 205, 210)` | Crimson `#B22222` | **Expired Batch:** Days to expiry $< 0$. Quarantine immediately! |
| **Soft Pink** | RGB `(255, 224, 230)` | Dark Red `#990000` | **Low Stock:** Total stock $\le$ Reorder Level. Purchase order required. |
| **Soft Amber** | RGB `(255, 243, 205)` | Olive/Amber `#856404` | **Near Expiry:** $0 \le \text{Days Left} \le 30$. Priority dispensing needed. |
| **Alternating White/Gray**| White / `#F8F9FA` | Slate `#212529` | **Optimal:** Healthy stock balance and safe expiration horizon. |

---

## 7. Reports Engine

*(Visible to all roles)*

The Reports engine generates comprehensive analytical summaries for regulatory, clinical, and procurement oversight.

```
+-----------------------------------------------------------------------------------------------+
| Report: [ LOW_STOCK                v ]   [ Generate Report ]   [ Export to File ]             |
+-----------------------------------------------------------------------------------------------+
| ==============================================================================                |
|                      MITS LOW STOCK REORDER REPORT                                            |
| ==============================================================================                |
| Code     | Name                    | Category   | Stock | Reorder | Shortage | Reorder Cost   |
| ---------|-------------------------|------------|-------|---------|----------|--------------  |
| MED-003  | Cetirizine 10mg         | TABLET     | 15    | 40      | 25       | ₹625.00        |
| MED-014  | Azithromycin 500mg      | ANTIBIOTIC | 10    | 30      | 20       | ₹2,400.00      |
|                                                                                               |
| TOTAL MEDICINES REQUIRING REORDER: 4                                                          |
| ESTIMATED PROCUREMENT COST: ₹8,450.00                                                         |
| ==============================================================================                |
+-----------------------------------------------------------------------------------------------+
| [==================================== 100% ====================================] Report Ready |
+-----------------------------------------------------------------------------------------------+
```

### 7.1 Available Analytical Reports

The system provides 5 specialized report generators:

1. **`LOW_STOCK` (Low Stock Reorder Report):**
   - Filters medicines where current stock $\le$ reorder level.
   - Calculates exact unit deficit and estimated procurement budget based on supplier unit pricing.
2. **`EXPIRY_RISK` (Expiry Risk & Quarantine Report):**
   - Segregates inventory into already-expired batches and batches expiring within 30 and 90 days.
   - Computes capital value at risk of write-off.
3. **`JAGGED_CONSUMPTION` (Monthly Consumption Jagged Array Report):**
   - Visualizes multi-department quarterly and monthly medicine consumption patterns using Java jagged (ragged) array structures ($N \times M_i$).
4. **`SUPPLIER_SUMMARY` (Supplier Directory & Portfolio Report):**
   - Enumerates registered pharmaceutical vendors, verified contact persons, phone numbers, and full supplier catalogs.
5. **`FULL_INVENTORY` (Complete Inventory Valuation Report):**
   - Full master balance sheet detailing SKU codes, names, total quantities, unit costs, and total balance valuation.

### 7.2 Asynchronous Background Generation

To prevent the user interface from locking up during data analysis:
- Reports are processed on an independent worker thread (`ReportGeneratorThread`).
- A progress bar communicates generation stages (0% $\rightarrow$ 20% $\rightarrow$ 50% $\rightarrow$ 100%).
- The completed report is rendered into the monospace report viewer upon completion.

### 7.3 File Export Operations

1. Click **[ Export to File ]** after a report is generated.
2. Select a target directory and filename via the standard `JFileChooser` dialog (e.g., `LowStockReport_Oct2026.txt`).
3. The report is written out using character streams (`BufferedWriter` / `PrintWriter`).

---

## 8. Remote Server Client

*(Visible to all roles)*

The Remote Server tab allows operators to interact with a centralized or network-hosted MITS TCP server instance directly from the GUI.

```
+-----------------------------------------------------------------------------------------------+
| Host: [ localhost    ]   Port: [ 5000 ]   [ Connect ]   [ Disconnect ]                        |
+-----------------------------------------------------------------------------------------------+
| 200 MITS Inventory Server Ready. Available commands: LOGIN, SEARCH, STOCK, ISSUE, ALERTS, QUIT|
| > SEARCH Insulin                                                                              |
|   COUNT 1                                                                                     |
|   DATA MED-012 | Insulin Glargine | INJECTION | Stock: 40 | ₹320.00                           |
|   OK END_SEARCH                                                                               |
| > STOCK MED-012                                                                               |
|   OK CODE: MED-012 | NAME: Insulin Glargine | STOCK: 40 | PRICE: ₹320.00 | EXPIRY: 2027-01-15 |
+-----------------------------------------------------------------------------------------------+
| Cmd: [ STOCK MED-012                                                   ]   [ Send ]           |
+-----------------------------------------------------------------------------------------------+
```

### 8.1 Network Host & Port Configuration

- **Host:** Enter the target IP address or hostname (`localhost` for local machine, or a LAN IP such as `192.168.1.100`).
- **Port:** Specify the server listening port (Default: `5000`).

### 8.2 Establishing Connection

1. Click **[ Connect ]**.
2. The client establishes a TCP socket connection via `InventoryClient`.
3. The server banner is displayed in the response log:
   `200 MITS Inventory Server Ready. Available commands: LOGIN, SEARCH, STOCK, ISSUE, ALERTS, QUIT`
4. The **Connect** button disables, and **Disconnect** / **Send** activate.

### 8.3 Issuing Commands & Viewing Responses

- Enter standard protocol commands (such as `LOGIN admin admin123`, `SEARCH Paracetamol`, or `ALERTS`) into the **Cmd** field.
- Press **[ Send ]** or press Enter.
- Responses from the server stream into the output console in real time.

---

## 9. Backup & Diagnostics Tools

*(Visible to `ADMIN` only)*

This screen provides administrative maintenance, data protection, and unit verification utilities.

```
+-----------------------------------------------------------------------------------------------+
| [ Backup Now ]  [ Restore Last Backup ]  [ Run Concurrency Demo ]  [ Run Exception Demo ]     |
+-----------------------------------------------------------------------------------------------+
| --- Concurrency Demo ---                                                                      |
| Initial Stock: 1000 units                                                                     |
| Launching 6 concurrent threads...                                                             |
| Each thread issues 15 batches of 10 units (150 units/thread)                                  |
| Expected total issued: 900 units                                                              |
| Expected final stock: 100 units                                                               |
| All worker threads terminated. Duration: 2 ms                                                 |
| Total successful issue operations: 90                                                         |
| Final Stock in Medicine: 100 units                                                            |
| >>> [PASSED] Concurrency test succeeded without race conditions!                              |
+-----------------------------------------------------------------------------------------------+
```

### 9.1 Immediate State Backup

Clicking **[ Backup Now ]**:
- Calls `InventoryService.triggerBackup()`.
- Serializes the live `Inventory` object graph using Java Object Serialization byte streams (`ObjectOutputStream`).
- Saves the snapshot to `data/backup/inventory_backup_YYYYMMDD_HHmmss.ser`.
- Displays confirmation with the backup file path in the output log.

### 9.2 Snapshot Recovery & Restoration

Clicking **[ Restore Last Backup ]**:
- Scans `data/backup/` for the most recent `.ser` backup file.
- Reads and deserializes the object graph into active memory using `ObjectInputStream`.
- Automatically refreshes the dashboard cards and table views.
- If no backup file exists or files are damaged, a descriptive warning is logged.

### 9.3 Concurrency Demonstration

Clicking **[ Run Concurrency Demo ]**:
- Runs `ConcurrencyDemo.runDemo()`, capturing stdout into the GUI text area.
- Simulates 6 concurrent dispenser threads with varying thread priorities competing to issue stock from a shared 1,000-unit reserve.
- Demonstrates thread safety, `synchronized` critical sections, and `join()` coordination.
- Confirms atomic execution: 90 operations $\times$ 10 units = 900 units deducted, leaving exactly 100 units without race conditions.

### 9.4 Exception Handling Architecture Demo

Clicking **[ Run Exception Demo ]**:
- Runs `ExceptionDemo.runAll()`, capturing stdout.
- Demonstrates:
  1. Standard `try-catch` with custom `throw` and `throws`.
  2. Multi-catch polymorphism handling `InsufficientStockException`, `ExpiredMedicineException`, `MedicineNotFoundException`, and `ArithmeticException`.
  3. Nested `try-catch` blocks for compound transactions.
  4. Reliable `finally` block resource cleanup during simulated stream failures.

---

## 10. Console Application

The MITS console application offers a fast terminal interface suitable for servers or headless environments.

### 10.1 Command-Line Arguments & Flags

Launch the console app with optional parameters:
```powershell
java -cp bin com.mits.app.ConsoleApp [options]
```

| Flag | Argument | Description | Example |
| :--- | :--- | :--- | :--- |
| `--seed` | None | Forces regeneration of 28 realistic seed medicines. | `java -cp bin com.mits.app.ConsoleApp --seed` |
| `--report` | None | Generates and prints the full inventory valuation report to stdout and exits. | `java -cp bin com.mits.app.ConsoleApp --report` |
| `--user` | `<username>` | Pre-fills the login prompt with the given username. | `java -cp bin com.mits.app.ConsoleApp --user pharmacist` |
| `--help` | None | Prints the CLI usage manual and exits immediately. | `java -cp bin com.mits.app.ConsoleApp --help` |

### 10.2 Interactive Console Menu Reference

After authenticating via username and password, the console presents the main menu:

```
========= MITS Main Menu =========
  1. View All Medicines
  2. Search Medicine
  3. Add Medicine
  4. Issue Stock
  5. Restock
  6. View Alerts
  7. Generate Report
  8. Backup Now
  9. Restore Backup
  0. Exit
==================================
Enter choice:
```

- **`1. View All Medicines`:** Prints formatted summaries for all medicines, displaying SKU, name, price, stock, and batch counts.
- **`2. Search Medicine`:** Enters the search submenu (see [Section 10.3](#103-search-overloading-in-console)).
- **`3. Add Medicine`:** Guided prompt to create a Tablet, Syrup, Injection, Ointment, or Antibiotic with category-specific parameters.
- **`4. Issue Stock`:** Dispenses units for a specific code with immediate remaining balance reporting.
- **`5. Restock`:** Inward receiving of stock specifying quantity, batch code, and expiration date.
- **`6. View Alerts`:** Lists active warnings (low stock and expiration notifications).
- **`7. Generate Report`:** Submenu offering Low Stock, Expiry Risk, Jagged Consumption, Supplier, or Full Inventory reports.
- **`8. Backup Now`:** Creates an immediate timestamped serialized backup snapshot.
- **`9. Restore Backup`:** Reverts inventory to the latest backup file.
- **`0. Exit`:** Exits the application.

### 10.3 Search Overloading in Console

The console search feature showcases compile-time method overloading:

```
  1. Search by Name/Code         -> search(String query)
  2. Search by Name + Category   -> search(String name, String category)
  3. Search by Price Range       -> search(double minPrice, double maxPrice)
```

Selecting option 3 allows price-range filtering (e.g., Min: `20.0`, Max: `100.0`), returning only items within the specified price window.

---

## 11. TCP Server Daemon

MITS includes a dedicated multi-client TCP socket server for networked inventory sharing across pharmacy counters and wards.

### 11.1 Server Architecture & Initialization

Start the server using `ServerApp`:

```powershell
# Start on default port (5000)
java -cp bin com.mits.app.ServerApp

# Or specify a custom port
java -cp bin com.mits.app.ServerApp 8080
```

- **Architecture:** `InventoryServer` creates a `ServerSocket` and dispatches incoming client connections to a cached thread pool (`Executors.newCachedThreadPool()`).
- Multiple clients can connect, search, and dispense stock concurrently with thread-safe data synchronization.
- To shut down the server gracefully, press **Enter** in the server terminal.

### 11.2 TCP Protocol Commands & Wire Format

The line-oriented text protocol supports the following commands:

| Command & Syntax | Description | Example Server Response |
| :--- | :--- | :--- |
| `LOGIN <user> <pass>` | Authenticates client connection session. Required before issuing stock. | `OK Welcome Chief Administrator (ADMIN)`<br>`ERROR Invalid username or password.` |
| `SEARCH [query]` | Searches medicine catalog by name or code. | `COUNT 2`<br>`DATA MED-001 \| Paracetamol 650mg \| TABLET \| Stock: 120 \| ₹30.00`<br>`DATA MED-002 \| Ibuprofen 400mg \| TABLET \| Stock: 80 \| ₹45.00`<br>`OK END_SEARCH` |
| `STOCK <code>` | Queries detailed stock, price, and expiry for a specific SKU. | `OK CODE: MED-001 \| NAME: Paracetamol 650mg \| CATEGORY: TABLET \| STOCK: 120 \| PRICE: ₹30.00 \| EXPIRY: 2028-04-09 \| REORDER: 50` |
| `ISSUE <code> <qty>` | Dispenses stock (requires authenticated user with stock management role). | `OK Successfully issued 10 units of Paracetamol 650mg (MED-001). Remaining stock: 110`<br>`ERROR Insufficient stock...` |
| `ALERTS` | Lists active low-stock and expiration alerts. | `COUNT 1`<br>`DATA [2026-10-09T01:40:00] [CRITICAL] EXPIRY: Amoxicillin (Expired)`<br>`OK END_ALERTS` |
| `QUIT` | Closes the client connection session. | `200 Bye. Connection closing.` |

### 11.3 External Client Integration Example

You can connect to the server using standard tools like PowerShell, Telnet, or Netcat:

```powershell
# Connect using PowerShell TCP Client
$client = New-Object System.Net.Sockets.TcpClient("localhost", 5000)
$stream = $client.GetStream()
$writer = New-Object System.IO.StreamWriter($stream)
$reader = New-Object System.IO.StreamReader($stream)
$writer.AutoFlush = $true

# Read greeting banner
$reader.ReadLine()

# Log in
$writer.WriteLine("LOGIN admin admin123")
$reader.ReadLine()

# Search
$writer.WriteLine("SEARCH Metformin")
while (($line = $reader.ReadLine()) -ne "OK END_SEARCH") { Write-Host $line }

# Disconnect
$writer.WriteLine("QUIT")
$reader.ReadLine()
$client.Close()
```

---

*Intelligent Medical Inventory Tracking System (MITS) — Built for B.Tech CSE-AI Capstone Assessment.*
