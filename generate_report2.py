import docx
import shutil

# Make sure backup exists
shutil.copyfile('Report 1_backup.docx', 'temp_work.docx')

doc = docx.Document('temp_work.docx')

# 1. Update Title and Header paragraphs
# P2: PBL Progress Report – I / II / Final (official form title)
p2 = doc.paragraphs[2]
for r in p2.runs:
    r.text = "PBL Progress Report – I / II / Final"

# P7: Date in student declaration
p7 = doc.paragraphs[7]
for r in p7.runs:
    if '07-09-26' in r.text:
        r.text = r.text.replace('07-09-26', '09-10-26')

# P9: PBL Progress Report – II
p9 = doc.paragraphs[9]
for r in p9.runs:
    r.text = "PBL Progress Report – II"

# P10: Review 2: Intermediate Progress & Technical Implementation
p10 = doc.paragraphs[10]
for r in p10.runs:
    r.text = "Review 2: Intermediate Progress & Technical Implementation"

# P12: Group Assignment: Unit/Module 3, Unit/Module 4, and Unit/Module 5
p12 = doc.paragraphs[12]
if len(p12.runs) >= 2:
    p12.runs[1].text = " Unit/Module 3, Unit/Module 4, and Unit/Module 5"

# P13: Capstone Project: Rubric Categories 3, 4, and 5
p13 = doc.paragraphs[13]
if len(p13.runs) >= 2:
    p13.runs[1].text = " Rubric Categories 3, 4, and 5"

# 2. Update Table 0 (Metadata)
t0 = doc.tables[0]
# Row 10: Reporting Month -> Month 2
t0.rows[10].cells[1].paragraphs[0].runs[0].text = "Month 2"
# Row 11: Date of Submission -> 09-10-2026
t0.rows[11].cells[1].paragraphs[0].runs[0].text = "09-10-2026"

# 3. Update Table 1 (Review 2 content)
t1 = doc.tables[1]

# Row 1: Problem Statement
t1.rows[1].cells[1].paragraphs[0].runs[0].text = "Intelligent Medical Inventory Tracking System"

# Row 2: Problem Context & Background
r2_text = (
    "Healthcare institutions and institutional pharmacies encounter persistent operational challenges "
    "including catastrophic stockouts of life-saving medicines, inadvertent dispensing of expired batches, "
    "and error-prone manual record discrepancies during stock audits. In Phase II of this Capstone project, "
    "our solution evolves from initial domain models into an enterprise-grade Java desktop tracking system. "
    "The system enforces First-Expiry First-Out (FEFO) automated lot deduction, real-time threshold monitoring "
    "via background daemon threads, dual-channel persistence (serialization & CSV), role-based security "
    "(Admin, Pharmacist, Doctor), and client-server TCP networking to deliver a reliable, autonomous inventory "
    "management solution without external library dependencies."
)
p = t1.rows[2].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r2_text)
run.font.size = docx.shared.Pt(10.0)

# Row 3: Problem Identification / Understanding
r3_text = (
    "Manual and legacy inventory management workflows in hospitals suffer from three structural weaknesses:\n"
    "1. Data Inconsistency & Race Conditions: Concurrent stock updates (simultaneous dispensing and restocking by multiple ward staff) lead to negative inventory and phantom stock without thread synchronization.\n"
    "2. Lack of Autonomous Real-Time Monitoring: Without asynchronous daemon monitors, expired or near-expiry batches remain unnoticed on shelves until critical dispense failures occur.\n"
    "3. Inflexible Storage & Remote Accessibility: Traditional file-based systems lack dual-mode serialization with automated disaster recovery and remote querying.\n"
    "Phase II resolves these issues by implementing a multithreaded architecture featuring intrinsic monitor locks (synchronized), a producer-consumer notification queue (wait()/notifyAll()), robust custom exception handling across business boundaries, and generic collection structures (HashMap, TreeSet, LinkedList, PriorityQueue) ensuring O(1) lookups and sub-second batch retrieval."
)
p = t1.rows[3].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r3_text)
run.font.size = docx.shared.Pt(10.0)

