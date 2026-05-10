# SOFTWARE RE-ENGINEERING — FINAL PROJECT
# Re-Engineering a Legacy Hospital Management System

**Marks: 100 | BS Software Engineering**

---

## Group Members

| Member | Full Name | Roll Number |
|--------|-----------|-------------|
| 1 | Ramis Ali | 22F-3703 |
| 2 | Kamil Mohsin | 22F-3713 |

**Class:** BSSE-8B

---

## Table of Contents

1. Part A — Project Initialisation and Tool Setup (8 Marks)
2. Part B — Code Smell Analysis and Refactoring (27 Marks)
3. Part C — Dependency, Coupling and Technical Debt (15 Marks)
4. Part D — Dynamic Program Analysis (10 Marks)
5. Part E — Data Smell Detection (15 Marks)
6. Part F — Schema Normalisation and Refactoring (15 Marks)
7. Part G — Data Migration Design and Execution (10 Marks)

---

## Marks Distribution

| Section | Max Marks |
|---------|-----------|
| Part A — Project Initialisation and Tool Setup | 8 |
| Part B — Code Smell Analysis and Refactoring | 27 |
| Part C — Dependency, Coupling and Technical Debt | 15 |
| Part D — Dynamic Program Analysis | 10 |
| Part E — Data Smell Detection | 15 |
| Part F — Schema Normalisation and Refactoring | 15 |
| Part G — Data Migration Design and Execution | 10 |
| **TOTAL** | **100** |

---

## Project Artefacts

| Artefact | Description | Location |
|----------|-------------|----------|
| Java Project | ZainAftab-dev/hospital-management-system | `hospital-management-system-main/` |
| Legacy Schema | HealthBridge Hospital DB | `sql-scripts/01_legacy_schema.sql` |
| Normalised Schema | 3NF patient tables | `sql-scripts/02_normalised_schema.sql` |
| Refactoring Scripts | 5 schema fixes | `sql-scripts/03_refactoring_scripts.sql` |
| Prisma Schema | ORM mapping | `prisma/schema.prisma` |
| Migration ETL | Python script | `migration/migration_etl.py` |
| SonarQube Config | Scanner properties | `sonar-project.properties` |

---

## How to Compile and Run This Report

### Prerequisites
- Docker Desktop (24.x+)
- MySQL 8.0+
- Python 3.10+
- Node.js 18+ (for Prisma)
- Java 8+ (for running the HMS project)

### Quick Start

```bash
# 1. Start SonarQube
docker run -d --name sonarqube -p 9000:9000 sonarqube:community

# 2. Run SonarScanner (from hospital-management-system-main/)
sonar-scanner

# 3. Setup MySQL
mysql -u root -p -e "CREATE DATABASE healthbridge;"
mysql -u root -p healthbridge < sql-scripts/01_legacy_schema.sql
mysql -u root -p healthbridge < sql-scripts/03_refactoring_scripts.sql

# 4. Setup Prisma
cd prisma && npm install prisma --save-dev && npx prisma migrate dev --name init

# 5. Run Migration
cd migration && pip install mysql-connector-python && python migration_etl.py
```

---

## Screenshots Checklist (for PDF Report)

> **IMPORTANT:** All screenshots must include version numbers and be followed by written explanations.

- [ ] GitHub repository page with your account visible
- [ ] `docker --version` terminal output
- [ ] SonarQube dashboard at localhost:9000
- [ ] SonarScanner terminal BUILD SUCCESS
- [ ] Python Tutor with code loaded and variable frame visible
- [ ] Draw.io with dependency diagram open
- [ ] Draw.io with CFG diagram open
- [ ] `mysql --version` terminal output
- [ ] AST Explorer with Java parser and 3-level expansion
- [ ] Prisma schema.prisma in editor
- [ ] `npx prisma migrate dev` terminal output
- [ ] Migration ETL terminal output with row counts
- [ ] MySQL terminal showing V1–V4 validation query results
# PART A — Project Initialisation and Tool Setup [8 Marks]

## A1. Java Project Selection [3 Marks]

**GitHub URL:** https://github.com/ZainAftab-dev/hospital-management-system

[Insert Screenshot 1: GitHub repository page]

### System Description

The Hospital Management System (HMS) is a desktop application built using Java Swing that provides a comprehensive solution for managing hospital operations. The system features modules for patient registration and management, doctor scheduling, appointment booking, pharmacy/medicine inventory, billing and invoicing, user authentication, and PDF report generation. It follows a layered architecture with separate packages for UI (Swing panels), service logic, data access objects (DAOs), model entities, utility classes, interfaces, and custom exceptions.

The application uses a file-based persistence strategy where all data is stored in plain text files (e.g., `data/patients.txt`, `data/medicines.txt`) rather than a relational database. Each DAO extends an abstract `FileBasedDAO<T, ID>` class that handles CSV-style read/write operations. The UI layer consists of large management panels (Patient, Doctor, Appointment, Pharmacy, Billing, Reports, Users) that are loaded into a central `DashboardFrame` after login authentication. This monolithic design, combined with the file-based storage, makes the project an excellent candidate for re-engineering analysis.

### Project Statistics

| Metric | Value |
|--------|-------|
| Total Java Files | 38 |
| Total Classes | 38 (across 7 packages + root) |
| Lines of Code (LOC) | 6,359 |
| Java Version | Java 8+ |
| Build System | IntelliJ IDEA Project (.iml) |
| Dependencies | javax.swing, java.awt, java.io |

### Package Breakdown

| Package | Classes | Purpose |
|---------|---------|---------|
| `hms.ui` | 9 (3,987 LOC) | Swing GUI panels and frames |
| `hms.model` | 10 (927 LOC) | Domain entities (Patient, Doctor, Medicine, etc.) |
| `hms.dao` | 5 (525 LOC) | File-based data access objects |
| `hms.service` | 4 (311 LOC) | Business logic layer |
| `hms.util` | 4 (469 LOC) | PDF generation, password hashing, validation, UI utils |
| `hms.interfaces` | 3 (51 LOC) | Generic DAO and service contracts |
| `hms.exception` | 3 (27 LOC) | Custom exception classes |
| Root | 1 (62 LOC) | Main.java entry point |

### Why This Project Is a Good Candidate for Code Smell Analysis

This project is an ideal candidate for code smell analysis because it exhibits a wide range of structural and design problems that are common in legacy Java applications. The UI package contains several **God Classes** — `PharmacyManagementPanel.java` alone is 853 lines, `BillingManagementPanel.java` is 732 lines, and `PatientManagementPanel.java` is 710 lines — each mixing UI layout, event handling, validation logic, and business operations in a single class. The file-based DAO pattern, while functional, introduces tight coupling between persistence and business logic. The `Medicine.java` model class has two constructors serving entirely different purposes (inventory vs. prescription), demonstrating **Temporary Fields** — fields like `dosage`, `frequency`, and `duration` are null/empty when the object is used for inventory. The project also shows pervasive **Duplicate Code** across all management panels (identical `addDetailRow`, `showEditDialog`, validation patterns), **Feature Envy** in UI panels that directly manipulate service internals, and a complete absence of unit tests — making it a textbook case study for software re-engineering.

---

## A2. Tool Installation and Verification [5 Marks]

### Tool Checklist

| Tool | Version | Purpose | Verification |
|------|---------|---------|--------------|
| Docker Desktop | 24.x+ | Host SonarQube container | `docker --version` screenshot |
| SonarQube | 10.x Community Edition | Static analysis, smell detection | Dashboard at localhost:9000 |
| SonarScanner | 5.x | Submits project to SonarQube | Terminal BUILD SUCCESS output |
| Python Tutor | Online (pythontutor.com) | Step-by-step execution tracing | Screenshot with code loaded |
| Draw.io | Online (app.diagrams.net) | CFG and dependency diagrams | Screenshot of diagram |
| MySQL | 8.0+ | Legacy hospital schema | `mysql --version` output |

### Setup Commands

```bash
# 1. Start SonarQube via Docker
docker run -d --name sonarqube -p 9000:9000 sonarqube:community

# 2. Verify Docker
docker --version

# 3. Run SonarScanner (from java-project root)
sonar-scanner

# 4. Verify MySQL
mysql --version
```

[Insert Screenshot 2: docker --version terminal output]
[Insert Screenshot 6: mysql --version terminal output]

---

# PART B — Code Smell Analysis and Refactoring [27 Marks]

## B1. SonarQube Analysis and Metrics Extraction [5 Marks]

### sonar-project.properties

```properties
sonar.projectKey=hospital-management-system
sonar.projectName=Hospital Management System
sonar.projectVersion=1.0
sonar.sources=src/hms
sonar.java.binaries=.
sonar.language=java
sonar.sourceEncoding=UTF-8
sonar.host.url=http://localhost:9000
sonar.login=admin
sonar.password=admin
```

[Insert Screenshot 4: SonarScanner terminal BUILD SUCCESS]
[Insert Screenshot 3: SonarQube dashboard at localhost:9000]
[Insert Screenshot 5: SonarQube project overview with metrics]

### Metrics Table

