-- V7__add_handler_columns.sql
-- Add handler and handler_role columns to leads table
ALTER TABLE leads
    ADD COLUMN handler VARCHAR(50) NULL AFTER next_call_date,
    ADD COLUMN handler_role VARCHAR(50) NULL AFTER handler;