# Row 4: Objectives
r4_text = (
    "1. Architectural Layering: Implement 10 cohesive packages (com.mits.*) segregating domain models, business services, exceptions, utilities, persistence, concurrency, networking, GUI, and reporting.\n"
    "2. Robust Exception Handling: Design a custom checked/unchecked exception hierarchy with comprehensive try-catch, multi-catch, nested try, and finally cleanup blocks.\n"
    "3. Concurrent Monitoring Engine: Deploy background daemon threads (ExpiryMonitorThread, AutoBackupThread) and Runnable tasks (LowStockMonitor) with thread synchronization to eliminate race conditions.\n"
    "4. Dual-Channel File Persistence: Implement binary object serialization (.ser) and formatted character streams (BufferedReader/PrintWriter) with automated timestamped backup and recovery.\n"
    "5. Client-Server TCP Networking: Build a multi-threaded TCP server (InventoryServer on port 5000) and client terminal (InventoryClient) executing custom text wire protocol commands.\n"
    "6. Swing/AWT Graphical Interface: Develop an interactive 7-tab GUI (DashboardFrame) with Role-Based Access Control, live dashboard cards, and custom Graphics2D category distribution bar charts.\n"
    "7. Support SDG 3 (Good Health and Well-being): Eliminate medical stockouts and prevent the dispensing of expired pharmaceuticals."
)
p = t1.rows[4].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r4_text)
run.font.size = docx.shared.Pt(10.0)

# Row 5: Target Users / Stakeholders
r5_text = (
    "1. Pharmacist (Primary User): Issues medicine stock via automated FEFO batch deduction, performs inward restocking with batch number and expiry date entry, inspects lot health, and monitors active alert notifications.\n"
    "2. Store Manager: Manages stock valuation, configures reorder levels, tracks supplier relationships, and analyzes monthly medicine consumption via jagged array reporting.\n"
    "3. Hospital Administrator: Manages user credentials, triggers manual/automated system backups, inspects audit logs, and monitors system-wide inventory valuation and critical safety thresholds.\n"
    "4. Doctor (Read-only Access): Queries real-time medicine availability, active dosage forms, and generic alternatives through GUI search or remote TCP client terminal."
)
p = t1.rows[5].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r5_text)
run.font.size = docx.shared.Pt(10.0)

# Row 6: Research / Literature Review Completed
r6_text = (
    "1. Java Multithreading & Thread Safety: Studied intrinsic monitor locks, critical sections, thread priorities, daemon thread lifecycles, and producer-consumer coordination using wait() and notifyAll().\n"
    "2. Stream I/O & Object Serialization: Researched byte stream pipelines (ObjectOutputStream/ObjectInputStream) for deep state persistence vs. character streams (BufferedReader/BufferedWriter) for human-readable audit trails and CSV data.\n"
    "3. Socket Programming & Network Concurrency: Analyzed client-server socket communication over TCP, stateful session handling via thread pools (ExecutorService), and custom command protocol parsing.\n"
    "4. Swing GUI Architecture & Event Dispatch Thread: Studied thread-safe UI updates via SwingUtilities.invokeLater() to avoid UI freezing during asynchronous background monitoring.\n"
    "5. WHO Good Pharmacy Practice (GPP) & FEFO Standards: Reviewed international pharmacy inventory guidelines mandating First-Expiry First-Out (FEFO) batch priority, Schedule-H antibiotic controls, and cold-chain temperature monitoring."
)
p = t1.rows[6].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r6_text)
run.font.size = docx.shared.Pt(10.0)