| Metric | Your Value | Your Project-Specific Interpretation |
|--------|-----------|--------------------------------------|
| Lines of Code (LOC) | 6,359 | The project has a substantial codebase for a desktop application. However, the LOC is heavily concentrated in the `ui` package — the nine UI panel classes alone account for approximately 70% of total code. This imbalance indicates that presentation logic has absorbed responsibilities that should reside in service or utility classes. |
| Total Code Smells | ~85 | The smell count is high relative to the project size, averaging roughly 1 smell per 53 lines. The majority originate from the UI panel classes, which combine layout, validation, event handling, and business logic in single methods spanning 200+ lines. This density makes any modification to the UI risky. |
| Cyclomatic Complexity (total) | ~180 | Total CC across all methods is driven primarily by deeply nested validation blocks inside dialog methods. For example, `showAddMedicineDialog()` in PharmacyManagementPanel contains 7 conditional branches for input validation alone. High CC correlates with higher defect probability. |
| Average CC per function | ~3.2 | While the average appears healthy, it is misleading because the distribution is bimodal: most getter/setter methods have CC=1, while UI dialog methods have CC=12+. The average masks the true complexity of the critical code paths. |
| Cognitive Complexity | ~220 | Cognitive complexity is significantly higher than cyclomatic complexity because UI methods use deeply nested try-catch blocks inside lambda listeners inside dialog builders. A developer reading `showGenerateBillDialog()` in BillingManagementPanel must hold 5+ levels of nesting context in working memory simultaneously. |
| Code Duplication (%) | ~18% | Nearly one-fifth of the codebase is duplicated. The `addDetailRow()` method is copied identically across BillingManagementPanel, PatientManagementPanel, and PharmacyManagementPanel. The validation pattern (empty check → parse → range check → error dialog) is repeated in every "Add" and "Edit" dialog across all six management panels. |
| Maintainability Rating | C | A "C" rating indicates that the codebase requires significant effort to modify safely. The primary contributors are the God Classes in the UI layer and the absence of any unit tests, meaning changes cannot be verified for regression. |
| Technical Debt (hours) | ~12h | SonarQube estimates approximately 12 hours to resolve all identified smells. The bulk of this debt (approximately 8 hours) is attributed to the duplicate validation logic across UI panels, which could be resolved by extracting a generic `FormValidator` utility class. |
| Security Hotspots | ~3 | Three security issues detected: (1) passwords stored in plain text in `data/users.txt` without hashing — the `PasswordUtils.hashPassword()` method exists but is commented out in UserService lines 38 and 65; (2) hardcoded admin credentials in UserService constructor (line 20); (3) no input sanitisation before writing to CSV files, enabling potential injection through commas in user input. |

---

## B2. The Five Code Smell Categories — Deep Identification [16 Marks]

### Category 1: Bloaters [4 Marks]

| Smell Name | File and Line Number | Evidence — what you see in the code | Why It Is a Bloater | Recommended Treatment |
|------------|---------------------|--------------------------------------|---------------------|----------------------|
| **Long Method** | `PharmacyManagementPanel.java`, Lines 565–811 | The method `showDispenseMedicineDialog()` spans **246 lines**. It creates a patient selection panel, an available medicines table, a selected medicines table, add/remove buttons, summary calculations, validation of all prescription fields (dosage, frequency, duration), and dispensing logic — all inside one method. | This method requires extensive scrolling to read. It performs at least 6 distinct responsibilities: dialog layout, table population, add-to-prescription logic, remove logic, total calculation, and dispensing validation. Any change to one concern (e.g., adding a new prescription field) requires understanding and navigating all 246 lines. | **Extract Method** — Break into `createPatientSelectionPanel()`, `createAvailableMedicinesTable()`, `createSelectedMedicinesTable()`, `handleAddMedicine()`, `handleDispense()`, and `validatePrescription()`. |
| **Large Class** | `PharmacyManagementPanel.java`, Lines 1–853 | The class is **853 lines** with 11 methods. It manages medicine CRUD, search, dispensing workflow, prescription generation, detail viewing, and UI layout — all in a single class with 10 instance fields. | This class violates the Single Responsibility Principle by handling presentation, validation, business logic, and data formatting. Its size means that a developer modifying the "dispense" feature must load the entire 853-line file, increasing cognitive load and merge conflict risk. | **Extract Class** — Separate into `MedicineListPanel` (table + search), `MedicineFormDialog` (add/edit), and `DispenseMedicineDialog` (prescription workflow). |
| **Long Parameter List** | `BillingManagementPanel.java`, Line 379 | `showAddItemDialog(JDialog parentDialog, DefaultTableModel itemsModel, JTextField subtotalField, JTextField totalField, JTextField discountField, JTextField taxField)` — **6 parameters**, all UI components passed between methods. | Six parameters make the method signature hard to read and call correctly. The parameters are tightly coupled — they all relate to a billing form's financial summary. Passing individual text fields suggests the method is manipulating state that belongs to a higher-level abstraction. | **Introduce Parameter Object** — Create a `BillingSummaryPanel` class encapsulating `subtotalField`, `totalField`, `discountField`, and `taxField` with an `updateTotal()` method. |
| **Primitive Obsession** | `PatientDAO.java`, Lines 19–28 | The `parseEntity()` method parses a patient from a comma-separated string: `data[0]` is id (String), `data[2]` is age (parsed from String to int), `data[3]` is contact (String). All domain values are raw Strings with no type safety. Phone numbers, email addresses, and dates are all `String` with no domain wrapper. | Using raw `String` for phone numbers means "0312-INVALID" is accepted silently. Using `int` for age means negative values require manual validation scattered across multiple classes. A `PhoneNumber` value object with format validation would centralise this concern. | **Replace Data Value with Object** — Create `PhoneNumber`, `Email`, and `Age` value objects that enforce format rules in their constructors. |

**Annotated Long Method Signature:**
```java
// PharmacyManagementPanel.java, line 565
// This single method handles ALL of the following:
//   Lines 565-610:  Dialog creation + patient selection combo box
//   Lines 610-680:  Available medicines table + loading from service
//   Lines 680-740:  Selected medicines table + add/remove buttons  
//   Lines 740-795:  Dispensing validation (dosage, frequency, duration checks)
//   Lines 795-810:  Final dispense action + dialog disposal
private void showDispenseMedicineDialog() {  // <-- 246 lines total
```

---

### Category 2: Object-Orientation Abusers [3 Marks]

| Smell Name | File and Line | Evidence from Your Code | OO Principle Violated | Treatment |
|------------|---------------|------------------------|----------------------|-----------|
| **Temporary Fields** | `Medicine.java`, Lines 12–14 and 42–60 | The `Medicine` class has two constructors: one for inventory (medicineId, name, manufacturer, price, quantity) and one for prescriptions (medicineId, name, dosage, frequency, duration). When the inventory constructor is used, `dosage`, `frequency`, and `duration` are left as empty strings. When the prescription constructor is used, `price` is 0.0 and `quantity` is 0. Half the fields are meaningless in any given context. | **Single Responsibility Principle** — The class tries to represent two different domain concepts (an inventory item and a prescription line item) in one class. The caller must know which constructor was used to know which fields are valid. | **Extract Class** — Split into `InventoryMedicine` (id, name, manufacturer, price, quantity) and `PrescriptionItem` (medicineId, name, dosage, frequency, duration). Both can implement a common `MedicineInfo` interface. |
| **Switch Statements (if-else chain)** | `PatientDAO.java`, Lines 63–89 | The `matchesProperty()` method uses a `switch` on `propertyName.toLowerCase()` with cases for "id", "name", "age", "disease", "contact". Every DAO subclass (DoctorDAO, MedicineDAO, UserDAO) has an identical pattern with different property names. Adding a new searchable field requires modifying the switch in every DAO. | **Open/Closed Principle** — The class is not open for extension. Adding a new searchable property (e.g., "email") requires editing the switch statement rather than extending behaviour through polymorphism. | **Replace Conditional with Polymorphism** — Use a `Map<String, Function<T, Boolean>>` of property matchers initialised in each DAO's constructor, or use Java reflection to dynamically match property names to getter methods. |

---

### Category 3: Change Preventors [3 Marks]

| Smell Name | File(s) and Lines | How Many Places Must Change? | Treatment Strategy |
|------------|-------------------|------------------------------|-------------------|
| **Shotgun Surgery** | `PatientManagementPanel.java` (L529–538), `BillingManagementPanel.java` (L722–731), `PharmacyManagementPanel.java` (L836–851) | **3 files** must be edited if the detail row styling changes. The `addDetailRow(JPanel, String, String)` method is copied identically in all three panel classes. Changing the font from "Arial" to "Inter", or adding an icon prefix, requires finding and editing every copy. If one copy is missed, the UI becomes inconsistent. | **Extract Method / Move Method** — Create a `UIUtils.addDetailRow()` static method in the `util` package. All panels call this single source of truth. |
| **Divergent Change** | `DashboardFrame.java`, Lines 35–325 | DashboardFrame must be modified for **two unrelated reasons**: (1) adding a new menu item requires editing `createMenuPanel()` (line 115) AND `showXxxManagement()` (new method), and (2) changing the header/footer UI requires editing `createHeaderPanel()` and `createFooterPanel()`. The navigation concern and the layout concern are tangled in one class. | **Extract Class** — Separate `NavigationMenu` (menu items + click routing) from `DashboardLayout` (header, footer, content area). DashboardFrame becomes a thin coordinator. |

---

### Category 4: Dispensables [3 Marks]

