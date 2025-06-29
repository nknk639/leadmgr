-- -----------------------------------------------------
-- V2__init.sql  (schema corrections & extensions)
-- -----------------------------------------------------
-- 変更点
--  1. leads.campany_name  → company_name               -- スペル修正
--  2. leads.trial_employees → trial_employment          -- スペル修正
--  3. leads に従業員数カラム追加                       -- total_employees / local_employees
--  4. result_codes.result_call_id → result_code_id      -- PK 名統一
--  5. 上記 PK 名変更に伴う全 FK の張り替え
--  6. BOOLEAN → TINYINT(1) 方針は現行定義を維持
-- -----------------------------------------------------

-- ① 既存 FK を一旦削除（result_codes PK 名変更のため）
ALTER TABLE `leadmgr`.`call_history`
    DROP FOREIGN KEY `fk_call_history_result`,
    DROP FOREIGN KEY `fk_call_history_prev`;

ALTER TABLE `leadmgr`.`leads`
    DROP FOREIGN KEY `fk_leads_result_call_id`;

-- ② leads テーブル列名修正 & 新規列追加
ALTER TABLE `leadmgr`.`leads`
    CHANGE COLUMN `campany_name`     `company_name`     VARCHAR(255) NOT NULL,
    CHANGE COLUMN `trial_employees`  `trial_employment` TINYINT NULL,
    ADD COLUMN    `total_employees`  INT NULL AFTER `posting_date`,
    ADD COLUMN    `local_employees`  INT NULL AFTER `total_employees`;

-- ③ result_codes 主キー列名を統一
ALTER TABLE `leadmgr`.`result_codes`
    CHANGE COLUMN `result_call_id` `result_code_id` BIGINT NOT NULL AUTO_INCREMENT,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`result_code_id`);

-- ④ 参照 FK を再生成（新しい PK 名で張り直す）
ALTER TABLE `leadmgr`.`leads`
    ADD CONSTRAINT `fk_leads_result_code_id`
        FOREIGN KEY (`last_call_result`)
        REFERENCES `leadmgr`.`result_codes` (`result_code_id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE `leadmgr`.`call_history`
    ADD CONSTRAINT `fk_call_history_result`
        FOREIGN KEY (`result_code`)
        REFERENCES `leadmgr`.`result_codes` (`result_code_id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    ADD CONSTRAINT `fk_call_history_prev`
        FOREIGN KEY (`prev_result_code`)
        REFERENCES `leadmgr`.`result_codes` (`result_code_id`)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

-- （任意）インデックス名が result_call を含む場合は DROP / CREATE で
-- リネーム可能。現状はカラム名変更のみで動作に支障はありません。

-- -----------------------------------------------------
-- ここまで
-- -----------------------------------------------------
