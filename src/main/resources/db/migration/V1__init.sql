-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema ichiri_leadmgr
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema ichiri_leadmgr
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ichiri_leadmgr` DEFAULT CHARACTER SET utf8 ;
USE `ichiri_leadmgr` ;

-- -----------------------------------------------------
-- Table `ichiri_leadmgr`.`result_codes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ichiri_leadmgr`.`result_codes` (
  `result_call_id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NULL,
  `label` VARCHAR(255) NULL,
  `active` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`result_call_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ichiri_leadmgr`.`leads`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ichiri_leadmgr`.`leads` (
  `lead_id` BIGINT NOT NULL AUTO_INCREMENT,
  `campany_name` VARCHAR(255) NOT NULL,
  `address` TEXT NULL,
  `phone_number` VARCHAR(45) NOT NULL,
  `job_title` TEXT NULL,
  `job_description` TEXT NULL,
  `posting_date` DATE NULL,
  `trial_employees` TINYINT NULL,
  `last_call_date` DATETIME NULL,
  `last_call_result` BIGINT NULL,
  `memo` TEXT NULL,
  `next_call_date` DATETIME NULL,
  PRIMARY KEY (`lead_id`),
  UNIQUE INDEX `phone_number_UNIQUE` (`phone_number` ASC) VISIBLE,
  INDEX `fk_leads_result_call_id_idx` (`last_call_result` ASC) VISIBLE,
  CONSTRAINT `fk_leads_result_call_id`
    FOREIGN KEY (`last_call_result`)
    REFERENCES `ichiri_leadmgr`.`result_codes` (`result_call_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ichiri_leadmgr`.`call_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ichiri_leadmgr`.`call_history` (
  `call_history_id` BIGINT NOT NULL AUTO_INCREMENT,
  `lead_id` BIGINT NULL,
  `call_datetime` DATETIME NOT NULL,
  `result_code` BIGINT NULL,
  `prev_result_code` BIGINT NULL,
  `note` TEXT NULL,
  PRIMARY KEY (`call_history_id`),
  INDEX `fk_call_history_lead_id_idx` (`lead_id` ASC) VISIBLE,
  INDEX `fk_call_history_result_idx` (`result_code` ASC) VISIBLE,
  INDEX `fk_call_history_prev_idx` (`prev_result_code` ASC) VISIBLE,
  CONSTRAINT `fk_call_history_lead_id`
    FOREIGN KEY (`lead_id`)
    REFERENCES `ichiri_leadmgr`.`leads` (`lead_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_call_history_result`
    FOREIGN KEY (`result_code`)
    REFERENCES `ichiri_leadmgr`.`result_codes` (`result_call_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_call_history_prev`
    FOREIGN KEY (`prev_result_code`)
    REFERENCES `ichiri_leadmgr`.`result_codes` (`result_call_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
