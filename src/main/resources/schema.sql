DROP TABLE IF EXISTS `records`;
CREATE TABLE `records` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `created_at` datetime(6) DEFAULT NULL,
                           `updated_at` datetime(6) DEFAULT NULL,
                           `attempt1` float NOT NULL,
                           `attempt2` float NOT NULL,
                           `attempt3` float NOT NULL,
                           `average_time` float NOT NULL,
                           `name` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `UKrtiy5cnc8ygo1mgcrsnkahuhf` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;