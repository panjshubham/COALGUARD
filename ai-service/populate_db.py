import psycopg2
import urllib.parse
import sys

# Password percent encoding for @
password = urllib.parse.quote("Shubham@123")

# Connection strings to try
conn_strings = [
    f"postgresql://postgres:{password}@db.pkynukxdzwlywrxcwtay.supabase.co:5432/postgres",
    f"postgresql://postgres.pkynukxdzwlywrxcwtay:{password}@aws-0-ap-south-1.pooler.supabase.com:6543/postgres",
    f"postgresql://postgres.pkynukxdzwlywrxcwtay:{password}@aws-0-eu-central-1.pooler.supabase.com:6543/postgres",
    f"postgresql://postgres.pkynukxdzwlywrxcwtay:{password}@aws-0-us-east-1.pooler.supabase.com:6543/postgres"
]

conn = None
for cs in conn_strings:
    try:
        print(f"Trying connection string: {cs.split('@')[1] if '@' in cs else cs}")
        conn = psycopg2.connect(cs, connect_timeout=10)
        print("Connected successfully!")
        break
    except Exception as e:
        print(f"Connection failed: {e}")

if not conn:
    print("Could not connect to database.")
    sys.exit(1)

cur = conn.cursor()

# Create Tables based on CoalGuard Schema
schema_sql = """
-- 1. Mines
CREATE TABLE IF NOT EXISTS public.mines (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    state TEXT,
    subsidiary TEXT,
    capacity_mtpa NUMERIC(10,2),
    current_risk_score INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Users / Mine Officials
CREATE TABLE IF NOT EXISTS public.users (
    id SERIAL PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT,
    role TEXT DEFAULT 'official',
    assigned_mine_id INTEGER REFERENCES public.mines(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Violations
CREATE TABLE IF NOT EXISTS public.violations (
    id SERIAL PRIMARY KEY,
    mine_id INTEGER REFERENCES public.mines(id),
    inspection_id INTEGER,
    regulation_ref TEXT DEFAULT 'CMR 2017 Reg 115',
    category TEXT DEFAULT 'safety',
    description TEXT NOT NULL,
    severity TEXT DEFAULT 'medium',
    status TEXT DEFAULT 'open',
    latitude NUMERIC(10,6),
    longitude NUMERIC(10,6),
    photo_url TEXT,
    corrective_action TEXT,
    data_hash TEXT,
    prev_hash TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Compliance Items
CREATE TABLE IF NOT EXISTS public.compliance_items (
    id SERIAL PRIMARY KEY,
    mine_id INTEGER REFERENCES public.mines(id),
    tracking_id TEXT,
    category TEXT DEFAULT 'safety',
    title TEXT NOT NULL,
    due_date DATE,
    status TEXT DEFAULT 'pending',
    severity TEXT DEFAULT 'medium',
    assigned_to TEXT,
    statutory_ref TEXT,
    document_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 5. Inspections
CREATE TABLE IF NOT EXISTS public.inspections (
    id SERIAL PRIMARY KEY,
    mine_id INTEGER REFERENCES public.mines(id),
    contractor_id INTEGER,
    date DATE DEFAULT CURRENT_DATE,
    inspector_name TEXT NOT NULL,
    type TEXT DEFAULT 'Statutory Safety & Gas Audit',
    status TEXT DEFAULT 'completed',
    synced_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    tracking_id TEXT
);

-- 6. Contractors
CREATE TABLE IF NOT EXISTS public.contractors (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    workers_count INTEGER DEFAULT 0,
    status TEXT DEFAULT 'checked-in',
    time TEXT,
    safety_cert_status TEXT DEFAULT 'valid'
);

-- 7. Risk Scores
CREATE TABLE IF NOT EXISTS public.risk_scores (
    mine_id INTEGER PRIMARY KEY REFERENCES public.mines(id),
    score INTEGER NOT NULL,
    explanation TEXT,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 8. Audit Ledger
CREATE TABLE IF NOT EXISTS public.audit_ledger (
    id SERIAL PRIMARY KEY,
    table_name TEXT NOT NULL,
    record_id INTEGER NOT NULL,
    action TEXT NOT NULL,
    data_hash TEXT NOT NULL,
    prev_hash TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
"""

