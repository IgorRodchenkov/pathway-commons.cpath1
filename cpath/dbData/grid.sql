# phpMyAdmin MySQL-Dump
# version 2.5.0
# http://www.phpmyadmin.net/ (download page)
#
# Host: localhost
# Generation Time: Jan 02, 2004 at 09:47 AM
# Server version: 4.0.12
# PHP Version: 4.3.2
# Database : `grid`
# --------------------------------------------------------

#
# Table structure for table `interactions`
#
# Creation: Dec 18, 2003 at 01:33 PM
# Last update: Dec 18, 2003 at 03:17 PM
#

CREATE TABLE `interactions` (
  `interaction_id` bigint(10) NOT NULL auto_increment,
  `geneA` varchar(100) default NULL,
  `geneB` varchar(100) default NULL,
  `experimental_system` varchar(150) default NULL,
  `owner` varchar(150) default NULL,
  `bait_allele` varchar(100) default NULL,
  `prey_allele` varchar(100) default NULL,
  `direction` enum('AB','BA','BOTH','NONE') default NULL,
  `pubmed_id` text,
  `deprecated` enum('T','F') default NULL,
  `status` varchar(10) default NULL,
  PRIMARY KEY  (`interaction_id`),
  KEY `geneAIndex` (`geneA`),
  KEY `geneBIndex` (`geneB`),
  KEY `interactionIDIndex` (`interaction_id`),
  KEY `geneAIndex2` (`geneA`),
  KEY `geneBIndex2` (`geneB`),
  KEY `interactionIDIndex2` (`interaction_id`),
  KEY `deprecatedIndex` (`deprecated`)
) TYPE=MyISAM AUTO_INCREMENT=13 ;
# --------------------------------------------------------

#
# Table structure for table `orf_info`
#
# Creation: Dec 18, 2003 at 01:33 PM
# Last update: Dec 18, 2003 at 03:17 PM
#

CREATE TABLE `orf_info` (
  `id` bigint(10) NOT NULL auto_increment,
  `orf_name` varchar(40) NOT NULL default '',
  `gene_names` varchar(255) NOT NULL default '',
  `description` varchar(255) NOT NULL default '',
  `function_ids` varchar(255) NOT NULL default '',
  `process_ids` varchar(255) NOT NULL default '',
  `component_ids` varchar(255) NOT NULL default '',
  `function_names` text NOT NULL,
  `process_names` text NOT NULL,
  `component_names` text NOT NULL,
  `special_ids` varchar(255) NOT NULL default '',
  `special_names` text NOT NULL,
  `external_ids` text NOT NULL,
  `external_names` text NOT NULL,
  `ambiguous` enum('Y','N') NOT NULL default 'N',
  `ambiguous_genes` varchar(255) NOT NULL default '',
  `status` varchar(10) NOT NULL default '',
  PRIMARY KEY  (`id`),
  KEY `orfInfo1` (`id`),
  KEY `orfInfo2` (`orf_name`),
  KEY `orfInfo3` (`gene_names`),
  KEY `orfInfo4` (`description`),
  KEY `orfInfo5` (`function_ids`),
  KEY `orfInfo6` (`process_ids`),
  KEY `orfInfo7` (`component_ids`),
  KEY `orfInfo8` (`function_names`(60)),
  KEY `orfInfo9` (`process_names`(60)),
  KEY `orfInfo10` (`component_names`(60)),
  KEY `orfInfo11` (`special_ids`),
  KEY `orfInfo12` (`special_names`(60))
) TYPE=MyISAM AUTO_INCREMENT=6070 ;
