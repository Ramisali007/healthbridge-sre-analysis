# PART A — Project Initialisation and Tool Setup [8 Marks]

## A1. Java Project Selection [3 Marks]

**GitHub URL:** https://github.com/ZainAftab-dev/hospital-management-system

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

> **Note:** Screenshots of each tool with version numbers must be captured during your live setup and inserted into the PDF report.
