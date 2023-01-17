-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema exam3sem
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema exam3sem
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `exam3sem` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `exam3sem` ;

-- -----------------------------------------------------
-- Table `exam3sem`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `exam3sem`.`roles` (
                                                  `id` INT NOT NULL AUTO_INCREMENT,
                                                  `role` VARCHAR(20) NULL DEFAULT NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB
    AUTO_INCREMENT = 5
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `exam3sem`.`cities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `exam3sem`.`cities` (
                                                   `id` INT NOT NULL AUTO_INCREMENT,
                                                   `name` VARCHAR(255) NOT NULL,
    `zipcode` INT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `exam3sem`.`festivals`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `exam3sem`.`festivals` (
                                                      `id` INT NOT NULL AUTO_INCREMENT,
                                                      `name` VARCHAR(255) NOT NULL,
    `startdate` DATE NOT NULL,
    `duration` INT NOT NULL,
    `cities_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    INDEX `fk_festivals_cities1_idx` (`cities_id` ASC) VISIBLE,
    CONSTRAINT `fk_festivals_cities1`
    FOREIGN KEY (`cities_id`)
    REFERENCES `exam3sem`.`cities` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `exam3sem`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `exam3sem`.`users` (
                                                  `id` INT NOT NULL AUTO_INCREMENT,
                                                  `password` VARCHAR(255) NOT NULL,
    `username` VARCHAR(25) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `phone` INT NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `festivals_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `username` (`username` ASC) VISIBLE,
    INDEX `fk_users_festivals1_idx` (`festivals_id` ASC) VISIBLE,
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    CONSTRAINT `fk_users_festivals1`
    FOREIGN KEY (`festivals_id`)
    REFERENCES `exam3sem`.`festivals` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB
    AUTO_INCREMENT = 3
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `exam3sem`.`users_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `exam3sem`.`users_roles` (
                                                        `role_id` INT NOT NULL,
                                                        `user_id` INT NOT NULL,
                                                        PRIMARY KEY (`role_id`, `user_id`),
    INDEX `FK_users_roles_user_id` (`user_id` ASC) VISIBLE,
    CONSTRAINT `FK_users_roles_role_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `exam3sem`.`roles` (`id`),
    CONSTRAINT `FK_users_roles_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `exam3sem`.`users` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `exam3sem`.`shows`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `exam3sem`.`shows` (
                                                  `id` INT NOT NULL AUTO_INCREMENT,
                                                  `name` VARCHAR(255) NOT NULL,
    `duration` INT NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `startdate` DATE NOT NULL,
    `starttime` TIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `exam3sem`.`shows_users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `exam3sem`.`shows_users` (
                                                        `show_id` INT NOT NULL,
                                                        `user_id` INT NOT NULL,
                                                        PRIMARY KEY (`show_id`, `user_id`),
    INDEX `fk_shows_has_users_users1_idx` (`user_id` ASC) VISIBLE,
    INDEX `fk_shows_has_users_shows1_idx` (`show_id` ASC) VISIBLE,
    CONSTRAINT `fk_shows_has_users_shows1`
    FOREIGN KEY (`show_id`)
    REFERENCES `exam3sem`.`shows` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_shows_has_users_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `exam3sem`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
