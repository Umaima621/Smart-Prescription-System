USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'smart_prescription_db')
BEGIN
    ALTER DATABASE smart_prescription_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE smart_prescription_db;
END
GO


CREATE DATABASE smart_prescription_db;
GO

USE smart_prescription_db;
GO

-- =============================================================
-- 1. USERS
-- =============================================================
CREATE TABLE Users (
    userID          INT           NOT NULL IDENTITY(1,1) PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    email           VARCHAR(150)  NOT NULL UNIQUE,
    password        VARCHAR(255)  NOT NULL,
    role            VARCHAR(20)   NOT NULL,
    specialization  VARCHAR(100)  NULL,
    licenseNumber   VARCHAR(50)   NULL,
    bloodType       VARCHAR(5)    NULL,
    medicalHistory  TEXT          NULL,
    created_at      DATETIME      NOT NULL DEFAULT GETDATE(),

    CONSTRAINT chk_users_role CHECK (role IN ('Patient', 'Doctor'))
);
GO

-- =============================================================
-- 2. MEDICINES
--    userID stores email (VARCHAR) to match Java code
-- =============================================================
CREATE TABLE Medicines (
    medicineID      INT           NOT NULL IDENTITY(1,1) PRIMARY KEY,
    userID          VARCHAR(150)  NOT NULL,               -- email of patient
    medicineName    VARCHAR(150)  NOT NULL,
    dosage          VARCHAR(50)   NOT NULL,
    frequency       VARCHAR(50)   NOT NULL,
    startDate       DATE          NOT NULL,
    endDate         DATE          NOT NULL,
    created_at      DATETIME      NOT NULL DEFAULT GETDATE(),

    CONSTRAINT chk_medicines_dates CHECK (endDate >= startDate)
);
GO

-- =============================================================
-- 3. PRESCRIPTIONS
--    Both doctorID and patientID store emails
-- =============================================================
CREATE TABLE Prescriptions (
    prescriptionID  INT           NOT NULL IDENTITY(1,1) PRIMARY KEY,
    doctorID        VARCHAR(150)  NOT NULL,               -- doctor's email
    patientID       VARCHAR(150)  NOT NULL,               -- patient's email
    medicineName    VARCHAR(150)  NOT NULL,
    dosageFrequency VARCHAR(100)  NOT NULL,
    instructions    TEXT          NULL,
    prescribedDate  DATETIME      NOT NULL DEFAULT GETDATE()
);
GO

CREATE INDEX idx_prescriptions_patient ON Prescriptions (patientID, prescribedDate DESC);
CREATE INDEX idx_prescriptions_doctor  ON Prescriptions (doctorID,  prescribedDate);
GO

-- =============================================================
-- 4. REMINDERS
--    userID stores email, medicineID is FK to Medicines
-- =============================================================
CREATE TABLE Reminders (
    reminderID      INT           NOT NULL IDENTITY(1,1) PRIMARY KEY,
    medicineID      INT           NOT NULL,
    userID          VARCHAR(150)  NOT NULL,               -- patient's email
    reminderTime    VARCHAR(10)   NOT NULL,               -- 'HH:mm'
    reminderDate    DATE          NOT NULL,
    is_active       TINYINT       NOT NULL DEFAULT 1,
    created_at      DATETIME      NOT NULL DEFAULT GETDATE(),

    CONSTRAINT fk_reminders_medicine
        FOREIGN KEY (medicineID) REFERENCES Medicines(medicineID)
        ON DELETE CASCADE
);
GO

CREATE INDEX idx_reminders_user_date ON Reminders (userID, reminderDate, reminderTime);
GO

-- =============================================================
-- 5. MEDICINE_INTAKE
--    patient_id stores email, medicine_id stores reminderID as string
-- =============================================================
CREATE TABLE medicine_intake (
    intake_id       VARCHAR(36)   NOT NULL PRIMARY KEY,   -- UUID
    notification_id VARCHAR(36)   NULL,
    medicine_id     VARCHAR(36)   NOT NULL,               -- reminderID cast to string
    patient_id      VARCHAR(150)  NOT NULL,               -- patient's email
    scheduled_time  DATETIME      NOT NULL,
    taken_at        DATETIME      NULL,
    is_taken        TINYINT       NOT NULL DEFAULT 0,
    created_at      DATETIME      NOT NULL DEFAULT GETDATE()
);
GO

