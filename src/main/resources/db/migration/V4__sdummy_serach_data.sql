/* -------------------------------------------------------------
   V4__dummy_search_data.sql
   検索テスト用ダミーレコード
   ※最終架電結果が入ったリード、
     next_call_date が「今日以前」のリード
------------------------------------------------------------- */

-- 1) 追加結果コード（重複時は無視）
INSERT IGNORE INTO result_codes (code, label, active) VALUES
  ('APPOINT', 'アポ取得', 1),
  ('NG',       '興味なし', 1);

-- 2) ダミー LEADS
INSERT INTO leads
  (company_name, address, phone_number,
   job_title, posting_date,
   total_employees, local_employees, trial_employment,
   last_call_date, last_call_result,
   memo, next_call_date)
VALUES
  -- ① 今日 next_call_date
  ('架電テスト株式会社',
   '東京都港区1-1-1',
   '0399990001',
   '営業職',
   '2025-06-15',
   30, 25, 0,
   '2025-06-30 10:00:00',
   (SELECT result_code_id FROM result_codes WHERE code = 'APPOINT' LIMIT 1),
   'アポ取得済み',
   DATE_FORMAT(NOW(), '%Y-%m-%d 10:00:00')),

  -- ② 昨日 next_call_date
  ('再コール株式会社',
   '大阪府大阪市2-2-2',
   '0666660002',
   '事務職',
   '2025-06-10',
   50, 45, 1,
   '2025-06-28 15:00:00',
   (SELECT result_code_id FROM result_codes WHERE code = 'NG' LIMIT 1),
   '再コール必要',
   DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 DAY), '%Y-%m-%d 09:00:00'));
