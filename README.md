# SRE Final Project — Re-Engineering a Legacy Hospital Management System

**Course:** Software Re-Engineering (BSSE-8B)  
**Authors:** Ramis Ali (22F-3703) & Kamil Mohsin (22F-3713)

## Project Overview

This project applies a full software re-engineering pipeline to:
1. **A Java project** (ZainAftab-dev/hospital-management-system) for code analysis (Parts A–D)
2. **The legacy HealthBridge Hospital schema** for database re-engineering (Parts E–G)

## Repository Structure

```
SRE_FINAL_PROJECT/
├── java-project/              # Cloned HMS Java project
├── sql-scripts/
│   ├── 01_legacy_schema.sql   # Legacy schema + sample data
│   ├── 02_normalised_schema.sql # Normalised tables (3NF)
│   └── 03_refactoring_scripts.sql # Five refactoring operations
├── prisma/
│   ├── schema.prisma          # Prisma ORM schema definition
│   └── .env                   # Database connection string
├── migration/
│   ├── migration_etl.py       # Python ETL script
│   └── legacy_appointments.csv # Sample legacy CSV data
├── sonar-project.properties   # SonarQube scanner config
├── docs/                      # Report and screenshots
└── README.md                  # This file
```

## Setup Instructions

### 1. Start SonarQube (Docker)

```bash
# Pull and run SonarQube Community Edition
docker run -d --name sonarqube -p 9000:9000 sonarqube:community

# Wait ~60 seconds, then visit http://localhost:9000
# Default credentials: admin / admin
```

### 2. Run SonarScanner

```bash
# Navigate to the Java project directory
cd java-project

# Copy sonar-project.properties into the project root
cp ../sonar-project.properties .

# Run SonarScanner (must be installed and on PATH)
sonar-scanner
```

### 3. Load Hospital Legacy Schema (MySQL)

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE healthbridge;
USE healthbridge;

# Load legacy schema
source sql-scripts/01_legacy_schema.sql;

# Load normalised schema
source sql-scripts/02_normalised_schema.sql;

# Run refactoring scripts
source sql-scripts/03_refactoring_scripts.sql;
```

### 4. Setup Prisma

```bash
# Install Prisma
npm install prisma --save-dev

# Initialize Prisma
npx prisma init

# Copy schema.prisma from prisma/ directory
# Update .env with your database URL

# Run migrations
npx prisma migrate dev --name init

# Generate Prisma Client
npx prisma generate
```

### 5. Run Migration Script

```bash
# Install Python dependencies
pip install mysql-connector-python

# Run the ETL migration
cd migration
python migration_etl.py
```

### 6. Post-Migration Validation

```sql
-- V1: Row count
SELECT COUNT(*) AS migrated_rows FROM appointments;

-- V2: No NULL dates
SELECT COUNT(*) AS null_dates FROM appointments WHERE appt_datetime IS NULL;

-- V3: Valid statuses only
SELECT DISTINCT status FROM appointments;

-- V4: No orphan appointments
SELECT COUNT(*) AS orphans FROM appointments a
LEFT JOIN patients p ON a.patient_id = p.patient_id
WHERE p.patient_id IS NULL;
```

## Tools Used

| Tool | Version | Purpose |
|------|---------|---------|
| Docker | 24.x+ | Host SonarQube container |
| SonarQube | 10.x Community | Static analysis & metrics |
| SonarScanner | 5.x | Submit project to SonarQube |
| MySQL | 8.0+ | Legacy hospital database |
| Python | 3.10+ | Migration ETL script |
| Prisma | 5.x | ORM for database tasks |
| Draw.io | Online | Dependency & CFG diagrams |
| Python Tutor | Online | Step-by-step execution tracing |
| AST Explorer | Online | Abstract Syntax Tree inspection |

## Java Project Analysed

**Repository:** https://github.com/ZainAftab-dev/hospital-management-system  
**Language:** Java 8+ (100% Java)  
**Architecture:** Swing GUI + File-based DAO + Service Layer  
**Total Classes:** 25+  
**Lines of Code:** ~4,500+  

## License

This project is for academic purposes only — Software Re-Engineering course, BSSE-8B.
