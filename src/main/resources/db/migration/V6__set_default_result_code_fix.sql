/* V6__set_default_result_code_fix.sql */
/* -----------------------------------------
   「未架電」コードの更新と leads への一括適用（再実行対応）
   その後、NULL 禁止に変更
----------------------------------------- */

-- 1. 「未架電」コード（ID: 1）を更新（存在前提）
UPDATE result_codes
SET code = 'NEW',
    label = '未架電',
    active = 1
WHERE result_code_id = 1;

-- 2. last_call_result が NULL の leads に「未架電」ID を設定
UPDATE leads
SET last_call_result = 1
WHERE last_call_result IS NULL;

-- 3. last_call_result を NOT NULL 制約付きに変更（MySQL用に型再指定）
ALTER TABLE leads
MODIFY last_call_result BIGINT NOT NULL;
