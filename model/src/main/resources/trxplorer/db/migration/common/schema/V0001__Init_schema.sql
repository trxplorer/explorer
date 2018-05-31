-- MySQL dump 10.13  Distrib 5.7.21, for Linux (x86_64)
--
-- Host: localhost    Database: trxplorer
-- ------------------------------------------------------
-- Server version	5.7.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_name` varchar(250) DEFAULT NULL,
  `type` tinyint(4) NOT NULL,
  `address` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `block`
--

DROP TABLE IF EXISTS `block`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `block` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `num` bigint(20) unsigned DEFAULT NULL,
  `hash` varchar(45) NOT NULL,
  `parent_hash` varchar(45) NOT NULL,
  `txTrieRoot` varchar(45) DEFAULT NULL,
  `timestamp` datetime(3) NOT NULL,
  `witness_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `num_UNIQUE` (`num`),
  KEY `fk_block_witness_id_idx` (`witness_id`),
  CONSTRAINT `fk_block_witness_id` FOREIGN KEY (`witness_id`) REFERENCES `witness` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_account_create`
--

DROP TABLE IF EXISTS `contract_account_create`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_account_create` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_name` varchar(250) DEFAULT NULL,
  `owner_id` bigint(20) unsigned NOT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_account_create_owner_id_idx` (`owner_id`),
  KEY `fk_contract_account_create_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_account_create_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_account_create_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_account_update`
--

DROP TABLE IF EXISTS `contract_account_update`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_account_update` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_name` varchar(250) NOT NULL,
  `owner_id` bigint(20) unsigned NOT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_account_update_owner_id_idx` (`owner_id`),
  KEY `fk_contract_account_update_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_account_update_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_account_update_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_asset_issue`
--

DROP TABLE IF EXISTS `contract_asset_issue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_asset_issue` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) unsigned NOT NULL,
  `name` varchar(150) NOT NULL,
  `total_supply` int(10) unsigned NOT NULL,
  `trx_num` int(11) NOT NULL,
  `start_time` datetime(3) NOT NULL,
  `end_time` datetime(3) DEFAULT NULL,
  `decay_ratio` int(10) unsigned DEFAULT NULL,
  `vote_score` int(11) DEFAULT NULL,
  `description` varchar(750) DEFAULT NULL,
  `url` varchar(250) DEFAULT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_asset_issue_owner_id_idx` (`owner_id`),
  KEY `fk_contract_asset_issue_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_asset_issue_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_asset_issue_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_deploy`
--

DROP TABLE IF EXISTS `contract_deploy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_deploy` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) unsigned NOT NULL,
  `script` text NOT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_deploy_owner_id_idx` (`owner_id`),
  KEY `fk_contract_deploy_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_deploy_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_deploy_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_participate_asset_issue`
--

DROP TABLE IF EXISTS `contract_participate_asset_issue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_participate_asset_issue` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) unsigned NOT NULL,
  `to_id` bigint(20) unsigned NOT NULL,
  `asset_name` varchar(250) NOT NULL,
  `amount` double DEFAULT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_participate_asset_issue_owner_id_idx` (`owner_id`),
  KEY `fk_contract_participate_asset_issue_to_id_idx` (`to_id`),
  KEY `fk_contract_participate_asset_issue_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_participate_asset_issue_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_participate_asset_issue_to_id` FOREIGN KEY (`to_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_participate_asset_issue_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_transfer`
--

DROP TABLE IF EXISTS `contract_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_transfer` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) unsigned NOT NULL,
  `to_id` bigint(20) unsigned NOT NULL,
  `amount` double unsigned NOT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_transfer_owner_id_idx` (`owner_id`),
  KEY `fk_contract_transfer_to_id_idx` (`to_id`),
  KEY `fk_contract_transfer_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_transfer_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_transfer_to_id` FOREIGN KEY (`to_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_transfer_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_transfer_asset`
--

DROP TABLE IF EXISTS `contract_transfer_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_transfer_asset` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `asset_name` varchar(250) NOT NULL,
  `owner_id` bigint(20) unsigned NOT NULL,
  `to_id` bigint(20) unsigned NOT NULL,
  `amount` double unsigned NOT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_transfer_asset_owner_id_idx` (`owner_id`),
  KEY `fk_contract_transfer_asset_to_id_idx` (`to_id`),
  KEY `fk_contract_transfer_asset_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_transfer_asset_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_transfer_asset_to_id` FOREIGN KEY (`to_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_transfer_asset_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_vote_asset`
--

DROP TABLE IF EXISTS `contract_vote_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_vote_asset` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) unsigned NOT NULL,
  `support` tinyint(3) unsigned NOT NULL,
  `count` int(10) unsigned NOT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_vote_asset_owner_id_idx` (`owner_id`),
  KEY `fk_contract_vote_asset_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_vote_asset_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_vote_asset_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_vote_asset_vote`
--

DROP TABLE IF EXISTS `contract_vote_asset_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_vote_asset_vote` (
  `id` bigint(20) unsigned NOT NULL,
  `owner_id` bigint(20) unsigned NOT NULL,
  `vote_asset_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_vote_asset_vote_owner_id_idx` (`owner_id`),
  KEY `fk_contract_vote_asset_vote_va_id_idx` (`vote_asset_id`),
  CONSTRAINT `fk_contract_vote_asset_vote_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_vote_asset_vote_va_id` FOREIGN KEY (`vote_asset_id`) REFERENCES `contract_vote_asset` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_vote_witness`
