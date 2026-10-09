# Intelligent Medical Inventory Tracking System (MITS)
## Comprehensive Viva Voce Questions & Answers (40 Code-Specific Questions)

This document contains 40 in-depth viva voce questions and answers meticulously tailored to the architecture, object-oriented design patterns, concurrency controls, networking protocols, data structures, and concrete Java source code implementations of the **Intelligent Medical Inventory Tracking System (MITS)**.

---

### Unit 1 & Unit 2: Object-Oriented Principles, Abstraction, and Polymorphism

1. **Q: What is the [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) class in the MITS project, why is it declared `abstract`, and what problem does this solve?**  
   **A:** The [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L23) class (`com.mits.model.Medicine`) is the root base entity of the pharmaceutical inventory model. It is declared `abstract` because in the physical pharmaceutical domain, a generic "medicine" does not exist in isolation—every dispensable medicine exists in a concrete dosage formulation (such as a tablet, oral suspension/syrup, sterile parenteral injection, or topical ointment) with form-specific physical, chemical, storage, and pricing rules.  
   Declaring `Medicine` as `abstract` prevents improper direct instantiation (`new Medicine(...)` is illegal at compile-time). It serves as an architectural blueprint that encapsulates common fields (`code`, `name`, `category`, `unitPrice`, `reorderLevel`, `supplierId`, and `batches`) and shared business logic (e.g., synchronized batch deduction and summary formatting), while deferring formulation-specific behaviors to concrete subclasses.

2. **Q: What abstract methods are declared in [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) and what contracts do they enforce across subclasses?**  
   **A:** The [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) class declares three abstract methods that every concrete subclass is strictly contractually required to implement:
   - [`getStorageCondition()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L73): Returns a `String` describing the mandatory environmental storage criteria (e.g., room temperature, cold chain refrigeration at 2°C–8°C, or light-sensitive storage).
   - [`calculateDiscountedPrice(int quantity)`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L80): Computes the net price after volume-based discounting according to policy rules unique to each dosage form.
   - [`getDosageFormDetails()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L86): Returns a formatted `String` describing physical packaging and dispensing specifications (e.g., blister strip size, bottle fluid volume in ml, ampoule/vial type, or tube net weight in grams).

