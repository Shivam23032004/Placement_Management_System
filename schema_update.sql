-- ============================================================
-- Placement Management System — Schema Update
-- Run this in MySQL Workbench or MySQL CLI BEFORE launching the app.
-- ============================================================

USE placement_management_system;

-- New table for Queue DSA (Member: Shashank)
-- Stores pending application queue entries in FIFO order.
-- Ordered by queue_id (AUTO_INCREMENT) — smallest queue_id = front of queue.
-- UNIQUE constraint prevents duplicate (student, drive) pairs in queue.

CREATE TABLE IF NOT EXISTS application_queue (
    queue_id   INT          AUTO_INCREMENT PRIMARY KEY,
    student_id INT          NOT NULL,
    drive_id   INT          NOT NULL,
    queued_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    -- Prevent same student queuing for same drive twice
    UNIQUE KEY uq_student_drive (student_id, drive_id),

    -- Cascade delete if student or drive is removed
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (drive_id)   REFERENCES drives(drive_id)     ON DELETE CASCADE
);

-- Verify table was created successfully
SELECT 'application_queue table created successfully!' AS status;
