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

DROP DATABASE IF EXISTS cpath;
CREATE DATABASE cpath;
USE cpath;

#
# Table structure for table `cpath`
#
# Creation: Apr 07, 2005 at 02:41 PM
# Last update: Apr 07, 2005 at 02:41 PM
#

CREATE TABLE `cpath` (
  `CPATH_ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(255) NOT NULL default '',
  `DESC` varchar(255) default NULL,
  `TYPE` varchar(255) NOT NULL default '',
  `SPECIFIC_TYPE` varchar(255) default NULL,
  `NCBI_TAX_ID` int(11) NOT NULL default '-9999',
  `XML_TYPE` varchar(50) NOT NULL default '',
  `XML_CONTENT` longtext NOT NULL,
  `CREATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  `UPDATE_TIME` datetime default '0000-00-00 00:00:00',
  PRIMARY KEY  (`CPATH_ID`)
) TYPE=MyISAM COMMENT='Contains core cPath Entities.' AUTO_INCREMENT=1;


#
# Table structure for table `external_db`
#
# Creation: Mar 14, 2005 at 03:55 PM
# Last update: Mar 14, 2005 at 03:55 PM
#
CREATE TABLE `external_db` (
  `EXTERNAL_DB_ID` int(11) NOT NULL auto_increment,
  `NAME` varchar(100) NOT NULL default '',
  `URL` varchar(255) default NULL,
  `SAMPLE_ID` varchar(255) default NULL,
  `DESC` varchar(255) default NULL,
  `DB_TYPE` varchar(100) NOT NULL default '',
  `DBDB_ID` int(11) default NULL,
  `DBDB_URL` varchar(255) default NULL,
  `CREATE_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  `UPDATE_TIME` datetime default '0000-00-00 00:00:00',
  PRIMARY KEY  (`EXTERNAL_DB_ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=MyISAM CHARSET=latin1 COMMENT='Contains information about external databases.' AUTO_INCREMENT=1;


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
`LOGO` TINYTEXT NOT NULL ,
`HOME_PAGE_TITLE` TINYTEXT NOT NULL ,
`HOME_PAGE_TAG_LINE` TINYTEXT NOT NULL ,
`HOME_PAGE_RIGHT_COLUMN_CONTENT` TEXT NULL ,
`DISPLAY_BROWSE_BY_PATHWAY_TAB` BOOL NOT NULL ,
`DISPLAY_BROWSE_BY_ORGANISM_TAB` BOOL NOT NULL ,
`FAQ_PAGE_CONTENT` TEXT NOT NULL ,
`ABOUT_PAGE_CONTENT` TEXT NOT NULL ,
`HOME_PAGE_MAINTENANCE_TAG_LINE` TINYTEXT NOT NULL
) TYPE = MYISAM COMMENT = 'Contains configurable Web UI Elements';

