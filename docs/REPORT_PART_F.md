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
