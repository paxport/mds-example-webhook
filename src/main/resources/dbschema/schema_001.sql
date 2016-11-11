CREATE TABLE `mds`.`schema_level` (
  `level` int(11) NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into `mds`.`schema_level` (level) values (1);