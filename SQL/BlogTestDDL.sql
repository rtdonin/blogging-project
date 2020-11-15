DROP DATABASE IF EXISTS `BlogDBTEST`;
CREATE DATABASE `BlogDBTEST`;

USE `BlogDBTEST`;

CREATE TABLE `Role`(
	`roleId`	INT PRIMARY KEY AUTO_INCREMENT,
    `name`		VARCHAR(8) NOT NULL
);

CREATE TABLE `User`(
	`userId`	INT PRIMARY KEY AUTO_INCREMENT,
    `email`		VARCHAR(100) UNIQUE NOT NULL,
    `username`	VARCHAR(25) UNIQUE NOT NULL,
    `password`	VARCHAR(100) NOT NULL,
    `enabled`	BOOL -- ,
-- 	`image`		BLOB
);

CREATE TABLE `UserRole`(
	`userId` INT NOT NULL,
    `roleId` INT NOT NULL,
    
    PRIMARY KEY (`userId`, `roleId`),
    FOREIGN KEY `fk_UserRole_User` (`userId`) REFERENCES `User` (`userId`),
    FOREIGN KEY `fk_UserRole_Role` (`roleId`) REFERENCES `Role` (`roleId`)
);

CREATE TABLE `Post`(
	`postId`	INT PRIMARY KEY AUTO_INCREMENT,
    `title`		VARCHAR(255) NOT NULL,
    `static`	BOOL,
    `enabled`	BOOL DEFAULT FALSE,
    `body`		TEXT NOT NULL,
	`start`		DATE NOT NULL,
    `end`		DATE,
    `userId`	INT NOT NULL,
    
    FOREIGN KEY `fk_Post_User` (`userId`) REFERENCES `User` (`userId`)
);

CREATE TABLE `Comment`(
	`commentId`	INT PRIMARY KEY AUTO_INCREMENT,
    `datetime`	DATETIME DEFAULT NOW(),
    `text`		VARCHAR(255),
    `userId`	INT NOT NULL,
    `postId`	INT NOT NULL,
    
    FOREIGN KEY `fk_Comment_User`(`userId`) REFERENCES `User`(`userId`),
    FOREIGN KEY `fk_Comment_Post` (`postId`) REFERENCES `Post`(`postId`)
);

CREATE TABLE `Hashtag`(
	`hashtagId`	INT PRIMARY KEY AUTO_INCREMENT,
	`name`		varchar(35) NOT NULL
);

CREATE TABLE `PostHashtag`(
	`postId`	INT NOT NULL,
    `hashtagId`	INT NOT NULL,
    
    PRIMARY KEY (`postId`, `hashtagId`),
    FOREIGN KEY `fk_PostHashtag_Post` (`postId`) REFERENCES `Post` (`postId`),
    FOREIGN KEY `fk_PostHashtag_Hashtag` (`hashtagId`) REFERENCES `Hashtag` (`hashtagId`)
);
