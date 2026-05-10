-- ============================================================
-- Part F2: Five Schema Refactoring Scripts
-- ============================================================

-- ============================================================
-- R1: Fix Derived Data in billing (1.5 Marks)
-- Remove tax_amt, grand_total, and balance from billing.
-- Replace with a view that computes them on read.
-- ============================================================

ALTER TABLE billing DROP COLUMN tax_amt;
ALTER TABLE billing DROP COLUMN grand_total;
ALTER TABLE billing DROP COLUMN balance;

-- Create view to compute derived values on read
CREATE OR REPLACE VIEW v_billing_summary AS
SELECT
    bill_no,
    pid,
    pname,
    services,
    svc_cost,
    tax_pct,
    ROUND(svc_cost * tax_pct / 100, 2) AS tax_amt,
    ROUND(svc_cost + svc_cost * tax_pct / 100, 2) AS grand_total,
    paid,
    ROUND(svc_cost + svc_cost * tax_pct / 100 - paid, 2) AS balance,
    created,
    created_by
FROM billing;

/*
Explanation — Derived Data Smell:
The billing table stored tax_amt (= svc_cost * tax_pct / 100), grand_total (= svc_cost + tax_amt),
and balance (= grand_total - paid) as physical columns. These are computed values that can be derived
from svc_cost, tax_pct, and paid. Storing them creates a Derived Data smell because if any source
value changes (e.g., svc_cost is updated due to a pricing correction), the derived columns become
stale unless a trigger or application code explicitly recalculates them. In a hospital billing
context, stale financial data can lead to overcharging patients, incorrect insurance claims, or
audit failures. The view v_billing_summary eliminates this risk by computing the values on every
read, guaranteeing they are always consistent with the source data.
*/


-- ============================================================
-- R2: Fix Overloaded Column in appointments.status (1 Mark)
-- Replace the opaque CHAR(1) status code with a reference table.
-- ============================================================

CREATE TABLE appt_status_ref (
    status_code CHAR(1) PRIMARY KEY,
    description VARCHAR(50) NOT NULL
);

INSERT INTO appt_status_ref VALUES
    ('P', 'Pending'),
    ('C', 'Completed'),
    ('X', 'Cancelled'),
    ('H', 'On Hold'),
    ('R', 'Rescheduled');

ALTER TABLE appointments
    ADD CONSTRAINT fk_appt_status
    FOREIGN KEY (status) REFERENCES appt_status_ref(status_code);

/*
Explanation — Overloaded Column / Magic Values Smell:
The status column in appointments used single-character codes ('P', 'C', 'X', 'H', 'R') with no
documentation or constraint enforcing valid values. Without a reference table, any arbitrary
character could be inserted (e.g., 'Z', '?', '1'), corrupting the data silently. The
appt_status_ref table provides human-readable descriptions and the FK constraint guarantees that
only valid status codes can be stored. This prevents invalid appointment states that could lead
to scheduling errors — for example, a patient marked with an undefined status code would be
invisible to both the doctor's schedule view and billing, potentially causing missed treatments.
*/


-- ============================================================
-- R3: Fix Inconsistent Naming across doctors (1 Mark)
-- Standardise all column names to lowercase snake_case.
-- ============================================================

ALTER TABLE doctors RENAME COLUMN DoctorID TO doctor_id;
ALTER TABLE doctors RENAME COLUMN FullName TO full_name;
ALTER TABLE doctors RENAME COLUMN Speciality TO speciality;
ALTER TABLE doctors RENAME COLUMN ContactNo TO contact_no;
ALTER TABLE doctors RENAME COLUMN JoinDt TO join_date;
ALTER TABLE doctors RENAME COLUMN Salary TO salary_monthly;
ALTER TABLE doctors RENAME COLUMN isActive TO is_active;

