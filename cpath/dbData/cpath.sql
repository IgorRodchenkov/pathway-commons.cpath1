# phpMyAdmin MySQL-Dump
# version 2.5.0
# http://www.phpmyadmin.net/ (download page)
#
# Host: localhost
# Generation Time: Nov 06, 2003 at 02:26 PM
# Server version: 4.0.12
# PHP Version: 4.1.2
# Database : `cpath`
# --------------------------------------------------------

#
# Table structure for table `cpath`
#
# Creation: Nov 06, 2003 at 02:25 PM
# Last update: Nov 06, 2003 at 02:25 PM
#

CREATE TABLE `CPATH` (
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
) TYPE=MyISAM COMMENT='Contains core cPath Entities.' AUTO_INCREMENT=6 ;
# --------------------------------------------------------

#
# Table structure for table `external_db`
#
# Creation: Nov 06, 2003 at 02:25 PM
# Last update: Nov 06, 2003 at 02:25 PM
#

CREATE TABLE `EXTERNAL_DB` (
  `EXTERNAL_DB_ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(100) NOT NULL default '',
  `URL` varchar(255) default NULL,
  `DESC` varchar(255) default NULL,
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
# Creation: Nov 06, 2003 at 02:25 PM
# Last update: Nov 06, 2003 at 02:25 PM
#

CREATE TABLE `EXTERNAL_DB_CV` (
  `CV_ID` int(11) NOT NULL auto_increment,
  `EXTERNAL_DB_ID` int(11) NOT NULL default '0',
  `CV_TERM` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`CV_ID`)
) TYPE=MyISAM COMMENT='Contains controlled vocabulary terms for external databases.' AUTO_INCREMENT=21 ;
# --------------------------------------------------------

#
# Table structure for table `external_link`
#
# Creation: Nov 06, 2003 at 02:25 PM
# Last update: Nov 06, 2003 at 02:25 PM
#

CREATE TABLE `EXTERNAL_LINK` (
  `EXTERNAL_LINK_ID` int(11) NOT NULL auto_increment,
  `CPATH_ID` int(11) NOT NULL default '0',
  `EXTERNAL_DB_ID` int(11) NOT NULL default '0',
  `LINKED_TO_ID` varchar(255) NOT NULL default '',
  `CREATE_TIME` timestamp(14) NOT NULL,
  `UPDATE_TIME` timestamp(14) NOT NULL,
  PRIMARY KEY  (`EXTERNAL_LINK_ID`)
) TYPE=MyISAM COMMENT='Contains links to external databases.' AUTO_INCREMENT=4 ;
# --------------------------------------------------------

#
# Table structure for table `import`
#
# Creation: Nov 06, 2003 at 02:25 PM
# Last update: Nov 06, 2003 at 02:25 PM
#

CREATE TABLE `IMPORT` (
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
) TYPE=MyISAM COMMENT='Contains import records from external sources.' AUTO_INCREMENT=3 ;
# --------------------------------------------------------

#
# Table structure for table `internal_link`
#
# Creation: Nov 06, 2003 at 02:25 PM
# Last update: Nov 06, 2003 at 02:25 PM
#

CREATE TABLE `INTERNAL_LINK` (
  `INTERNAL_LINK_ID` int(11) NOT NULL auto_increment,
  `CPATH_ID_A` int(11) NOT NULL default '0',
  `CPATH_ID_B` int(11) NOT NULL default '0',
  PRIMARY KEY  (`INTERNAL_LINK_ID`)
) TYPE=MyISAM COMMENT='Contains all Internal cPath Links' AUTO_INCREMENT=4 ;