CREATE INDEX idx_intake_patient_scheduled ON medicine_intake (patient_id, scheduled_time);
CREATE INDEX idx_intake_taken             ON medicine_intake (patient_id, is_taken, scheduled_time);
GO

-- =============================================================
-- 6. REPORT
--    patient_id stores email so VARCHAR(150) to stay consistent
-- =============================================================
CREATE TABLE report (
    report_id        VARCHAR(36)   NOT NULL PRIMARY KEY,
    patient_id       VARCHAR(150)  NOT NULL,              -- ✅ was VARCHAR(36), now 150
    report_type      VARCHAR(20)   NOT NULL,
    generated_at     DATETIME      NOT NULL DEFAULT GETDATE(),
    start_date       DATE          NOT NULL,
    end_date         DATE          NOT NULL,
    total_scheduled  INT           NOT NULL DEFAULT 0,
    total_taken      INT           NOT NULL DEFAULT 0,
    total_missed     INT           NOT NULL DEFAULT 0,

    CONSTRAINT chk_report_type  CHECK (report_type IN ('weekly', 'monthly')),
    CONSTRAINT chk_report_dates CHECK (end_date >= start_date)
);
GO

CREATE INDEX idx_report_patient ON report (patient_id, generated_at DESC);
GO

-- =============================================================
-- SAMPLE DATA
-- =============================================================

-- Sample patient
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'ali@example.com')
    INSERT INTO Users (name, email, password, role, bloodType)
    VALUES ('Ali Hassan', 'ali@example.com', 'hashed_password_here', 'Patient', 'B+');

-- Sample doctor
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'sara@clinic.com')
    INSERT INTO Users (name, email, password, role, specialization, licenseNumber)
    VALUES ('Dr. Sara Khan', 'sara@clinic.com', 'hashed_password_here', 'Doctor', 'Cardiology', 'PMC-12345');

-- Sample medicine (userID = email now)
IF NOT EXISTS (SELECT 1 FROM Medicines WHERE medicineName = 'Amlodipine' AND userID = 'ali@example.com')
    INSERT INTO Medicines (userID, medicineName, dosage, frequency, startDate, endDate)
    VALUES (
        'ali@example.com',
        'Amlodipine', '5mg', '2',
        CAST(GETDATE() AS DATE),
        CAST(DATEADD(DAY, 30, GETDATE()) AS DATE)
    );

-- Sample prescription
IF NOT EXISTS (SELECT 1 FROM Prescriptions WHERE doctorID = 'sara@clinic.com' AND patientID = 'ali@example.com')
    INSERT INTO Prescriptions (doctorID, patientID, medicineName, dosageFrequency, instructions)
    VALUES ('sara@clinic.com', 'ali@example.com', 'Amlodipine', '5mg twice daily', 'Take after meals');

-- Sample reminder
IF NOT EXISTS (SELECT 1 FROM Reminders WHERE userID = 'ali@example.com' AND reminderTime = '08:00')
    INSERT INTO Reminders (medicineID, userID, reminderTime, reminderDate)
    VALUES (
        (SELECT TOP 1 medicineID FROM Medicines WHERE userID = 'ali@example.com'),
        'ali@example.com',
        '08:00',
        CAST(GETDATE() AS DATE)
    );

-- Sample intake
IF NOT EXISTS (SELECT 1 FROM medicine_intake WHERE intake_id = 'int-001')
    INSERT INTO medicine_intake (intake_id, medicine_id, patient_id, scheduled_time, taken_at, is_taken)
    VALUES (
        'int-001',
        (SELECT TOP 1 CAST(reminderID AS VARCHAR) FROM Reminders WHERE userID = 'ali@example.com'),
        'ali@example.com',
        CAST(CAST(GETDATE() AS DATE) AS DATETIME),
        GETDATE(), 1
    );
GO

-- =============================================================
-- LOGIN / USER
-- =============================================================
USE master;
GO

IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'medicine_user')
BEGIN
    CREATE LOGIN medicine_user
        WITH PASSWORD      = 'Pass1234',
             CHECK_POLICY      = OFF,
             CHECK_EXPIRATION  = OFF;
END
GO

USE smart_prescription_db;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'medicine_user')
BEGIN
    CREATE USER medicine_user FOR LOGIN medicine_user;
END
GO

EXEC sp_addrolemember 'db_datareader', 'medicine_user';
EXEC sp_addrolemember 'db_datawriter', 'medicine_user';
GRANT EXECUTE TO medicine_user;
GO
















