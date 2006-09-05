# phpMyAdmin MySQL-Dump
# version 2.5.0
# http://www.phpmyadmin.net/ (download page)
#
# Host: localhost
# Generation Time: May 27, 2004 at 10:01 AM
# Server version: 4.0.12
# PHP Version: 4.3.2
# Database : `cpath`
# --------------------------------------------------------

#
# Table structure for table `cpath`
#
# Creation: May 25, 2004 at 03:36 PM
# Last update: May 25, 2004 at 04:56 PM
#
DROP DATABASE IF EXISTS db_name__value;
CREATE DATABASE db_name__value;
USE db_name__value;

#
# Table structure for table `cpath`
#

CREATE TABLE `cpath` (
  `CPATH_ID` int(11) NOT NULL auto_increment COMMENT 'Primary Key',
  `NAME` varchar(255) NOT NULL default '' COMMENT 'Preferred name of entity',
  `DESC` varchar(255) default NULL COMMENT 'Short description of entity',
  `TYPE` varchar(255) NOT NULL default '' COMMENT 'Indicates entity type, such as PATHWAY, INTERACTION or PHYSICAL_ENTITY',
  `SPECIFIC_TYPE` varchar(255) default NULL COMMENT 'Specific entity type, e.g. protein, catalysis, etc. (mainly derived from BioPAX entity type)',
  `NCBI_TAX_ID` int(11) NOT NULL default '-9999' COMMENT 'Organism NCBI Taxonomy ID',
  `XML_TYPE` varchar(50) NOT NULL default '' COMMENT 'XML Record Type, e.g. PSI_MI or BIOPAX',
  `XML_CONTENT` longtext NOT NULL COMMENT 'XML Content Blob',
  `CREATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00' COMMENT 'Timestamp when record was originally created',
  `UPDATE_TIME` datetime default '0000-00-00 00:00:00' COMMENT 'Timestamp when record was last modified',
  PRIMARY KEY  (`CPATH_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Contains core cPath Entities.' AUTO_INCREMENT=1 ;


#
# Table structure for table `external_db`
#

CREATE TABLE `external_db` (
  `EXTERNAL_DB_ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(100) NOT NULL default '',
  `DESC` varchar(255) default NULL,
  `DB_TYPE` varchar(100) NOT NULL default '',
  `HOME_PAGE_URL` text default NULL,
  `URL_PATTERN` varchar(255) default NULL,
  `SAMPLE_ID` varchar(255) default NULL,
  `PATH_GUIDE_ID` varchar(255) default NULL,
  `ICON_BLOB` blob,
  `ICON_FILE_EXTENSION` varchar(255) default NULL,
  `CREATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  `UPDATE_TIME` datetime default '0000-00-00 00:00:00',
  PRIMARY KEY  (`EXTERNAL_DB_ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Contains information about external databases.' AUTO_INCREMENT=1;

#
# Table structure for table `external_db_cv`
#
CREATE TABLE `external_db_cv` (
  `CV_ID` int(11) NOT NULL auto_increment,
  `EXTERNAL_DB_ID` int(11) NOT NULL default '0',
  `CV_TERM` char(100) NOT NULL default '',
  `MASTER_FLAG` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`CV_ID`),
  UNIQUE KEY `CV_TERM` (`CV_TERM`)
) ENGINE=MyISAM CHARSET=latin1 COMMENT='Contains controlled vocabulary terms for external databases.' AUTO_INCREMENT=1;

# --------------------------------------------------------

#
# Table structure for table `external_link`
#
# Creation: May 25, 2004 at 03:36 PM
# Last update: May 25, 2004 at 04:56 PM
#

CREATE TABLE `external_link` (
  `EXTERNAL_LINK_ID` int(11) NOT NULL auto_increment,
  `CPATH_ID` int(11) NOT NULL default '0',
  `EXTERNAL_DB_ID` int(11) NOT NULL default '0',
  `LINKED_TO_ID` char(25) NOT NULL default '',
  PRIMARY KEY  (`EXTERNAL_LINK_ID`),
  KEY `QUERY_INDEX` (`EXTERNAL_DB_ID`,`LINKED_TO_ID`)
) TYPE=MyISAM COMMENT='Contains links to external databases.' AUTO_INCREMENT=1 ;
# --------------------------------------------------------

#
# Table structure for table `import`
#
# Creation: Apr 12, 2005 at 10:57 AM
# Last update: Apr 12, 2005 at 11:31 AM
#

CREATE TABLE `import` (
  `IMPORT_ID` int(11) NOT NULL auto_increment,
  `DESC` varchar(255) NOT NULL default '',
  `XML_TYPE` varchar(25) NOT NULL default '',
  `DOC_BLOB` longblob NOT NULL,
  `DOC_MD5` varchar(255) NOT NULL default '',
  `STATUS` varchar(20) NOT NULL default '',
  `CREATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  `UPDATE_TIME` datetime default '0000-00-00 00:00:00',
  `EX_DB_ID` varchar(30) default NULL,
  `LO_ID` int(11) default NULL,
  PRIMARY KEY  (`IMPORT_ID`)
) TYPE=MyISAM COMMENT='Contains import records from external sources.' AUTO_INCREMENT=1 ;
# --------------------------------------------------------

#
# Table structure for table `internal_link`
#
# Creation: May 25, 2004 at 03:36 PM
# Last update: May 25, 2004 at 04:56 PM
#

CREATE TABLE `internal_link` (
  `INTERNAL_LINK_ID` int(11) NOT NULL auto_increment,
  `SOURCE_ID` int(11) NOT NULL default '0',
  `TARGET_ID` int(11) NOT NULL default '0',
  PRIMARY KEY  (`INTERNAL_LINK_ID`)
) ENGINE=MyISAM CHARSET=latin1 COMMENT='Contains all Internal cPath Links' AUTO_INCREMENT=1 ;

#
# Table structure for table `log`
#
# Creation: May 24, 2004 at 10:53 AM
# Last update: May 27, 2004 at 09:39 AM
#

CREATE TABLE `log` (
  `TIMESTAMP` datetime NOT NULL default '0000-00-00 00:00:00',
  `PRIORITY` varchar(255) NOT NULL default '',
  `MESSAGE` longtext NOT NULL,
  `STACK_TRACE` mediumtext NOT NULL,
  `WEB_URL` mediumtext NOT NULL,
  `REMOTE_HOST` varchar(255) NOT NULL default '',
  `REMOTE_IP` varchar(255) NOT NULL default ''
) TYPE=MyISAM;
# --------------------------------------------------------

#
# Table structure for table `organism`
#
# Creation: May 25, 2004 at 03:36 PM
# Last update: May 25, 2004 at 03:41 PM
#

CREATE TABLE `organism` (
  `NCBI_TAXONOMY_ID` int(11) NOT NULL default '0',
  `SPECIES_NAME` varchar(255) NOT NULL default '',
  `COMMON_NAME` varchar(255) NOT NULL default '',
  UNIQUE KEY `ncbi_taxonomy_id` (`NCBI_TAXONOMY_ID`)
) TYPE=MyISAM COMMENT='Stores Organism Data';
# --------------------------------------------------------

#
# Table structure for table `xml_cache`
#
# Creation: Apr 19, 2005 at 01:13 PM
# Last update: Apr 19, 2005 at 01:16 PM
#

CREATE TABLE `xml_cache` (
  `CACHE_ID` int(11) NOT NULL auto_increment,
  `URL` text NOT NULL,
  `XML_TYPE` varchar(25) NOT NULL default '',
  `DOC_MD5` varchar(100) NOT NULL default '',
  `NUM_HITS` int(11) NOT NULL default '0',
  `DOC_BLOB` longblob NOT NULL,
  `LAST_USED` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`CACHE_ID`),
  KEY `QUERY` (`DOC_MD5`)
) TYPE=MyISAM COMMENT='Contains Cached XML Documents' AUTO_INCREMENT=1;

#
# Table structure for table `background_reference`
#
# Creation: Mar 14, 2005 at 03:55 PM
# Last update: Mar 14, 2005 at 03:55 PM
#

CREATE TABLE `background_reference` (
  `BACKGROUND_REFERENCE_ID` int(11) NOT NULL auto_increment,
  `REFERENCE_TYPE` varchar(25) NOT NULL default '',
  `DB_1` int(11) NOT NULL default '0',
  `ID_1` varchar(255) NOT NULL default '',
  `DB_2` int(11) NOT NULL default '0',
  `ID_2` varchar(255) NOT NULL default '',
  `HASH_CODE` int(30) NOT NULL default '0',
  PRIMARY KEY  (`BACKGROUND_REFERENCE_ID`),
  KEY `HASH_CODE` (`HASH_CODE`),
  KEY `DB ID Pair1` (`DB_1`,`ID_1`),
  KEY `DB ID Pair2` (`DB_2`,`ID_2`)
) TYPE=MyISAM COMMENT='Background References' AUTO_INCREMENT=1 ;

#
# Table structure for table `id_generator`
#
# Creation: Apr 07, 2005 at 11:18 AM
# Last update: Apr 07, 2005 at 11:20 AM
#

CREATE TABLE `id_generator` (
  `NEXT_ID` int(11) NOT NULL default '0'
) TYPE=MyISAM COMMENT='ID Generator';

#
# Table structure for table `web_ui`
#
# Creation: Nov 4, 2005 at 9:47 AM
# Last update: Nov 4, 2005 at 9:47 AM
#

CREATE TABLE `web_ui` (
`APPLICATION_NAME` TEXT NOT NULL ,
`HOME_PAGE_HEADER` TEXT NOT NULL ,
`HOME_PAGE_TAG_LINE` TEXT NOT NULL ,
`HOME_PAGE_RIGHT_COLUMN_CONTENT` TEXT NULL ,
`DISPLAY_BROWSE_BY_PATHWAY_TAB` BOOL NOT NULL ,
`DISPLAY_BROWSE_BY_ORGANISM_TAB` BOOL NOT NULL ,
`FAQ_PAGE_CONTENT` TEXT NOT NULL ,
`ABOUT_PAGE_CONTENT` TEXT NOT NULL ,
`MAINTENANCE_TAG_LINE` TEXT NOT NULL
) TYPE = MYISAM COMMENT = 'Contains configurable Web UI Elements';

Alter table internal_link add INDEX internal_link_source_idx (SOURCE_ID);
Alter table internal_link add INDEX internal_link_target_idx (TARGET_ID);
Alter table external_link add INDEX cpath_id_idx (cpath_id); 

--
-- Table structure for table `source_tracker`
--
CREATE TABLE `source_tracker` (
  `SOURCE_TRACKER_ID` int(11) NOT NULL auto_increment,
  `ID_OF_CPATH_GENERATED_RECORD` int(11) NOT NULL default '0',
  `ID_OF_SOURCE_RECORD` int(11) NOT NULL default '0',
  PRIMARY KEY  (`SOURCE_TRACKER_ID`),
  KEY `ID_OF_CPATH_GENERATED_RECORD` (`ID_OF_CPATH_GENERATED_RECORD`),
  KEY `ID_OF_SOURCE_RECORD` (`ID_OF_SOURCE_RECORD`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 COMMENT = 'Links source records with cPath generated records.';

--
-- Table structure for table `external_db_snapshot`
--

CREATE TABLE `external_db_snapshot` (
  `EXTERNAL_DB_SNAPSHOT_ID` int(11) NOT NULL auto_increment,
  `EXTERNAL_DB_ID` int(11) NOT NULL default '0',
  `SNAPSHOT_DATE` date NOT NULL default '0000-00-00',
  `SNAPSHOT_VERSION` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`EXTERNAL_DB_SNAPSHOT_ID`),
  KEY `EXTERNAL_DB_ID` (`EXTERNAL_DB_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Contains database snapshot information.' AUTO_INCREMENT=1;

-- 
-- Table structure for table `internal_family`
--

CREATE TABLE `internal_family` (
  `ANCESTOR_ID` int(11) NOT NULL default '0',
  `DESCENDENT_ID` int(11) NOT NULL default '0',
  `DESCENDENT_TYPE` varchar(255) NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Constraints for table `source_tracker`
--
ALTER TABLE `source_tracker`
  ADD CONSTRAINT `source_tracker_ibfk_2` FOREIGN KEY (`ID_OF_SOURCE_RECORD`) REFERENCES `cpath` (`CPATH_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `source_tracker_ibfk_1` FOREIGN KEY (`ID_OF_CPATH_GENERATED_RECORD`) REFERENCES `cpath` (`CPATH_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `external_db_snapshot`
--
ALTER TABLE `external_db_snapshot`
  ADD CONSTRAINT `external_db_snapshot_ibfk_1` FOREIGN KEY (`EXTERNAL_DB_ID`) REFERENCES `external_db` (`EXTERNAL_DB_ID`) ON DELETE CASCADE ON UPDATE CASCADE;