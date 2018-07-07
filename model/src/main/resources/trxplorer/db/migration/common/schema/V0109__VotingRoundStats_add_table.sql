CREATE TABLE `voting_round_stats` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `witness_id` BIGINT UNSIGNED NOT NULL,
  `voting_round_id` INT UNSIGNED NOT NULL,
  `total_votes` BIGINT UNSIGNED NOT NULL,
  `position` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_voting_round_stats_witness_id_idx` (`witness_id` ASC),
  INDEX `fk_voting_round_stats_votin_round_id_idx` (`voting_round_id` ASC),
  CONSTRAINT `fk_voting_round_stats_witness_id`
    FOREIGN KEY (`witness_id`)
    REFERENCES `witness` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_voting_round_stats_votin_round_id`
    FOREIGN KEY (`voting_round_id`)
    REFERENCES `voting_round` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
