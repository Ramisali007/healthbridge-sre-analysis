# 📸 Complete Screenshot Guide — SRE Final Project
# Ramis Ali (22F-3703) & Kamil Mohsin (22F-3713)

> Take ALL 13 screenshots in ORDER. Save each as PNG in a `docs/screenshots/` folder.
> Use `Win + Shift + S` (Windows Snipping Tool) to capture.

---

## PREREQUISITE: Install Required Tools

Before taking any screenshots, make sure these are installed:

```
1. Docker Desktop    → https://www.docker.com/products/docker-desktop/
2. MySQL 8.0         → https://dev.mysql.com/downloads/installer/
3. Python 3.10+      → https://www.python.org/downloads/
4. Node.js 18+       → https://nodejs.org/
5. SonarScanner      → https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner/
```

---

## SCREENSHOT 1: GitHub Repository Page
**For:** Part A1 — Project Selection

### Steps:
1. Open browser → go to `https://github.com/Ramisali007/healthbridge-sre-analysis`
2. Make sure you're LOGGED IN as **Ramisali007** (your profile photo should be visible in top-right)
3. The repo should show all your files (sql-scripts, docs, migration, etc.)

### What to capture:
- Full browser window showing the repo page
- Your GitHub username/avatar MUST be visible in the top-right corner
- The file listing must be visible

### Save as: `screenshot_01_github_repo.png`

---

## SCREENSHOT 2: Docker Version
**For:** Part A2 — Tool Verification

### Steps:
1. Open PowerShell/CMD
2. Run: `docker --version`

### What to capture:
- Terminal showing the docker version output (e.g., `Docker version 24.x.x`)

### Save as: `screenshot_02_docker_version.png`

---

## SCREENSHOT 3: SonarQube Dashboard
**For:** Part A2 + Part B1

### Steps:
1. Open PowerShell and run:
   ```
   docker run -d --name sonarqube -p 9000:9000 sonarqube:community
   ```
2. Wait 60-90 seconds for SonarQube to start
3. Open browser → go to `http://localhost:9000`
4. Login with username: `admin`, password: `admin`
   (It will ask you to change password — change it to something like `admin123`)
5. You should see the SonarQube dashboard

### What to capture:
- Full browser window showing SonarQube dashboard at localhost:9000
- The version number should be visible (usually in the footer)

### Save as: `screenshot_03_sonarqube_dashboard.png`

---

## SCREENSHOT 4: SonarScanner BUILD SUCCESS
**For:** Part A2 + Part B1

### Steps:
1. First, download SonarScanner if not installed:
   - Go to: https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/scanners/sonarscanner/
   - Download the Windows version
   - Extract to a folder (e.g., `C:\sonar-scanner`)
   - Add `C:\sonar-scanner\bin` to your PATH environment variable

2. Copy sonar-project.properties into the Java project folder:
   ```
   copy "c:\Users\Friends\Desktop\Ramis\SRE_FINAL _PROJECT\sonar-project.properties" "c:\Users\Friends\Desktop\Ramis\SRE_FINAL _PROJECT\hospital-management-system-main\"
   ```

3. Navigate to the Java project and run the scanner:
   ```
   cd "c:\Users\Friends\Desktop\Ramis\SRE_FINAL _PROJECT\hospital-management-system-main"
   sonar-scanner
   ```

4. Wait for it to finish — it should print "EXECUTION SUCCESS" or "BUILD SUCCESS"

### What to capture:
- Terminal showing the scanner output with "EXECUTION SUCCESS" visible
- The project key "hospital-management-system" should be visible in the output

### Save as: `screenshot_04_sonarscanner_success.png`

---

## SCREENSHOT 5: SonarQube Project Overview (with metrics)
**For:** Part B1 — Metrics Extraction

### Steps:
1. After SonarScanner runs successfully, go back to browser
2. Open `http://localhost:9000`
3. Click on the "Hospital Management System" project
4. You should see the project overview with metrics:
   - Lines of Code
   - Bugs
   - Code Smells
   - Vulnerabilities
   - Security Hotspots
   - Duplications %
   - Technical Debt