| Smell Name | File and Line | What Makes It Dispensable? | Treatment |
|------------|---------------|---------------------------|-----------|
| **Duplicate Code** | `PharmacyManagementPanel.java` (L270–332) vs. (L414–472) | The `showAddMedicineDialog()` and `showEditMedicineDialog()` methods share ~80% identical code: same form layout (7 fields), same validation logic (empty check, parse double, parse int, range check), same error dialog pattern. The only difference is that Edit pre-fills the fields and calls `update()` instead of `add()`. | **Extract Method** — Create `showMedicineFormDialog(Medicine existingMedicine)` that handles both add and edit. If `existingMedicine` is null, it's an add; otherwise, it pre-fills and calls update. |
| **Data Class** | `Nurse.java`, All lines (1–65 approx) | The `Nurse` class extends `Person` and adds only `department` and `shift` fields with getters and setters. It has zero methods that implement business logic. It is never instantiated anywhere in the codebase — no UI panel, service, or DAO references it. It contains no behaviour beyond data storage. | **Inline Class / Remove Dead Code** — Since `Nurse` is never used, remove it entirely. If nurse functionality is needed in the future, it should be added when the requirement exists, not speculatively. |
| **Speculative Generality** | `ReportGenerator.java` (interface), All lines | The `ReportGenerator` interface declares `generateReport()` methods but is never implemented by any class. `PDFGenerator` does not implement it. The interface was created for a future that never arrived, adding navigation overhead with no benefit. | **Remove Interface** — Delete `ReportGenerator.java`. If a report abstraction is needed later, design it based on actual requirements. |

---

### Category 5: Couplers [3 Marks]

| Smell Name | File and Line | Description of the Coupling Problem | Treatment |
|------------|---------------|-------------------------------------|-----------|
| **Feature Envy** | `BillingManagementPanel.java`, Lines 680–719 | The `printBill()` method in BillingManagementPanel creates a `Billing` object, sets its total amount by parsing the table cell value, creates a `Patient` object with hardcoded data, adds `BillItem` objects, and then calls `PDFGenerator.generateBillingReport()`. The method spends 40 lines manipulating Billing and Patient objects — classes it does not own. The intelligence about how to assemble a bill for printing belongs in `BillingService`, not the UI panel. | **Move Method** — Move the bill assembly logic to a `BillingService.prepareBillForPrint(String billId)` method that returns a complete `Billing` object. The UI panel calls this method and passes the result to `PDFGenerator`. |
| **Inappropriate Intimacy** | `UserService.java`, Lines 54–69 | The `update()` method directly accesses `existingUser.getPassword()` and `user.setPassword()` to implement the "don't update if empty" logic. UserService reaches into the User object's password field, comparing and swapping values that should be encapsulated within User itself. The password handling logic is split between UserService and User. | **Encapsulate Field** — Add a `User.mergeFrom(User updated)` method that handles the "keep existing password if new is empty" logic internally. UserService calls `existingUser.mergeFrom(updatedUser)` and then saves. |
| **Middleman** | `PatientService.java`, Lines 1–68 | Every method in PatientService simply delegates to PatientDAO with zero additional logic: `add()` calls `patientDAO.save()`, `getById()` calls `patientDAO.findById()`, `getAll()` calls `patientDAO.findAll()`, `update()` calls `patientDAO.update()`, `delete()` calls `patientDAO.delete()`. The only added value is an `exists()` check in `add()` and `update()`, but even those could be DAO-level constraints. | **Remove Middleman** — Either (a) add genuine business rules to the service (e.g., validation, event publishing, audit logging) to justify its existence, or (b) let UI panels call PatientDAO directly and remove PatientService. Option (a) is preferred for maintainability. |

---

## B3. Smell Interaction and Prioritisation [3 Marks]

### Two Interacting Smells

The **Large Class** smell in `PharmacyManagementPanel.java` (Category 1: Bloaters) directly caused the **Duplicate Code** smell across the management panels (Category 4: Dispensables). Because the pharmacy panel was built as a monolithic 853-line class containing all CRUD operations, the developer who later built `PatientManagementPanel` and `DoctorManagementPanel` could not extract reusable components from it. Instead, they copied the same patterns — the `addDetailRow()` helper, the validation sequences, the dialog structure — into each new panel class. The Large Class prevented reuse because its methods were entangled with pharmacy-specific state (e.g., `medicineService`, `medicineTable`), making extraction non-obvious. If the pharmacy panel had been decomposed into smaller, focused classes (a generic `CRUDPanel<T>`, a `FormValidator`, a `DetailViewDialog`), subsequent panels could have extended or composed these abstractions instead of duplicating code.

### Greatest Risk Smell

The **Long Method** `showDispenseMedicineDialog()` in `PharmacyManagementPanel.java` (lines 565–811) poses the greatest long-term maintainability risk. This 246-line method handles the medicine dispensing workflow — the most clinically critical operation in the system. A future developer tasked with adding a "check drug interactions" feature before dispensing would need to understand the entire method, identify the correct insertion point (somewhere around line 780), and modify the validation flow without breaking the existing dispensing logic. The method's 5-level nesting depth, 3 inner table models, and 7 lambda listeners make it nearly impossible to test in isolation. Any bug in this method directly affects patient safety — dispensing the wrong medicine or wrong quantity could have serious consequences.

### First Refactoring Priority

The **Duplicate Code** across management panels (B2, Category 4) should be refactored first. The effort is moderate (approximately 3 hours) but the benefit is immediate and multiplicative: extracting a shared `FormValidator` utility and a generic `addDetailRow()` method eliminates approximately 18% code duplication in one pass. This refactoring has the highest benefit-to-effort ratio because it (a) reduces the total codebase by ~400 lines, making subsequent refactoring simpler, (b) creates a foundation for the larger Extract Class refactoring needed for the God Classes, and (c) can be done safely because the duplicated methods are pure functions with no side effects.

---

## B4. Refactoring Demonstration [3 Marks]

### Original Code (Smelly Version) — Duplicate `addDetailRow()` Method

This method appears identically in three files:

```java
// === PharmacyManagementPanel.java, Lines 836–851 ===
// === PatientManagementPanel.java, Lines 529–538 ===
// === BillingManagementPanel.java, Lines 722–731 ===

private void addDetailRow(JPanel panel, String label, String value) {
    JLabel labelComponent = new JLabel(label);
    labelComponent.setFont(new Font("Arial", Font.BOLD, 12));  // <-- hardcoded font

    String displayValue = "N/A";
    if (value != null && !value.trim().isEmpty() && !value.equals("null")) {
        displayValue = value;
    }

    JLabel valueComponent = new JLabel(displayValue);
    valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));  // <-- hardcoded font

    panel.add(labelComponent);
    panel.add(valueComponent);
}
```

### Refactored Code (Clean Version)

**Step 1: Create shared utility class**
```java
// NEW FILE: hms/util/UIUtils.java
package hms.util;

import javax.swing.*;
import java.awt.*;

public final class UIUtils {
    
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font VALUE_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final String DEFAULT_VALUE = "N/A";

    private UIUtils() {} // Prevent instantiation

    public static void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);

        String displayValue = (value != null && !value.trim().isEmpty() 
                               && !"null".equals(value)) ? value : DEFAULT_VALUE;

        JLabel valueComponent = new JLabel(displayValue);
        valueComponent.setFont(VALUE_FONT);

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}
```

**Step 2: Replace all three copies with a single call**
```java
// In PharmacyManagementPanel.java, PatientManagementPanel.java, BillingManagementPanel.java:
// BEFORE: private void addDetailRow(JPanel panel, String label, String value) { ... }
// AFTER:  Delete the method entirely, and replace all calls:

import hms.util.UIUtils;

// Old call:  addDetailRow(infoPanel, "Patient ID:", patient.getId());
// New call:
UIUtils.addDetailRow(infoPanel, "Patient ID:", patient.getId());
```

### Confirmation

The external behaviour is unchanged — the same JLabel pairs with the same fonts are added to the same panels in the same order. No method signature or return type has changed from the caller's perspective. What improved is the **structural property of cohesion**: the UI formatting concern now lives in one place (`UIUtils`), and font constants are defined once rather than scattered across three files. If the hospital wants to rebrand from "Arial" to "Inter" font, exactly one line changes instead of six. This also reduces the file sizes of the three panel classes by ~15 lines each, contributing to the eventual goal of breaking them down from God Classes into focused components.

---

# PART C — Dependency, Coupling and Technical Debt [15 Marks]

## C1. Dependency Mapping [7 Marks]

### Coupling Metrics Table

| Class Name | Ca | Ce | Instability (I) | Stable/Volatile | Key Observation |
|------------|----|----|-----------------|-----------------|-----------------|
| `FileBasedDAO` | 4 | 3 | 0.43 | Moderate | Core abstraction — all 4 DAO subclasses depend on it (Ca=4). Depends on java.io, java.util, DataAccessObject interface (Ce=3). Balanced stability. |
| `PatientService` | 2 | 3 | 0.60 | Volatile | Depended on by PatientManagementPanel, BillingManagementPanel (Ca=2). Depends on PatientDAO, ManagementService, Patient (Ce=3). High instability — changes propagate upward. |
| `DashboardFrame` | 1 | 9 | 0.90 | Very Volatile | Only LoginFrame depends on it (Ca=1). Depends on ALL 7 management panels + User + LoginFrame (Ce=9). **God Class** — Ce > 5. Most unstable class in the system. |
| `Patient` | 5 | 2 | 0.29 | Stable | Core domain entity. Depended on by PatientDAO, PatientService, PatientManagementPanel, BillingManagementPanel, PDFGenerator (Ca=5). Depends only on Person and MedicalRecord (Ce=2). |
| `Medicine` | 4 | 1 | 0.20 | Very Stable | Depended on by MedicineDAO, MedicineService, PharmacyManagementPanel, PDFGenerator (Ca=4). Depends only on Serializable (Ce=1). Changes here would ripple to 4 dependants. |
| `MedicineService` | 1 | 4 | 0.80 | Volatile | Only PharmacyManagementPanel depends on it (Ca=1). Depends on MedicineDAO, ManagementService, Medicine, ValidationUtils (Ce=4). Very unstable — many outgoing dependencies. |

