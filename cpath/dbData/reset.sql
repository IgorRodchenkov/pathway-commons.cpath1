# phpMyAdmin MySQL-Dump
# version 2.5.0
# http://www.phpmyadmin.net/ (download page)
#
# Host: localhost
# Generation Time: Sep 22, 2003 at 03:16 PM
# Server version: 4.0.12
# PHP Version: 4.1.2
# --------------------------------------------------------
#

use cpath;
truncate table IMPORT;
truncate table EXTERNAL_DB;
truncate table EXTERNAL_DB_CV;
truncate table EXTERNAL_LINK;
truncate table CPATH;
truncate table INTERNAL_LINK;

#
#  Insert data into table 'EXTERNAL_DB'
#

INSERT INTO `EXTERNAL_DB` VALUES (1, 'Swiss-Prot', 'http://us.expasy.org/cgi-bin/niceprot.pl?%ID%', 'Swiss-Prot is a curated protein sequence database which strives to provide a high level of annotation, a minimal level of redundancy and high level of integration with other databases.', NULL, NULL, 20031002162924, 20031002102704);
INSERT INTO `EXTERNAL_DB` VALUES (2, 'PIR', 'http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%ID%', 'The Protein Information Resource (PIR), located at Georgetown University Medical Center (GUMC), is an integrated public bioinformatics resource that supports genomic and proteomic research and scientific studies.', NULL, NULL, 20031002103329, 20031002103329);
INSERT INTO `EXTERNAL_DB` VALUES (3, 'RefSeq', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Search&db=Protein&doptcmdl=GenPept&term=%ID%', 'The Reference Sequence (RefSeq) collection aims to provide a comprehensive, integrated, non-redundant set of sequences, including genomic DNA, transcript (RNA), and protein products, for major research organisms.', NULL, NULL, 20031002163800, 20031002103655);
INSERT INTO `EXTERNAL_DB` VALUES (4, 'InterPro', 'http://www.ebi.ac.uk/interpro/IEntry?ac=%ID%', ' InterPro is a database of protein families, domains and functional sites in which identifiable features found in known proteins can be applied to unknown protein sequences.', NULL, NULL, 20031002162303, 20031002162303);
INSERT INTO `EXTERNAL_DB` VALUES (5, 'PROSITE', 'http://us.expasy.org/cgi-bin/nicedoc.pl?%ID%', 'Database of protein families and domains', NULL, NULL, 20031002162542, 20031002162542);
INSERT INTO `EXTERNAL_DB` VALUES (6, 'DIP', 'http://dip.doe-mbi.ucla.edu/dip/Search.cgi?SM=3&AC=DIP:%ID%&Search2=Query+DIP&GE=&DS=&PIR=&GB=&TX=&SF=&FN=&LO=&KW=', 'Database of Interacting Proteins', NULL, NULL, 20031002162656, 20031002162656);
INSERT INTO `EXTERNAL_DB` VALUES (7, 'Pfam', 'http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?%ID%', 'Protein families database of alignments and HMMs.', NULL, NULL, 20031002162804, 20031002162804);
INSERT INTO `EXTERNAL_DB` VALUES (8, 'Entrez GI', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=protein&list_uids=%ID%&dopt=GenPept&term=%ID%&qty=1', 'NCBI Entrez System', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `EXTERNAL_DB` VALUES (9, 'Locus Link', 'http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=%ID%', 'NCBI Locus Link', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `EXTERNAL_DB` VALUES (10, 'GO', 'http://godatabase.org/cgi-bin/go.cgi?view=query&query=%ID%', 'Gene Ontology', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `EXTERNAL_DB` VALUES (11, 'PubMed', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=%ID%&dopt=Abstract', 'NCBI PubMed', NULL, NULL, 20031002163709, 20031002163709);

#
#  Insert data into table 'EXTERNAL_DB_CV'
#

INSERT INTO `EXTERNAL_DB_CV` VALUES (1, 1, 'SWISS-PROT');
INSERT INTO `EXTERNAL_DB_CV` VALUES (2, 1, 'Swiss-Prot');
INSERT INTO `EXTERNAL_DB_CV` VALUES (3, 1, 'SwissProt');
INSERT INTO `EXTERNAL_DB_CV` VALUES (4, 1, 'SWP');
INSERT INTO `EXTERNAL_DB_CV` VALUES (5, 2, 'PIR');
INSERT INTO `EXTERNAL_DB_CV` VALUES (6, 3, 'RefSeq');
INSERT INTO `EXTERNAL_DB_CV` VALUES (7, 4, 'InterPro');
INSERT INTO `EXTERNAL_DB_CV` VALUES (8, 5, 'PROSITE');
INSERT INTO `EXTERNAL_DB_CV` VALUES (9, 6, 'DIP');
INSERT INTO `EXTERNAL_DB_CV` VALUES (10, 7, 'Pfram');
INSERT INTO `EXTERNAL_DB_CV` VALUES (11, 8, 'Entrez GI');
INSERT INTO `EXTERNAL_DB_CV` VALUES (12, 8, 'GI');
INSERT INTO `EXTERNAL_DB_CV` VALUES (13, 9, 'LocusLink');
INSERT INTO `EXTERNAL_DB_CV` VALUES (14, 10, 'GO');
INSERT INTO `EXTERNAL_DB_CV` VALUES (15, 11, 'PubMed');
INSERT INTO `EXTERNAL_DB_CV` VALUES (16, 11, 'pubmed');
INSERT INTO `EXTERNAL_DB_CV` VALUES (17, 11, 'PMID');
INSERT INTO `EXTERNAL_DB_CV` VALUES (18, 11, 'pmid');



