# phpMyAdmin MySQL-Dump
# version 2.5.0
# http://www.phpmyadmin.net/ (download page)
#
# Host: localhost
# Generation Time: May 07, 2004 at 03:52 PM
# Server version: 4.0.12
# PHP Version: 4.3.2
# Database : `cpath`
# --------------------------------------------------------

#
# Table structure for table `cpath`
#
# Creation: May 07, 2004 at 11:48 AM
# Last update: May 07, 2004 at 11:49 AM
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
) TYPE=MyISAM COMMENT='Contains core cPath Entities.' AUTO_INCREMENT=948 ;
# --------------------------------------------------------

#
# Table structure for table `external_db`
#
# Creation: May 07, 2004 at 11:48 AM
# Last update: May 07, 2004 at 11:48 AM
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
) TYPE=MyISAM COMMENT='Contains information about external databases.' AUTO_INCREMENT=13 ;
# --------------------------------------------------------

#
# Table structure for table `external_db_cv`
#
# Creation: May 07, 2004 at 11:48 AM
# Last update: May 07, 2004 at 11:48 AM
#

CREATE TABLE `external_db_cv` (
  `CV_ID` int(11) NOT NULL auto_increment,
  `EXTERNAL_DB_ID` int(11) NOT NULL default '0',
  `CV_TERM` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`CV_ID`)
) TYPE=MyISAM COMMENT='Contains controlled vocabulary terms for external databases.' AUTO_INCREMENT=21 ;
# --------------------------------------------------------

#
# Table structure for table `external_link`
#
# Creation: May 07, 2004 at 11:48 AM
# Last update: May 07, 2004 at 11:49 AM
#

CREATE TABLE `external_link` (
  `EXTERNAL_LINK_ID` int(11) NOT NULL auto_increment,
  `CPATH_ID` int(11) NOT NULL default '0',
  `EXTERNAL_DB_ID` int(11) NOT NULL default '0',
  `LINKED_TO_ID` varchar(255) NOT NULL default '',
  `CREATE_TIME` timestamp(14) NOT NULL,
  `UPDATE_TIME` timestamp(14) NOT NULL,
  PRIMARY KEY  (`EXTERNAL_LINK_ID`)
) TYPE=MyISAM COMMENT='Contains links to external databases.' AUTO_INCREMENT=3262 ;
# --------------------------------------------------------

#
# Table structure for table `import`
#
# Creation: May 07, 2004 at 11:48 AM
# Last update: May 07, 2004 at 11:49 AM
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
) TYPE=MyISAM COMMENT='Contains import records from external sources.' AUTO_INCREMENT=2 ;
# --------------------------------------------------------

#
# Table structure for table `internal_link`
#
# Creation: May 07, 2004 at 11:48 AM
# Last update: May 07, 2004 at 11:49 AM
#

CREATE TABLE `internal_link` (
  `INTERNAL_LINK_ID` int(11) NOT NULL auto_increment,
  `CPATH_ID_A` int(11) NOT NULL default '0',
  `CPATH_ID_B` int(11) NOT NULL default '0',
  PRIMARY KEY  (`INTERNAL_LINK_ID`)
) TYPE=MyISAM COMMENT='Contains all Internal cPath Links' AUTO_INCREMENT=1035 ;
# --------------------------------------------------------

#
# Table structure for table `log`
#
# Creation: May 07, 2004 at 10:29 AM
# Last update: May 07, 2004 at 10:29 AM
#

CREATE TABLE `log` (
  `date` datetime NOT NULL default '0000-00-00 00:00:00',
  `logger` varchar(255) NOT NULL default '',
  `priority` varchar(255) NOT NULL default '',
  `message` varchar(255) NOT NULL default '',
  `ip` varchar(255) NOT NULL default ''
) TYPE=MyISAM;
# --------------------------------------------------------

#
# Table structure for table `organism`
#
# Creation: May 07, 2004 at 11:48 AM
# Last update: May 07, 2004 at 11:49 AM
#

CREATE TABLE `organism` (
  `ncbi_taxonomy_id` int(11) NOT NULL default '0',
  `species_name` varchar(255) NOT NULL default '',
  `common_name` varchar(255) NOT NULL default '',
  UNIQUE KEY `ncbi_taxonomy_id` (`ncbi_taxonomy_id`)
) TYPE=MyISAM COMMENT='Stores Organism Data';
# --------------------------------------------------------

#
# Table structure for table `xml_cache`
#
# Creation: May 07, 2004 at 11:48 AM
# Last update: May 07, 2004 at 03:48 PM
#

CREATE TABLE `xml_cache` (
  `CACHE_ID` int(11) NOT NULL auto_increment,
  `DOC_MD5` varchar(255) NOT NULL default '',
  `NUM_HITS` int(11) NOT NULL default '0',
  `DOC_BLOB` longblob NOT NULL,
  `CREATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`CACHE_ID`)
) TYPE=MyISAM COMMENT='Contains Cached XML Documents' AUTO_INCREMENT=37 ;

    