cur.execute(schema_sql)
conn.commit()
print("Schema tables created successfully.")

# Insert Initial Seed Data
seed_sql = """
-- Mines Seed
INSERT INTO public.mines (id, name, state, subsidiary, capacity_mtpa, current_risk_score) VALUES
(1, 'Govindpur Colliery (BCCL)', 'Jharkhand', 'BCCL', 3.20, 42),
(2, 'Dhori Khas (CCL)', 'Jharkhand', 'CCL', 6.00, 64),
(3, 'Karo Special Seam (CCL)', 'Jharkhand', 'CCL', 4.50, 78),
(4, 'Tetaria Khar (ECL)', 'West Bengal', 'ECL', 4.50, 87)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, current_risk_score = EXCLUDED.current_risk_score;

-- Users Seed
INSERT INTO public.users (email, full_name, role, assigned_mine_id) VALUES
('corporate@coalguard.demo', 'Shri R. K. Mahapatra', 'corporate', 1),
('official@coalguard.demo', 'Smt. Ananya Sen', 'mine_official', 1),
('regulator@coalguard.demo', 'Er. Rajesh Kumar', 'regulator', 2)
ON CONFLICT (email) DO NOTHING;

-- Violations Seed
INSERT INTO public.violations (id, mine_id, category, description, severity, status, regulation_ref) VALUES
(215, 4, 'production', 'Stay outside the mines - Unsecured pithead perimeter', 'high', 'open', 'CMR-2017-R155'),
(214, 4, 'production', 'Elevated haul road berm erosion near Bench 2', 'high', 'open', 'CMR-2017-R155'),
(207, 1, 'safety', 'Exposed high-voltage cable runway in East Pit 3', 'high', 'open', 'CMR-2017-R83'),
(206, 2, 'environment', 'Dust PM10 concentration exceeding statutory limits', 'high', 'open', 'CMR-2017-R129'),
(205, 2, 'labour', 'Contractor dumper operators deployed without active VTC card', 'medium', 'open', 'MINES-VTC-1966')
ON CONFLICT (id) DO NOTHING;

-- Compliance Seed
INSERT INTO public.compliance_items (id, mine_id, tracking_id, category, title, due_date, status, severity, assigned_to, statutory_ref) VALUES
(1, 1, 'DIR-2025-1042', 'safety', 'Installation of Real-Time CH4 Gas Monitoring Telemetry', '2025-10-01', 'overdue', 'critical', 'Shri R. K. Mahapatra', 'CMR 2017 Sec 104 - Underground Ventilation'),
(2, 2, 'DIR-2025-1043', 'environment', 'InSAR Satellite Subsidence Bench Survey Validation', '2025-10-15', 'pending', 'high', 'Dr. Arindam Sen', 'DGMS Circular No. 4/2022 - Highwall Slope Stability'),
(3, 3, 'DIR-2025-1044', 'safety', 'Hydraulic Roof Support & Strata Barricade Recertification', '2025-10-08', 'in_progress', 'critical', 'Er. V. K. Sharma', 'CMR 2017 Reg 124 - Systematic Support Rules'),
(4, 4, 'DIR-2025-1045', 'production', 'Overhead Heavy Machinery Emergency Cut-off Inspection', '2025-09-28', 'overdue', 'high', 'Inspector S. Roy', 'DGMS (Tech) S&T Circular 08 - Heavy Machinery')
ON CONFLICT (id) DO NOTHING;

-- Contractors Seed
INSERT INTO public.contractors (id, name, workers_count, status, time, safety_cert_status) VALUES
(1, 'M/s RK Earthmovers Pvt Ltd', 34, 'checked-in', '06:12 AM', 'valid'),
(2, 'M/s Suvidha Drilling Co.', 18, 'checked-in', '06:45 AM', 'valid'),
(3, 'M/s Bharat Explosives', 8, 'pending', '--', 'expiring'),
(4, 'M/s Ganesh Haulage', 22, 'absent', '--', 'valid')
ON CONFLICT (id) DO NOTHING;
"""

cur.execute(seed_sql)
conn.commit()
print("Seed data inserted successfully!")

cur.close()
conn.close()