--

DROP TABLE IF EXISTS `contract_vote_witness`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_vote_witness` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) unsigned NOT NULL,
  `support` tinyint(3) unsigned NOT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_vote_witness_owner_id_idx` (`owner_id`),
  KEY `fk_contract_vote_witness_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_vote_witness_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_vote_witness_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_vote_witness_vote`
--

DROP TABLE IF EXISTS `contract_vote_witness_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_vote_witness_vote` (
  `id` bigint(20) unsigned NOT NULL,
  `owner_id` bigint(20) unsigned NOT NULL,
  `vote_count` double unsigned NOT NULL,
  `vote_witness_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_vote_witness_vote_owner_id_idx` (`owner_id`),
  KEY `fk_contract_vote_witness_vote_vw_id_idx` (`vote_witness_id`),
  CONSTRAINT `fk_contract_vote_witness_vote_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_vote_witness_vote_vw_id` FOREIGN KEY (`vote_witness_id`) REFERENCES `contract_vote_witness` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_witness_create`
--

DROP TABLE IF EXISTS `contract_witness_create`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_witness_create` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `owner_id` bigint(20) unsigned NOT NULL,
  `url` varchar(250) DEFAULT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_witness_create_owner_id_idx` (`owner_id`),
  KEY `fk_contract_witness_create_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_witness_create_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_witness_create_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_witness_update`
--

DROP TABLE IF EXISTS `contract_witness_update`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contract_witness_update` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_name` varchar(250) DEFAULT NULL,
  `owner_id` bigint(20) unsigned NOT NULL,
  `transaction_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_witness_update_owner_id_idx` (`owner_id`),
  KEY `fk_contract_witness_update_tx_id_idx` (`transaction_id`),
  CONSTRAINT `fk_contract_witness_update_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_witness_update_tx_id` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sync_block`
--

DROP TABLE IF EXISTS `sync_block`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sync_block` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `block_num` bigint(20) DEFAULT NULL,
  `date_start` datetime(3) DEFAULT NULL,
  `date_end` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NOT NULL,
  `timestamp` datetime NOT NULL,
  `block_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_transaction_block_idx` (`block_id`),
  CONSTRAINT `fk_transaction_block` FOREIGN KEY (`block_id`) REFERENCES `block` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `witness`
--

DROP TABLE IF EXISTS `witness`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `witness` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `address` varchar(45) NOT NULL,
  `url` varchar(250) DEFAULT NULL,
  `total_produced` int(10) unsigned NOT NULL DEFAULT '0',
  `total_missed` int(10) unsigned NOT NULL DEFAULT '0',
  `latest_block_num` bigint(20) unsigned NOT NULL,
  `latest_slot_num` bigint(20) DEFAULT NULL,
  `isJobs` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'trxplorer'
--

--
-- Dumping routines for database 'trxplorer'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-04-19 23:11:28
