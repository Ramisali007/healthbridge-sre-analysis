-- ============================================================
-- Part F1: Normalised Schema (3NF) for pat_master
-- ============================================================

-- Drop normalised tables if they exist
DROP TABLE IF EXISTS patient_addresses;
DROP TABLE IF EXISTS patient_phones;
DROP TABLE IF EXISTS patients;

-- ============================================================
-- patients — core patient information (1NF/2NF/3NF compliant)
-- ============================================================
CREATE TABLE patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,                    -- proper DATE type, not VARCHAR
    gender ENUM('Male', 'Female', 'Non-Binary') NOT NULL,  -- explicit labels, not magic codes
    email VARCHAR(255),
    registered_doctor_id INT,                       -- proper FK to doctors table
    total_visits INT DEFAULT 0,
    last_bill_amount DECIMAL(12,2) DEFAULT 0.00,   -- DECIMAL for currency, not FLOAT
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient_doctor FOREIGN KEY (registered_doctor_id) REFERENCES doctors(DoctorID)
);

-- ============================================================
-- patient_phones — eliminates repeating phone group (1NF fix)
-- ============================================================
CREATE TABLE patient_phones (
    phone_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    phone_type ENUM('Primary', 'Secondary', 'Emergency') DEFAULT 'Primary',
    CONSTRAINT fk_phone_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE
);

-- ============================================================
-- patient_addresses — separate address entity (1NF/3NF fix)
-- ============================================================
CREATE TABLE patient_addresses (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    province VARCHAR(100),
    postal_code VARCHAR(10),
    address_type ENUM('Home', 'Work', 'Other') DEFAULT 'Home',
    CONSTRAINT fk_address_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE
);