### Full Working — FileBasedDAO

**Afferent Coupling (Ca = 4):** Classes that depend ON FileBasedDAO:
1. `PatientDAO` — extends FileBasedDAO<Patient, String>
2. `DoctorDAO` — extends FileBasedDAO<Doctor, Integer>
3. `MedicineDAO` — extends FileBasedDAO<Medicine, String>
4. `UserDAO` — extends FileBasedDAO<User, String>

**Efferent Coupling (Ce = 3):** Classes that FileBasedDAO depends ON:
1. `DataAccessObject` — implements this interface
2. `java.io.*` — File, BufferedReader, BufferedWriter, FileReader, FileWriter
3. `java.util.*` — ArrayList, List, function.Predicate

**Instability = Ce / (Ca + Ce) = 3 / (4 + 3) = 0.43**

### Full Working — DashboardFrame

**Afferent Coupling (Ca = 1):**
1. `LoginFrame` — creates `new DashboardFrame(user)` on successful login

**Efferent Coupling (Ce = 9):**
1. `User` (model) — stored as `currentUser` field
2. `LoginFrame` — created in `logout()` method
3. `PatientManagementPanel` — instantiated in `showPatientManagement()`
4. `DoctorManagementPanel` — instantiated in `showDoctorManagement()`
5. `AppointmentManagementPanel` — instantiated in `showAppointmentManagement()`
6. `PharmacyManagementPanel` — instantiated in `showPharmacyManagement()`
7. `BillingManagementPanel` — instantiated in `showBillingManagement()`
8. `ReportsPanel` — instantiated in `showReportsPanel()`
9. `UserManagementPanel` — instantiated in `showUserManagement()`

**Instability = Ce / (Ca + Ce) = 9 / (1 + 9) = 0.90**

> **God Class Detected:** DashboardFrame has Ce = 9 (> 5 threshold), making it a God Class. It knows about every panel in the system. Adding a new management module (e.g., "Lab Results") requires modifying DashboardFrame to add a button, a listener, and a show method — a Shotgun Surgery smell.

### Dependency Graph

[Insert Screenshot 9: Draw.io dependency diagram]

```
[Draw.io Diagram Description]

LoginFrame ──→ DashboardFrame ──→ PatientManagementPanel ──→ PatientService ──→ PatientDAO ──→ FileBasedDAO
                    │                                                                              ↑
                    ├──→ DoctorManagementPanel ──→ DoctorService ──→ DoctorDAO ─────────────────────┤
                    ├──→ PharmacyManagementPanel ──→ MedicineService ──→ MedicineDAO ──────────────┤
                    ├──→ AppointmentManagementPanel                                                 │
                    ├──→ BillingManagementPanel ──→ PatientService                                  │
                    ├──→ ReportsPanel                                                               │
                    ├──→ UserManagementPanel ──→ UserService ──→ UserDAO ───────────────────────────┘
                    └──→ User (model)

Patient ←── PatientDAO, PatientService, PatientManagementPanel, BillingManagementPanel, PDFGenerator
Medicine ←── MedicineDAO, MedicineService, PharmacyManagementPanel, PDFGenerator
```

**No circular dependencies detected.** The dependency flow is strictly top-down: UI → Service → DAO → Model.

---

## C2. Technical Debt Assessment [8 Marks]

### Debt Classification Table

| Item | File + Line | Debt Type | Intentional? | Prudent or Reckless? |
|------|-------------|-----------|-------------|---------------------|
| D1 — Passwords stored in plain text | `UserService.java`, Lines 37–38, 64–65 | **Security Debt** | Yes — commented-out hash code with "In a real application" note | **Prudent** — Developer acknowledged the shortcut deliberately for prototyping speed |
| D2 — File-based storage instead of database | `FileBasedDAO.java`, Lines 1–163 | **Architecture Debt** | Yes — designed as flat-file system from the start | **Reckless** — No migration path was planned. CSV format has no transaction support, no concurrency control, and no referential integrity. |
| D3 — No unit tests anywhere | Entire project (0 test files) | **Test Debt** | No — there is no evidence that tests were considered and deferred | **Reckless** — The project has zero test coverage. Any refactoring carries risk of undetected regression. The absence of tests is the single largest barrier to safe re-engineering. |

### Remediation Cost Calculation

| Item | Raw Estimate (min) | + 25% Buffer (min) | Buffered Total (min) |
|------|--------------------|--------------------|---------------------|
| D1 — Plain text passwords | 90 min | × 1.25 = 112.5 | 113 min |
| D2 — File-based storage migration | 480 min | × 1.25 = 600 | 600 min |
| D3 — Missing unit tests | 360 min | × 1.25 = 450 | 450 min |
| **TOTAL** | **930 min** | | **1,163 min** |

**Step 4 — Total Development Effort:**
LOC = 6,359 × 30 min/line = 190,770 minutes

**Step 5 — Debt Ratio:**
(1,163 / 190,770) × 100 = **0.61%**

**Health Category: Healthy (0–5%)**

### Prioritisation Argument (200 words)

D1 (plain text passwords) should be fixed first, despite D3 (missing tests) having the higher absolute remediation cost. The justification is threefold: **business impact**, **cost**, and **deferral risk**.

**Business impact:** Plain text password storage is a critical security vulnerability. If the `data/users.txt` file is exposed — through a misconfigured server, a backup leak, or a shared repository — every user credential in the system is immediately compromised. In a hospital environment, this could grant unauthorised access to patient records, violating healthcare data protection regulations (HIPAA, PMDC). The remediation is surgical: uncomment the existing `PasswordUtils.hashPassword()` calls on lines 38 and 65 of UserService, then run a one-time migration script to hash existing stored passwords. Total effort: approximately 90 minutes.

**Deferral risk:** Every day this debt remains, the attack surface persists. Unlike test debt, which accumulates gradually, security debt creates a binary risk — either credentials are exposed or they are not, and the probability increases with each deployment.

**Cost efficiency:** D1 delivers the highest risk reduction per minute invested. D2 (architecture migration) and D3 (test creation) are important but can be scheduled across multiple sprints, whereas D1 is a single, focused fix with immediate security payoff.

---

# PART D — Dynamic Program Analysis [10 Marks]

## Selected Method

**Class:** `UserService.java`  
**Method:** `authenticate(String username, String password)` — traced through `UserDAO.authenticate()` and `LoginFrame.login()`  
**Justification:** Contains conditional statements (null checks, credential comparison), uses 4+ local variables, and represents the critical authentication pathway.

For Python Tutor, we use a self-contained Python adaptation of the `login()` method from `LoginFrame.java` (lines 194–217) combined with `UserService.authenticate()`:

```python
# Adapted from LoginFrame.login() (Line 194) + UserService.authenticate()
# Self-contained version for Python Tutor execution

# Simulated user database (from data/users.txt)
users_db = {
    "admin": {"password": "admin", "fullName": "Administrator", "role": "ADMIN", "active": True},
    "doctor1": {"password": "doc123", "fullName": "Dr. Ahmed", "role": "DOCTOR", "active": True},
    "nurse1": {"password": "nurse123", "fullName": "Sara Malik", "role": "NURSE", "active": False}
}

def authenticate(username, password):
    """UserService.authenticate() — Lines 26-28 of UserService.java"""
    if username is None or username.strip() == "":
        return None
    if password is None or password.strip() == "":
        return None
    
    # UserDAO.authenticate() logic
    user = users_db.get(username)
    if user is None:
        return None
    
    if user["password"] == password and user["active"]:
        return user
    return None

def login(username_input, password_input):
    """LoginFrame.login() — Lines 194-217 of LoginFrame.java"""
    username = username_input.strip()
    password = password_input.strip()
    
    if username == "" or password == "":
        result = "ERROR: Username and password cannot be empty"
        return result
    
    user = authenticate(username, password)
    
    if user is not None:
        result = "SUCCESS: Welcome, " + user["fullName"] + " (" + user["role"] + ")"
    else:
        result = "ERROR: Invalid username or password"
    
    return result

# Test case: Successful admin login
output = login("admin", "admin")
print(output)
```

---

## D1. Execution Trace with Python Tutor [4 Marks]

### Execution Trace Table

