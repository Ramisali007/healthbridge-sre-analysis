# ============================================================
# migration_etl.py — Load legacy CSV into refactored appointments schema
# Part G: Data Migration Design and Execution
#
# Authors: Ramis Ali (22F-3703), Kamil Mohsin (22F-3713)
# Course: Software Re-Engineering — BSSE-8B
# ============================================================

import csv
import mysql.connector
from datetime import datetime
import sys
import os

# Valid appointment status codes (from appt_status_ref table)
VALID_STATUSES = {'P', 'C', 'X', 'H', 'R'}

def parse_appt_date(raw):
    """
    T1: Convert appointment date from 'DD/MM/YYYY HH:MM' string to datetime object.
    
    The legacy system stores dates as plain text in DD/MM/YYYY HH:MM format,
    which prevents date-based queries, sorting, and range filtering.
    This function converts the string to a proper Python datetime object
    that MySQL can store as DATETIME type.
    
    Args:
        raw (str): Date string in format 'DD/MM/YYYY HH:MM'
    
    Returns:
        datetime: Parsed datetime object
    
    Raises:
        ValueError: If the date string cannot be parsed
    """
    # Strip any leading/trailing whitespace from the raw date string
    raw = raw.strip()
    
    try:
        # Primary format: DD/MM/YYYY HH:MM (as specified in legacy CSV)
        return datetime.strptime(raw, '%d/%m/%Y %H:%M')
    except ValueError:
        try:
            # Fallback format: YYYY-MM-DD HH:MM (some records use ISO format)
            return datetime.strptime(raw, '%Y-%m-%d %H:%M')
        except ValueError:
            try:
                # Another fallback: DD/MM/YYYY (date only, no time)
                return datetime.strptime(raw, '%d/%m/%Y')
            except ValueError:
                # Raise descriptive error if no format matches
                raise ValueError(f"Cannot parse date '{raw}' — expected DD/MM/YYYY HH:MM")


def split_room(raw):
    """
    T2: Split the room column ('Room 3 Block B') into room_number and building_block.
    
    The legacy schema stores two distinct facts in one column — the room number and
    the building/block identifier. This violates First Normal Form (non-atomic values).
    Splitting them allows independent queries (e.g., "all appointments in Block A")
    and proper indexing.
    
    Args:
        raw (str): Room string like 'Room 3 Block B'
    
    Returns:
        tuple: (room_number: int, building_block: str)
               e.g., (3, 'Block B')
    """
    # Strip whitespace from the raw room string
    raw = raw.strip()
    
    try:
        # Expected format: "Room <number> Block <letter>"
        # Split by 'Block' keyword to separate room number from block
        parts = raw.split('Block')
        
        if len(parts) == 2:
            # Extract room number from "Room 3 " part
            room_part = parts[0].strip()           # "Room 3"
            block_part = 'Block' + parts[1].strip() # "Block B"
            
            # Extract the numeric room number
            room_number = int(room_part.replace('Room', '').strip())
            
            return room_number, block_part
        else:
            # If format doesn't match, return defaults
            print(f"  WARNING: Unexpected room format: '{raw}', using defaults")
            return 0, raw
    except (ValueError, IndexError) as e:
        # If parsing fails, log warning and return safe defaults
        print(f"  WARNING: Could not parse room '{raw}': {e}")
        return 0, raw


