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

> **Insert:** Draw.io dependency diagram here showing all 6 classes as nodes with directed arrows for dependencies. DashboardFrame should be in the center with 9 outgoing arrows. FileBasedDAO should be at the bottom with 4 incoming arrows.

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
