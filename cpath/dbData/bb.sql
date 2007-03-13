USE db_name__value;


#
# Table structure for table `bb_pathway`
#
DROP TABLE IF EXISTS `bb_pathway`;
CREATE TABLE `bb_pathway` (
	`EXTERNAL_PATHWAY_ID` varchar(255) NOT NULL default '',
    `PATHWAY_NAME` varchar(255) NOT NULL default '',
    `SOURCE` varchar(255) NOT NULL default '',
	`URL` varchar(255) NOT NULL default '',
    PRIMARY KEY  (`EXTERNAL_PATHWAY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

#
# Table structure for table `bb_gene`
#
DROP TABLE IF EXISTS `bb_gene`;
CREATE TABLE `bb_gene` (
    `ENTREZ_GENE_ID` varchar(255) NOT NULL default '',
    `GENE_NAME` varchar(255) NOT NULL default '',
    PRIMARY KEY  (`ENTREZ_GENE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

#
# Table structure for table `bb_internal_link`
#
DROP TABLE IF EXISTS `bb_internal_link`;
CREATE TABLE `bb_internal_link` (
	`EXTERNAL_PATHWAY_ID` varchar(255) NOT NULL default '',
    `ENTREZ_GENE_ID` varchar(255) NOT NULL default '',
    PRIMARY KEY  (`EXTERNAL_PATHWAY_ID`, `ENTREZ_GENE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
