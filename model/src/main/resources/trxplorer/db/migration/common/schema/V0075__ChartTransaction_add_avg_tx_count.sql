ALTER TABLE `chart_transaction` 
ADD COLUMN `avg_tx_count` INT(10) UNSIGNED NOT NULL AFTER `avg_block_time`;
