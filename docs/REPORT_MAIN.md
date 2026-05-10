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

1. [Part A — Project Initialisation and Tool Setup (8 Marks)](./REPORT_PART_A.md)
2. [Part B — Code Smell Analysis and Refactoring (27 Marks)](./REPORT_PART_B.md)
3. [Part C — Dependency, Coupling and Technical Debt (15 Marks)](./REPORT_PART_C.md)
4. [Part D — Dynamic Program Analysis (10 Marks)](./REPORT_PART_D.md)
5. [Part E — Data Smell Detection (15 Marks)](./REPORT_PART_E.md)
6. [Part F — Schema Normalisation and Refactoring (15 Marks)](./REPORT_PART_F.md)
7. [Part G — Data Migration Design and Execution (10 Marks)](./REPORT_PART_G.md)

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