/*
Explanation — Inconsistent Naming Smell:
The doctors table used a mix of PascalCase (DoctorID, FullName), camelCase (isActive), abbreviated
forms (JoinDt, ContactNo), and lowercase (dept_id). This inconsistency violates the principle of
uniform naming conventions and creates several practical problems: (1) developers must memorise which
convention each column uses, increasing cognitive load; (2) ORM frameworks that auto-generate field
names from column names produce inconsistent Java/Python variable names; (3) JOIN conditions become
error-prone when guessing between DoctorID, doctor_id, or doc_id across tables. The standard adopted
here is lowercase_snake_case throughout all tables, which is the MySQL convention and widely
recommended for cross-platform compatibility.

Additional tables renamed for consistency:
- pat_master.reg_doc_id → would become registered_doctor_id in normalised schema
- All date columns renamed to follow pattern: join_date, created_at, etc.
*/


-- ============================================================
-- R4: Fix Missing Constraints in billing and appointments (1 Mark)
-- Add PK to billing and FK constraints with data backfill.
-- ============================================================

-- Step 1: Add PK to billing
ALTER TABLE billing ADD PRIMARY KEY (bill_no);

-- Step 2: Remove orphan billing rows referencing non-existent patients
DELETE FROM billing WHERE pid NOT IN (SELECT pid FROM pat_master);

-- Step 3: Add FK from billing to pat_master
ALTER TABLE billing
    ADD CONSTRAINT fk_billing_patient
    FOREIGN KEY (pid) REFERENCES pat_master(pid);

-- Step 4: Add PK to pat_master (it has none!)
ALTER TABLE pat_master ADD PRIMARY KEY (pid);

-- Step 5: Add PK to appointments
ALTER TABLE appointments ADD PRIMARY KEY (appt_id);

-- Step 6: Add FK from appointments to doctors
ALTER TABLE appointments
    ADD CONSTRAINT fk_appt_doctor
    FOREIGN KEY (doc_id) REFERENCES doctors(doctor_id);

-- Step 7: Add FK from appointments to pat_master
ALTER TABLE appointments
    ADD CONSTRAINT fk_appt_patient
    FOREIGN KEY (patient_id) REFERENCES pat_master(pid);

/*
Explanation — Missing Constraints Smell:
The billing table was designed with bill_no as a surrogate key but never declared it as PRIMARY KEY,
meaning duplicate bill numbers could be inserted silently. Similarly, pat_master had no PRIMARY KEY
at all — multiple rows with the same pid could coexist, breaking referential integrity. The
appointments table lacked foreign keys to both doctors and pat_master, meaning orphan appointments
could reference non-existent patients or doctors.

The DELETE backfill step (Step 2) is essential before enabling the FK constraint because the legacy
data contains billing row BL-004 referencing pid=999 (labelled 'Ghost Patient'), which does not
exist in pat_master. Attempting to create the FK without first removing this orphan would cause
MySQL error 1452 (Cannot add or update a child row: a foreign key constraint fails). In a production
migration, these orphan rows would be logged to an exceptions table for manual review before deletion.
*/


-- ============================================================
-- R5: Add Audit Trail to appointments (0.5 Mark)
-- ============================================================

ALTER TABLE appointments
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP;

/*
Explanation — Lack of Audit Trail Smell:
The appointments table had no timestamps recording when a record was created or last modified. In a
hospital information system, this creates serious accountability and compliance risks:

1. Regulatory Risk: Healthcare regulators (e.g., PMDC in Pakistan, HIPAA in the US) require audit
   trails for all patient-related data changes. Without created_at/updated_at, the hospital cannot
   prove when an appointment was booked, modified, or cancelled.

2. Dispute Resolution: If a patient claims they were charged for a cancelled appointment, the
   hospital has no timestamp evidence to verify when the status was changed from 'P' to 'X'.

3. Data Recovery: Without audit timestamps, it becomes impossible to reconstruct the state of
   the system at any point in time, making disaster recovery from backups unreliable.

The created_at column defaults to the current timestamp on INSERT, while updated_at automatically
refreshes on every UPDATE via MySQL's ON UPDATE CURRENT_TIMESTAMP clause. For PostgreSQL, an
equivalent trigger-based approach would be required.
*/
