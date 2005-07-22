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
truncate table background_reference;
truncate table id_generator;

#
#  Insert data into table 'external_db'
#

INSERT INTO `external_db` VALUES (1, 'UniProt', 'http://www.pir.uniprot.org/cgi-bin/upEntry?id=%ID%', NULL, 'Universal Protein Resource (UniProt)', 6, 'PROTEIN_UNIFICATION', NULL, NULL, 20031002162924, 20031002102704);
INSERT INTO `external_db` VALUES (2, 'Entrez Gene', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=%ID%', NULL, 'NCBI Entrez Gene', 21, 'LINK_OUT', NULL, NULL, 20031002163800, 20031002103655);
INSERT INTO `external_db` VALUES (3, 'PubMed', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=%ID%&dopt=Abstract', NULL, 'NCBI PubMed', 29, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (4, 'UniGene', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=unigene&cmd=search&term=%ID%', NULL, 'NCBI UniGene Database', 31, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (5, 'OMIM', 'http://www.ncbi.nlm.nih.gov/entrez/dispomim.cgi?id=%ID%', NULL, 'Online Mendelian Inheritance in Man (OMIM)', 32, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (6, 'PIR', 'http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%ID%', NULL, 'The Protein Information Resource (PIR)', 50, 'PROTEIN_UNIFICATION', NULL, NULL, 20031002103329, 20031002103329);
INSERT INTO `external_db` VALUES (7, 'InterPro', 'http://www.ebi.ac.uk/interpro/IEntry?ac=%ID%', NULL, 'InterPro database of protein families, domains and functional sites.', 52, 'LINK_OUT', NULL, NULL, 20031002162303, 20031002162303);
INSERT INTO `external_db` VALUES (8, 'PROSITE', 'http://us.expasy.org/cgi-bin/nicedoc.pl?%ID%', NULL, 'PROSITE database of protein families and domains.', 52, 'LINK_OUT', NULL, NULL, 20031002162542, 20031002162542);

INSERT INTO `external_db` VALUES (9, 'DIP', 'http://dip.doe-mbi.ucla.edu/dip/Search.cgi?SM=3&AC=DIP:%ID%&Search2=Query+DIP&GE=&DS=&PIR=&GB=&TX=&SF=&FN=&LO=&KW=',NULL, 'Database of Interacting Proteins (DIP)', 53, 'INTERACTION_UNIFICATION', NULL, NULL, 20031002162656, 20031002162656);
INSERT INTO `external_db` VALUES (10, 'Pfam', 'http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?%ID%', NULL, 'Pfam protein families database of alignments and hidden Markov models.', 54, 'LINK_OUT', NULL, NULL, 20031002162804, 20031002162804);
INSERT INTO `external_db` VALUES (11, 'GO', 'http://www.godatabase.org/cgi-bin/amigo/go.cgi?open_1=%ID%', NULL, 'Gene Ontology (GO)', 55, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (12, 'Affymetrix', '', NULL, 'Affymetrix ID', 56, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (13, 'IntAct', 'http://www.ebi.ac.uk/intact/search/do/hvWelcome?searchString=%ID%', NULL, 'IntAct database of protein interaction data', 57, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (14, 'FlyBase', 'http://flybase.bio.indiana.edu/.bin/fbidq.html?%ID%', NULL, 'FlyBase is a comprehensive database for information on the genetics and molecular biology of Drosophila.', 58, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (15, 'PDB', 'http://www.rcsb.org/pdb/cgi/explore.cgi?pdbId=%ID%', NULL, 'Protein Data Bank (PDB)', 59, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (16, 'SGD', '', NULL, 'Saccharomyces Genome Database (SGD)', 60, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (17, 'HPRD', 'http://hprd.ibioinformatics.org/hprdId?hprdId=%ID%', NULL, 'Human Protein Reference Database (HPRD)', 61, 'INTERACTION_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (18, 'MINT', 'http://mint.bio.uniroma2.it/mint/search/db_view_interaction.php?mint_id=%ID%', NULL, 'Molecular INTeraction database (MINT)', 62, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (19, 'Reactome', 'http://www.reactome.org/cgi-bin/search?SUBMIT=1&QUERY_CLASS=DatabaseIdentifier&QUERY=SWALL:%ID%', NULL, 'Reactome: a knowledgebase of biological processes', 64, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (20, 'HUGE', 'http://www.kazusa.or.jp/huge/gfpage/%ID%/', NULL, 'HUGE Protein Database', 64, 'PROTEIN_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (21, 'Ensembl', 'http://www.ensembl.org/Homo_sapiens/textview?species=All&idx=All&q=%ID%&x=20&y=11', NULL,  'Ensembl Database', 65, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (22, 'NCBI GenBank', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', NULL, 'NCBI GenBank', 26, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (23, 'NCBI GI', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', NULL, 'NCBI GI', 27, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (24, 'RefSeq', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=%ID%', NULL, 'NCBI RefSeq', 25, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (25, 'RefSeq Protein', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', NULL, 'NCBI RefSeq Protein', 48, 'PROTEIN_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (26, 'aMAZE', '', NULL, 'aMAZE Database', 48, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (27, 'KEGG', '', NULL, 'Kyoto Encyclopedia of Genes and Genomes (KEGG)', 67, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (28, 'CAS', '', NULL, 'CAS Database', 69, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (29, 'KEGG Ligand Database', '', NULL, 'KEGG Ligand Database', 70, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (30, 'EcoCyc', '', NULL, 'EcoCyc Encyclopedia of Escherichia coli K12 Genes and Metabolism', 71, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (31, 'UM-BBD', '', NULL, 'University of Minnesota Biocatalysis/Biodegradation Database', 72, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (32, 'SWISS-MODEL', '', NULL,  'SWISS-MODEL Automated Comparative Protein Modelling Server', 73, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (33, 'OTHER PLACEHOLDER', '', NULL, 'OTHER PLACEHOLDER', 74, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (34, 'ChEBI', 'http://www.ebi.ac.uk/chebi/searchId.do?chebiId=%ID%', NULL, 'Chemical Entities of Biological Interest (ChEBI)', 76, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (35, 'EMBL', 'http://www.ebi.ac.uk/cgi-bin/emblfetch?style=html&id=%ID%&Submit=Go', NULL, 'EMBL Nucleotide Sequence Database', 77, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (36, 'COMPOUND', 'http://www.genome.ad.jp/dbget-bin/www_bget?cpd:%ID', NULL,  'KEGG COMPOUND', 78, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` VALUES (37, 'GLYCAN', 'http://www.genome.ad.jp/dbget-bin/www_bget?cpd:%ID', NULL, 'KEGG GLYCAN', 79, 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);

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
INSERT INTO `external_db_cv` VALUES (7, 1, 'SWISS-PROT/TREMBL');

#  Terms Associated with NCBI Databases (20-49)
#  Entrez Gene will soon replace LocusLink
INSERT INTO `external_db_cv` VALUES (21, 2, 'ENTREZ_GENE');
INSERT INTO `external_db_cv` VALUES (22, 2, 'LOCUS_LINK');
INSERT INTO `external_db_cv` VALUES (23, 2, 'LOCUSLINK');
INSERT INTO `external_db_cv` VALUES (47, 2, 'LOCUS-LINK');

#  Terms Associated with RefSeq
INSERT INTO `external_db_cv` VALUES (24, 24, 'REFSEQ');
INSERT INTO `external_db_cv` VALUES (25, 24, 'REF_SEQ');
INSERT INTO `external_db_cv` VALUES (48, 25, 'REF_SEQ PROTEIN');
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

# All Other Databases (50 - 999)
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
INSERT INTO `external_db_cv` VALUES (66, 26, 'AMAZE');
INSERT INTO `external_db_cv` VALUES (67, 27, 'KEGG');
INSERT INTO `external_db_cv` VALUES (68, 11, 'GENE ONTOLOGY');
INSERT INTO `external_db_cv` VALUES (69, 28, 'CAS');
INSERT INTO `external_db_cv` VALUES (70, 29, 'LIGAND');
INSERT INTO `external_db_cv` VALUES (71, 30, 'EcoO157Cyc');
INSERT INTO `external_db_cv` VALUES (72, 31, 'UMBBD');
INSERT INTO `external_db_cv` VALUES (73, 32, 'SWISS-MODEL');
INSERT INTO `external_db_cv` VALUES (74, 33, 'PSIMI');
INSERT INTO `external_db_cv` VALUES (75, 33, 'IOB');
INSERT INTO `external_db_cv` VALUES (76, 34, 'ChEBI');
INSERT INTO `external_db_cv` VALUES (77, 35, 'EMBL');
INSERT INTO `external_db_cv` VALUES (78, 36, 'COMPOUND');
INSERT INTO `external_db_cv` VALUES (79, 37, 'GLYCAN');