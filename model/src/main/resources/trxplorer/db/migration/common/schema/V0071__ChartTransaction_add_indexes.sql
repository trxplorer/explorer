ALTER TABLE `chart_transaction` 
ADD UNIQUE INDEX `day_mont_year_unique` (`day` ASC, `month` ASC, `year` ASC),
ADD INDEX `day_index` (`day` ASC),
ADD INDEX `month_index` (`month` ASC),
ADD INDEX `year_index` (`year` ASC);