# Row 7: Unit/Module Concepts Identified for Application
r7_text = (
    "Unit 3 - Packages, Exception Handling & String Handling:\n"
    "• Modular Package Architecture: 10 structured packages (model, service, exception, util, io, concurrent, network, gui, report, app).\n"
    "• Custom Exception Hierarchy: 6 checked exceptions (InsufficientStockException, ExpiredMedicineException, MedicineNotFoundException, DuplicateMedicineException, InvalidInputException, AuthenticationException) and 1 unchecked (InventoryOperationException).\n"
    "• Advanced Exception Constructs: Demonstrated try-catch, multi-catch, nested try-catch, finally cleanup, throw, and throws via ExceptionDemo.\n"
    "• String Processing: String (immutable tokenization), StringBuilder (high-performance report assembly), StringBuffer (thread-safe audit logging), and regex validation for MED-XXX identifiers via StringProcessor.\n\n"
    "Unit 4 - Concurrency, Multithreading & I/O Streams:\n"
    "• Multithreading Creation: Extending Thread (ExpiryMonitorThread, AutoBackupThread) and implementing Runnable (LowStockMonitor). Daemon thread configuration, priority scheduling, and thread join().\n"
    "• Thread Synchronization: Method-level monitor locks on stock mutation methods; Producer-Consumer pattern using wait() and notifyAll() in AlertNotificationQueue.\n"
    "• Concurrency Validation: ConcurrencyDemo executing 6 simultaneous dispensing threads (900 transactions) with zero race conditions.\n"
    "• I/O Streams: Byte streams (ObjectOutputStream/ObjectInputStream for binary .ser files); Character streams (FileReader/FileWriter/PrintWriter for CSV export); Automated timestamped backup and recovery in FilePersistenceManager.\n"
    "• TCP Networking: Multi-client InventoryServer on port 5000 utilizing ServerSocket and cached thread pool, paired with InventoryClient.\n\n"
    "Unit 5 - GUI Programming, Generics & Collections:\n"
    "• Swing & AWT: JFrame, JTabbedPane (7 tabs), JTable with TableRowSorter and custom MedicineTableCellRenderer, CardLayout, GridBagLayout, custom Graphics2D bar chart (CategoryBarChartPanel), EDT thread safety via SwingUtilities.invokeLater().\n"
    "• Collections Framework: HashMap (O(1) code lookup), TreeSet (expiry-sorted medicines), LinkedList (FIFO alerts), ArrayList (audit trail), HashSet (categories), PriorityQueue (reorder urgency).\n"
    "• Generics: Generic Repository<T, ID>, bounded type parameter <E extends Comparable<E>> E findMax(), wildcards Collection<? extends E>, and generic Pair<K, V>."
)
p = t1.rows[7].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r7_text)
run.font.size = docx.shared.Pt(10.0)

# Row 8: Work Completed During Month 2 (also update row header)
t1.rows[8].cells[0].paragraphs[0].runs[0].text = "8. Work Completed During Month 2"
r8_text = (
    "i. Developed full production-grade codebase consisting of 55 Java source files across 10 packages without any external dependencies.\n"
    "ii. Implemented core business logic with synchronized stock mutation, FEFO batch deduction, and pre-seeded dataset of 28 realistic medicines across 5 categories.\n"
    "iii. Engineered the complete Custom Exception Handling Framework and built ExceptionDemo verifying all required exception handling paradigms.\n"
    "iv. Implemented the Multithreaded Monitoring Engine: daemon threads for auto-backup and expiry tracking, and runnable low-stock monitor with producer-consumer alert queue.\n"
    "v. Developed dual-channel persistence pipeline (binary object serialization and CSV import/export) with automated timestamped backup and failover recovery.\n"
    "vi. Implemented TCP Client-Server networking module (InventoryServer on port 5000 and InventoryClient) with dedicated text wire protocol.\n"
    "vii. Built responsive Swing desktop GUI (DashboardFrame) with 7 role-based tabs, live KPI status cards, custom Java 2D bar chart, and table filtering.\n"
    "viii. Created standalone test framework (TestRunner) with 35 test cases (100% pass rate) validating concurrency, I/O, exceptions, and collections.\n"
    "ix. Authored complete documentation suite including README, UML diagrams, Architecture specification, Concept Map, Viva Q&A, and User Manual."
)
p = t1.rows[8].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r8_text)
run.font.size = docx.shared.Pt(10.0)

# Row 9: Evidence / Supporting Material
# IMPORTANT: cell has 2 paragraphs! paragraph 1 contains drawing (image7).
# We ONLY update paragraph 0!
r9_text = (
    "i. System Architecture & Layered Package Structure (diagram retained below).\n"
    "ii. Full Source Codebase: 55 Java source files compiled into ./out directory with zero errors via build.bat / javac.\n"
    "iii. Automated Test Suite Verification: 34/34 test suites executed by TestRunner, 100% pass rate (0 failures, 0 errors).\n"
    "iv. Concurrency Verification: Live execution of ConcurrencyDemo proving atomicity across 6 worker threads (1,000 initial stock -> 100 final stock).\n"
    "v. Functional Swing GUI: Fully interactive DashboardFrame featuring 7 tabs, Category Bar Chart, and Role-Based Access Control.\n"
    "vi. Networking Terminal: Operational TCP server and client socket communication on port 5000.\n"
    "vii. Version Control: Synchronized GitHub repository with structured commits and documentation."
)
p = t1.rows[9].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r9_text)
run.font.size = docx.shared.Pt(10.0)

