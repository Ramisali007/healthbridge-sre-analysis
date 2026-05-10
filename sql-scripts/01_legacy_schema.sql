-- ============================================================
-- HealthBridge Hospital Management System — Legacy Schema
-- Accumulated 2009–2024. Never formally reviewed.
-- Part E: Data Smell Detection - Legacy Schema Setup
-- ============================================================

-- Drop existing tables if they exist for clean setup
DROP TABLE IF EXISTS billing;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS pat_master;

-- Create the legacy schema exactly as provided
CREATE TABLE pat_master (
    pid INT,
    p_name VARCHAR(255),
    dob VARCHAR(50),                -- stored as 'DD/MM/YYYY' plain text
    sex CHAR(1),                    -- 'M', 'F', or '3' for non-binary
    ph1 VARCHAR(255),
    ph2 VARCHAR(255),
    ph3 VARCHAR(255),               -- repeating phone group
    addr1 VARCHAR(255),
    addr2 VARCHAR(255),
    city VARCHAR(255),
    reg_doc VARCHAR(255),           -- doctor full name stored as plain text
    reg_doc_id VARCHAR(255),        -- sometimes INT string, sometimes 'DR-042'
    total_visits INT,               -- updated manually, not via trigger
    last_bill FLOAT,               -- stores PKR amounts; FLOAT used for currency
    notes TEXT                      -- JSON blobs, free text, and CSV tags all mixed
);

CREATE TABLE appointments (
    appt_id INT,
    patient_id INT,
    patient_nm VARCHAR(255),        -- duplicated from pat_master
    patient_ph VARCHAR(255),        -- duplicated from pat_master
    doc_id INT,
    doc_name VARCHAR(255),          -- duplicated from doctors table
    appt_date VARCHAR(50),          -- 'YYYY-MM-DD HH:MM' stored as text
    status CHAR(1),                 -- 'P'=Pending 'C'=Complete 'X'=Cancel 'H'=Hold 'R'=Rescheduled
    fee FLOAT,
    discount FLOAT,
    net_fee FLOAT,                  -- always = fee - discount (derived value)
    room VARCHAR(255)               -- 'Room 3 Block B' — two facts in one column
);

CREATE TABLE doctors (
    DoctorID INT PRIMARY KEY,
    FullName VARCHAR(255),
    Speciality VARCHAR(255),
    ContactNo VARCHAR(255),
    JoinDt VARCHAR(50),             -- date stored as text
    Salary FLOAT,                   -- monthly salary stored as FLOAT
    dept_id INT,                    -- references departments but no FK defined
    isActive CHAR(1)                -- 'Y', 'N', or sometimes '1'
);

CREATE TABLE billing (
    bill_no VARCHAR(50),            -- intended as PK but no constraint defined
    pid INT,
    pname VARCHAR(255),             -- patient name duplicated again
    services TEXT,                  -- 'Lab,Xray,OPD' — comma-separated list
    svc_cost FLOAT,
    tax_pct FLOAT,
    tax_amt FLOAT,                  -- derived: svc_cost * tax_pct / 100
    grand_total FLOAT,              -- derived: svc_cost + tax_amt
    paid FLOAT,
    balance FLOAT,                  -- derived: grand_total - paid
    created VARCHAR(50),            -- date stored as text
    created_by VARCHAR(255)         -- username as free text; no FK to users
);

CREATE TABLE departments (
    dept_id INT PRIMARY KEY,
    dept_nm VARCHAR(255),
    hod VARCHAR(255),               -- head-of-department stored as plain name
    budget FLOAT
);

-- ============================================================
-- Insert sample data for testing
-- ============================================================

INSERT INTO departments VALUES (1, 'Cardiology', 'Dr. Ahmed Khan', 5000000.00);
INSERT INTO departments VALUES (2, 'Neurology', 'Dr. Sara Malik', 4500000.00);
INSERT INTO departments VALUES (3, 'Orthopedics', 'Dr. Kamran Raza', 3500000.00);
INSERT INTO departments VALUES (4, 'Pediatrics', 'Dr. Ayesha Noor', 4000000.00);
INSERT INTO departments VALUES (5, 'General Surgery', 'Dr. Hina Iqbal', 6000000.00);