3. **Q: How does the [`Tablet`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java) subclass override the abstract methods inherited from [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java)?**  
   **A:** The [`Tablet`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java#L9) class extends [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) and adds specific fields: `stripSize` (pills per blister strip) and `isChewable` (boolean). It overrides the abstract methods as follows:
   - [`getStorageCondition()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java#L48): Returns `"Store at room temperature below 25°C in a dry place. Protect from moisture."`
   - [`calculateDiscountedPrice(int quantity)`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java#L53): Implements bulk tiers—if `quantity >= 500`, a 10% discount is applied (`subtotal * 0.90`); if `quantity >= 100`, a 5% discount is applied (`subtotal * 0.95`); otherwise, standard subtotal (`unitPrice * quantity`) is returned.
   - [`getDosageFormDetails()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java#L65): Returns `String.format("Tablet [Strip Size: %d pills, %s]", stripSize, (isChewable ? "Chewable" : "Swallow with water"))`.

4. **Q: How does the [`Syrup`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Syrup.java) subclass override the abstract methods inherited from [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java)?**  
   **A:** The [`Syrup`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Syrup.java#L9) class extends [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) and introduces `bottleVolumeMl` (e.g., 60ml, 100ml, 200ml) and `dosageMeasuringCupIncluded` (boolean). It implements the abstract methods as follows:
   - [`getStorageCondition()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Syrup.java#L47): Returns `"Store below 25°C. Do not freeze. Shake well before use. Keep bottle tightly closed."`
   - [`calculateDiscountedPrice(int quantity)`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Syrup.java#L52): Applies an 8% bulk discount when ordering cartons of 50 or more bottles (`quantity >= 50 ? subtotal * 0.92 : subtotal`).
   - [`getDosageFormDetails()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Syrup.java#L61): Returns `String.format("Syrup [%d ml bottle, Measuring Cap: %s]", bottleVolumeMl, (dosageMeasuringCupIncluded ? "Included" : "Not Included"))`.

5. **Q: How do [`Injection`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Injection.java) and [`Ointment`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Ointment.java) override the abstract methods from [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java)?**  
   **A:** Both classes specialize their behavior according to clinical packaging:
   - In [`Injection`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Injection.java#L9) (fields: `vialType`, `isColdChainRequired`):
     - [`getStorageCondition()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Injection.java#L47): Dynamically checks `isColdChainRequired`. If true, returns `"COLD CHAIN: Store in refrigerator at 2°C - 8°C. Do not freeze. Protect from light."`; otherwise returns `"Store below 25°C in original packaging. Single use sterile container."`
     - [`calculateDiscountedPrice(int quantity)`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Injection.java#L55): Applies a 10% discount for institutional hospital purchases of 100 or more units (`quantity >= 100 ? subtotal * 0.90 : subtotal`).
     - [`getDosageFormDetails()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Injection.java#L64): Returns `String.format("Injection [%s, %s]", vialType, (isColdChainRequired ? "Cold Chain 2-8°C" : "Room Temp"))`.
   - In [`Ointment`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Ointment.java#L9) (fields: `tubeWeightGrams`, `baseType`):
     - [`getStorageCondition()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Ointment.java#L47): Returns `"Store below 30°C. For external use only. Keep cap tightly closed."`
     - [`calculateDiscountedPrice(int quantity)`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Ointment.java#L52): Applies a 7% bulk discount if `quantity >= 40` (`subtotal * 0.93`).
     - [`getDosageFormDetails()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Ointment.java#L61): Returns `String.format("Topical [%.1fg tube, Base: %s]", tubeWeightGrams, baseType)`.

6. **Q: What interfaces are implemented by [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) and what method signatures do they declare?**  
   **A:** [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L23) implements three custom domain interfaces, plus standard Java interfaces:
   - [`Expirable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Expirable.java#L9): Declares `boolean isExpired()`, `long daysToExpiry()`, and `LocalDate getExpiryDate()`.
   - [`Reorderable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Reorderable.java#L7): Declares `boolean needsReorder()` and `int reorderQuantity()`.
   - [`Reportable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Reportable.java#L7): Declares `String generateSummary()`.
   - [`Comparable<Medicine>`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L23): Declares `int compareTo(Medicine other)` for natural alphanumeric sorting by medicine `code`.
   - `java.io.Serializable`: Marks instances eligible for binary object serialization.

7. **Q: Why are `Expirable`, `Reorderable`, and `Reportable` implemented as interfaces instead of abstract classes?**  
   **A:** Java enforces single inheritance for classes (`extends`), meaning a class can inherit state and code from only one parent class. If `Expirable` or `Reorderable` were abstract classes, `Medicine` could not inherit from all of them simultaneously.  
   Furthermore, interfaces represent decoupled behavioural contracts (capabilities) that unrelated classes across different class hierarchies can implement:
   - [`Batch`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Batch.java#L15) also implements [`Expirable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Expirable.java) and `Comparable<Batch>` independently without extending `Medicine`.
   - In a broader hospital enterprise, medical supplies, reagents, blood bags, or PPE kits could implement [`Expirable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Expirable.java) or [`Reorderable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Reorderable.java) without belonging to the `Medicine` class tree. This follows the **Interface Segregation Principle (ISP)**.

8. **Q: How does multiple interface implementation provide polymorphic flexibility in MITS?**  
   **A:** By implementing multiple interfaces, a single [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) object exhibits multiple polymorphic identities depending on the consumer's context:
   - In the background [`ExpiryMonitorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java), medicines are treated purely as instances of [`Expirable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Expirable.java) to query `isExpired()` and `daysToExpiry()`.
   - In the restock pipeline ([`LowStockMonitor`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/LowStockMonitor.java)), medicines are treated as [`Reorderable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Reorderable.java) to evaluate `needsReorder()` and `reorderQuantity()`.
   - In the report generator ([`ReportService`](file:///c:/Users/devis/Downloads/java/src/com/mits/report/ReportService.java)), medicines are processed as [`Reportable`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Reportable.java) to call `generateSummary()`.  
   This decouples services from the concrete class hierarchy and allows callers to depend only on the minimal behavioral interface they need.

9. **Q: Explain the multilevel inheritance chain in `Antibiotic extends Tablet extends Medicine`. Why was this design chosen?**  
   **A:** The hierarchy represents classic 3-tier multilevel inheritance:
   $$\text{Medicine (Abstract Class)} \longrightarrow \text{Tablet (Concrete Class)} \longrightarrow \text{Antibiotic (Specialized Subclass)}$$
   - Level 1 ([`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java)): Defines generic medicine properties (`code`, `name`, `unitPrice`, `batches`, synchronization logic).
   - Level 2 ([`Tablet`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java)): Adds oral solid tablet attributes (`stripSize`, `isChewable`) and tablet-specific behavior.
   - Level 3 ([`Antibiotic`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Antibiotic.java)): Antibiotics in oral dosage form are manufactured as tablets (or capsules), thus inheriting strip packaging and tablet administration characteristics from `Tablet`. However, antibiotics carry clinical and legal restrictions (Schedule-H prescription requirements, microbial spectrum) requiring further specialization.  
   Multilevel inheritance eliminates duplicate code: [`Antibiotic`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Antibiotic.java) directly reuses `stripSize` and `isChewable` from `Tablet` while inheriting stock calculation and batch FIFO issuance from `Medicine`.

10. **Q: How does [`Antibiotic`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Antibiotic.java) customize the behavior it inherits from [`Tablet`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java) and [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java)?**  
    **A:** [`Antibiotic`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Antibiotic.java#L9) introduces fields `spectrum` (e.g. `"Broad-Spectrum"`, `"Narrow-Spectrum"`) and `requiresPrescription` (boolean). It overrides methods to enforce strict pharmacological constraints:
    - [`getStorageCondition()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Antibiotic.java#L48): Overrides `Tablet`'s generic room temperature rule with: `"Store in cool, dry place away from direct sunlight (15°C - 25°C). Keep sealed."`
    - [`calculateDiscountedPrice(int quantity)`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Antibiotic.java#L53): Replaces `Tablet`'s 5%/10% bulk discount policy with a restricted 4% discount available only for orders of 200 or more units (`quantity >= 200 ? subtotal * 0.96 : subtotal`), modeling strict regulatory price controls on antimicrobial drugs to discourage over-purchasing.
    - [`getDosageFormDetails()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Antibiotic.java#L63): Returns `String.format("Antibiotic Tablet [%s, Rx Required: %s, %d/strip]", spectrum, (requiresPrescription ? "YES (Schedule-H)" : "NO"), getStripSize())`, combining its own properties with `getStripSize()` inherited from `Tablet`.

11. **Q: How is method overloading implemented in [`Medicine.addStock`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L132)? Explain the 3 overloaded versions.**  
    **A:** Method overloading (compile-time polymorphism) allows multiple methods in the same class to share the same name with different parameter lists. In [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java):
    - **Overload 1:** `public synchronized void addStock(int quantity)`  
      Takes only an integer quantity. It assigns a default 1-year expiry date by delegating to Overload 2: `addStock(quantity, LocalDate.now().plusYears(1))`.
    - **Overload 2:** `public synchronized void addStock(int quantity, LocalDate expiryDate)`  
      Takes a quantity and an explicit `LocalDate`. It auto-generates a unique batch number (`"B-" + code + "-" + System.currentTimeMillis() % 10000`), instantiates a new [`Batch`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Batch.java), and delegates to Overload 3: `addStock(quantity, b)`.
    - **Overload 3:** `public synchronized void addStock(int quantity, Batch batch)`  
      Takes a quantity and a complete [`Batch`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Batch.java) object. It checks if the batch number already exists in `batches` (incrementing quantity if found) or appends the new batch, and sorts the batch list by expiration date using `Collections.sort(batches)`.

12. **Q: How is method overloading implemented in [`InventoryService.search`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java#L386)? Explain the 3 overloaded versions.**  
    **A:** In [`InventoryService`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java), three search operations are overloaded with distinct signatures:
    - **Overload 1:** `public synchronized List<Medicine> search(String query)`  
      Performs case-insensitive substring searching across medicine code, name, and description using [`StringProcessor.matchesQuery()`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/StringProcessor.java#L116).
    - **Overload 2:** `public synchronized List<Medicine> search(String query, String category)`  
      Filters results by both text query and specific category (`"TABLET"`, `"ANTIBIOTIC"`, `"SYRUP"`, `"INJECTION"`, `"OINTMENT"`). If `category` is `"ALL"` or empty, it returns the result of `search(query)`.
    - **Overload 3:** `public synchronized List<Medicine> search(double minPrice, double maxPrice)`  
      Filters medicines whose `unitPrice` falls within the numeric boundary `[minPrice, maxPrice]`.  
    Callers choose the appropriate search filter based on the parameters passed at compile-time.

13. **Q: What is the technical difference between compile-time polymorphism (overloading) and runtime polymorphism (overriding) as demonstrated in MITS?**  
    **A:** 
    - **Compile-Time Polymorphism (Method Overloading):**  
      Demonstrated by `addStock(int)`, `addStock(int, LocalDate)`, and `addStock(int, Batch)` in [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java), as well as `search(...)` in [`InventoryService`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java). The compiler determines which method to bind at compile-time based on the method signature (name, parameter count, and parameter types). Methods exist within the same class scope.
    - **Runtime Polymorphism (Method Overriding):**  
      Demonstrated by `getStorageCondition()`, `calculateDiscountedPrice(int)`, and `getDosageFormDetails()` in [`Tablet`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Tablet.java), [`Syrup`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Syrup.java), [`Injection`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Injection.java), and [`Ointment`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Ointment.java). The method signature remains identical across superclass and subclasses. The exact method executed is determined dynamically at runtime by the JVM's virtual method table (vtable) based on the actual object type instantiated on the heap.

14. **Q: Give a concrete example of method overriding in MITS and explain why the `@Override` annotation is vital.**  
    **A:** In [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L73), `public abstract String getStorageCondition();` is declared. In [`Ointment`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Ointment.java#L47), this method is overridden:
    ```java
    @Override
    public String getStorageCondition() {
        return "Store below 30°C. For external use only. Keep cap tightly closed.";
    }
    ```
    The `@Override` annotation instructs the Java compiler to verify that a method with this exact name, return type, and parameter list exists in the superclass. If a developer accidentally misspells the name (e.g., `getStorageConditions()` with an 's') or changes a parameter type, the compiler issues an immediate compilation error instead of silently treating it as an unintended overload, preventing subtle runtime bugs.

15. **Q: How does dynamic method dispatch work when calling `calculateDiscountedPrice` on a `Medicine` reference?**  
    **A:** When code executes:
    ```java
    Medicine med = inventory.getMedicine("MED-109"); // Returns Insulin (an Injection)
    double price = med.calculateDiscountedPrice(100);
    ```
    The reference variable `med` has the static (compile-time) type `Medicine`. However, the actual object on the heap is of runtime type [`Injection`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Injection.java).  
    When `calculateDiscountedPrice(100)` is invoked, the JVM does not execute the abstract method in `Medicine`. Instead, it resolves the invocation dynamically via the object's virtual method table (vtable) and dispatches execution to `Injection.calculateDiscountedPrice(100)`, returning the 10% hospital bulk discount. No manual `if (med instanceof Injection)` casting or `switch` statements are required.

16. **Q: How are `toString()`, `equals()`, and `hashCode()` overridden in [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) and [`Batch`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Batch.java)?**  
    **A:** Both classes override these foundational `java.lang.Object` methods:
    - In [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L336):
      - `equals(Object o)`: Compares `Objects.equals(code, medicine.code)` using Java 16+ pattern matching (`o instanceof Medicine medicine`). Two medicine objects are equal if and only if their unique business code (e.g., `"MED-001"`) matches.
      - `hashCode()`: Returns `Objects.hash(code)`. Conforms to the `equals`/`hashCode` contract: equal objects produce identical hash codes, ensuring dependable behavior in `HashMap` and `HashSet`.
      - `toString()`: Returns `String.format("%s - %s [Stock: %d, ₹%.2f]", code, name, getTotalStock(), unitPrice)`.
    - In [`Batch`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Batch.java#L116):
      - `equals(Object o)`: Checks equality based on `batchNumber`.
      - `hashCode()`: Returns `Objects.hash(batchNumber)`.
      - `toString()`: Returns `String.format("Batch[%s, qty=%d, exp=%s, daysLeft=%d]", batchNumber, quantity, expiryDate, daysToExpiry())`.

17. **Q: Explain the difference between Composition and Aggregation in MITS using the `Medicine`-`Batch` and `Inventory`-`Medicine` relationships.**  
    **A:** Both represent "has-a" relationships, but with distinct lifecycles:
    - **Composition (`Medicine` owns [`Batch`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Batch.java)):**  
      Composition is a strong "death relationship" where the component's lifetime is bound to the owner. In [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L36), `protected List<Batch> batches = new ArrayList<>();` is encapsulated inside the medicine. A batch of Paracetamol cannot exist in the real world or in system memory without its parent medicine. When a `Medicine` instance is garbage-collected or deleted from the system, all of its inner `Batch` instances are deleted with it.
    - **Aggregation ([`Inventory`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Inventory.java) aggregates [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java)):**  
      Aggregation is a weak "has-a" relationship where the parts can exist independently of the container. `Inventory` maintains a collection (`medicineMap`). If an `Inventory` object is cleared, recreated, or closed, the medicines themselves represent real-world clinical catalog items that still exist conceptually and can be persisted to CSV, backed up, or loaded into another warehouse repository.

18. **Q: Explain the Association relationship between [`Supplier`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Supplier.java) and [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) in MITS.**  
    **A:** Association represents a structural relationship where two independent entities interact without owning each other's lifecycle. In MITS:
    - [`Supplier`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Supplier.java#L12) is a self-contained entity stored in `SupplierRepository` with attributes like `id`, `name`, `contactPerson`, `phone`, and `email`.
    - [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java#L32) maintains a loose foreign reference `protected String supplierId;` (e.g. `"SUP-001"`).  
    A supplier exists in the system regardless of whether any medicines are currently associated with it. If a medicine is deleted, its supplier remains unaffected. The relationship is a navigational, loose association via identifier lookup.

---

### Unit 2: Arrays, Jagged Arrays, and Functional Programming

19. **Q: Why is a jagged array used in [`InventoryService.getMonthlyConsumptionJaggedArray()`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java#L442) and what problem does it solve?**  
    **A:** A jagged array (ragged array) is a multi-dimensional array where each sub-array row can have a different length (`int[][] jagged = new int[size][]`).  
    In medical inventory tracking, different medicines have been tracked for differing lengths of time:
    - Long-established staple medicines (e.g., Paracetamol, Aspirin) have 12 months of recorded consumption history.
    - Newly introduced specialty drugs or seasonal formulations may only have 3, 6, 8, or 9 months of active records.  
    If a standard rectangular 2D array (`int[N][12]`) were used, memory would be wasted with unused zero-padded cells, and computing accurate statistical averages would require complicated sentinel value checks. A jagged array allocates exactly the memory required for each medicine's actual historical record length.

20. **Q: How are totals and averages computed over the jagged array in [`InventoryService`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java)?**  
    **A:** In [`InventoryService`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java):
    - [`getMedicineConsumptionTotals(int[][] jagged)`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java#L465):
      Allocates `int[] totals = new int[jagged.length]`. For each row `i`, it iterates across each element `val` in `jagged[i]`, computes `sum += val`, and stores `totals[i] = sum`.
    - [`getMedicineConsumptionAverages(int[][] jagged)`](file:///c:/Users/devis/Downloads/java/src/com/mits/service/InventoryService.java#L481):
      Allocates `double[] averages = new double[jagged.length]`. For each row `i`, it guards against empty rows (`if (jagged[i].length == 0) averages[i] = 0.0;`). Otherwise, it divides the row sum by that specific row's length (`(double) sum / jagged[i].length`). Because the denominator dynamically reflects `jagged[i].length`, the average is statistically accurate for 3-month, 6-month, or 12-month series alike.

21. **Q: How are `Predicate<Medicine>` lambda expressions implemented in [`LambdaProcessor.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java)?**  
    **A:** The [`LambdaProcessor`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java#L22) class provides a generic higher-order filter method:
    ```java
    public static List<Medicine> filter(List<Medicine> medicines, Predicate<Medicine> predicate) {
        List<Medicine> result = new ArrayList<>();
        for (Medicine m : medicines) {
            if (predicate.test(m)) result.add(m);
        }
        return result;
    }
    ```
    It implements domain filters using lambda expressions:
    - [`filterLowStock`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java#L54): Uses a method reference lambda: `Predicate<Medicine> lowStockPredicate = Medicine::needsReorder;`
    - [`filterExpiringWithinDays`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java#L62): Uses a closure lambda: `m -> m.daysToExpiry() >= 0 && m.daysToExpiry() <= days`.

22. **Q: How are `Comparator<Medicine>` lambdas used for sorting across [`LambdaProcessor.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java) and [`Inventory.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Inventory.java)?**  
    **A:** Lambda expressions provide concise in-line comparators without verbose anonymous classes:
    - In [`LambdaProcessor.sortByExpiryDate`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java#L70):
      `Comparator<Medicine> expiryComparator = (m1, m2) -> m1.getExpiryDate().compareTo(m2.getExpiryDate());`
    - In [`LambdaProcessor.sortByStockQuantityDesc`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java#L86):
      `Comparator<Medicine> stockComparator = (m1, m2) -> Integer.compare(m2.getTotalStock(), m1.getTotalStock());`
    - In [`Inventory.getExpirySortedMedicines()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Inventory.java#L93):
      Constructs a `TreeSet<Medicine>` with a multi-criteria lambda comparator that sorts primarily by expiry date ascending, resolving date ties by medicine code:
      ```java
      Comparator<Medicine> expiryComparator = (m1, m2) -> {
          int dateCmp = m1.getExpiryDate().compareTo(m2.getExpiryDate());
          if (dateCmp != 0) return dateCmp;
          return m1.getCode().compareTo(m2.getCode());
      };
      ```
    - In [`Inventory.getReorderPriorityQueue()`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Inventory.java#L111):
      Uses `Comparator.comparingDouble(m -> (double) m.getTotalStock() / Math.max(1, m.getReorderLevel()))` to prioritize medicines with the lowest stock-to-threshold ratio.

23. **Q: What is the custom `@FunctionalInterface StockEvaluator` in [`LambdaProcessor.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java) and how is it used?**  
    **A:** Inside [`LambdaProcessor.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java#L27), a custom functional interface is declared:
    ```java
    @FunctionalInterface
    public interface StockEvaluator {
        double evaluate(Medicine medicine);
    }
    ```
    The `@FunctionalInterface` annotation validates that the interface defines exactly one abstract method (`evaluate`), qualifying it as a valid lambda expression or method reference target.  
    It is used in [`LambdaProcessor.evaluateInventoryRisk(...)`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/LambdaProcessor.java#L124) to calculate aggregate inventory risk scores:
    ```java
    double risk = LambdaProcessor.evaluateInventoryRisk(medicines, 
        m -> m.isExpired() ? 100.0 : (m.needsReorder() ? 50.0 : 0.0));
    ```
    This enables dynamic injection of risk evaluation business algorithms without modifying the underlying class.

---

### Unit 3: Exception Handling and String Processing

24. **Q: What custom exceptions are implemented in MITS, and which are checked versus unchecked?**  
    **A:** MITS implements seven custom exceptions under the `com.mits.exception` package:
    - **Checked Exceptions (extend `java.lang.Exception`):**
      1. [`InsufficientStockException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/InsufficientStockException.java): Thrown when requested dispensing quantity exceeds total available stock.
      2. [`ExpiredMedicineException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/ExpiredMedicineException.java): Thrown when attempting to dispense from a batch that has passed its expiration date.
      3. [`MedicineNotFoundException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/MedicineNotFoundException.java): Thrown when looking up, updating, or deleting a non-existent medicine code.
      4. [`DuplicateMedicineException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/DuplicateMedicineException.java): Thrown when attempting to register a medicine code that already exists.
      5. [`InvalidInputException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/InvalidInputException.java): Thrown when user inputs fail business validation (e.g., negative price, invalid regex code format).
      6. [`AuthenticationException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/AuthenticationException.java): Thrown when user login credentials fail authentication.
    - **Unchecked Exceptions (extend `java.lang.RuntimeException`):**
      1. [`InventoryOperationException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/InventoryOperationException.java): Represents unexpected runtime operational or I/O pipeline failures.

25. **Q: When should checked exceptions be used versus unchecked exceptions in a medical inventory tracking system?**  
    **A:** 
    - **Checked Exceptions (`Exception`):** Must be used for **anticipated, recoverable business conditions** where the calling client code is expected to react programmatically. In a pharmacy, attempting to issue 50 units when only 10 exist ([`InsufficientStockException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/InsufficientStockException.java)) or trying to issue expired medication ([`ExpiredMedicineException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/ExpiredMedicineException.java)) are expected business events that must not crash the application. The compiler enforces that callers handle them with `try-catch` or declare them with `throws`, forcing the UI to display helpful error dialogues to the pharmacist.
    - **Unchecked Exceptions (`RuntimeException`):** Must be used for **unrecoverable system faults or programmer bugs** (e.g. `NullPointerException`, `IndexOutOfBoundsException`, or [`InventoryOperationException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/InventoryOperationException.java) caused by low-level disk I/O failure). These should bubble up to a top-level error handler or logger without cluttering intermediate business methods with mandatory `throws` declarations.

26. **Q: How does [`ExceptionDemo.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/ExceptionDemo.java) demonstrate nested `try` blocks and the `finally` block guarantee?**  
    **A:** In [`ExceptionDemo.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/ExceptionDemo.java):
    - **Nested `try` Blocks ([`demonstrateNestedTry()`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/ExceptionDemo.java#L76)):**  
      An outer `try` block wraps user authentication (`authenticateUser(...)`). Inside this block, an inner `try` block checks for duplicate medicine code registration (`checkDuplicateCode(...)`). If the inner block throws [`DuplicateMedicineException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/DuplicateMedicineException.java), the inner `catch` block catches and handles it locally, allowing execution in the outer `try` block to resume safely without terminating the outer workflow. If authentication fails, the outer `catch` handles [`AuthenticationException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/AuthenticationException.java).
    - **`finally` Block Guarantee ([`demonstrateFinallyBlock()`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/ExceptionDemo.java#L110)):**  
      Simulates an open socket/file connection (`connectionOpen = true`). Inside the `try`, an [`InventoryOperationException`](file:///c:/Users/devis/Downloads/java/src/com/mits/exception/InventoryOperationException.java) is deliberately thrown. The `finally` block executes **unconditionally**—regardless of whether an exception occurred, was caught, or bypassed handlers—setting `connectionOpen = false` to guarantee that critical system resources are never leaked.

27. **Q: Compare `String`, `StringBuilder`, and `StringBuffer`. How does immutability impact heap memory allocation?**  
    **A:** 
    - **`String`:** Immutable sequence of characters stored in the heap and String Constant Pool. Once created, its content cannot be altered. Any modification (e.g. `str += "abc"`, `replace()`, `substring()`) creates a brand-new `String` object on the heap, abandoning the old one. In repetitive loops, this causes excessive temporary object allocation, memory fragmentation, and frequent Garbage Collection pauses.
    - **`StringBuilder`:** Mutable buffer of characters. Modifications (`append()`, `insert()`) alter characters directly in an internal resizing `char[]` buffer without creating new heap objects. It is **not synchronized** (single-threaded), making it the fastest choice for text formatting.
    - **`StringBuffer`:** Mutable buffer of characters with the same API as `StringBuilder`, but all public methods are **synchronized**. It is thread-safe, making it suitable for concurrent shared logging across multiple threads, with a small locking overhead.

28. **Q: Where and why are `String`, `StringBuilder`, and `StringBuffer` specifically used in [`StringProcessor.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/StringProcessor.java)?**  
    **A:** In [`StringProcessor.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/StringProcessor.java):
    - **`String`:** Used for immutable business tokens, validation, and search matching:
      - Validating codes via regular expressions: `MEDICINE_CODE_PATTERN.matcher(code.trim()).matches()`
      - CSV parsing: `String[] tokens = csvLine.split(",", -1)`
      - Searching: `target.toLowerCase().contains(query.toLowerCase())`
    - **`StringBuilder`:** Used in single-threaded formatting helpers:
      - [`padRight(text, len)`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/StringProcessor.java#L75) and [`padLeft(text, len)`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/StringProcessor.java#L88): Rapidly pads spacing without creating temporary string garbage.
      - [`repeatChar(ch, count)`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/StringProcessor.java#L126): Constructs ASCII table borders for text reports.
    - **`StringBuffer`:** Used for the central system audit log buffer:
      ```java
      private static final StringBuffer AUDIT_LOG_BUFFER = new StringBuffer();
      ```
      Because background worker threads ([`ExpiryMonitorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java), [`AutoBackupThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/AutoBackupThread.java)), TCP socket threads, and the GUI thread all invoke [`StringProcessor.logActivity(msg)`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/StringProcessor.java#L29) concurrently, `StringBuffer` guarantees that log messages are appended atomically without interleaved text or memory corruption.

---

### Unit 4: Multithreading, Concurrency, and File I/O

29. **Q: How do [`ExpiryMonitorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java) and [`LowStockMonitor`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/LowStockMonitor.java) demonstrate the two distinct ways of creating threads in Java?**  
    **A:** 
    1. **Extending `java.lang.Thread`:** Demonstrated by [`ExpiryMonitorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java#L17):
       ```java
       public class ExpiryMonitorThread extends Thread { ... }
       ```
       The class inherits directly from `Thread` and overrides `run()`. It has direct access to thread control methods (`setName()`, `setDaemon()`, `setPriority()`, `interrupt()`).
    2. **Implementing `java.lang.Runnable`:** Demonstrated by [`LowStockMonitor`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/LowStockMonitor.java#L15):
       ```java
       public class LowStockMonitor implements Runnable { ... }
       ```
       The class implements the single abstract method `public void run()`. It decouples the task logic from the execution mechanism. Because Java does not permit multiple class inheritance, implementing `Runnable` preserves the option for `LowStockMonitor` to extend another domain class while allowing it to be executed via `new Thread(runnable).start()` or managed by an `ExecutorService` thread pool.

30. **Q: How are [`ExpiryMonitorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java) and [`LowStockMonitor`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/LowStockMonitor.java) stopped gracefully without using the deprecated `Thread.stop()`?**  
    **A:** In both classes, a graceful shutdown pattern is implemented using a `volatile` flag and thread interruption:
    - Both declare `private volatile boolean running = true;`. The `volatile` keyword ensures that updates made to the variable by one thread (e.g. the UI thread) are immediately visible to the background thread without thread-local CPU caching.
    - The main loop condition checks: `while (running && !isInterrupted())`.
    - In [`ExpiryMonitorThread.stopMonitoring()`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java#L79), it sets `this.running = false;` and calls `interrupt()`. If the thread is sleeping inside `Thread.sleep(intervalMillis)`, the interruption causes an `InterruptedException` to be thrown immediately, waking the thread so it can exit its loop and terminate cleanly.
    This avoids the dangerous deprecated `Thread.stop()` method, which could release object monitors abruptly and leave shared data structures in inconsistent states.

31. **Q: What is a daemon thread in Java and why are [`ExpiryMonitorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java) and [`AutoBackupThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/AutoBackupThread.java) marked as daemon threads?**  
    **A:** In Java, threads are either user (non-daemon) threads or daemon threads. A daemon thread is a background service provider whose lifecycle is subordinate to user threads. The JVM terminates automatically as soon as all user threads (such as the main thread and the Swing Event Dispatch Thread) finish running, regardless of whether daemon threads are still executing.  
    In both [`ExpiryMonitorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java#L28) and [`AutoBackupThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/AutoBackupThread.java#L22), the constructors explicitly call:
    ```java
    setDaemon(true);
    ```
    If these threads were left as regular user threads, their infinite `while (running)` loops would keep the JVM running even after the user closes the GUI window (`DashboardFrame`), resulting in orphaned background processes consuming CPU and memory.

32. **Q: Why is the `synchronized` keyword placed on stock operations (`issueStock` and `addStock`) in [`Medicine`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Medicine.java) and [`Inventory`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Inventory.java)?**  
    **A:** Stock deduction is a multi-step **read-modify-write** critical section:
    1. Read the current total stock across batches (`getTotalStock()`).
    2. Check if total stock is sufficient (`quantity <= total`).
    3. Iterate through batches to deduct units via FIFO (`batch.deductQuantity()`).  
    If multiple concurrent threads (such as multiple TCP socket clients or simultaneous pharmacists) invoke `issueStock` without synchronization, a **race condition** occurs: two threads might simultaneously read `stock = 10`, both conclude that 10 units are available to dispense, and both proceed to deduct 10 units. This leads to overselling, inventory discrepancies, or negative stock balances.  
    Declaring `synchronized` acquires the object's intrinsic monitor lock, ensuring that only one thread can execute within the method at any given moment, guaranteeing **atomicity, visibility, and mutual exclusion**.

33. **Q: How does [`ConcurrencyDemo.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ConcurrencyDemo.java) prove that race conditions are prevented under high concurrent load?**  
    **A:** [`ConcurrencyDemo.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ConcurrencyDemo.java#L21) performs an automated, mathematically verified stress test:
    - **Initial State:** Instantiates a dedicated medicine (`Tablet testMedicine`) with an initial stock of exactly **1,000 units** in batch `B-CONC-01`.
    - **Concurrency Setup:** Launches **6 concurrent worker threads** (`Dispenser-Thread-1` through `6`), exceeding the minimum requirement of 5 threads.
    - **Operations Per Thread:** Each thread runs a loop issuing stock **15 times**, requesting **10 units** on each iteration ($15 \times 10 = 150\text{ units}$ issued per thread).
    - **Expected Total Deduction:** $6 \times 150 = 900\text{ units}$.
    - **Expected Final Stock:** $1000 - 900 = 100\text{ units}$.
    - **Verification:** The main thread invokes `t.join()` on all 6 worker threads, blocking until all threads terminate. It then inspects `testMedicine.getTotalStock()`. Because `issueStock` is `synchronized`, the final stock matches **100 units** exactly. If synchronization were omitted, race conditions would cause lost updates and unpredictable stock discrepancies.

34. **Q: How do `wait()` and `notifyAll()` implement the Producer-Consumer pattern in [`AlertNotificationQueue`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/AlertNotificationQueue.java)?**  
    **A:** [`AlertNotificationQueue`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/AlertNotificationQueue.java#L14) coordinates asynchronous alert production and consumption over a shared bounded queue:
    - **Producer ([`produce(Alert alert)`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/AlertNotificationQueue.java#L29)):**
      Acquires the monitor lock. If the queue is at capacity (`queue.size() >= capacity`), it invokes `wait()`. This suspends the producer and releases the monitor lock so consumers can run. When woken, it enqueues the alert and calls `notifyAll()` to alert waiting consumers that data is available.
    - **Consumer ([`consume()`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/AlertNotificationQueue.java#L52)):**
      Acquires the monitor lock. If the queue is empty (`queue.isEmpty()`), it invokes `wait()`. Once a producer enqueues an item and calls `notifyAll()`, the consumer wakes, dequeues the item (`queue.poll()`), and calls `notifyAll()` to wake any producers waiting for queue space.  
    Both methods enclose `wait()` inside a `while` loop (not an `if`) to guard against **spurious wakeups**.

35. **Q: Explain the difference between Byte Streams and Character Streams as used in [`FilePersistenceManager.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/io/FilePersistenceManager.java).**  
    **A:** In [`FilePersistenceManager.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/io/FilePersistenceManager.java):
    - **Byte Streams (`InputStream` / `OutputStream`):**
      Handle raw 8-bit bytes without character encoding translation. Used in [`saveInventoryBinary`](file:///c:/Users/devis/Downloads/java/src/com/mits/io/FilePersistenceManager.java#L49) and [`loadInventoryBinary`](file:///c:/Users/devis/Downloads/java/src/com/mits/io/FilePersistenceManager.java#L64) via `FileOutputStream`, `BufferedOutputStream`, and `ObjectOutputStream` (and matching input streams) to serialize and deserialize the entire Java object graph (`Inventory`, `Medicine`, `Batch`) to `./data/inventory.ser`.
    - **Character Streams (`Reader` / `Writer`):**
      Handle 16-bit Unicode characters with automatic character set encoding/decoding. Used in [`exportMedicinesCsv`](file:///c:/Users/devis/Downloads/java/src/com/mits/io/FilePersistenceManager.java#L90), [`importMedicinesCsv`](file:///c:/Users/devis/Downloads/java/src/com/mits/io/FilePersistenceManager.java#L145), and [`appendAuditLog`](file:///c:/Users/devis/Downloads/java/src/com/mits/io/FilePersistenceManager.java#L217) via `FileWriter`, `BufferedWriter`, `PrintWriter`, `FileReader`, and `BufferedReader` to read and write human-readable CSV and audit log files.  
    All streams use Java's `try-with-resources` syntax to guarantee automatic resource closing.

---

### Unit 4 & Unit 5: Networking, Generics, Collections, and GUI

36. **Q: Describe the custom TCP socket protocol implemented in [`InventoryServer`](file:///c:/Users/devis/Downloads/java/src/com/mits/network/InventoryServer.java) and [`InventoryClient`](file:///c:/Users/devis/Downloads/java/src/com/mits/network/InventoryClient.java).**  
    **A:** The MITS networking layer implements a multi-client client-server architecture over TCP port 8888 (`ServerSocket` and `Socket`). The server uses an `ExecutorService` thread pool (`Executors.newCachedThreadPool()`) to handle each incoming connection in a dedicated [`ClientHandler`](file:///c:/Users/devis/Downloads/java/src/com/mits/network/InventoryServer.java#L103) runnable. The line-based text protocol supports six core commands:
    1. `LOGIN <username> <password>`: Authenticates credentials; returns `OK Welcome <name> (<role>)` or `ERROR <reason>`.
    2. `SEARCH <query>`: Queries inventory; returns `COUNT <n>`, followed by `DATA <details>` rows and `OK END_SEARCH`.
    3. `STOCK <code>`: Checks stock for a specific medicine; returns `OK CODE: ... STOCK: ...` or `ERROR`.
    4. `ISSUE <code> <qty>`: Requires prior login and role authorization (`canManageStock()`); safely issues stock and returns `OK Successfully issued...`.
    5. `ALERTS`: Returns active system alerts; returns `COUNT <n>`, lines of `DATA [...]`, and `OK END_ALERTS`.
    6. `QUIT`: Closes the client session with `200 Bye. Connection closing.`.

37. **Q: How does the generic [`Repository<T, ID>`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/Repository.java) class work, including `findBy(Predicate)` and bounded type `findMax`?**  
    **A:** In [`Repository.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/util/Repository.java#L18):
    - **Class Generics (`<T, ID>`):**  
      `T` represents the domain entity type (e.g. `User`, `Supplier`, `Medicine`), and `ID` represents the identifier type (e.g. `String`). It stores items in `Map<ID, T> storage = new LinkedHashMap<>()` using an injected key extractor function `Function<T, ID> idExtractor`.
    - **Predicate Filtering:**  
      `public synchronized List<T> findBy(Predicate<T> predicate)` iterates through `storage.values()`, testing each element against `predicate.test(item)` to return matching items.
    - **Bounded Type Parameter Method (`findMax`):**  
      ```java
      public static <E extends Comparable<E>> E findMax(List<E> list)
      ```
      The type parameter `<E extends Comparable<E>>` ensures at compile time that type `E` implements natural ordering via `Comparable`. The method iterates through the list, safely calling `item.compareTo(max) > 0` to identify the maximum element without requiring unchecked runtime casts.
    - **Upper-Bounded Wildcard Method:**  
      `public static <E> void printAll(Collection<? extends E> collection)` accepts any collection whose element type is `E` or a subtype of `E`.

38. **Q: Identify the 5 key collections used in [`Inventory.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Inventory.java) and explain why each was chosen.**  
    **A:** In [`Inventory.java`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Inventory.java#L25):
    1. **`HashMap<String, Medicine> medicineMap`:**  
       Provides $O(1)$ constant-time performance for primary key lookups, updates, and removals using normalized uppercase medicine codes (e.g., `"MED-001"`).
    2. **`TreeSet<Medicine> getExpirySortedMedicines()`:**  
       A sorted set backed by a Red-Black tree. Uses a custom comparator to maintain medicines sorted by earliest expiration date, providing $O(\log N)$ insertion and keeping near-expiry items immediately accessible at the front of the set.
    3. **`LinkedList<Alert> alertHistory`:**  
       A doubly-linked list used as a bounded FIFO/LIFO deque. New alerts are inserted at the head via `addFirst()` in $O(1)$ time; once size exceeds 100 entries, the oldest alert is removed via `removeLast()` in $O(1)$ time.
    4. **`HashSet<String> categories`:**  
       A hash table set ensuring uniqueness for category names, with $O(1)$ add and search operations.
    5. **`PriorityQueue<Medicine> getReorderPriorityQueue()`:**  
       A binary min-heap ordered by stock-to-reorder ratio (`totalStock / reorderLevel`). Guarantees that the medicine with the most critically depleted inventory is always at the head of the queue ($O(1)$ lookup via `peek()`, $O(\log N)$ via `poll()`).

39. **Q: How is Role-Based Access Control (RBAC) implemented in [`DashboardFrame`](file:///c:/Users/devis/Downloads/java/src/com/mits/gui/DashboardFrame.java) to govern tab visibility?**  
    **A:** In [`DashboardFrame.initUI()`](file:///c:/Users/devis/Downloads/java/src/com/mits/gui/DashboardFrame.java#L89), tabs are conditionally added to the central `JTabbedPane` based on the authenticated user's [`Role`](file:///c:/Users/devis/Downloads/java/src/com/mits/model/Role.java) (`ADMIN`, `PHARMACIST`, or `DOCTOR`):
    - **Read-Only Tabs:**  
      `"Dashboard"`, `"Search"`, `"Reports"`, and `"Remote Server"` are added for all authenticated users, allowing doctors to search medications and view stock status.
    - **Stock Management Tabs (Restricted):**
      ```java
      if (user.canManageStock()) {
          tabbedPane.addTab("Add/Edit Medicine", createAddEditTab());
          tabbedPane.addTab("Stock Operations", createStockOpsTab());
      }
      ```
      `user.canManageStock()` returns `true` only for `ADMIN` and `PHARMACIST`. Doctors cannot see or access these tabs, preventing unauthorized medicine additions or stock issuance.
    - **Administrative Tools Tab (Strictly Restricted):**
      ```java
      if (user.isAdmin()) {
          tabbedPane.addTab("Backup & Tools", createBackupToolsTab());
      }
      ```
      `user.isAdmin()` returns `true` only for `Role.ADMIN`. This tab provides manual backup creation, backup restoration, and demo test execution, protecting administrative features from unauthorized access.

40. **Q: Why is `SwingUtilities.invokeLater` necessary when updating GUI components from background threads in MITS?**  
    **A:** The Java Swing GUI toolkit is single-threaded and **not thread-safe**. All Swing components (`JTextArea`, `JProgressBar`, `JTable`, etc.) must be created, modified, and queried exclusively on the **Event Dispatch Thread (EDT)**.  
    In MITS, multiple background threads run concurrently:
    - [`ExpiryMonitorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ExpiryMonitorThread.java) detects expired medicines and dispatches alerts.
    - [`ReportGeneratorThread`](file:///c:/Users/devis/Downloads/java/src/com/mits/concurrent/ReportGeneratorThread.java) computes reports asynchronously in the background.  
    If these background threads were to update UI components directly (e.g. `alertsArea.append(...)` or `reportProgress.setValue(...)`), race conditions, paint artifacts, corrupted component state, or deadlocks could occur.  
    To update the GUI safely, MITS wraps UI modifications in `SwingUtilities.invokeLater(...)`:
    ```java
    inventoryService.addAlertListener(alert -> SwingUtilities.invokeLater(() -> {
        if (alertsArea != null) {
            alertsArea.append(alert + "\n");
            alertsArea.setCaretPosition(alertsArea.getDocument().getLength());
        }
    }));
    ```
    This packages the UI update logic into a `Runnable` and places it on the EDT's event queue, guaranteeing that the GUI is modified safely and synchronously by the EDT.