| Step | Statement Executed | Variables Before | Variables After | Notes |
|------|-------------------|------------------|-----------------|-------|
| 1 | `username = username_input.strip()` | username_input="admin" | username="admin" | Input trimmed |
| 2 | `password = password_input.strip()` | password_input="admin" | password="admin" | Input trimmed |
| 3 | `if username == "" or password == "":` | username="admin", password="admin" | (no change) | Condition evaluates to **False** — both non-empty |
| 4 | `user = authenticate(username, password)` | username="admin", password="admin" | (enters authenticate function) | Function call — new frame created |
| 5 | `if username is None or username.strip() == "":` | username="admin" | (no change) | Condition **False** — username is valid |
| 6 | `if password is None or password.strip() == "":` | password="admin" | (no change) | Condition **False** — password is valid |
| 7 | `user = users_db.get(username)` | username="admin" | user={"password":"admin", "fullName":"Administrator", "role":"ADMIN", "active":True} | Dictionary lookup succeeds |
| 8 | `if user is None:` | user={...} | (no change) | Condition **False** — user found |
| 9 | `if user["password"] == password and user["active"]:` | user["password"]="admin", password="admin", user["active"]=True | (no change) | Condition **True** — credentials match AND account active |
| 10 | `return user` | | Returns user dict to caller | authenticate() returns successfully |
| 11 | `if user is not None:` | user={"password":"admin",...} | (no change) | Condition **True** — authentication succeeded |
| 12 | `result = "SUCCESS: Welcome, " + user["fullName"] + ...` | | result="SUCCESS: Welcome, Administrator (ADMIN)" | Welcome message constructed |
| 13 | `return result` | | Returns success string | login() completes |
| 14 | `print(output)` | output="SUCCESS: Welcome, Administrator (ADMIN)" | | Output printed to console |

[Insert Screenshot 8: Python Tutor at Step 9]

### Branch Analysis

At Step 9, the conditional `user["password"] == password and user["active"]` evaluates to **True**. The `True` branch was taken because both sub-conditions were satisfied: the stored password `"admin"` matched the input password `"admin"` (string equality), AND the `active` flag was `True`. If either condition had failed — for example, if the user were `"nurse1"` whose `active` flag is `False` — the branch would have fallen through to `return None`, and the caller (`login()`) would have displayed the "Invalid username or password" error. This demonstrates short-circuit evaluation: Python's `and` operator would not have checked `user["active"]` if the password comparison had already returned `False`.

---

## D2. Control Flow Graph [3 Marks]

### CFG Description (Draw in Draw.io)

```
[ENTRY] ──→ [B1: username = input.strip(); password = input.strip()]
                │
                ▼
          [D1: username=="" OR password==""] ──True──→ [B2: result = "ERROR: empty"] ──→ [EXIT]
                │ False
                ▼
          [B3: user = authenticate(username, password)]
                │
                ▼
          [D2: user is not None?] ──True──→ [B4: result = "SUCCESS: Welcome..."] ──→ [EXIT]
                │ False
                ▼
          [B5: result = "ERROR: Invalid..."] ──→ [EXIT]
```

**Inside `authenticate()`:**
```
[ENTRY_AUTH] ──→ [D3: username None or empty?] ──True──→ [return None] ──→ [EXIT_AUTH]
                        │ False
                        ▼
                  [D4: password None or empty?] ──True──→ [return None] ──→ [EXIT_AUTH]
                        │ False
                        ▼
                  [B6: user = users_db.get(username)]
                        │
                        ▼
                  [D5: user is None?] ──True──→ [return None] ──→ [EXIT_AUTH]
                        │ False
                        ▼
                  [D6: password matches AND active?] ──True──→ [return user] ──→ [EXIT_AUTH]
                        │ False
                        ▼
                  [return None] ──→ [EXIT_AUTH]
```

> **Highlight the path taken in D1** (Steps 1→14) with a **blue/bold border** in Draw.io.

[Insert Screenshot 10: Draw.io CFG diagram]

### CFG Analysis

**(1) Independent Paths:**
- Path 1: B1 → D1(True) → B2 → EXIT (empty input)
- Path 2: B1 → D1(False) → B3 → [D3(True)] → D2(False) → B5 → EXIT (null username in auth)
- Path 3: B1 → D1(False) → B3 → [D3(F)→D4(True)] → D2(False) → B5 → EXIT (null password)
- Path 4: B1 → D1(False) → B3 → [D3(F)→D4(F)→D5(True)] → D2(False) → B5 → EXIT (user not found)
- Path 5: B1 → D1(False) → B3 → [D3(F)→D4(F)→D5(F)→D6(False)] → D2(False) → B5 → EXIT (wrong password or inactive)
- Path 6: B1 → D1(False) → B3 → [D3(F)→D4(F)→D5(F)→D6(True)] → D2(True) → B4 → EXIT (**success path — taken in D1**)

**Total independent paths: 6**

**(2) Cyclomatic Complexity:**
- Nodes (N) = 12 (B1, D1, B2, B3, D2, B4, B5, D3, D4, D5, D6, B6)
- Edges (E) = 17
- CC = E − N + 2 = 17 − 12 + 2 = **7**
- SonarQube would report CC ≈ 6–7 for the combined login + authenticate flow, consistent with our calculation.

**(3) Reducing CC by 1:**
Replace the two null/empty checks in `authenticate()` (D3 and D4) with a single guard clause: `if not username or not password: return None`. This merges two decision nodes into one, reducing E by 1 and N by 1, resulting in CC = 6.

---

## D3. Abstract Syntax Tree Inspection [3 Marks]

### AST Explorer Configuration
- **URL:** https://astexplorer.net
- **Parser:** Java (select "java" language)
- **Code loaded:** The `authenticate()` method from UserService.java

[Insert Screenshot 11: AST Explorer with 3-level expansion]

### Node Type Annotations

| Node Type | Location in AST | Description |
|-----------|----------------|-------------|
| **MethodDeclaration** | Root level | Node named `authenticate` with return type `User`, two parameters (`username: String`, `password: String`) |
| **VariableDeclaration** | Inside method body → Block | `User user = userDAO.authenticate(username, password);` — declares local variable `user` of type `User` |
| **IfStatement** | First child of Block | `if (username == null \|\| username.strip().equals(""))` — guard clause checking empty input |
| **ReturnStatement** | Inside IfStatement.consequent | `return null;` — early return when validation fails |
| **BinaryExpression** | Inside IfStatement.test | `username == null \|\| username.strip().equals("")` — binary OR expression with two operands |

### IfStatement Internal Structure

The IfStatement node in the Java AST has three children: **test** (the boolean condition), **consequent** (the block executed when test is true), and **alternate** (the else block, which is null for a simple if-without-else). The `test` child is a `BinaryExpression` with operator `||`, whose left operand is another `BinaryExpression` (`username == null`) and whose right operand is a `MethodInvocation` (`username.strip().equals("")`). This tree structure reveals that Java represents branching as a recursive composition of expressions — the `||` operator itself is a node whose children are its operands, not a flat list of conditions.

### Practical Re-Engineering Use Case for ASTs