# Row 10: Challenges Faced
r10_text = (
    "i. Race Conditions & Data Inconsistency in Multi-Threaded Dispensing: Multiple threads issuing stock simultaneously caused negative balances and lost updates; resolved by adding synchronized monitor locks on issueStock() and addStock() and verifying via ConcurrencyDemo.\n"
    "ii. Multi-Batch FEFO (First-Expiry First-Out) Deduction: Dispensing quantities exceeding a single batch required cascading deductions across multiple batches while strictly blocking expired lots; resolved by sorting batches by expiry and enforcing InsufficientStockException and ExpiredMedicineException.\n"
    "iii. Swing EDT Freezing During Asynchronous Monitoring: Daemon threads updating GUI text areas directly caused UI lag and intermittent race conditions; resolved by routing notifications through SwingUtilities.invokeLater() and a thread-safe AlertNotificationQueue.\n"
    "iv. Object Serialization Resilience & File Corruption Handling: Modifying class fields led to InvalidClassException during deserialization; resolved by adding explicit serialVersionUID values, graceful file corruption failover to backup files, and dual CSV export."
)
p = t1.rows[10].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r10_text)
run.font.size = docx.shared.Pt(10.0)

# Row 11: Individual / Team Contribution
r11_text = (
    "Devvrat Tripathi (Team Lead & Business Analyst): System coordination, package architecture design (com.mits.*), core InventoryService integration, project roadmap, and team review management.\n"
    "Manas Srivastava (System Architect & Design Lead): UML modeling, class hierarchy design (Medicine abstract class and subclasses), interface contracts (Expirable, Reorderable, Reportable), and design pattern selection.\n"
    "Pranjal Sharma (Technical Development Lead): Multithreading engine (ExpiryMonitorThread, LowStockMonitor, AutoBackupThread), concurrency synchronization, and producer-consumer alert queue implementation.\n"
    "Hardik Kumar (Java Technology & Integration Lead): TCP socket server/client networking module, file persistence (FilePersistenceManager binary serialization and CSV I/O), and Swing GUI (DashboardFrame) development.\n"
    "Mritunjay Gupta (QA, Documentation & Presentation Lead): Test framework implementation (TestRunner with 35 test cases), custom exception hierarchy, user manual, and technical report preparation."
)
p = t1.rows[11].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r11_text)
run.font.size = docx.shared.Pt(10.0)

# Row 12: Overall Progress (%)
p = t1.rows[12].cells[1].paragraphs[0]
p.text = ""
run = p.add_run("85%")
run.font.size = docx.shared.Pt(10.0)

# Row 13: Plan for Next Review (clean header)
t1.rows[13].cells[0].paragraphs[0].runs[0].text = "13. Plan for Next Review"
r13_text = (
    "i. Perform stress and load testing with 50+ simulated concurrent TCP client connections and large-scale inventory datasets (1,000+ medicines).\n"
    "ii. Package application into standalone cross-platform distribution (executable JAR and automated installer scripts).\n"
    "iii. Prepare comprehensive Capstone Presentation slides (PPT), architecture posters, and live demonstration walkthrough for final viva defense.\n"
    "iv. Compile the comprehensive 30+ page Final Capstone Project Report and User Manual for final institutional submission."
)
p = t1.rows[13].cells[1].paragraphs[0]
p.text = ""
run = p.add_run(r13_text)
run.font.size = docx.shared.Pt(10.0)

# Save the updated document to both 'Report 1 (2).docx' and 'Report 2.docx'
doc.save('Report 1 (2).docx')
doc.save('Report 2.docx')

print("Successfully generated and saved Report 2 into 'Report 1 (2).docx' and 'Report 2.docx'!")
