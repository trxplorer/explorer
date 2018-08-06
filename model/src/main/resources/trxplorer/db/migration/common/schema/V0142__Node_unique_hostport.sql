ALTER TABLE `node` 
ADD UNIQUE INDEX `node_unique` (`host` ASC, `port` ASC);