def migrate(csv_path, db_config):
    """
    Main ETL function: Extract from CSV, Transform, Load into MySQL.
    
    Transformations applied:
    T1 - Convert appt_date from DD/MM/YYYY HH:MM to DATETIME
    T2 - Split room into room_number INT and building_block VARCHAR
    T3 - Omit patient_nm, patient_ph, doc_name (redundant after normalisation)
    T4 - Validate status against appt_status_ref; skip invalid codes
    
    Args:
        csv_path (str): Path to the legacy CSV file
        db_config (dict): MySQL connection parameters
    """
    # Track migration statistics for the post-migration report
    total_rows = 0        # Total rows read from CSV
    migrated_rows = 0     # Successfully inserted rows
    skipped_rows = []     # Rows skipped due to validation failures
    error_rows = []       # Rows that caused insertion errors
    
    print("=" * 60)
    print("HealthBridge Hospital — Legacy Data Migration ETL")
    print("=" * 60)
    print(f"Source: {csv_path}")
    print(f"Target: {db_config.get('database', 'N/A')} @ {db_config.get('host', 'localhost')}")
    print(f"Start time: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("-" * 60)
    
    # Establish database connection
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()
        print("[OK] Database connection established")
    except mysql.connector.Error as e:
        print(f"[FAIL] Database connection failed: {e}")
        sys.exit(1)
    
    # Open and process the CSV file
    try:
        with open(csv_path, newline='', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            
            for row in reader:
                total_rows += 1
                appt_id = row['appt_id'].strip()
                
                # ──────────────────────────────────────────────
                # T4: Validate status against reference table
                # Skip and log rows with unknown status codes
                # ──────────────────────────────────────────────
                status = row['status'].strip()
                if status not in VALID_STATUSES:
                    skipped_rows.append({
                        'appt_id': appt_id,
                        'reason': f"Invalid status code: '{status}'"
                    })
                    print(f"  SKIP row {appt_id}: invalid status '{status}'")
                    continue
                
                # ──────────────────────────────────────────────
                # T1: Convert date string to proper datetime
                # ──────────────────────────────────────────────
                try:
                    appt_dt = parse_appt_date(row['appt_date'])
                except ValueError as e:
                    skipped_rows.append({
                        'appt_id': appt_id,
                        'reason': str(e)
                    })
                    print(f"  SKIP row {appt_id}: {e}")
                    continue
                
                # ──────────────────────────────────────────────
                # T2: Split room column into two separate fields
                # ──────────────────────────────────────────────
                room_no, block = split_room(row['room'])
                
                # ──────────────────────────────────────────────
                # T3: Intentionally omit patient_nm, patient_ph,
                #     and doc_name — they are redundant duplicates
                #     that now derive from FK lookups
                # ──────────────────────────────────────────────
                
                # Parse numeric fields with error handling
                try:
                    patient_id = int(row['patient_id'].strip())
                    doc_id = int(row['doc_id'].strip())
                    fee = float(row['fee'].strip())
                    discount = float(row['discount'].strip())
                except (ValueError, KeyError) as e:
                    skipped_rows.append({
                        'appt_id': appt_id,
                        'reason': f"Numeric parse error: {e}"
                    })
                    print(f"  SKIP row {appt_id}: numeric parse error — {e}")
                    continue
                
                # ──────────────────────────────────────────────
                # INSERT into refactored appointments table
                # ──────────────────────────────────────────────
                try:
                    cursor.execute(
                        '''INSERT INTO appointments
                           (appt_id, patient_id, doc_id, appt_datetime,
                            status, fee, discount, room_number, building_block)
                           VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)''',
                        (
                            int(appt_id),           # appointment ID
                            patient_id,              # FK to patients table
                            doc_id,                  # FK to doctors table
                            appt_dt,                 # T1: converted datetime
                            status,                  # T4: validated status code
                            fee,                     # consultation fee
                            discount,                # discount amount
                            room_no,                 # T2: extracted room number
                            block                    # T2: extracted building block
                        )
                    )
                    migrated_rows += 1
                    
                except mysql.connector.Error as e:
                    error_rows.append({
                        'appt_id': appt_id,
                        'reason': str(e)
                    })
                    print(f"  ERROR row {appt_id}: {e}")
                    continue
            
            # Commit all successful insertions as a single transaction
            conn.commit()
    
    except FileNotFoundError:
        print(f"[FAIL] CSV file not found: {csv_path}")
        cursor.close()
        conn.close()
        sys.exit(1)
    except Exception as e:
        print(f"[FAIL] Unexpected error: {e}")
        conn.rollback()
        cursor.close()
        conn.close()
        sys.exit(1)
    
    # ──────────────────────────────────────────────
    # Print migration summary report
    # ──────────────────────────────────────────────
    print("-" * 60)
    print("MIGRATION SUMMARY")
    print("-" * 60)
    print(f"  Total rows in CSV:    {total_rows}")
    print(f"  Successfully migrated:{migrated_rows}")
    print(f"  Skipped (validation): {len(skipped_rows)}")
    print(f"  Errors (insertion):   {len(error_rows)}")
    print(f"  End time:             {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    if skipped_rows:
        print(f"\n  Skipped rows detail:")
        for s in skipped_rows:
            print(f"    appt_id={s['appt_id']}: {s['reason']}")
    
    if error_rows:
        print(f"\n  Error rows detail:")
        for e in error_rows:
            print(f"    appt_id={e['appt_id']}: {e['reason']}")
    
    print("=" * 60)
    print("Migration complete.")
    print("=" * 60)
    
    # Close database resources
    cursor.close()
    conn.close()


# ============================================================
# Post-Migration Validation Queries (G4)
# ============================================================
def run_validation(db_config):
    """
    Run all four post-migration validation queries.
    """
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    
    print("\n" + "=" * 60)
    print("POST-MIGRATION VALIDATION")
    print("=" * 60)
    
    # V1: Row count must match valid rows in CSV
    cursor.execute("SELECT COUNT(*) AS migrated_rows FROM appointments;")
    result = cursor.fetchone()
    print(f"\nV1 — Row count: {result[0]}")
    
    # V2: No NULL datetime values
    cursor.execute("SELECT COUNT(*) AS null_dates FROM appointments WHERE appt_datetime IS NULL;")
    result = cursor.fetchone()
    v2_pass = result[0] == 0
    print(f"V2 — NULL dates: {result[0]} {'[PASS]' if v2_pass else '[FAIL]'}")
    
    # V3: Only valid status codes exist
    cursor.execute("SELECT DISTINCT status FROM appointments;")
    results = cursor.fetchall()
    statuses = [r[0] for r in results]
    v3_pass = all(s in VALID_STATUSES for s in statuses)
    print(f"V3 — Distinct statuses: {statuses} {'[PASS]' if v3_pass else '[FAIL]'}")
    
    # V4: No orphan appointments
    cursor.execute("""
        SELECT COUNT(*) AS orphans FROM appointments a
        LEFT JOIN patients p ON a.patient_id = p.patient_id
        WHERE p.patient_id IS NULL;
    """)
    result = cursor.fetchone()
    v4_pass = result[0] == 0
    print(f"V4 — Orphan appointments: {result[0]} {'[PASS]' if v4_pass else '[FAIL — orphans detected]'}")
    
    print("=" * 60)
    
    cursor.close()
    conn.close()


# ============================================================
# Main entry point
# ============================================================
if __name__ == '__main__':
    # Database configuration — update these for your local setup
    DB_CONFIG = {
        'host': 'localhost',
        'user': 'root',
        'password': 'root',        # Change to your MySQL password
        'database': 'healthbridge'  # Database name
    }
    
    # Path to legacy CSV file
    CSV_PATH = os.path.join(os.path.dirname(__file__), 'legacy_appointments.csv')
    
    # Check if CSV exists
    if not os.path.exists(CSV_PATH):
        print(f"CSV file not found at: {CSV_PATH}")
        print("Creating sample CSV for demonstration...")
        
        # Create sample CSV with the data from the project specification
        sample_data = '''appt_id,patient_id,patient_nm,patient_ph,doc_id,doc_name,appt_date,status,fee,discount,net_fee,room
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
1012,45,"Zara Ahmed","0300-7776655",18,"Dr. Sara Malik","23/03/2024 14:30","H",1800.00,0.00,1800.00,"Room 2 Block D"'''
        
        with open(CSV_PATH, 'w', newline='', encoding='utf-8') as f:
            f.write(sample_data)
        print(f"Sample CSV created at: {CSV_PATH}")
    
    # Run migration
    migrate(CSV_PATH, DB_CONFIG)
    
    # Run validation (uncomment after setting up refactored schema)
    # run_validation(DB_CONFIG)
