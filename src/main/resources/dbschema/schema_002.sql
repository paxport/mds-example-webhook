CREATE TABLE `mds`.`supplier_transactions` (
  `txn_id` varchar(24) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `target` varchar(12) NOT NULL,
  `agent_id` varchar(45) NOT NULL,
  `supplier` varchar(45) NOT NULL,
  `system` varchar(45) NOT NULL,
  `type` varchar(24) NOT NULL,
  `status` varchar(24) NOT NULL,
  `most_relevant_date` date NOT NULL,
  `cost_amount` decimal(19,4) NOT NULL,
  `cost_currency` varchar(3) NOT NULL,
  `order_id` varchar(45) NOT NULL,
  `booking_reference` varchar(255) DEFAULT NULL,
  `pax_name` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `failure_reason` varchar(1024) DEFAULT NULL,
  `price_amount` decimal(19,4) DEFAULT NULL,
  `price_currency` varchar(3) DEFAULT NULL,
  `request_id` varchar(45) DEFAULT NULL,
  `tracing_id` varchar(45) DEFAULT NULL,
  `logical_session_id` varchar(128) DEFAULT NULL,
  `external_payment_reference` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`txn_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `mds`.`transaction_items` (
  `item_id` varchar(24) NOT NULL,
  `txn_id` varchar(24) NOT NULL,
  `service` varchar(128) NOT NULL,
  `relevant_date` date NOT NULL,
  `description` varchar(255) NOT NULL,
  `base_cost_amount` decimal(19,4) NOT NULL,
  `cost_amount_with_fees` decimal(19,4) NOT NULL,
  `cost_currency` varchar(3) NOT NULL,
  `price_amount` decimal(19,4) DEFAULT NULL,
  `price_currency` varchar(3) DEFAULT NULL,
  `idx` int(11) DEFAULT '99',
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `item_id_UNIQUE` (`item_id`),
  KEY `parent_id_idx` (`txn_id`),
  CONSTRAINT `txn_id` FOREIGN KEY (`txn_id`) REFERENCES `supplier_transactions` (`txn_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `mds`.`schema_level` (level) values (2);