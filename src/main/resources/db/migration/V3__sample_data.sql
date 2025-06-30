-- ==== RESULT_CODES 追加入力（足りなければ） ====
INSERT IGNORE INTO result_codes (code, label) VALUES
 ('FOLLOW', '要フォロー'),
 ('DONE',   '完了');

-- ==== LEADS ダミー 10 件 ====
INSERT INTO leads
(company_name, address, phone_number, job_title, posting_date,
 total_employees, local_employees, trial_employment,
 last_call_date, last_call_result, memo, next_call_date)
VALUES
 ('サンプル株式会社A', '東京都渋谷区○○1-1-1', '0311112222', 'Webエンジニア', '2025-06-01',
  50, 40, 1, NULL, NULL, NULL, '2025-07-01 10:00:00'),
 ('サンプル株式会社B', '大阪府大阪市○○2-2-2', '0666667777', '営業職',      '2025-05-20',
  30,  5, 0, '2025-06-25 14:00:00',
  (SELECT result_code_id FROM result_codes WHERE code='CALL_NG'),
  '不在→再コール', '2025-07-02 10:00:00'),
 -- さらに 8 行ほどコピー …
 ('ダミー株式会社J', '福岡県福岡市○○10-10-10','0921234567','機械オペ',    '2025-04-10',
  80, 60, 1, NULL, NULL, NULL, NULL);
