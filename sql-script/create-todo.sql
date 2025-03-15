-- Drop the existing schema if it exists
DROP SCHEMA IF EXISTS `todo_web`;
CREATE DATABASE todo_web;
USE todo_web;

-- Creating User Table
CREATE TABLE `users` (
   `id` INT NOT NULL AUTO_INCREMENT,
   `firstname` VARCHAR(68) NOT NULL,
   `lastname` VARCHAR(68) NOT NULL,
   `email` VARCHAR(68) NOT NULL,
   `password` VARCHAR(72) NOT NULL,
   `enabled` BOOLEAN DEFAULT TRUE,
   `profile_picture` LONGBLOB,
	`reset_password_token` VARCHAR(30) DEFAULT NULL,
   
   PRIMARY KEY (`id`),
   UNIQUE KEY `EMAIL_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Testing user table
INSERT INTO `users` (`firstname`, `lastname`, `email`, `password`)
VALUES ('e', 'e', 'e', '$2a$10$gXbqYUpkluE42A1mxLzu7eOLyBYJmmuDCmX5hXe4ZWpbMuRQV76Um');

-- Creating Tasks Table
CREATE TABLE `tasks` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `due_date` DATE NOT NULL,
    `task_info` VARCHAR(215) NOT NULL,
    `user_id` INT NOT NULL,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) 
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Enable Foreign Key Checks
SET FOREIGN_KEY_CHECKS = 1;

-- Testing task table
INSERT INTO `tasks` (`due_date`, `task_info`, `user_id`) VALUES ('2025-03-10', 'Test Task', 1);
