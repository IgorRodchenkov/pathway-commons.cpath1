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

CREATE TABLE `cpath` (
  `CPATH_ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(255) NOT NULL default '',
  `DESC` varchar(255) default NULL,
  `TYPE` varchar(255) NOT NULL default '',
  `SPEC_TYPE` varchar(255) default NULL,
  `NCBI_TAX_ID` int(11) NOT NULL default '-9999',
  `XML_CONTENT` longtext NOT NULL,
  `CREATE_TIME` timestamp(14) NOT NULL,
  `UPDATE_TIME` timestamp(14) NOT NULL,
  PRIMARY KEY  (`CPATH_ID`)
) TYPE=MyISAM COMMENT='Contains core cPath Entities.' AUTO_INCREMENT=1 ;
# --------------------------------------------------------

#
# Table structure for table `external_db`
#
# Creation: May 25, 2004 at 03:36 PM
# Last update: May 25, 2004 at 03:37 PM
#

CREATE TABLE `external_db` (
  `EXTERNAL_DB_ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(100) NOT NULL default '',
  `URL` varchar(255) default NULL,
  `DESC` varchar(255) default NULL,
  `FIXED_CV_TERM` int(11) NOT NULL default '0',
  `DBDB_ID` int(11) default NULL,
  `DBDB_URL` varchar(255) default NULL,
  `CREATE_TIME` timestamp(14) NOT NULL,
  `UPDATE_TIME` timestamp(14) NOT NULL,
  PRIMARY KEY  (`EXTERNAL_DB_ID`)
) TYPE=MyISAM COMMENT='Contains information about external databases.' AUTO_INCREMENT=1;
# --------------------------------------------------------

#
# Table structure for table `external_db_cv`
#
# Creation: May 25, 2004 at 03:36 PM
# Last update: May 25, 2004 at 03:37 PM
#

CREATE TABLE `external_db_cv` (
  `CV_ID` int(11) NOT NULL auto_increment,
  `EXTERNAL_DB_ID` int(11) NOT NULL default '0',
  `CV_TERM` char(25) NOT NULL default '',
  PRIMARY KEY  (`CV_ID`),
  KEY `QUERY` (`CV_TERM`)
) TYPE=MyISAM COMMENT='Contains controlled vocabulary terms for external databases.' AUTO_INCREMENT=1 ;
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
# Creation: May 25, 2004 at 03:36 PM
# Last update: May 25, 2004 at 04:56 PM
#

CREATE TABLE `import` (
  `IMPORT_ID` int(11) NOT NULL auto_increment,
  `DESC` varchar(255) NOT NULL default '',
  `DOC_BLOB` longblob NOT NULL,
  `DOC_MD5` varchar(255) NOT NULL default '',
  `STATUS` varchar(20) NOT NULL default '',
  `CREATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  `UPDATE_TIME` datetime default '0000-00-00 00:00:00',
  `EX_DB_ID` varchar(30) default NULL,
  `LO_ID` int(11) default NULL,
  PRIMARY KEY  (`IMPORT_ID`,`IMPORT_ID`)
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
  `CPATH_ID_A` int(11) NOT NULL default '0',
  `CPATH_ID_B` int(11) NOT NULL default '0',
  PRIMARY KEY  (`INTERNAL_LINK_ID`)
) TYPE=MyISAM COMMENT='Contains all Internal cPath Links' AUTO_INCREMENT=1 ;
# --------------------------------------------------------

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
# Creation: May 25, 2004 at 03:36 PM
# Last update: May 27, 2004 at 09:39 AM
#

CREATE TABLE `xml_cache` (
  `CACHE_ID` int(11) NOT NULL auto_increment,
  `URL` text NOT NULL,
  `DOC_MD5` varchar(100) NOT NULL default '',
  `NUM_HITS` int(11) NOT NULL default '0',
  `DOC_BLOB` longblob NOT NULL,
  `LAST_USED` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`CACHE_ID`),
  KEY `QUERY` (`DOC_MD5`)
) TYPE=MyISAM COMMENT='Contains Cached XML Documents' AUTO_INCREMENT=1 ;