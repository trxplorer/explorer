ALTER TABLE `chart_daily` 
CHANGE COLUMN `tx_per_second` `avg_tx_per_second` FLOAT UNSIGNED NOT NULL AFTER `avg_tx_count`;