### What to capture:
- Full project overview page showing ALL metrics
- Take a SECOND screenshot of the "Measures" tab if available

### Save as: `screenshot_05_sonarqube_metrics.png`

> **IMPORTANT:** After taking this screenshot, UPDATE the values in
> `docs/REPORT_PART_B.md` with the ACTUAL values from SonarQube!
> Replace the approximate values (~85, ~180, etc.) with real ones.

---

## SCREENSHOT 6: MySQL Version
**For:** Part A2 — Tool Verification

### Steps:
1. Open PowerShell/CMD
2. Run: `mysql --version`

### What to capture:
- Terminal showing MySQL version output

### Save as: `screenshot_06_mysql_version.png`

---

## SCREENSHOT 7: MySQL — Load Schema and Run Validation Queries
**For:** Part F + Part G4

### Steps:
1. Open MySQL terminal:
   ```
   mysql -u root -p
   ```
   (Enter your MySQL root password)

2. Create and use the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS healthbridge;
   USE healthbridge;
   ```

3. Load the legacy schema:
   ```sql
   SOURCE c:/Users/Friends/Desktop/Ramis/SRE_FINAL_PROJECT/sql-scripts/01_legacy_schema.sql;
   ```
   (Note: Use forward slashes in MySQL)

4. Load the normalised schema:
   ```sql
   SOURCE c:/Users/Friends/Desktop/Ramis/SRE_FINAL_PROJECT/sql-scripts/02_normalised_schema.sql;
   ```

5. Run the refactoring scripts:
   ```sql
   SOURCE c:/Users/Friends/Desktop/Ramis/SRE_FINAL_PROJECT/sql-scripts/03_refactoring_scripts.sql;
   ```

6. Run the validation queries:
   ```sql
   SELECT COUNT(*) AS migrated_rows FROM appointments;
   SELECT COUNT(*) AS null_dates FROM appointments WHERE appt_datetime IS NULL;
   SELECT DISTINCT status FROM appointments;
   ```

### What to capture:
- Terminal showing the queries running and their results

### Save as: `screenshot_07_mysql_queries.png`

---

## SCREENSHOT 8: Python Tutor — Execution Trace
**For:** Part D1

### Steps:
1. Open browser → go to `https://pythontutor.com/visualize.html`
2. Make sure "Python 3.6+" is selected as the language
3. Copy ALL the code from: `docs/python_tutor_code.py`
4. Paste it into the Python Tutor editor
5. Click "Visualize Execution"
6. Step through until you reach the line:
   ```python
   if user["password"] == password and user["active"]:
   ```
   (This is Step 9 in the trace table — inside the authenticate() function)
7. At this point, the right panel should show the variable values:
   - username = "admin"
   - password = "admin"  
   - user = {"password": "admin", "fullName": "Administrator", ...}

### What to capture:
- Full browser window showing:
  - The code on the LEFT with the current line highlighted
  - The variable frame panel on the RIGHT showing variable values
  - The step counter visible

### Save as: `screenshot_08_python_tutor.png`

---

## SCREENSHOT 9: Draw.io — Dependency Graph
**For:** Part C1

### Steps:
1. Open browser → go to `https://app.diagrams.net/` (Draw.io)
2. Click "Create New Diagram" → Choose "Blank Diagram"
3. Create a dependency diagram with these 6 classes as boxes:
   
   ```
   Arrange them in layers:
   
   TOP:        [LoginFrame]
   
   MIDDLE:     [DashboardFrame] ←───── (THE GOD CLASS with 9 outgoing arrows)
   
   PANELS:     [PatientMgmtPanel]  [PharmacyMgmtPanel]  [BillingMgmtPanel]
                     ↓                    ↓                    ↓
   SERVICES:   [PatientService]    [MedicineService]    (uses PatientService)
                     ↓                    ↓
   DAO:        [PatientDAO]        [MedicineDAO]
                     ↓                    ↓
   BASE:            [FileBasedDAO]  ←───── (all DAOs extend this)
                          ↓
   INTERFACE:      [DataAccessObject]
   
   MODELS:     [Patient]           [Medicine]           [User]
   ```

