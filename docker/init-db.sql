CREATE TABLE `recipes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `instructions` varchar(2000) DEFAULT NULL,
  `is_vegetarian` bit(1) DEFAULT NULL,
  `modified_time` datetime(6) DEFAULT NULL,
  `num_of_servings` smallint DEFAULT NULL,
  `recipe_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ki12bp7g638chyap3hdmxe93f` (`recipe_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ingredients` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ingredient_name` varchar(255) DEFAULT NULL,
  `recipe_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKs1ky3yjrtq501mghupnbpr4l9` (`recipe_id`,`ingredient_name`),
  CONSTRAINT `FK7p08vcn6wf7fd6qp79yy2jrwg` FOREIGN KEY (`recipe_id`) REFERENCES `recipes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;