INSERT INTO doctors VALUES (7, 'Dr. Ayesha Noor', 'Pediatrics', '0300-1234567', '15/03/2015', 350000.00, 4, 'Y');
INSERT INTO doctors VALUES (12, 'Dr. Kamran Raza', 'Orthopedics', '0321-9876543', '01/06/2012', 450000.00, 3, 'Y');
INSERT INTO doctors VALUES (15, 'Dr. Ahmed Khan', 'Cardiology', '0333-5551234', '10/01/2010', 500000.00, 1, '1');
INSERT INTO doctors VALUES (18, 'Dr. Sara Malik', 'Neurology', '0312-7778899', '22/09/2018', 380000.00, 2, 'N');
INSERT INTO doctors VALUES (20, 'Dr. Hina Iqbal', 'General Surgery', '0345-6667788', '05/11/2020', 400000.00, 5, 'Y');

INSERT INTO pat_master VALUES (5, 'Ali Hassan', '12/05/1990', 'M', '0312-9876543', '0301-1112233', NULL, '123 Main St', 'Block B', 'Lahore', 'Dr. Kamran Raza', 'DR-042', 15, 1500.00, '{"allergies": "none", "notes": "regular patient"}');
INSERT INTO pat_master VALUES (8, 'Sara Malik', '25/11/1985', 'F', '0333-1234567', NULL, NULL, '456 Park Ave', 'DHA Phase 5', 'Karachi', 'Dr. Ayesha Noor', '7', 8, 2000.00, 'Follow-up required, diabetes,hypertension');
INSERT INTO pat_master VALUES (21, 'Hina Iqbal', '03/07/2001', 'F', '0321-5556789', '0300-9998877', '0345-1234567', '789 Garden Rd', NULL, 'Islamabad', 'Dr. Ayesha Noor', 'DR-007', 3, 1500.00, NULL);
INSERT INTO pat_master VALUES (33, 'Usman Ali', '18/02/1975', 'M', '0345-3332211', NULL, NULL, '321 Lake View', 'Gulberg III', 'Lahore', 'Dr. Ahmed Khan', '15', 22, 5000.00, 'cardiac,stent,2019,follow-up,monthly');
INSERT INTO pat_master VALUES (45, 'Zara Ahmed', '30/09/1998', '3', '0300-7776655', NULL, NULL, '567 River Rd', NULL, 'Rawalpindi', 'Dr. Sara Malik', 'DR-018', 5, 3500.00, '{"condition": "epilepsy", "meds": ["valproate", "levetiracetam"]}');

INSERT INTO appointments VALUES (1001, 5, 'Ali Hassan', '0312-9876543', 12, 'Dr. Kamran Raza', '2024-03-15 09:30', 'P', 1500.00, 0.00, 1500.00, 'Room 3 Block B');
INSERT INTO appointments VALUES (1002, 8, 'Sara Malik', '0333-1234567', 7, 'Dr. Ayesha Noor', '2024-03-15 10:00', 'C', 2000.00, 200.00, 1800.00, 'Room 7 Block A');
INSERT INTO appointments VALUES (1003, 5, 'Ali Hassan', '0312-9876543', 12, 'Dr. Kamran Raza', '2024-03-16 11:00', 'X', 1500.00, 0.00, 1500.00, 'Room 3 Block B');
INSERT INTO appointments VALUES (1004, 21, 'Hina Iqbal', '0321-5556789', 7, 'Dr. Ayesha Noor', '2024-03-17 09:00', 'H', 2000.00, 500.00, 1500.00, 'Room 7 Block A');

INSERT INTO billing VALUES ('BL-001', 5, 'Ali Hassan', 'Lab,Xray,OPD', 3500.00, 16.0, 560.00, 4060.00, 4060.00, 0.00, '15/03/2024', 'admin');
INSERT INTO billing VALUES ('BL-002', 8, 'Sara Malik', 'OPD,Medicine', 2000.00, 16.0, 320.00, 2320.00, 1500.00, 820.00, '15/03/2024', 'receptionist1');
INSERT INTO billing VALUES ('BL-003', 21, 'Hina Iqbal', 'OPD', 1500.00, 16.0, 240.00, 1740.00, 1740.00, 0.00, '17/03/2024', 'admin');
INSERT INTO billing VALUES ('BL-004', 999, 'Ghost Patient', 'Lab', 500.00, 16.0, 80.00, 580.00, 0.00, 580.00, '20/03/2024', 'admin');
