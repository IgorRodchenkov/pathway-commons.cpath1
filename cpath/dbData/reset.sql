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

INSERT INTO `external_db` VALUES (1, 'UniProt', 'http://www.pir.uniprot.org/cgi-bin/upEntry?id=%ID%', 'UniProt (Universal Protein Resource) is the world\'s most comprehensive catalog of information on proteins.', 6, NULL, NULL, 20031002162924, 20031002102704);
INSERT INTO `external_db` VALUES (2, 'Entrez Gene', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=%ID%', 'NCBI Entrez Gene', 21, NULL, NULL, 20031002163800, 20031002103655);
INSERT INTO `external_db` VALUES (3, 'PubMed', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=%ID%&dopt=Abstract', 'NCBI PubMed', 29, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (4, 'Unigene', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=unigene&cmd=search&term=%ID%', 'NCBI Unigene Database', 31, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (5, 'OMIM', 'http://www.ncbi.nlm.nih.gov/entrez/dispomim.cgi?id=%ID%', 'Online Mendelian Inheritance in Man', 32, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (6, 'PIR', 'http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%ID%', 'The Protein Information Resource (PIR), located at Georgetown University Medical Center (GUMC), is an integrated public bioinformatics resource that supports genomic and proteomic research and scientific studies.', 50, NULL, NULL, 20031002103329, 20031002103329);
INSERT INTO `external_db` VALUES (7, 'InterPro', 'http://www.ebi.ac.uk/interpro/IEntry?ac=%ID%', 'InterPro is a database of protein families, domains and functional sites in which identifiable features found in known proteins can be applied to unknown protein sequences.', 52, NULL, NULL, 20031002162303, 20031002162303);
INSERT INTO `external_db` VALUES (8, 'PROSITE', 'http://us.expasy.org/cgi-bin/nicedoc.pl?%ID%', 'Database of protein families and domains', 52, NULL, NULL, 20031002162542, 20031002162542);
INSERT INTO `external_db` VALUES (9, 'DIP', 'http://dip.doe-mbi.ucla.edu/dip/Search.cgi?SM=3&AC=DIP:%ID%&Search2=Query+DIP&GE=&DS=&PIR=&GB=&TX=&SF=&FN=&LO=&KW=','Database of Interacting Proteins', 53, NULL, NULL, 20031002162656, 20031002162656);
INSERT INTO `external_db` VALUES (10, 'Pfam', 'http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?%ID%', 'Protein families database of alignments and HMMs.', 54, NULL, NULL, 20031002162804, 20031002162804);
INSERT INTO `external_db` VALUES (11, 'GO', 'http://www.godatabase.org/cgi-bin/amigo/go.cgi?open_1=%ID%', 'Gene Ontology', 55, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (12, 'Affymetrix', '', 'Affymetrix ID', 56, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (13, 'IntAct', 'http://www.ebi.ac.uk/intact/search/do/hvWelcome?searchString=%ID%', 'IntAct', 57, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (14, 'FlyBase', 'http://flybase.bio.indiana.edu/.bin/fbidq.html?%ID%', 'FlyBase', 58, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (15, 'PDB', 'http://www.rcsb.org/pdb/cgi/explore.cgi?pdbId=%ID%', 'PDB', 59, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (16, 'SGD', '', 'SGD', 60, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (17, 'HPRD', 'http://hprd.ibioinformatics.org/hprdId?hprdId=%ID%', 'Human Protein Reference Database', 61, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (18, 'MINT', 'http://mint.bio.uniroma2.it/mint/search/db_view_interaction.php?mint_id=%ID%', 'MINT (Molecular INTeraction database)', 62, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (19, 'Reactome', 'http://www.reactome.org/cgi-bin/search?SUBMIT=1&QUERY_CLASS=DatabaseIdentifier&QUERY=SWALL:%ID%', 'Reactome - a knowledgebase of biological processes', 64, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (20, 'HUGE', 'http://www.kazusa.or.jp/huge/gfpage/%ID%/', 'HUGE Protein Database', 64, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (21, 'Ensembl', 'http://www.ensembl.org/Homo_sapiens/textview?species=All&idx=All&q=%ID%&x=20&y=11', 'Ensembl Database', 65, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (22, 'NCBI GenBank', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', 'NCBI GenBank', 26, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (23, 'NCBI GI', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', 'NCBI GI', 27, NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (24, 'RefSeq', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', 'NCBI RefSeq', 25, NULL, NULL, 20031002163709, 20031002163709);

#
#
#  Insert data into table 'external_db_cv'
#

#  Terms Associated with UNIPROT and SWISS-PROT (1-19)
#  All these terms map to the UniProt Database
INSERT INTO `external_db_cv` VALUES (1, 1, 'SWISS-PROT');
INSERT INTO `external_db_cv` VALUES (3, 1, 'SWISSPROT');
INSERT INTO `external_db_cv` VALUES (4, 1, 'SWISSTREMBL');
INSERT INTO `external_db_cv` VALUES (5, 1, 'SWP');
INSERT INTO `external_db_cv` VALUES (6, 1, 'UNIPROT');

#  Terms Associated with NCBI Databases (20-49)
#  Entrez Gene will soon replace LocusLink
INSERT INTO `external_db_cv` VALUES (21, 2, 'ENTREZ_GENE');
INSERT INTO `external_db_cv` VALUES (22, 2, 'LOCUS_LINK');
INSERT INTO `external_db_cv` VALUES (23, 2, 'LOCUSLINK');

#  Terms Associated with RefSeq
INSERT INTO `external_db_cv` VALUES (24, 24, 'REFSEQ');
INSERT INTO `external_db_cv` VALUES (25, 24, 'REF_SEQ');
INSERT INTO `external_db_cv` VALUES (49, 24, 'REF-SEQ');

#  Terms Associated with GenBank and GI
INSERT INTO `external_db_cv` VALUES (26, 22, 'GENBANK');
INSERT INTO `external_db_cv` VALUES (27, 23, 'ENTREZ GI');
INSERT INTO `external_db_cv` VALUES (28, 23, 'GI');

#  Pub Med IDs
INSERT INTO `external_db_cv` VALUES (29, 3, 'PUBMED');
INSERT INTO `external_db_cv` VALUES (30, 3, 'PMID');

# Other NCBI Databses
INSERT INTO `external_db_cv` VALUES (31, 4, 'UNIGENE');
INSERT INTO `external_db_cv` VALUES (32, 5, 'OMIM');

# All Other Databases (50 - 99)
INSERT INTO `external_db_cv` VALUES (50, 6, 'PIR');
INSERT INTO `external_db_cv` VALUES (51, 7, 'INTERPRO');
INSERT INTO `external_db_cv` VALUES (52, 8, 'PROSITE');
INSERT INTO `external_db_cv` VALUES (53, 9, 'DIP');
INSERT INTO `external_db_cv` VALUES (54, 10, 'PFAM');
INSERT INTO `external_db_cv` VALUES (55, 11, 'GO');
INSERT INTO `external_db_cv` VALUES (56, 12, 'AFFYMETRIX');
INSERT INTO `external_db_cv` VALUES (57, 13, 'INTACT');
INSERT INTO `external_db_cv` VALUES (58, 14, 'FLYBASE');
INSERT INTO `external_db_cv` VALUES (59, 15, 'PDB');
INSERT INTO `external_db_cv` VALUES (60, 16, 'SGD');
INSERT INTO `external_db_cv` VALUES (61, 17, 'HPRD');
INSERT INTO `external_db_cv` VALUES (62, 18, 'MINT');
INSERT INTO `external_db_cv` VALUES (63, 19, 'REACTOME');
INSERT INTO `external_db_cv` VALUES (64, 20, 'HUGE');
INSERT INTO `external_db_cv` VALUES (65, 21, 'ENSEMBL');
