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

use db_name__value;
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
truncate table external_db_snapshot;

#
#  Insert data into table 'external_db'
#

INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (1, 'UniProt', 'http://www.uniprot.org/entry/%ID%', NULL, 'Universal Protein Resource (UniProt)', 'PROTEIN_UNIFICATION', NULL, NULL, 20031002162924, 20031002102704);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (2, 'Entrez Gene', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=%ID%', NULL, 'NCBI Entrez Gene', 'LINK_OUT', NULL, NULL, 20031002163800, 20031002103655);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (3, 'PubMed', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=%ID%&dopt=Abstract', NULL, 'NCBI PubMed', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (4, 'UniGene', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=unigene&cmd=search&term=%ID%', NULL, 'NCBI UniGene Database', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (5, 'OMIM', 'http://www.ncbi.nlm.nih.gov/entrez/dispomim.cgi?id=%ID%', NULL, 'Online Mendelian Inheritance in Man (OMIM)', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (6, 'PIR', 'http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%ID%', NULL, 'The Protein Information Resource (PIR)', 'PROTEIN_UNIFICATION', NULL, NULL, 20031002103329, 20031002103329);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (7, 'InterPro', 'http://www.ebi.ac.uk/interpro/IEntry?ac=%ID%', NULL, 'InterPro database of protein families, domains and functional sites.', 'LINK_OUT', NULL, NULL, 20031002162303, 20031002162303);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (8, 'PROSITE', 'http://us.expasy.org/cgi-bin/nicedoc.pl?%ID%', NULL, 'PROSITE database of protein families and domains.', 'LINK_OUT', NULL, NULL, 20031002162542, 20031002162542);

INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (9, 'DIP', 'http://dip.doe-mbi.ucla.edu/dip/Search.cgi?SM=3&AC=DIP:%ID%&Search2=Query+DIP&GE=&DS=&PIR=&GB=&TX=&SF=&FN=&LO=&KW=',NULL, 'Database of Interacting Proteins (DIP)', 'INTERACTION_PATHWAY_UNIFICATION', NULL, NULL, 20031002162656, 20031002162656);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (10, 'Pfam', 'http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?%ID%', NULL, 'Pfam protein families database of alignments and hidden Markov models.', 'LINK_OUT', NULL, NULL, 20031002162804, 20031002162804);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (11, 'GO', 'http://www.godatabase.org/cgi-bin/amigo/go.cgi?view=details&search_constraint=terms&depth=0&query=%ID%', NULL, 'Gene Ontology (GO)', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (12, 'Affymetrix', '', NULL, 'Affymetrix ID', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (13, 'IntAct', 'http://www.ebi.ac.uk/intact/search/do/hvWelcome?searchString=%ID%', NULL, 'IntAct database of protein interaction data', 'INTERACTION_PATHWAY_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (14, 'FlyBase', 'http://flybase.bio.indiana.edu/.bin/fbidq.html?%ID%', NULL, 'FlyBase is a comprehensive database for information on the genetics and molecular biology of Drosophila.', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (15, 'PDB', 'http://www.rcsb.org/pdb/cgi/explore.cgi?pdbId=%ID%', NULL, 'Protein Data Bank (PDB)', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (16, 'SGD', '', NULL, 'Saccharomyces Genome Database (SGD)', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (17, 'HPRD', 'http://hprd.org/protein/%ID%', NULL, 'Human Protein Reference Database (HPRD)', 'INTERACTION_PATHWAY_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (18, 'MINT', 'http://mint.bio.uniroma2.it/mint/search/db_view_interaction.php?mint_id=%ID%', NULL, 'Molecular INTeraction database (MINT)', 'INTERACTION_PATHWAY_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (19, 'Reactome', 'http://reactome.org/cgi-bin/eventbrowser?DB=gk_current&ID=%ID%', NULL, 'Reactome: a knowledgebase of biological processes', 'INTERACTION_PATHWAY_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (20, 'HUGE', 'http://www.kazusa.or.jp/huge/gfpage/%ID%/', NULL, 'HUGE Protein Database', 'PROTEIN_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (21, 'Ensembl', 'http://www.ensembl.org/Homo_sapiens/textview?species=All&idx=All&q=%ID%&x=20&y=11', NULL,  'Ensembl Database', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (22, 'NCBI GenBank', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', NULL, 'NCBI GenBank', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (23, 'NCBI GI', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', NULL, 'NCBI GI', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (24, 'RefSeq', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=%ID%', NULL, 'NCBI RefSeq', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (25, 'RefSeq Protein', 'http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val=%ID%', NULL, 'NCBI RefSeq Protein', 'PROTEIN_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (26, 'aMAZE', '', NULL, 'aMAZE Database', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (27, 'KEGG', '', NULL, 'Kyoto Encyclopedia of Genes and Genomes (KEGG)', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (28, 'CAS', '', NULL, 'CAS Database', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (29, 'KEGG Ligand', 'http://www.genome.jp/dbget-bin/www_bget?cpd:%ID%', NULL, 'KEGG Ligand Database', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (30, 'EcoCyc', '', NULL, 'EcoCyc Encyclopedia of Escherichia coli K12 Genes and Metabolism', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (31, 'UM-BBD', '', NULL, 'University of Minnesota Biocatalysis/Biodegradation Database', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (32, 'SWISS-MODEL', '', NULL,  'SWISS-MODEL Automated Comparative Protein Modelling Server', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (33, 'OTHER PLACEHOLDER', '', NULL, 'OTHER PLACEHOLDER', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (34, 'ChEBI', 'http://www.ebi.ac.uk/chebi/searchId.do?chebiId=%ID%', NULL, 'Chemical Entities of Biological Interest (ChEBI)', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (35, 'EMBL', 'http://www.ebi.ac.uk/cgi-bin/emblfetch?style=html&id=%ID%&Submit=Go', NULL, 'EMBL Nucleotide Sequence Database', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (36, 'COMPOUND', 'http://www.genome.ad.jp/dbget-bin/www_bget?cpd:%ID', NULL,  'KEGG COMPOUND', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (37, 'GLYCAN', 'http://www.genome.ad.jp/dbget-bin/www_bget?cpd:%ID', NULL, 'KEGG GLYCAN', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (38, 'WORMBASE', 'http://wormbase.org/db/seq/gbrowse/wormbase/?name=%ID%', NULL, 'WormBase:  The Biology and Genome of C. elegans', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (39, 'UNIPARC', 'http://www.ebi.ac.uk/cgi-bin/dbfetch?db=uniparc&id=%ID%', NULL, 'UniProt Archive (UniParc) is part of UniProt project. It is a non-redundant archive of protein sequences extracted from public databases UniProtKB/Swiss-Prot, UniProtKB/TrEMBL, PIR-PSD, EMBL, EMBL WGS, Ensembl, IPI, PDB, PIR-PSD, RefSeq, FlyBase, WormBase, H-Invitational Database, TROME database, European Patent Office proteins, United States Patent and Trademark Office proteins (USPTO) and Japan Patent Office proteins.', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (40, 'ENCODE', 'http://genome.ucsc.edu/ENCODE/', NULL, 'Encyclopedia Of DNA Elements', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (41, 'KEGG Pathway', 'http://www.genome.jp/dbget-bin/www_bget?path:%ID%', NULL, 'Kyoto Encyclopedia of Genes and Genomes (KEGG)', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (42, 'KEGG Genomes', 'http://www.genome.jp/dbget-bin/www_bget?bja:%ID%', NULL, 'Kyoto Encyclopedia of Genes and Genomes (KEGG)', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (43, 'NCBI PubChem', 'http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=%ID%', NULL, 'NCBI PubChem provides information on the biological activities of small molecules. ', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (44, 'Gene Symbol', NULL, NULL, 'Gene Symbol', 'LINK_OUT', NULL, NULL, 20031002163709, 20031002163709);
INSERT INTO `external_db` (`EXTERNAL_DB_ID`, `NAME`, `URL_PATTERN`, `SAMPLE_ID`, `DESC`, `DB_TYPE`, `PATH_GUIDE_ID`, `ICON_BLOB`, `CREATE_TIME`, `UPDATE_TIME`) VALUES (45, 'BioGRID', 'http://www.thebiogrid.org/search.php?keywords=%ID%', NULL, 'General Repository for Interaction Datasets', 'INTERACTION_PATHWAY_UNIFICATION', NULL, NULL, 20031002163709, 20031002163709);

#
#
#  Insert data into table 'external_db_cv'
#

#  Terms Associated with UNIPROT and SWISS-PROT (1-19)
#  All these terms map to the UniProt Database
INSERT INTO `external_db_cv` VALUES (1, 1, 'SWISS-PROT', 0);
INSERT INTO `external_db_cv` VALUES (2, 1, 'SWISSPROT', 0);
INSERT INTO `external_db_cv` VALUES (3, 1, 'SWISSTREMBL', 0);
INSERT INTO `external_db_cv` VALUES (4, 1, 'SWP', 0);
INSERT INTO `external_db_cv` VALUES (5, 1, 'UNIPROT', 1);
INSERT INTO `external_db_cv` VALUES (6, 1, 'SWISS-PROT/TREMBL', 0);
INSERT INTO `external_db_cv` VALUES (7, 1, 'UNIPROTKB', 0);
INSERT INTO `external_db_cv` VALUES (8, 1, 'REACTOME PROTEIN', 0);
INSERT INTO `external_db_cv` VALUES (9, 1, 'UNIPROT KNOWLEDGE BASE', 0);

#  Terms Associated with NCBI Databases (20-49)
#  Entrez Gene will soon replace LocusLink
INSERT INTO `external_db_cv` VALUES (20, 2, 'ENTREZ_GENE', 1);
INSERT INTO `external_db_cv` VALUES (21, 2, 'LOCUS_LINK', 0);
INSERT INTO `external_db_cv` VALUES (22, 2, 'LOCUSLINK', 0);
INSERT INTO `external_db_cv` VALUES (23, 2, 'LOCUS-LINK', 0);
INSERT INTO `external_db_cv` VALUES (24, 2, 'ENTREZGENE', 0);
INSERT INTO `external_db_cv` VALUES (25, 2, 'ENTREZ GENE/LOCUSLINK', 0);

#  Terms Associated with RefSeq
INSERT INTO `external_db_cv` VALUES (26, 25, 'REFSEQ',  0);
INSERT INTO `external_db_cv` VALUES (27, 25, 'REF_SEQ', 1);
INSERT INTO `external_db_cv` VALUES (28, 25, 'REF-SEQ', 0);
INSERT INTO `external_db_cv` VALUES (29, 25, 'REF_SEQ PROTEIN', 0);

#  Terms Associated with GenBank and GI
INSERT INTO `external_db_cv` VALUES (30, 22, 'GENBANK', 1);
INSERT INTO `external_db_cv` VALUES (31, 23, 'ENTREZ GI', 1);
INSERT INTO `external_db_cv` VALUES (32, 23, 'GI', 0);

#  Pub Med IDs
INSERT INTO `external_db_cv` VALUES (33, 3, 'PUBMED', 1);
INSERT INTO `external_db_cv` VALUES (34, 3, 'PMID', 0);

# Other NCBI Databses
INSERT INTO `external_db_cv` VALUES (35, 4, 'UNIGENE', 1);
INSERT INTO `external_db_cv` VALUES (36, 5, 'OMIM', 1);

# All Other Databases (50 - 999)
INSERT INTO `external_db_cv` VALUES (50, 6, 'PIR', 1);
INSERT INTO `external_db_cv` VALUES (51, 7, 'INTERPRO', 1);
INSERT INTO `external_db_cv` VALUES (52, 8, 'PROSITE', 1);
INSERT INTO `external_db_cv` VALUES (53, 9, 'DIP', 1);
INSERT INTO `external_db_cv` VALUES (54, 10, 'PFAM', 1);
INSERT INTO `external_db_cv` VALUES (55, 11, 'GO', 0);
INSERT INTO `external_db_cv` VALUES (56, 11, 'GENE ONTOLOGY', 0);
INSERT INTO `external_db_cv` VALUES (57, 11, 'GENE_ONTOLOGY', 1);

INSERT INTO `external_db_cv` VALUES (58, 12, 'AFFYMETRIX', 1);
INSERT INTO `external_db_cv` VALUES (59, 13, 'INTACT', 1);
INSERT INTO `external_db_cv` VALUES (60, 14, 'FLYBASE', 1);
INSERT INTO `external_db_cv` VALUES (61, 15, 'PDB', 1);
INSERT INTO `external_db_cv` VALUES (62, 16, 'SGD', 1);
INSERT INTO `external_db_cv` VALUES (63, 17, 'HPRD', 1);
INSERT INTO `external_db_cv` VALUES (64, 18, 'MINT', 1);
INSERT INTO `external_db_cv` VALUES (65, 19, 'REACTOME', 1);
INSERT INTO `external_db_cv` VALUES (66, 20, 'HUGE', 1);
INSERT INTO `external_db_cv` VALUES (67, 21, 'ENSEMBL', 1);
INSERT INTO `external_db_cv` VALUES (68, 26, 'AMAZE', 1);
INSERT INTO `external_db_cv` VALUES (69, 27, 'KEGG', 1);
INSERT INTO `external_db_cv` VALUES (70, 28, 'CAS', 1);
INSERT INTO `external_db_cv` VALUES (71, 29, 'LIGAND', 1);
INSERT INTO `external_db_cv` VALUES (72, 30, 'EcoO157Cyc', 1);
INSERT INTO `external_db_cv` VALUES (73, 31, 'UMBBD', 1);
INSERT INTO `external_db_cv` VALUES (74, 32, 'SWISS-MODEL', 1);
INSERT INTO `external_db_cv` VALUES (75, 33, 'PSIMI', 1);
INSERT INTO `external_db_cv` VALUES (76, 33, 'IOB', 1);
INSERT INTO `external_db_cv` VALUES (77, 34, 'ChEBI', 1);
INSERT INTO `external_db_cv` VALUES (78, 35, 'EMBL', 1);
INSERT INTO `external_db_cv` VALUES (79, 36, 'COMPOUND', 1);
INSERT INTO `external_db_cv` VALUES (80, 37, 'GLYCAN', 1);
INSERT INTO `external_db_cv` VALUES (81, 38, 'WORMBASE', 1);
INSERT INTO `external_db_cv` VALUES (82, 41, 'KEGG PATHWAY', 1);
INSERT INTO `external_db_cv` VALUES (83, 39, 'UNIPARC', 1);
INSERT INTO `external_db_cv` VALUES (84, 40, 'ENCODE', 1);
INSERT INTO `external_db_cv` VALUES (85, 30, 'ECOCYC', 1);
INSERT INTO `external_db_cv` VALUES (86, 2, 'ENTREZ GENE', 0);
INSERT INTO `external_db_cv` VALUES (87, 29, 'KEGG LIGAND', 1);
INSERT INTO `external_db_cv` VALUES (88, 42, 'KEGG GENOME', 1);
INSERT INTO `external_db_cv` VALUES (89, 42, 'KEGG GENOMES', 1);
INSERT INTO `external_db_cv` VALUES (90, 43, 'PUBCHEM COMPOUND', 1);
INSERT INTO `external_db_cv` VALUES (91, 43, 'PUBCHEM', 1);
INSERT INTO `external_db_cv` VALUES (92, 44, 'GENE_SYMBOL', 1);
INSERT INTO `external_db_cv` VALUES (93, 45, 'BIOGRID', 1);