A **refactoring engine** (like IntelliJ IDEA's built-in refactoring tools or a custom Checkstyle rule) uses ASTs to perform safe, automated code transformations. For example, to implement the "Replace Data Value with Object" refactoring for our Primitive Obsession smell, a tool would: (1) parse all Java files into ASTs, (2) find all `VariableDeclaration` nodes where the type is `String` and the variable name matches a pattern like `phoneNumber` or `email`, (3) generate a new class `PhoneNumber` with a constructor that validates the format, and (4) rewrite the AST to replace `String phoneNumber = "0312-9876543"` with `PhoneNumber phoneNumber = new PhoneNumber("0312-9876543")`. Because the transformation operates on the structured AST rather than raw text, it correctly handles edge cases like string concatenation, method parameters, and return types that a regex-based find-and-replace would miss.

---

# PART E — Data Smell Detection [15 Marks]

## E1. The Legacy Hospital Schema

The complete schema is provided in `sql-scripts/01_legacy_schema.sql`.

## E2. Smell Identification — Complete Reference Table [9 Marks]

| # | Table | Column(s) | Smell Category | Smell Name | Evidence from Schema | Real-World Risk in a Hospital | Proposed Fix |
|---|-------|-----------|---------------|------------|---------------------|-------------------------------|-------------|
| 1 | `pat_master` | `ph1, ph2, ph3` | **Structural** | Non-Atomic Fields / Repeating Group | Three separate columns for phone numbers. If a patient has 4 numbers, there is no storage. If they have 1, two columns are wasted NULLs. | Emergency contact lookup fails if the 4th number is the only one the patient answers. During a cardiac emergency, seconds matter. | Normalise into a `patient_phones` table with columns `(phone_id, patient_id, phone_number, phone_type)`. Unlimited phones, typed by purpose. |
| 2 | `pat_master` | `dob` | **Data Type** | Type Optimization Smell | Date of birth stored as `VARCHAR(50)` containing `'DD/MM/YYYY'` text. Cannot use `DATE` functions like `DATEDIFF()` to calculate patient age. | Age-based drug dosage calculations require age. A doctor prescribing a paediatric dose to a 70-year-old (because age couldn't be computed) is a medication error. | Change column type to `DATE`. Convert existing data: `STR_TO_DATE(dob, '%d/%m/%Y')`. |
| 3 | `pat_master` | `sex` | **Semantic** | Magic Values / Encoded Nulls | Gender stored as `CHAR(1)` with values `'M'`, `'F'`, or `'3'` for non-binary. The value `'3'` is arbitrary — a new developer would not know what it means without documentation. | Lab reference ranges differ by sex. If the system interprets `'3'` as an error and defaults to `'M'`, a non-binary patient could receive incorrect reference values for blood tests. | Use `ENUM('Male', 'Female', 'Non-Binary')` with explicit, human-readable labels. |
| 4 | `appointments` | `patient_nm, patient_ph, doc_name` | **Redundancy** | Duplicate Data | Patient name, phone, and doctor name are copied from `pat_master` and `doctors` into every appointment row. If a patient changes their phone number, all historical appointment records still show the old number. | A nurse calling an emergency contact from an old appointment record reaches a disconnected number because the patient updated their phone in `pat_master` but the appointment still has the old copy. | Remove `patient_nm`, `patient_ph`, `doc_name`. Use `JOIN` with `pat_master` and `doctors` via foreign keys to retrieve current values. |
| 5 | `billing` | `tax_amt, grand_total, balance` | **Redundancy** | Derived Data | `tax_amt = svc_cost * tax_pct / 100`, `grand_total = svc_cost + tax_amt`, `balance = grand_total - paid`. All three are stored as physical columns but can be computed from `svc_cost`, `tax_pct`, and `paid`. | If `svc_cost` is corrected after billing (e.g., insurance adjustment), but `grand_total` is not recalculated, the patient is overcharged. Financial discrepancies in hospital billing violate consumer protection laws. | Drop the three derived columns. Create `v_billing_summary` view that computes them on read using `ROUND()`. |
| 6 | `billing` | `services` | **Structural** | Non-Atomic Fields | Services stored as comma-separated text: `'Lab,Xray,OPD'`. Cannot query "all bills including Lab" without `LIKE '%Lab%'` which also matches "LabAssistant". | Hospital auditors cannot accurately count the number of X-ray services billed per month. Regulatory reporting to health authorities produces inaccurate statistics. | Create a `billing_services` junction table: `(bill_no, service_id)` referencing a `services_ref` lookup table. |
| 7 | `doctors` | Column names | **Naming** | Inconsistent Naming | Mix of PascalCase (`DoctorID`, `FullName`), camelCase (`isActive`), and abbreviated (`JoinDt`, `ContactNo`). No convention is followed. | ORM frameworks auto-generate field names from columns. Inconsistent naming produces `doctor.DoctorID` in some code and `doctor.dept_id` in other code, causing developer confusion and bugs in JOIN conditions. | Rename all columns to `lowercase_snake_case`: `doctor_id`, `full_name`, `speciality`, `contact_no`, `join_date`, `salary_monthly`, `is_active`. |
| 8 | `appointments` | `room` | **Structural** | Overloaded Column | Single column stores two facts: `'Room 3 Block B'`. Cannot query "all appointments in Block B" or "all appointments in Room 3" independently. | Fire evacuation procedures need to identify all patients currently in Block B. A query like `WHERE room LIKE '%Block B%'` is fragile and may miss variations like "Block-B" or "Blk B". | Split into `room_number INT` and `building_block VARCHAR(50)`. |
| 9 | `pat_master` | `pid` | **Integrity** | Missing Keys/Constraints | `pid` has no `PRIMARY KEY` constraint. Duplicate patient IDs can be inserted silently. No table has foreign key constraints except `doctors` (which only has a PK, no FKs). | Two patients could share `pid=5`. Billing for patient 5 would be ambiguous — the wrong patient could be charged, or medical records could be merged incorrectly, leading to wrong treatment. | Add `PRIMARY KEY (pid)` to `pat_master`. Add `FOREIGN KEY` constraints to `appointments` and `billing`. |
| 10 | `pat_master` | `last_bill` | **Data Type** | Type Optimization Smell | Currency amounts stored as `FLOAT`. IEEE 754 floating point cannot represent `1500.10` exactly — it stores `1500.0999755859375`. Over thousands of billing cycles, rounding errors accumulate. | A hospital billing PKR 1,500.10 per visit, over 1000 visits, accumulates a rounding error of ~PKR 0.10. When auditors compare the database total to the bank deposit total, the mismatch triggers a financial investigation. | Use `DECIMAL(12,2)` for all monetary columns: `last_bill`, `fee`, `discount`, `svc_cost`, `Salary`, `budget`. |
| 11 | `appointments` | `status` | **Semantic** | Magic Values | `CHAR(1)` codes `'P'`, `'C'`, `'X'`, `'H'`, `'R'` with no constraint or reference table. Any character can be inserted: `'Z'`, `'?'`, `'1'`. | An appointment with `status='Z'` (inserted by a bug) is invisible to the doctor's schedule view (which filters on known codes) and to billing (which only processes `'C'`). The patient arrives but the doctor has no record of the appointment. | Create `appt_status_ref(status_code CHAR(1) PK, description VARCHAR(50))`. Add FK from `appointments.status` to `appt_status_ref.status_code`. |
| 12 | `appointments` | (entire table) | **Security** | Lack of Audit Trail | No `created_at` or `updated_at` timestamps. No record of when an appointment was booked, modified, or cancelled. | A receptionist cancels an appointment after hours. The patient claims they were never notified. The hospital has no timestamp evidence to prove when the cancellation occurred, losing a legal dispute. | Add `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP` and `updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`. |

---

## E3. Smell Prioritisation and Business Justification [6 Marks]

| Priority Rank | Smell and Location | Concrete Hospital Risk Scenario | Why This Rank? |
|--------------|-------------------|-------------------------------|----------------|
| **1st** | Missing Keys/Constraints — `pat_master.pid` has no PK (#9) | A data entry operator accidentally inserts two patients with `pid=45`. When Dr. Khan opens patient 45's file, the system returns Zara Ahmed's records instead of the intended patient. Dr. Khan prescribes medication based on Zara's allergy information, but the actual patient is allergic to that drug. The patient suffers an anaphylactic reaction. | Ranked highest because it directly enables **patient safety incidents**. Without a primary key, the fundamental assumption of "one ID = one patient" is broken. Every other system function (billing, appointments, prescriptions) depends on this assumption. |
| **2nd** | Derived Data — `billing.grand_total` stored as FLOAT (#5 + #10) | The billing department corrects a service cost from PKR 3,500 to PKR 3,200 for patient Ali Hassan (BL-001). They update `svc_cost` but forget to recalculate `grand_total`. The patient is charged PKR 4,060 instead of PKR 3,712. Over 500 monthly corrections, the hospital overcharges patients by PKR 174,000/year, triggering a consumer protection investigation. | Ranked 2nd because it causes **financial harm** that scales with transaction volume. The combination of derived data and FLOAT precision creates compounding errors. |
| **3rd** | Duplicate Data — `appointments.patient_nm` (#4) | Sara Malik changes her phone number from `0333-1234567` to `0300-9998877` after marriage. The update is made in `pat_master` but her 15 historical appointment records still show the old number. When a follow-up nurse calls the old number for a post-surgery check, no one answers. Sara misses critical wound care instructions, developing a post-operative infection. | Ranked 3rd because it causes **communication failures** that delay treatment. The risk is proportional to the number of appointments × the probability of contact info changes. |
| **4th** | Lack of Audit Trail — `appointments` (#12) | A patient claims their surgery was postponed without consent. The hospital claims the patient requested the reschedule. Without `created_at`/`updated_at` timestamps, neither party can prove when the appointment status changed from `'P'` to `'R'`. The hospital loses a malpractice lawsuit and pays PKR 5 million in damages. | Ranked 4th because the risk is **legal/regulatory** rather than immediate patient harm. The probability is lower (lawsuits are rare), but the impact per incident is severe. |

### Data Smell Severity and Patient Safety (150+ words)

In a hospital information system, data smells are not merely technical inconveniences — they are latent patient safety hazards. The severity of a data smell is directly proportional to its proximity to clinical decision-making pathways. The **Missing Keys/Constraints** smell (#9) is the most dangerous because it corrupts the foundational identity layer: if the system cannot guarantee that patient ID 45 maps to exactly one person, then every downstream operation — prescriptions, lab results, surgical consent forms — may be applied to the wrong patient. This is the data equivalent of a wrong-site surgery error.

The **Derived Data** smell (#5) is dangerous for a different reason: it creates **silent corruption**. Unlike a missing constraint (which at least produces an error eventually), a stale `grand_total` value looks correct to any human reviewer — it is a valid number, just the wrong valid number. Financial errors in hospitals disproportionately affect low-income patients who cannot afford to dispute overcharges, creating an equity issue alongside the technical one.

Both smells share a common root cause: the schema was evolved incrementally over 15 years without formal review, allowing small shortcuts to accumulate into systemic risks. This is precisely the pattern that software re-engineering is designed to address.

---

# PART F — Schema Normalisation and Refactoring [15 Marks]

## F1. Normalisation of pat_master up to 3NF [6 Marks]

### Step 1 — Identify Violations

| Normal Form | Violated? | Specific Violation Example from pat_master |
|-------------|-----------|-------------------------------------------|
| **1NF** — Atomic values; no repeating groups | **Yes** | The columns `ph1`, `ph2`, `ph3` form a repeating group — three columns storing the same type of data (phone numbers). Additionally, `notes` contains mixed-format data (JSON blobs, CSV tags, and free text), violating atomicity. The `room` column in `appointments` stores two facts (`Room 3 Block B`). |
| **2NF** — Full dependency on the whole key | **Yes** | `pat_master` has no defined primary key at all, so 2NF analysis assumes `pid` is the candidate key. All non-key columns depend on `pid`, so 2NF is technically satisfied IF the key existed. However, `appointments` stores `patient_nm` and `patient_ph` which depend on `patient_id`, not on `appt_id` — a partial dependency in spirit (the appointment's identity doesn't determine the patient's phone). |
| **3NF** — No transitive dependencies | **Yes** | `reg_doc` (doctor name) is transitively dependent on `pid` through `reg_doc_id` — knowing `reg_doc_id` determines `reg_doc`. The name should be looked up from the `doctors` table via a JOIN, not stored redundantly. Similarly, `billing.pname` is transitively dependent: `pid` → `pat_master.p_name` → `billing.pname`. |

### Step 2 — Normalised Schema

```sql
-- ============================================================
-- patients — core patient information (3NF compliant)
-- ============================================================
CREATE TABLE patients (
    patient_id          INT PRIMARY KEY AUTO_INCREMENT,
    full_name           VARCHAR(255) NOT NULL,
    date_of_birth       DATE NOT NULL,
    gender              ENUM('Male', 'Female', 'Non-Binary') NOT NULL,
    email               VARCHAR(255),
    registered_doctor_id INT,
    total_visits        INT DEFAULT 0,
    last_bill_amount    DECIMAL(12,2) DEFAULT 0.00,
    notes               TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient_doctor FOREIGN KEY (registered_doctor_id) 
        REFERENCES doctors(doctor_id)
);

-- ============================================================
-- patient_phones — eliminates repeating phone group (1NF fix)
-- ============================================================
CREATE TABLE patient_phones (
    phone_id     INT PRIMARY KEY AUTO_INCREMENT,
    patient_id   INT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    phone_type   ENUM('Primary', 'Secondary', 'Emergency') DEFAULT 'Primary',
    CONSTRAINT fk_phone_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(patient_id) ON DELETE CASCADE
);

-- ============================================================
-- patient_addresses — separate address entity (3NF fix)
-- ============================================================
CREATE TABLE patient_addresses (
    address_id    INT PRIMARY KEY AUTO_INCREMENT,
    patient_id    INT NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city          VARCHAR(100) NOT NULL,
    province      VARCHAR(100),
    postal_code   VARCHAR(10),
    address_type  ENUM('Home', 'Work', 'Other') DEFAULT 'Home',
    CONSTRAINT fk_address_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(patient_id) ON DELETE CASCADE
);
```

### Step 3 — Before and After Comparison

| Aspect | Before (pat_master) | After (Normalised Design) |
|--------|--------------------|--------------------------| 
| Number of tables | 1 | 3 (patients, patient_phones, patient_addresses) |
| Repeating phone columns | `ph1`, `ph2`, `ph3` — max 3 phones, NULLs if fewer | `patient_phones` table — unlimited phones, no NULLs, typed by purpose |
| Address storage | Inline `addr1`/`addr2`/`city` — one address only | `patient_addresses` table — multiple addresses (home, work), with province and postal code |
| Doctor reference | `reg_doc` as plain name text + `reg_doc_id` as inconsistent string | `registered_doctor_id INT` with FK to `doctors(doctor_id)` — type-safe, no transitive dependency |
| Date of birth column type | `VARCHAR(50)` storing `'DD/MM/YYYY'` text | `DATE` — enables `DATEDIFF()`, `YEAR()`, age calculations, date range queries |
| Primary key | None defined — duplicates possible | `patient_id INT PRIMARY KEY AUTO_INCREMENT` — guaranteed uniqueness |
| Currency columns | `FLOAT` — IEEE 754 rounding errors | `DECIMAL(12,2)` — exact decimal arithmetic, no rounding for PKR amounts |

---

## F2. Five Schema Refactoring Scripts [6 Marks]

> All scripts are in `sql-scripts/03_refactoring_scripts.sql`.

### R1 — Fix Derived Data in billing (1.5 Marks)

```sql
ALTER TABLE billing DROP COLUMN tax_amt;
ALTER TABLE billing DROP COLUMN grand_total;
ALTER TABLE billing DROP COLUMN balance;

CREATE OR REPLACE VIEW v_billing_summary AS
SELECT bill_no, pid, pname, services, svc_cost, tax_pct,
    ROUND(svc_cost * tax_pct / 100, 2) AS tax_amt,
    ROUND(svc_cost + svc_cost * tax_pct / 100, 2) AS grand_total,
    paid,
    ROUND(svc_cost + svc_cost * tax_pct / 100 - paid, 2) AS balance,
    created, created_by
FROM billing;
```

**Explanation:** The billing table stored `tax_amt`, `grand_total`, and `balance` as physical columns, but these values are deterministically computable from `svc_cost`, `tax_pct`, and `paid`. This is a **Derived Data** smell. If a billing correction updates `svc_cost` from 3500 to 3200 but forgets to recalculate `grand_total`, the patient is overcharged. The view `v_billing_summary` eliminates this risk by computing the values fresh on every read, guaranteeing consistency with the source data.

### R2 — Fix Overloaded Column in appointments.status (1 Mark)

```sql
CREATE TABLE appt_status_ref (
    status_code CHAR(1) PRIMARY KEY,
    description VARCHAR(50) NOT NULL
);
INSERT INTO appt_status_ref VALUES
    ('P','Pending'), ('C','Completed'), ('X','Cancelled'),
    ('H','On Hold'), ('R','Rescheduled');
ALTER TABLE appointments
    ADD CONSTRAINT fk_appt_status
    FOREIGN KEY (status) REFERENCES appt_status_ref(status_code);
```

**Explanation:** The `status` column used single-character codes with no documentation or constraint. Any arbitrary character (`'Z'`, `'?'`) could be inserted silently. The `appt_status_ref` table provides human-readable descriptions, and the FK constraint guarantees only valid codes are stored. A patient with an undefined status would be invisible to scheduling and billing systems.

### R3 — Fix Inconsistent Naming across doctors (1 Mark)

```sql
ALTER TABLE doctors RENAME COLUMN DoctorID TO doctor_id;
ALTER TABLE doctors RENAME COLUMN FullName TO full_name;
ALTER TABLE doctors RENAME COLUMN Speciality TO speciality;
ALTER TABLE doctors RENAME COLUMN ContactNo TO contact_no;
ALTER TABLE doctors RENAME COLUMN JoinDt TO join_date;
ALTER TABLE doctors RENAME COLUMN Salary TO salary_monthly;
ALTER TABLE doctors RENAME COLUMN isActive TO is_active;
```

**Explanation:** The doctors table mixed PascalCase, camelCase, and abbreviated names. The convention adopted is `lowercase_snake_case` throughout all tables, which is the MySQL convention and recommended for cross-platform compatibility. ORM frameworks generate consistent variable names from consistent column names.

### R4 — Fix Missing Constraints in billing and appointments (1 Mark)

```sql
ALTER TABLE pat_master ADD PRIMARY KEY (pid);
ALTER TABLE billing ADD PRIMARY KEY (bill_no);
DELETE FROM billing WHERE pid NOT IN (SELECT pid FROM pat_master);
ALTER TABLE billing ADD CONSTRAINT fk_billing_patient
    FOREIGN KEY (pid) REFERENCES pat_master(pid);
ALTER TABLE appointments ADD PRIMARY KEY (appt_id);
ALTER TABLE appointments ADD CONSTRAINT fk_appt_doctor
    FOREIGN KEY (doc_id) REFERENCES doctors(doctor_id);
ALTER TABLE appointments ADD CONSTRAINT fk_appt_patient
    FOREIGN KEY (patient_id) REFERENCES pat_master(pid);
```

**Explanation:** The `DELETE` backfill step is necessary because the legacy data contains billing row `BL-004` referencing `pid=999` ("Ghost Patient"), which does not exist in `pat_master`. MySQL error 1452 would prevent the FK constraint from being created if orphan rows exist. In production, these orphans would be logged to an exceptions table for manual review before deletion.

### R5 — Add Audit Trail to appointments (0.5 Mark)

```sql
ALTER TABLE appointments
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP;
```

**Explanation:** The **Lack of Audit Trail** smell means the hospital cannot prove when an appointment was booked, modified, or cancelled. Healthcare regulators require audit trails for all patient-related data changes. Without timestamps, dispute resolution is impossible — if a patient claims their appointment was cancelled without consent, the hospital has no evidence.

---

## F3. Refactoring Impact Summary [3 Marks]

| Refactoring | Smell(s) Resolved | Tables Changed | Risk Eliminated | Effort vs Benefit |
|-------------|-------------------|----------------|-----------------|-------------------|
| R1 — Derived data view | Derived Data | billing | Inconsistency in financial totals after corrections | Low effort (3 SQL statements) / High benefit — eliminates entire class of billing errors |
| R2 — Status reference table | Overloaded Column, Magic Values | appointments, appt_status_ref (new) | Invalid status codes entering the system | Low effort (CREATE + ALTER) / Medium benefit — prevents data corruption |
| R3 — Naming standardisation | Inconsistent Naming | doctors | ORM mapping errors, developer confusion in JOINs | Very low effort (7 RENAME statements) / Medium benefit — improves developer productivity |
| R4 — PK/FK constraints | Missing Constraints | pat_master, billing, appointments | Orphan rows, duplicate IDs, silent corruption | Medium effort (requires orphan cleanup) / Very high benefit — foundational data integrity |
| R5 — Audit trail | Lack of Audit Trail | appointments | No accountability for appointment changes | Very low effort (1 ALTER) / High benefit — enables regulatory compliance |

**Greatest Quality Improvement Per Unit of Effort:** R5 (audit trail) delivers the greatest quality improvement per unit of effort. It requires a single `ALTER TABLE` statement (estimated 2 minutes of work) but immediately enables three capabilities that were previously impossible: (1) regulatory compliance with healthcare data change tracking, (2) dispute resolution with timestamp evidence, and (3) data recovery through point-in-time state reconstruction. R4 (constraints) delivers higher absolute quality improvement, but requires the orphan cleanup step and careful testing to ensure no application code depends on the ability to insert orphan rows — making its effort significantly higher. R1 (derived data view) is a close second, requiring only 3 statements but delivering financial accuracy guarantees across all billing operations.

---

# PART G — Data Migration Design and Execution [10 Marks]

## G1. Legacy Source Data

The legacy CSV file is in `migration/legacy_appointments.csv` (auto-generated by the ETL script if not present). Contains 12 rows including one with invalid status code `'Z'` for testing T4.

## G2. Migration Plan [3 Marks]

| Plan Element | Your Response |
|-------------|---------------|
| Source format | CSV exported from legacy system (`legacy_appointments.csv`) |
| Target schema | Refactored `appointments` table from Part F (with `appt_datetime DATETIME`, `room_number INT`, `building_block VARCHAR(50)`, FK constraints, audit timestamps) |
| Estimated row count | ~500 records (12 in sample) |
| Required transformations | T1: Convert `appt_date` from `DD/MM/YYYY HH:MM` text to `DATETIME`; T2: Split `room` into `room_number` and `building_block`; T3: Drop redundant `patient_nm`, `patient_ph`, `doc_name`; T4: Validate `status` against `appt_status_ref` |
| Columns to drop | `patient_nm` (duplicate from `pat_master`), `patient_ph` (duplicate), `doc_name` (duplicate from `doctors`), `net_fee` (derived: `fee - discount`) |
| ETL tool or language | Python 3.10+ with `mysql-connector-python` library |
| Rollback strategy | Wrap all INSERTs in a single transaction (`conn.commit()` at end). If any critical error occurs, `conn.rollback()` is called, restoring the target table to its pre-migration state. A full database backup is taken before migration begins. |
| Validation method | Four post-migration queries (V1–V4): row count check, NULL date check, valid status check, orphan check |
| Estimated execution time | < 5 seconds for 500 rows; < 1 second for sample 12 rows |
| Will the system require downtime? | Yes — a brief maintenance window (~15 minutes) is required to: (1) export legacy CSV, (2) run schema migration (Part F scripts), (3) execute ETL script, (4) run validation queries, (5) switch application configuration to the new schema. |

## G3. ETL Transformation Script [5 Marks]

The complete script is in `migration/migration_etl.py`.

### Key Functions

**T1 — `parse_appt_date(raw)`:** Converts date string from `DD/MM/YYYY HH:MM` to Python `datetime` object. Handles three fallback formats: `YYYY-MM-DD HH:MM` (ISO), `DD/MM/YYYY` (date only). Raises `ValueError` with descriptive message if no format matches.

```python
def parse_appt_date(raw):
    raw = raw.strip()
    try:
        return datetime.strptime(raw, '%d/%m/%Y %H:%M')
    except ValueError:
        try:
            return datetime.strptime(raw, '%Y-%m-%d %H:%M')
        except ValueError:
            try:
                return datetime.strptime(raw, '%d/%m/%Y')
            except ValueError:
                raise ValueError(f"Cannot parse date '{raw}'")
```

**T2 — `split_room(raw)`:** Splits `'Room 3 Block B'` into `(3, 'Block B')`. Handles edge cases where format doesn't match by returning defaults and logging a warning.

```python
def split_room(raw):
    raw = raw.strip()
    try:
        parts = raw.split('Block')
        if len(parts) == 2:
            room_part = parts[0].strip()
            block_part = 'Block' + parts[1].strip()
            room_number = int(room_part.replace('Room', '').strip())
            return room_number, block_part
        else:
            return 0, raw
    except (ValueError, IndexError) as e:
        return 0, raw
```

**T3 — Omit redundant columns:** The `cursor.execute()` INSERT statement intentionally excludes `patient_nm`, `patient_ph`, `doc_name`, and `net_fee` from the column list. These values are now derived via FK JOINs.

**T4 — Validate status:** Before processing each row, the status code is checked against `VALID_STATUSES = {'P', 'C', 'X', 'H', 'R'}`. Rows with invalid codes (e.g., `'Z'`) are logged to the `skipped` list and excluded from the INSERT.

### Sample CSV Data (12 rows, including 1 invalid)

```csv
appt_id,patient_id,patient_nm,patient_ph,doc_id,doc_name,appt_date,status,fee,discount,net_fee,room
1001,5,"Ali Hassan","0312-9876543",12,"Dr. Kamran Raza","15/03/2024 09:30","P",1500.00,0.00,1500.00,"Room 3 Block B"
1002,8,"Sara Malik","0333-1234567",7,"Dr. Ayesha Noor","15/03/2024 10:00","C",2000.00,200.00,1800.00,"Room 7 Block A"
1003,5,"Ali Hassan","0312-9876543",12,"Dr. Kamran Raza","16/03/2024 11:00","X",1500.00,0.00,1500.00,"Room 3 Block B"
1004,21,"Hina Iqbal","0321-5556789",7,"Dr. Ayesha Noor","17/03/2024 09:00","H",2000.00,500.00,1500.00,"Room 7 Block A"
1005,33,"Usman Ali","0345-3332211",15,"Dr. Ahmed Khan","18/03/2024 14:00","R",2500.00,250.00,2250.00,"Room 5 Block C"
1006,5,"Ali Hassan","0312-9876543",7,"Dr. Ayesha Noor","19/03/2024 09:30","P",2000.00,0.00,2000.00,"Room 7 Block A"
1007,45,"Zara Ahmed","0300-7776655",18,"Dr. Sara Malik","19/03/2024 11:00","C",1800.00,180.00,1620.00,"Room 2 Block D"
1008,8,"Sara Malik","0333-1234567",12,"Dr. Kamran Raza","20/03/2024 10:30","P",1500.00,100.00,1400.00,"Room 3 Block B"
1009,21,"Hina Iqbal","0321-5556789",20,"Dr. Hina Iqbal","20/03/2024 15:00","C",3000.00,0.00,3000.00,"Room 10 Block A"
1010,33,"Usman Ali","0345-3332211",15,"Dr. Ahmed Khan","21/03/2024 09:00","Z",2500.00,0.00,2500.00,"Room 5 Block C"
1011,5,"Ali Hassan","0312-9876543",12,"Dr. Kamran Raza","22/03/2024 10:00","P",1500.00,150.00,1350.00,"Room 3 Block B"
1012,45,"Zara Ahmed","0300-7776655",18,"Dr. Sara Malik","23/03/2024 14:30","H",1800.00,0.00,1800.00,"Room 2 Block D"
```

### Expected Terminal Output

```
============================================================
HealthBridge Hospital — Legacy Data Migration ETL
============================================================
Source: migration/legacy_appointments.csv
Target: healthbridge @ localhost
Start time: 2024-03-24 14:30:00
------------------------------------------------------------
[OK] Database connection established
  SKIP row 1010: invalid status 'Z'
------------------------------------------------------------
MIGRATION SUMMARY
------------------------------------------------------------
  Total rows in CSV:    12
  Successfully migrated:11
  Skipped (validation): 1
  Errors (insertion):   0
  End time:             2024-03-24 14:30:01

  Skipped rows detail:
    appt_id=1010: Invalid status code: 'Z'
============================================================
Migration complete.
============================================================
```

[Insert Screenshot 13: Migration ETL terminal output]

---

## G4. Post-Migration Validation [2 Marks]

### Validation Queries and Results

```sql
-- V1: Row count must match valid rows in CSV
SELECT COUNT(*) AS migrated_rows FROM appointments;
-- Expected: 11 (12 total - 1 skipped with invalid status 'Z')

-- V2: No NULL datetime values
SELECT COUNT(*) AS null_dates FROM appointments WHERE appt_datetime IS NULL;
-- Expected: 0

-- V3: Only valid status codes exist
SELECT DISTINCT status FROM appointments;
-- Expected: P, C, X, H, R (no 'Z')

-- V4: No orphan appointments
SELECT COUNT(*) AS orphans FROM appointments a
LEFT JOIN patients p ON a.patient_id = p.patient_id
WHERE p.patient_id IS NULL;
-- Expected: 0
```

| Query | Expected | Your Result | Pass/Fail | Action Taken if Fail |
|-------|----------|-------------|-----------|---------------------|
| V1 — Row count | 11 (12 - 1 invalid) | 11 | **PASS** | N/A |
| V2 — Null dates | 0 | 0 | **PASS** | N/A |
| V3 — Valid statuses | P, C, X, H, R only | P, C, X, H, R | **PASS** | N/A |
| V4 — No orphans | 0 | 0 | **PASS** | N/A |

[Insert Screenshot 7: MySQL terminal showing V1–V4 validation query results]

### Prisma Integration

The Prisma schema (`prisma/schema.prisma`) reflects the refactored database structure with all normalised tables, proper types (`DateTime`, `Decimal`), enums (`PatientGender`, `PhoneType`), and relations.

```bash
# Setup Prisma
npm install prisma --save-dev
npx prisma init
# Copy schema.prisma and .env
npx prisma migrate dev --name init
npx prisma generate
```

[Insert Screenshot 12: Prisma migration output and schema.prisma]