INSERT INTO `web_ui` VALUES ('none', '<h1>cPath:  Pathway Database</h1><h2>Memorial Sloan-Kettering Cancer Center</h2>', 'cPath is a database and software suite for storing, visualizing, and analyzing biological pathways. <A HREF="about.do">more...</A>', '<IMG SRC="jsp/images/ismb_poster_2005.png"/>\r\n<BR>\r\nDownload cPath Poster from \r\n<A HREF="http://www.iscb.org/ismb2005/">ISMB 2005</A>.\r\n<BR>\r\n<A HREF="http://cbio.mskcc.org/dev_site/cpath_poster_2005.pdf">PDF Format</A> [9.5 MB]\r\n', 1, 1, '<h1>What is cPath?</h1>\r\n<P>\r\ncPath is a database and software suite for storing, visualizing, and analyzing biological pathways.\r\n</P>\r\n<P>\r\nMain features include:\r\n<UL>\r\n<LI>Import pipeline capable of aggregrating pathway and interaction data sets from multiple sources, including:  MINT, IntAct,  HPRD, DIP, BioCyc, KEGG, PUMA2 and Reactome.\r\n<LI>Import/Export support for the Proteomics Standards Initiative Molecular Interaction (<A HREF="http://psidev.sourceforge.net/mi/xml/doc/user/">PSI-MI</A>) and the Biological Pathways Exchange (<A HREF="http://www.biopax.org">BioPAX</A>) XML formats.\r\n<LI>Data visualization and analysis via <A HREF="http://www.cytoscape.org">Cytoscape</A>.\r\n<LI>Simple HTTP URL based XML web service.\r\n<LI>Complete software is <A HREF="http://cbio.mskcc.org/dev_site/cpath/">freely available for local install</A>.  Easy to install and administer.\r\n<LI>Partly funded by the U.S. National Cancer Institute, via the Cancer Biomedical Informatics Grid (<A HREF="https://cabig.nci.nih.gov/">caBIG</A>), and aims to meet "silver-level" requirements for software interoperability and data exchange.\r\n</UL>\r\n</P>\r\n\r\n<h1>Who do I contact for additional information regarding cPath?</h1>\r\n<P>\r\nFor scientific questions regarding cPath, please contact\r\n<A HREF="http://www.cbio.mskcc.org/people/info/gary_bader.html">Gary Bader</A>.\r\nFor technical / programming questions regarding cPath, please contact\r\n<A HREF="http://www.cbio.mskcc.org/people/info/ethan_cerami.html">Ethan Cerami</A>\r\nor <A HREF="http://cbio.mskcc.org/people/info/benjamin_gross.html">Benjamin Gross</A>.\r\n</P>\r\n<h1>What is Cytoscape? How can I visualize cPath data in Cytoscape?</h1>\r\n<P><A HREF="http://www.cytoscape.org">Cytoscape</A> is a bioinformatics software platform for visualizing molecular interaction networks and integrating these interactions with gene expression profiles and other state data. Cytoscape is open source software, and available for download from the cytoscape.org web site. Cytoscape is written in Java, and therefore runs on Windows, Mac OS X, and Linux.\r\n</P>\r\n<P>\r\nCytoscape includes a built-in PlugIn framework for adding new features and functionality. The <A HREF="cytoscape.do">cPath PlugIn</A> enables Cytoscape users to directly query, retrieve and visualize interactions retrieved from the cPath database. \r\n</P>\r\n<h1>How can I programmatically access cPath? How do I use the Web Services API?</h1>\r\n<P>cPath provides a complete <A HREF="webservice.do?cmd=help">Web Services API</A> for programmatically accessing cPath data. Complete details are available at the <A HREF="webservice.do?cmd=help">Web Services API Help Page</A>. \r\n</P>', '<h1>About cPath</h1>\r\n<P>\r\ncPath is a database and software suite for storing, visualizing, and analyzing biological pathways.  \r\n</P>\r\n<P>\r\nMain features:\r\n<UL>\r\n<LI>Import pipeline capable of aggregrating pathway and interaction data sets from multiple sources, including:  MINT, IntAct,  HPRD, DIP, BioCyc, KEGG, PUMA2 and Reactome.\r\n<LI>Import/Export support for the Proteomics Standards Initiative Molecular Interaction (<A HREF="http://psidev.sourceforge.net/mi/xml/doc/user/">PSI-MI</A>) and the Biological Pathways Exchange (<A HREF="http://www.biopax.org">BioPAX</A>) XML formats.\r\n<LI>Data visualization and analysis via <A HREF="http://www.cytoscape.org">Cytoscape</A>.\r\n<LI>Simple HTTP URL based XML web service.\r\n<LI>Complete software is <A HREF="http://cbio.mskcc.org/dev_site/cpath/">freely available for local install</A>.  Easy to install and administer.\r\n<LI>Partly funded by the U.S. National Cancer Institute, via the Cancer Biomedical Informatics Grid (<A HREF="https://cabig.nci.nih.gov/">caBIG</A>), and aims to meet "silver-level" requirements for software interoperability and data exchange.\r\n</UL>\r\n</P>\r\n<P>\r\ncPath is currently being developed by the\r\n<A HREF="http://cbio.mskcc.org">Computational Biology Center</A>\r\nat <A HREF="http://www.mskcc.org">Memorial Sloan-Kettering Cancer Center</A>.\r\n</P>\r\n<h1>About cBio @ MSKCC</h1>\r\n<P>The Computational Biology Center at Memorial Sloan-Kettering Cancer Center (<a href="http://www.mskcc.org">MSKCC</a>) pursues computational biology research projects and the development of bioinformatics resources in the areas of\r\n<ul>\r\n   <li> sequence-structure analysis </li>\r\n   <li> gene regulation </li>\r\n   <li> molecular pathways and networks </li>\r\n   <li> diagnostic and prognostic indicators </li>\r\n</ul>\r\nThe mission of cBio is to move the theoretical methods and genome-scale data resources of computational biology into everyday laboratory practice and use, and is reflected in the organization of cBio into research and service components &#126; the intention being that new computational methods created through the process of scientific inquiry should be generalized and supported as open-source and shared community resources.\r\n</P>', 'cPath is created and maintained by the <A HREF="http://www.cbio.mskcc.org">Computational Biology Center</A> at <A HREF="http://www.mskcc.org">Memorial Sloan-Kettering Cancer Center</A>.');

Alter table internal_link add INDEX internal_link_source_idx (SOURCE_ID);
Alter table internal_link add INDEX internal_link_target_idx (TARGET_ID);
Alter table external_link add INDEX cpath_id_idx (cpath_id); 

