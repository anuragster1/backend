
CREATE TABLE IF NOT EXISTS `countries` (
  `id` SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `code` VARCHAR(5) NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `states` (
  `id` MEDIUMINT(5) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) NOT NULL,
  `country_id` SMALLINT(5) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`country_id`) REFERENCES countries(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `cities` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) NOT NULL,
  `state_id` MEDIUMINT(5) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`state_id`) REFERENCES states(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `verticals` (
  `id` TINYINT(3) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) DEFAULT NULL,
  `has_groups` TINYINT(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `categories` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) NOT NULL,
  `vertical_id` TINYINT(3)NOT NULL,
  `has_fans` TINYINT(1) DEFAULT 0,
  `type` TINYINT(1) DEFAULT NULL,
  `date_created` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`vertical_id`) REFERENCES verticals(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `sub_categories` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) NOT NULL,
  `category_id` int(11) UNSIGNED NOT NULL,
  `type` TINYINT(1) DEFAULT NULL,
  `date_created` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`category_id`) REFERENCES categories(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `groups` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) NOT NULL,
  `description` VARCHAR(20) DEFAULT NULL,
  `sub_category_id` int(11) UNSIGNED NOT NULL,
  `type` TINYINT(1) NOT NULL,
  `status` TINYINT(1) NOT NULL,
  `createdby_id` INT(10) NOT NULL,
  `date_created` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updatedby_id` INT(10) DEFAULT NULL,
  `date_updated` DATETIME ON UPDATE  CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`sub_category_id`) REFERENCES sub_categories(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `userId` VARCHAR (32) NOT NULL,
  `first_name` VARCHAR(20) NOT NULL,
  `last_name` VARCHAR(20) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `password` CHAR(255) NOT NULL,
  `salt` CHAR(128) NOT NULL,
  `gender` CHAR(1) NOT NULL,
  `dob` DATE NOT NULL,
  `country` SMALLINT(5) UNSIGNED NOT NULL,
  `state` MEDIUMINT(5) UNSIGNED  NOT NULL,
  `city` INT(11) UNSIGNED  NOT NULL,
  `is_mentor` TINYINT(1) NOT NULL,
  `status` TINYINT(1) NOT NULL,
  `activate_key` VARCHAR(40) NOT NULL,
  `activated` TINYINT(1) NOT NULL,
  `date_activated` DATETIME DEFAULT NULL,
  `date_created` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_updated` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`country`) REFERENCES countries(`id`),
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `group_group_connects` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `group_id_from` int(11) UNSIGNED NOT NULL,
  `group_id_to` int(11) UNSIGNED NOT NULL,
  `requestby_user_id` int(11) UNSIGNED NOT NULL,
  `request_status` TINYINT(3)NOT NULL,
  `actionby_user_id` int(11) UNSIGNED DEFAULT NULL,
  `date_requested` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_action` DATETIME ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`group_id_from`) REFERENCES groups(`id`),
  FOREIGN KEY (`group_id_to`) REFERENCES groups(`id`),
  FOREIGN KEY (`requestby_user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`actionby_user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_category_relations` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `sub_category_id` int(11) UNSIGNED NOT NULL,
  `vertical_id` TINYINT(3)NOT NULL,
  `is_mentor` TINYINT(1) NOT NULL,
  `status` TINYINT(1) NOT NULL,
  `date_updated` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`sub_category_id`) REFERENCES sub_categories(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `roles` (
  `id` TINYINT(3) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_group_relations` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `group_id` int(11) UNSIGNED NOT NULL,
  `role_id` TINYINT(3)  NOT NULL,
  `role_alias` VARCHAR(20) DEFAULT NULL,
  `rolesetby_id` int(11) UNSIGNED DEFAULT NULL,
  `status` TINYINT(1) NOT NULL,
  `invitedby_id` int(11) UNSIGNED DEFAULT NULL,
  `blockedbyid` int(11) UNSIGNED DEFAULT NULL,
  `date_requested` DATETIME DEFAULT NULL,
  `date_invited` DATETIME DEFAULT NULL,
  `date_joined` DATETIME DEFAULT NULL,
  `date_roleset` DATETIME DEFAULT NULL,
  `date_exited` DATETIME DEFAULT NULL,
  `date_blocked` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`rolesetby_id`) REFERENCES users(`id`),
  FOREIGN KEY (`invitedby_id`) REFERENCES users(`id`),
  FOREIGN KEY (`blockedbyid`) REFERENCES users(`id`),
  FOREIGN KEY (`group_id`) REFERENCES groups(`id`),
  FOREIGN KEY (`role_id`) REFERENCES roles(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_mentor_followers` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `mentor_id` int(11) UNSIGNED NOT NULL,
  `status` TINYINT(1) NOT NULL,
  `date_updated` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`mentor_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_mentor_ratings` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `mentor_id` int(11) UNSIGNED NOT NULL,
  `rating` TINYINT(1) NOT NULL,
  `date_rated` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`mentor_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_group_followers` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `groupid` int(11) UNSIGNED NOT NULL,
  `status` TINYINT(1) NOT NULL,
  `date_updated` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_friends` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id_a` int(11) UNSIGNED NOT NULL,
  `user_id_b` int(11) UNSIGNED NOT NULL,
  `request_status` TINYINT(1) NOT NULL,
  `date_requested` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_activated` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id_a`) REFERENCES users(`id`),
  FOREIGN KEY (`user_id_b`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_educations` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `education`  VARCHAR(50) NOT NULL,
  `institute`  VARCHAR(50) NOT NULL,
  `university`  VARCHAR(50) NOT NULL,
  `date_from` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_to` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `user_certifications` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `certification`  VARCHAR(50) NOT NULL,
  `authority`  VARCHAR(50) NOT NULL,
  `date_certified` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_works` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `company`  VARCHAR(50) NOT NULL,
  `jobtitle`  VARCHAR(50) NOT NULL,
  `date_from` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_to` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_awards` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `user_id` int(11) UNSIGNED NOT NULL,
  `award`  VARCHAR(50) NOT NULL,
  `authority`  VARCHAR(50) NOT NULL,
  `date_awarded` DATE ,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_group_profile_pics` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `wall_id` int(11) UNSIGNED NOT NULL,
  `wall_type`  TINYINT(1) NOT NULL,
  `link`  VARCHAR(100) NOT NULL,
  `is_avatar` TINYINT(1) NOT NULL,
  `is_active` TINYINT(1) NOT NULL,
  `date_shared` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `permissions` (
  `id` SMALLINT(5) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `role_permission_relations` (
  `id` TINYINT(3) NOT NULL AUTO_INCREMENT ,
  `permission_id` SMALLINT(5) NOT NULL,
  `role_id` TINYINT(3) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`permission_id`) REFERENCES permissions(`id`),
  FOREIGN KEY (`role_id`) REFERENCES roles(`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `post_types`(
  `id` TINYINT(3) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) DEFAULT NULL,
  `folder` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `posts`(
  `id` INT(10)UNSIGNED NOT NULL AUTO_INCREMENT ,
  `postby_id` INT(10) UNSIGNED NOT NULL,
  `postto_id` INT(10) NOT NULL,
  `wall_type` TINYINT(1) NOT NULL,
  `is_private` TINYINT(1) NOT NULL,
  `post_type` TINYINT(1) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `description` LONGTEXT DEFAULT NULL,
  `link` varchar(100) DEFAULT NULL,
  `keywords` TEXT(500) DEFAULT NULL,
  `status` TINYINT(1) NOT NULL,
  `deletedby_id` INT(10) UNSIGNED DEFAULT NULL,
  `date_posted` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_updated` DATETIME DEFAULT NULL,
  `date_deleted` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`postby_id`) REFERENCES users(`id`),
  FOREIGN KEY (`deletedby_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `post_user_comments`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `post_id` INT(10) UNSIGNED NOT NULL,
  `comment` TEXT DEFAULT NULL,
  `status` TINYINT(1) NOT NULL,
  `deletedby_id` INT(10) UNSIGNED DEFAULT NULL,
  `date_commented` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_deleted` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`post_id`) REFERENCES posts(`id`),
  FOREIGN KEY (`deletedby_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `post_user_likes`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `item_id` INT(10) NOT NULL,
  `item_type` TINYINT(1) NOT NULL,
  `status` TINYINT(1) NOT NULL,
  `date_liked` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_unliked` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `post_user_shares`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `shareby_id` INT(10) UNSIGNED NOT NULL,
  `shareto_id` INT(10) UNSIGNED NOT NULL,
  `wall_type` TINYINT(1) NOT NULL,
  `is_private` TINYINT(1) NOT NULL,
  `date_shared` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`shareby_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_report_abuses`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `item_id` INT(10) NOT NULL,
  `item_type` TINYINT(1) NOT NULL,
  `date_reported` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_post_views`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `post_id` INT(10) UNSIGNED NOT NULL,
  `ip_address` varchar(15) NOT NULL,
  `date_viewed` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`post_id`) REFERENCES posts(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_event_actions`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `event_id` INT(10) NOT NULL,
  `status` TINYINT(1) NOT NULL,
  `date_acted` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `log_action_types`(
  `id` TINYINT(3) NOT NULL AUTO_INCREMENT ,
  `name` varchar(20) NOT NULL,
  `description` varchar(150) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_logs`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `action_id` TINYINT(3) NOT NULL,
  `wall_id` int(11) UNSIGNED DEFAULT NULL,
  `wall_type`  TINYINT(1) DEFAULT NULL,
  `extras` varchar(200) DEFAULT NULL,
  `ip_address` varchar(15) NOT NULL,
  `date_viewed` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `group_events`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `group_id` INT(10) UNSIGNED NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `event_start` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `event_end` DATETIME DEFAULT NULL,
  `status` TINYINT(3) NOT NULL,
  `createdby_id` int(11) UNSIGNED DEFAULT NULL,
  `modifiedby_id` int(11) UNSIGNED DEFAULT NULL,
  `date_created` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `date_modified` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`group_id`) REFERENCES groups(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `user_profile_status`(
  `id` INT(10) NOT NULL AUTO_INCREMENT ,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `status` VARCHAR(150) NOT NULL,
  `is_mentor` TINYINT(2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;



CREATE TABLE IF NOT EXISTS `url_custom_name`(
  `id` BIGINT(20) UNSIGNED AUTO_INCREMENT ,
  `wall_id` BIGINT(20)  NOT NULL,
  `wall_type` TINYINT(2) NOT NULL,
  `name` VARCHAR (50) NOT NULL,
  `status` TINYINT(3) NOT NULL,
  `date_created` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