4. Draw arrows FROM dependent class TO dependency
5. Label DashboardFrame as "GOD CLASS (Ce=9)"
6. Color DashboardFrame RED (it's the most volatile)
7. Color Patient and Medicine GREEN (most stable)

### What to capture:
- Full Draw.io window showing the complete dependency graph

### Save as: `screenshot_09_dependency_graph.png`

---

## SCREENSHOT 10: Draw.io — Control Flow Graph (CFG)
**For:** Part D2

### Steps:
1. In the same Draw.io session, create a NEW page (tab)
2. Draw the CFG for the `login()` + `authenticate()` method:

   ```
   [ENTRY]
      ↓
   [B1: username = input.strip(); password = input.strip()]
      ↓
   <D1: username=="" OR password==""?> ──YES──→ [B2: result="ERROR: empty"] → [EXIT]
      ↓ NO
   [B3: user = authenticate(username, password)]
      ↓
   <D2: user is not None?> ──YES──→ [B4: result="SUCCESS: Welcome..."] → [EXIT]
      ↓ NO
   [B5: result="ERROR: Invalid..."] → [EXIT]
   ```

   Inside authenticate():
   ```
   [ENTRY_AUTH]
      ↓
   <D3: username empty?> ──YES──→ [return None]
      ↓ NO
   <D4: password empty?> ──YES──→ [return None]
      ↓ NO
   [B6: user = users_db.get(username)]
      ↓
   <D5: user is None?> ──YES──→ [return None]
      ↓ NO
   <D6: password match AND active?> ──YES──→ [return user] → [EXIT_AUTH]
      ↓ NO
   [return None] → [EXIT_AUTH]
   ```

3. Use DIAMOND shapes for decisions (D1-D6)
4. Use RECTANGLES for basic blocks (B1-B6)
5. Use ROUNDED RECTANGLES for ENTRY/EXIT
6. **HIGHLIGHT the success path in BLUE/BOLD:**
   ENTRY → B1 → D1(NO) → B3 → ENTRY_AUTH → D3(NO) → D4(NO) → B6 → D5(NO) → D6(YES) → return user → D2(YES) → B4 → EXIT

### What to capture:
- Full Draw.io window showing the CFG with highlighted path

### Save as: `screenshot_10_cfg_diagram.png`

---

## SCREENSHOT 11: AST Explorer
**For:** Part D3

### Steps:
1. Open browser → go to `https://astexplorer.net/`
2. At the top, select language: **Java**
3. Select parser: **java-parser** (or whichever Java parser is available)
4. In the LEFT panel, paste this code:
   ```java
   public User authenticate(String username, String password) {
       if (username == null || username.isEmpty()) {
           return null;
       }
       if (password == null || password.isEmpty()) {
           return null;
       }
       User user = userDAO.findById(username);
       if (user != null && user.getPassword().equals(password) && user.isActive()) {
           return user;
       }
       return null;
   }
   ```
5. In the RIGHT panel, expand the AST tree to at least 3 levels:
   - Expand `MethodDeclaration` (authenticate)
     - Expand `parameters`
       - See `VariableDeclaration` nodes
     - Expand `body` → `Block`
       - See `IfStatement` nodes
         - Expand `test` → `BinaryExpression`
       - See `ReturnStatement` nodes

### What to capture:
- Full browser window showing code on LEFT and expanded AST tree on RIGHT
- Make sure MethodDeclaration, IfStatement, VariableDeclaration, ReturnStatement, and BinaryExpression are visible in the tree

### Save as: `screenshot_11_ast_explorer.png`

---

## SCREENSHOT 12: Prisma Schema + Migration
**For:** Part E/F/G — Prisma Requirement

### Steps:
1. Open PowerShell, navigate to the project:
   ```
   cd "c:\Users\Friends\Desktop\Ramis\SRE_FINAL _PROJECT"
   ```

2. Install Prisma:
   ```
   npm install
   ```

3. Run Prisma migration:
   ```
   npx prisma migrate dev --name init
   ```
   (This requires MySQL to be running with the `healthbridge` database)

4. If migration succeeds, you'll see output like:
   ```
   Applying migration `20240324_init`
   The following migration(s) have been applied:
   migrations/20240324_init/migration.sql
   Your database is now in sync with your schema.
   ```

### What to capture:
- Terminal showing `npx prisma migrate dev` output with success message
- BONUS: Also screenshot `schema.prisma` open in your editor (you already have this open!)

### Save as: `screenshot_12_prisma_migration.png`

---

## SCREENSHOT 13: Migration ETL Script Output
**For:** Part G3 + G4

### Steps:
1. First make sure MySQL is running and has the healthbridge database with tables loaded
2. Open PowerShell:
   ```
   cd "c:\Users\Friends\Desktop\Ramis\SRE_FINAL _PROJECT\migration"
   ```

3. Install the Python MySQL connector:
   ```
   pip install mysql-connector-python
   ```

4. Edit `migration_etl.py` — update the database credentials near the bottom:
   ```python
   db_config = {
       'host': 'localhost',
       'user': 'root',         # <-- your MySQL username
       'password': 'root',     # <-- your MySQL password  
       'database': 'healthbridge'
   }
   ```

5. Run the migration:
   ```
   python migration_etl.py
   ```

6. You should see output like:
   ```
   ============================================================
   HealthBridge Hospital — Legacy Data Migration ETL
   ============================================================
     SKIP row 1010: invalid status 'Z'
   ------------------------------------------------------------
   MIGRATION SUMMARY
     Total rows in CSV:    12
     Successfully migrated:11
     Skipped (validation): 1
   ============================================================
   ```

### What to capture:
- Terminal showing the full migration output with row counts and the skipped row

### Save as: `screenshot_13_migration_output.png`

---

# ✅ SCREENSHOT CHECKLIST

| # | Screenshot | Part | Done? |
|---|-----------|------|-------|
| 1 | GitHub repo page with your account visible | A1 | ☐ |
| 2 | `docker --version` output | A2 | ☐ |
| 3 | SonarQube dashboard at localhost:9000 | A2 | ☐ |
| 4 | SonarScanner terminal BUILD SUCCESS | A2, B1 | ☐ |
| 5 | SonarQube project overview with metrics | B1 | ☐ |
| 6 | `mysql --version` output | A2 | ☐ |
| 7 | MySQL validation queries output | F, G4 | ☐ |
| 8 | Python Tutor at Step 9 (branch evaluation) | D1 | ☐ |
| 9 | Draw.io dependency graph (6 classes) | C1 | ☐ |
| 10 | Draw.io control flow graph with highlighted path | D2 | ☐ |
| 11 | AST Explorer with 3-level expansion | D3 | ☐ |
| 12 | Prisma migration output | E/F/G | ☐ |
| 13 | Migration ETL terminal output | G3 | ☐ |

---

# 📝 AFTER ALL SCREENSHOTS

## Compile the PDF Report:
1. Open Microsoft Word or Google Docs
2. Copy content from each `REPORT_PART_*.md` file (A through G) IN ORDER
3. Insert the corresponding screenshots RIGHT AFTER the sections that say "> **Insert:** Screenshot..."
4. Add a caption under each screenshot (e.g., "Figure 1: SonarQube Dashboard showing project overview")
5. Add page numbers
6. Export as PDF
7. Name it: `SRE_Final_Project_22F-3703_22F-3713.pdf`

## Update Report with REAL SonarQube Values:
After Screenshot 5, open `docs/REPORT_PART_B.md` and replace the approximate
values in the metrics table with the ACTUAL values from SonarQube:
- Replace `~85` with the actual Total Code Smells number
- Replace `~180` with the actual Cyclomatic Complexity
- Replace `~3.2` with the actual average CC
- Replace `~220` with the actual Cognitive Complexity
- Replace `~18%` with the actual Duplication %
- Replace `C` with the actual Maintainability Rating
- Replace `~12h` with the actual Technical Debt hours
- Replace `~3` with the actual Security Hotspots count
