# phpMyAdmin MySQL-Dump
# version 2.5.0
# http://www.phpmyadmin.net/ (download page)
#
# Host: localhost
# Generation Time: Nov 06, 2003 at 02:27 PM
# Server version: 4.0.12
# PHP Version: 4.1.2
# Database : `log`
# --------------------------------------------------------

#
# Table structure for table `record`
#
# Creation: Nov 06, 2003 at 02:25 PM
# Last update: Nov 06, 2003 at 02:25 PM
#

CREATE TABLE `record` (
  `date` datetime NOT NULL default '0000-00-00 00:00:00',
  `logger` varchar(255) NOT NULL default '',
  `priority` varchar(255) NOT NULL default '',
  `message` varchar(255) NOT NULL default '',
  `ip` varchar(255) NOT NULL default ''
) TYPE=MyISAM;


