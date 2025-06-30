/* V5__set_default_result_code.sql */
/* -----------------------------------------
   「未架電」コードの挿入と leads への一括適用
   その後、NULL 禁止に変更
----------------------------------------- */

-- 1. 「未架電」コード（ID: 固定値 1）を挿入（存在しなければ）
INSERT INTO result_codes (result_code_id, code, label, active)
SELECT 1, 'NEW', '未架電', 1
WHERE NOT EXISTS (
    SELECT 1 FROM result_codes WHERE result_code_id = 1
);

-- 2. last_call_result が NULL の leads に「未架電」ID を設定
UPDATE leads
SET last_call_result = 1
WHERE last_call_result IS NULL;

-- 3. last_call_result を NOT NULL 制約付きに変更（MySQLは一度 DROP/ADD が必要）
ALTER TABLE leads
MODIFY last_call_result BIGINT NOT NULL;
