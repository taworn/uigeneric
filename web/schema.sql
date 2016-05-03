DROP TABLE IF EXISTS sample;
CREATE TABLE sample (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
	icon MEDIUMBLOB,
	name VARCHAR(255) NOT NULL,
	detail TEXT,
	category INT DEFAULT 0,
	deleted DATETIME,
	PRIMARY KEY (id)
);