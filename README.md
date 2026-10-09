# Intelligent Medical Inventory Tracking System (MITS)

B.Tech CSE-AI, Object Oriented Techniques using Java, Capstone / PBL Assessment 2

## Overview
A complete Java desktop application for tracking medicine stock, batches, expiry dates, suppliers and issues in hospitals/pharmacies. Raises alerts for low stock and near-expiry/expired medicines.

## Features
- 28 realistic seed medicines across 5 categories (Tablet, Antibiotic, Syrup, Injection, Ointment)
- Swing GUI with login, dashboard, data entry, stock operations, search, reports, remote server client
- TCP socket server/client for remote inventory access
- Background daemon threads for expiry monitoring, low-stock alerts, auto-backup
- File persistence (serialized snapshots + CSV export)
- Backup and recovery with timestamped files
- Role-based access control (Admin, Pharmacist, Doctor)
- 35 automated test cases (all passing)

## Prerequisites
- JDK 17 or later (tested with Java 26)
- No external libraries, Maven, or Gradle required

## Quick Start

### Build (Windows)
```batch
build.bat
```

### Build (Linux/macOS)
```bash
chmod +x build.sh
./build.sh
```

### Run
```batch
REM Windows
run_gui.bat           REM Swing GUI (recommended)
run_console.bat       REM Console menu-driven app
run_server.bat        REM TCP inventory server (port 5000)
run_server.bat 8080   REM Custom port

REM Run tests
java -cp out com.mits.app.TestRunner

REM Run demos
java -cp out com.mits.concurrent.ConcurrencyDemo
java -cp out com.mits.exception.ExceptionDemo
```

Linux/macOS: use .sh scripts instead.

### Console App Args
```
java -cp out com.mits.app.ConsoleApp --help
java -cp out com.mits.app.ConsoleApp --seed --user admin
java -cp out com.mits.app.ConsoleApp --report
```

## Default Login Credentials
| Username | Password | Role | Access |
|---|---|---|---|
| admin | admin123 | ADMIN | Full access |
| pharmacist | pharma123 | PHARMACIST | Stock operations, reports |
| doctor | doctor123 | DOCTOR | Search and reports only |

## Project Structure
```
java/
├── src/com/mits/
│   ├── model/       -> Medicine, Batch, Supplier, User, Inventory, Alert, Role, etc.
│   ├── service/     -> InventoryService, MedicineRepository, SupplierRepository, UserRepository
│   ├── exception/   -> 6 custom checked + 1 unchecked exception, ExceptionDemo
│   ├── util/        -> Pair, Repository, Searchable, Validator, StringProcessor, LambdaProcessor, Constants
│   ├── io/          -> FilePersistenceManager
│   ├── concurrent/  -> ExpiryMonitorThread, LowStockMonitor, AutoBackupThread, ReportGeneratorThread, ConcurrencyDemo, AlertNotificationQueue
│   ├── network/     -> InventoryServer, InventoryClient
│   ├── gui/         -> LoginFrame, DashboardFrame, CategoryBarChartPanel, StatusCardPanel, MedicineTableModel, MedicineTableCellRenderer
│   ├── report/      -> ReportService
│   └── app/         -> ConsoleApp, GuiApp, ServerApp, TestRunner
├── data/            -> Auto-created at startup (inventory.ser, medicines.csv, audit.log, backup/)
├── docs/            -> Documentation
├── build.bat/.sh    -> Compile scripts
├── run_gui.bat/.sh, run_console.bat/.sh, run_server.bat/.sh
└── out/             -> Compiled classes
```

## Data Persistence
- `./data/inventory.ser` — Binary serialized snapshot
- `./data/medicines.csv` — CSV export
- `./data/audit.log` — Activity log
- `./data/backup/` — Timestamped backup files

All directories are auto-created on first run. Seed data (28 medicines, 4 suppliers, 3 users) is generated if no data file exists.

## Syllabus Coverage
See [CONCEPT_MAP.md](CONCEPT_MAP.md) for exact mapping of every Unit 1-5 concept to source file, class, and method.
