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
truncate table import;
truncate table external_db;
truncate table external_db_cv;
truncate table external_link;
truncate table cpath;
truncate table internal_link;
truncate table xml_cache;
truncate table organism;
truncate table id_map;

#
#  Insert data into table 'external_db'
#

INSERT INTO `external_db` VALUES (1, 'Swiss-Prot', 'http://www.pir.uniprot.org/cgi-bin/upEntry?id=%ID%', 'Swiss-Prot is a curated protein sequence database which strives to provide a high level of annotation, a minimal level of redundancy and high level of integration with other databases.', 3, NULL, NULL, 20031002162924, 20031002102704);
INSERT INTO `external_db` VALUES (2, 'PIR', 'http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%ID%', 'The Protein Information Resource (PIR), located at Georgetown University Medical Center (GUMC), is an integrated public bioinformatics resource that supports genomic and proteomic research and scientific studies.', 6, NULL, NULL, 20031002103329, 20031002103329);
INSERT INTO `external_db` VALUES (3, 'Entrez Gene', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=%ID%', 'NCBI Entrez Gene', 7, NULL, NULL, 20031002163800, 20031002103655);
INSERT INTO `external_db` VALUES (4, 'InterPro', 'http://www.ebi.ac.uk/interpro/IEntry?ac=%ID%', ' InterPro is a database of protein families, domains and functional sites in which identifiable features found in known proteins can be applied to unknown protein sequences.', 8, NULL, NULL, 20031002162303, 20031002162303);
INSERT INTO `external_db` VALUES (5, 'PROSITE', 'http://us.expasy.org/cgi-bin/nicedoc.pl?%ID%', 'Database of protein families and domains', 9, NULL, NULL, 20031002162542, 20031002162542);
INSERT INTO `external_db` VALUES (6, 'DIP', 'http://dip.doe-mbi.ucla.edu/dip/Search.cgi?SM=3&AC=DIP:%ID%&Search2=Query+DIP&GE=&DS=&PIR=&GB=&TX=&SF=&FN=&LO=&KW=', 'Database of Interacting Proteins', 10, NULL, NULL, 20031002162656, 20031002162656);
INSERT INTO `external_db` VALUES (7, 'Pfam', 'http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?%ID%', 'Protein families database of alignments and HMMs.', 11, NULL, NULL, 20031002162804, 20031002162804);
INSERT INTO `external_db` VALUES (8, 'Entrez GI', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=%ID%', 'NCBI Entrez System', 12, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (9, 'Locus Link', 'http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=%ID%', 'NCBI Locus Link', 14, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (10, 'GO', 'http://www.godatabase.org/cgi-bin/amigo/go.cgi?open_1=%ID%', 'Gene Ontology', 15, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (11, 'PubMed', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=%ID%&dopt=Abstract', 'NCBI PubMed', 16, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (12, 'Affymetrix', '', 'Affymetrix ID', 20, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (13, 'UniProt', 'http://www.pir.uniprot.org/cgi-bin/upEntry?id=%ID%', 'UniProt (Universal Protein Resource) is the world\'s most comprehensive catalog of information on proteins.', 21, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (14, 'IntAct', 'http://www.ebi.ac.uk/intact/search/do/hvWelcome?searchString=%ID%', 'IntAct', 22, NULL, NULL, 20031002163709, 20031002163709); 
INSERT INTO `external_db` VALUES (15, 'FlyBase', 'http://flybase.bio.indiana.edu/.bin/fbidq.html?%ID%', 'FlyBase', 23, NULL, NULL, 20031002163709, 20031002163709); 
INSERT INTO `external_db` VALUES (16, 'PDB', 'http://www.rcsb.org/pdb/cgi/explore.cgi?pdbId=%ID%', 'PDB', 24, NULL, NULL, 20031002163709, 20031002163709); 
INSERT INTO `external_db` VALUES (17, 'SGD', '', 'SGD', 25, NULL, NULL, 20031002163709, 20031002163709); 
INSERT INTO `external_db` VALUES (18, 'HPRD', 'http://hprd.ibioinformatics.org/hprdId?hprdId=%ID%', 'Human Protein Reference Database', 26, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (19, 'OMIM', 'http://www.ncbi.nlm.nih.gov/entrez/dispomim.cgi?id=%ID%', 'Online Mendelian Inheritance in Man', 28, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (20, 'GenBank', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Search&db=Protein&doptcmdl=GenPept&term=%ID%', 'GenBank', 29, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (21, 'MINT', 'http://mint.bio.uniroma2.it/mint/search/db_view_interaction.php?mint_id=%ID%', 'MINT (Molecular INTeraction database)', 31, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (22, 'Reactome', 'http://www.reactome.org/cgi-bin/search?SUBMIT=1&QUERY_CLASS=DatabaseIdentifier&QUERY=SWALL:%ID%', 'Reactome - a knowledgebase of biological processes', 32, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (23, 'HUGE', 'http://www.kazusa.or.jp/huge/gfpage/%ID%/', 'HUGE Protein Database', 33, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (24, 'Unigene', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=unigene&cmd=search&term=%ID%', 'Unigene Database', 35, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (25, 'Ensembl', 'http://www.ensembl.org/Homo_sapiens/textview?species=All&idx=All&q=%ID%&x=20&y=11', 'Ensembl Database', 36, NULL, NULL, 20031002163709, 20031002163709);

#
#
#  Insert data into table 'external_db_cv'
#

INSERT INTO `external_db_cv` VALUES (1, 1, 'SWISS-PROT');
INSERT INTO `external_db_cv` VALUES (2, 1, 'Swiss-Prot');
INSERT INTO `external_db_cv` VALUES (3, 1, 'SwissProt');
INSERT INTO `external_db_cv` VALUES (4, 1, 'SwissTrembl');
INSERT INTO `external_db_cv` VALUES (5, 1, 'SWP');
INSERT INTO `external_db_cv` VALUES (6, 2, 'PIR');
INSERT INTO `external_db_cv` VALUES (7, 3, 'RefSeq');
INSERT INTO `external_db_cv` VALUES (8, 4, 'InterPro');
INSERT INTO `external_db_cv` VALUES (9, 5, 'PROSITE');
INSERT INTO `external_db_cv` VALUES (10, 6, 'DIP');
INSERT INTO `external_db_cv` VALUES (11, 7, 'Pfam');
INSERT INTO `external_db_cv` VALUES (12, 8, 'Entrez GI');
INSERT INTO `external_db_cv` VALUES (13, 8, 'GI');
INSERT INTO `external_db_cv` VALUES (14, 9, 'LocusLink');
INSERT INTO `external_db_cv` VALUES (15, 10, 'GO');
INSERT INTO `external_db_cv` VALUES (16, 11, 'PubMed');
INSERT INTO `external_db_cv` VALUES (17, 11, 'pubmed');
INSERT INTO `external_db_cv` VALUES (18, 11, 'PMID');
INSERT INTO `external_db_cv` VALUES (19, 11, 'pmid');
INSERT INTO `external_db_cv` VALUES (20, 12, 'Affymetrix');
INSERT INTO `external_db_cv` VALUES (21, 13, 'uniprot');
INSERT INTO `external_db_cv` VALUES (22, 14, 'intact');
INSERT INTO `external_db_cv` VALUES (23, 15, 'flybase');
INSERT INTO `external_db_cv` VALUES (24, 16, 'pdb');
INSERT INTO `external_db_cv` VALUES (25, 17, 'sgd');
INSERT INTO `external_db_cv` VALUES (26, 18, 'HPRD');
INSERT INTO `external_db_cv` VALUES (27, 3, 'Ref-Seq');
INSERT INTO `external_db_cv` VALUES (28, 19, 'OMIM');
INSERT INTO `external_db_cv` VALUES (29, 20, 'GenBank');
INSERT INTO `external_db_cv` VALUES (30, 3, 'Ref_Seq');
INSERT INTO `external_db_cv` VALUES (31, 21, 'MINT');
INSERT INTO `external_db_cv` VALUES (32, 22, 'Reactome');
INSERT INTO `external_db_cv` VALUES (33, 23, 'HUGE');
INSERT INTO `external_db_cv` VALUES (34, 9, 'Locus-Link');
INSERT INTO `external_db_cv` VALUES (35, 24, 'Unigene');
INSERT INTO `external_db_cv` VALUES (36, 25, 'Ensembl');