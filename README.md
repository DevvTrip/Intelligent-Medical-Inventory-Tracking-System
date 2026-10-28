# Intelligent Medical Inventory Tracking System

Capstone project for **Object Oriented Techniques using Java (CCSE0355)**, Semester III, B.Tech CSE (AI), NIET Greater Noida.

**Group 24** — Devvrat Tripathi, Manas Srivastava, Pranjal Sharma, Hardik Kumar, Mritunjay Gupta
Faculty: Ms Shweta Singh, Ms Barkha Bhardwaj — SDG 3: Good Health and Well-being

## What the project does

A small hospital pharmacy keeps medicine stock in a paper register. Staff lose track of what
is running low and what is about to expire, so patients either wait or get the wrong batch.
This project replaces that register with a desktop application that:

- stores medicines with batch, price, quantity, expiry date, threshold and supplier
- warns about low stock and medicines expiring within 30 days
- records each sale and blocks a sale when stock is not enough
- searches by name, category or supplier and prints stock / expiry / reorder reports
- serves stock updates to a second counter over a TCP socket
- keeps a timestamped backup of the data file

The application is written only with the JDK. There is no database server, no Maven, and no
external library, so it can be compiled and shown on any college machine.

## Requirements

- JDK 8 or newer (`javac` and `java` on the PATH)
- Windows, Linux or macOS

## Build and run

Windows:

```bat
run.bat
```

Linux / macOS:

```bash
chmod +x run.sh
./run.sh
```

The script compiles every source file into `out/` and starts the GUI. Other entry points:

```bat
run.bat --console                      REM text menu instead of the Swing window
java -cp out network.InventoryServer   REM TCP server on port 5555
java -cp out network.CounterClient     REM sample counter that sells one strip
```

## Login

| Username | Password  | Role       |
|----------|-----------|------------|
| admin    | admin123  | Admin      |
| pharma   | pharma123 | Pharmacist |

Only an admin can add, edit or delete a medicine; the pharmacist can sell stock, search and
print reports. This is the role check shown in `usermanagement.LoginCheck`.

## Project structure

```
src/
├── model/            Medicine, Tablet, Syrup, Injection, Supplier, Hospital
├── usermanagement/   User, LoginCheck
├── businesslogic/    Inventory, StockService, Repository, DataStore,
│                     ExpiryMonitor, MedicineValidation, custom exceptions
├── reporting/        ReportMaker, AnalyticsReport, Reportable, Alertable
├── dataprocessing/   SampleData, SalesData
├── utils/            FileUtil, BackupUtil, InventoryFileLock, SortUtil
├── network/          InventoryServer, CounterClient
└── gui/              Main, LoginScreen, Dashboard, MedicineForm, SellDialog,
                      SearchScreen, ReportScreen, ConsoleMenu, Ui, StatusRenderer
data/
├── medicines.csv              live stock file (500 records)
├── kaggle_source_500.csv      original download, never edited
└── kaggle_dataset_metadata.json
backup/                        copies written by BackupUtil
out/                           compiled classes (ignored by git)
```

## Data

`data/medicines.csv` holds 500 medicine records imported from a public Kaggle medicine
dataset. The original file is kept as `data/kaggle_source_500.csv` with its metadata, so the
difference between downloaded data and demo data stays visible in the repository. Supplier
names, phone numbers and quantities were replaced with demo values during the import.

CSV columns:

```
type,id,name,price,qty,expiry,threshold,supplierName,supplierPhone,extra
```

`type` selects the subclass (`Tablet`, `Syrup`, `Injection`), and `extra` stores the
category specific field such as tablets per strip or millilitres per bottle.

## Deliverables

| File | Contents |
|------|----------|
| `Team24_CCSE0355_IntelligentMedicalInventoryTrackingSystem.docx` | Final report (sections 1-16) |
| `Team24_CCSE0355_IntelligentMedicalInventoryTrackingSystem.pdf` | Same report as PDF |
| `Team24_CCSE0355_IntelligentMedicalInventoryTrackingSystem_Presentation.pptx` | 10 slide presentation |
| `Report 1 (2).docx`, `Report 1_backup.docx`, `Report 2.docx` | Earlier progress reports |
| `CP_Object Oriented Techniques using JAVA.pdf` | Course syllabus and guidelines |

## Notes

- The data file is locked while the application writes to it (`InventoryFileLock`), so two
  counters cannot overwrite each other silently.
- `ExpiryMonitor` runs on a background thread and refreshes the dashboard status bar every
  few seconds.
- The TCP server accepts one short request per connection: `SELL <id> <qty>` and answers
  `OK <remaining>` or `ERROR <reason>`.
