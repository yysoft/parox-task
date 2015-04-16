SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';


-- -----------------------------------------------------
-- Table `job_status`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `job_status` (
  `id` INT(20) NOT NULL AUTO_INCREMENT,
  `job_name` VARCHAR(45) NOT NULL COMMENT '任务ID',
  `gmt_basetime` DATETIME NOT NULL COMMENT '任务执行的时间基准',
  `result` VARCHAR(250) NULL DEFAULT NULL COMMENT '执行结果',
  `runtime` INT(20) NULL DEFAULT NULL COMMENT '执行时间',
  `gmt_trigger` DATETIME NULL DEFAULT NULL COMMENT '任务触发时间',
  `error_msg` VARCHAR(2000) NULL DEFAULT NULL,
  `category` VARCHAR(45) NULL DEFAULT NULL COMMENT '任务执行类型\nscheduler：任务调度\nmanual：手动执行',
  `num_retry` TINYINT(4) NULL DEFAULT NULL COMMENT '任务重试次数',
  `gmt_created` DATETIME NULL DEFAULT NULL,
  `gmt_modified` DATETIME NULL DEFAULT NULL,
  `node_key` VARCHAR(45) NULL DEFAULT '',
  PRIMARY KEY (`id`),
  INDEX `idx_job_name` (`job_name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 3190
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

-- -----------------------------------------------------
-- Table `job_definition`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `job_definition` (
  `id` INT(20) NOT NULL AUTO_INCREMENT,
  `gmt_created` DATETIME NOT NULL,
  `gmt_modified` DATETIME NOT NULL,
  `job_name` VARCHAR(45) NOT NULL COMMENT '任务名称',
  `job_group` VARCHAR(45) NULL DEFAULT 'task' COMMENT '任务组\ntask:普通任务\nidx_task:索引任务',
  `job_classpath` VARCHAR(250) NULL DEFAULT NULL COMMENT '任务程序存放的位置',
  `job_class_name` VARCHAR(255) NOT NULL COMMENT '任务完整类名',
  `description` VARCHAR(100) NULL DEFAULT NULL,
  `cron` VARCHAR(45) NULL DEFAULT NULL COMMENT '任务执行的开始时段',
  `is_in_use` TINYINT(1) NULL DEFAULT '0' COMMENT '是否启用：1启用，0停用',
  `start_time` DATETIME NULL DEFAULT NULL COMMENT '任务执行的开始时段',
  `end_time` DATETIME NULL DEFAULT NULL COMMENT '任务执行的结束时段\n最后一次任务执行时间',
  `single_running` VARCHAR(45) NULL DEFAULT '' COMMENT '运行模式：\nSINGLE：单点（默认）\nMULITY：多点',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `job_name_UNIQUE` (`job_name` ASC),
  UNIQUE INDEX `job_class_name_UNIQUE` (`job_class_name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 32
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
