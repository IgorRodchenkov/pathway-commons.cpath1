# phpMyAdmin MySQL-Dump
# version 2.5.0
# http://www.phpmyadmin.net/ (download page)
#
# Host: localhost
# Generation Time: Sep 22, 2003 at 03:16 PM
# Server version: 4.0.12
# PHP Version: 4.1.2
# Database : `grid`
# --------------------------------------------------------
#
# Dumping data for table `orf_info`
#

use cpath;
truncate table import;
truncate table external_db;
truncate table external_db_cv;
truncate table external_link;
truncate table cpath;
truncate table internal_link;

#
#  Insert data into table 'external_db'
#

INSERT INTO `external_db` VALUES (1, 'Swiss-Prot', 'http://us.expasy.org/cgi-bin/niceprot.pl?%ID%', 'Swiss-Prot is a curated protein sequence database which strives to provide a high level of annotation, a minimal level of redundancy and high level of integration with other databases.', NULL, NULL, 20031002162924, 20031002102704);
INSERT INTO `external_db` VALUES (2, 'PIR', 'http://pir.georgetown.edu/cgi-bin/nbrfget?uid=%ID%', 'The Protein Information Resource (PIR), located at Georgetown University Medical Center (GUMC), is an integrated public bioinformatics resource that supports genomic and proteomic research and scientific studies.', NULL, NULL, 20031002103329, 20031002103329);
INSERT INTO `external_db` VALUES (3, 'RefSeq', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=Protein&dopt=GenPept&list_uids=%ID%', 'The Reference Sequence (RefSeq) collection aims to provide a comprehensive, integrated, non-redundant set of sequences, including genomic DNA, transcript (RNA), and protein products, for major research organisms.', NULL, NULL, 20031002163800, 20031002103655);
INSERT INTO `external_db` VALUES (4, 'InterPro', 'http://www.ebi.ac.uk/interpro/IEntry?ac=%ID%', ' InterPro is a database of protein families, domains and functional sites in which identifiable features found in known proteins can be applied to unknown protein sequences.', NULL, NULL, 20031002162303, 20031002162303);
INSERT INTO `external_db` VALUES (5, 'PROSITE', 'http://us.expasy.org/cgi-bin/nicedoc.pl?%ID%', 'Database of protein families and domains', NULL, NULL, 20031002162542, 20031002162542);
INSERT INTO `external_db` VALUES (6, 'DIP', 'http://dip.doe-mbi.ucla.edu/dip/Search.cgi?SM=3&AC=%ID%&Search2=Query+DIP', 'Database of Interacting Proteins', NULL, NULL, 20031002162656, 20031002162656);
INSERT INTO `external_db` VALUES (7, 'Pfam', 'http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?%ID%', 'Protein families database of alignments and HMMs.', NULL, NULL, 20031002162804, 20031002162804);
INSERT INTO `external_db` VALUES (8, 'Entrez GI', 'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=protein&list_uids=%ID%&dopt=GenPept&term=%ID%&qty=1', 'NCBI Entrez System', NULL, NULL, 20031002163709, 20031002163709);

#
#  Insert data into table 'external_db_cv'
#

INSERT INTO `external_db_cv` VALUES (6, 1, 'SWISS-PROT');
INSERT INTO `external_db_cv` VALUES (5, 1, 'SWP');
INSERT INTO `external_db_cv` VALUES (12, 1, 'SWISS-PROT');
INSERT INTO `external_db_cv` VALUES (11, 1, 'SWP');
INSERT INTO `external_db_cv` VALUES (13, 2, 'PIR');
INSERT INTO `external_db_cv` VALUES (14, 1, 'SWP');
INSERT INTO `external_db_cv` VALUES (15, 1, 'SWISS-PROT');
INSERT INTO `external_db_cv` VALUES (16, 3, 'RefSeq');
INSERT INTO `external_db_cv` VALUES (17, 4, 'InterPro');
INSERT INTO `external_db_cv` VALUES (18, 5, 'PROSITE');
INSERT INTO `external_db_cv` VALUES (19, 6, 'DIP');
INSERT INTO `external_db_cv` VALUES (20, 7, 'Pfram');
INSERT INTO `external_db_cv` VALUES (21, 8, 'Entrez GI');
INSERT INTO `external_db_cv` VALUES (22, 8, 'GI');
INSERT INTO `external_db_cv` VALUES (23, 1, 'Swiss-Prot');

use grid;
truncate table orf_info;
truncate table interactions;

INSERT INTO `orf_info` VALUES (1662, 'YER006W', ';NUG1;', 'NUclear GTPase', ';GO:0005554;', ';GO:0000004;', ';GO:0005634;GO:0005730;', 'molecular_function unknown;', 'biological_process unknown;', 'nucleus;nucleolus;', ';GO:0000004;', ';biological_process unknown;', ';603598;6320842;AAB64539.1;CA1803;IPR002917;NP_010921.1;P40010;S50464;U18778.1;YER006W;YER006W;', ';Entrez GI;RefSeq GI;Entrez Protein_ID;CandidaDB;InterPro;RefSeq Version;SWP;PIR;GenBank DNA Version;MIPS;MIPS;', 'N', '', '');
INSERT INTO `orf_info` VALUES (6065, 'YPL211W', ';NIP7;', 'Nip7p is required for 60S ribosome subunit biogenesis', ';GO:0005515;', ';GO:0000027;GO:0006364;', ';GO:0005730;GO:0005842;', 'protein binding;', 'ribosomal large subunit assembly and maintenance;rRNA processing;', 'nucleolus;cytosolic large ribosomal subunit  sensu Eukarya;', ';GO:0016043;GO:0008151;GO:0006396;GO:0007046;GO:0006350;GO:0008152;', ';cell organization and biogenesis;cell growth and/or maintenance;RNA processing;ribosome biogenesis;transcription;metabolism;', ';1370438;6325045;CA2900;CAA97926.1;NP_015113.1;Q08962;S65230;YPL211W;YPL211W;Z73567.1;', ';Entrez GI;RefSeq GI;CandidaDB;Entrez Protein_ID;RefSeq Version;SWP;PIR;MIPS;MIPS;GenBank DNA Version;', 'N', '', '');
INSERT INTO `orf_info` VALUES (3479, 'YKL009W', ';MRT4;', 'mRna turnover 4', ';GO:0005554;', ';GO:0042273;', ';GO:0008372;', 'molecular_function unknown;', 'ribosomal large subunit biogenesis;', 'cellular_component unknown;', ';GO:0007046;GO:0016043;GO:0008151;', ';ribosome biogenesis;cell organization and biogenesis;cell growth and/or maintenance;', ';485985;6322843;CA2283;CAA81844.1;IPR001790;NP_012916.1;P33201;S30013;S53418.1;YKL009W;YKL009W;Z28009.1;', ';Entrez GI;RefSeq GI;CandidaDB;Entrez Protein_ID;InterPro;RefSeq Version;SWP;PIR;GenBank DNA Version;MIPS;MIPS;GenBank DNA Version;', 'N', '', '');
INSERT INTO `orf_info` VALUES (4854, 'YNL002C', ';RLP7;RPL7;', 'Significant sequence similarity to RPL7B, but neither can functionally replace the other. Does not correspond to any ribosomal component identified so far, based on its biochemical features', ';GO:0019843;', ';GO:0030489;GO:0042273;', ';GO:0005730;', 'rRNA binding;', 'processing of 27S pre-rRNA;ribosomal large subunit biogenesis;', 'nucleolus;', ';GO:0006396;GO:0007046;GO:0006350;GO:0008152;GO:0016043;GO:0008151;', ';RNA processing;ribosome biogenesis;transcription;metabolism;cell organization and biogenesis;cell growth and/or maintenance;', ';1301814;6324326;CA3102;CAA95861.1;IPR000517;L19167.1;NP_014396.1;P40693;S38194;X77114.1;YNL002C;YNL002C;Z71278.1;', ';Entrez GI;RefSeq GI;CandidaDB;Entrez Protein_ID;InterPro;GenBank DNA Version;RefSeq Version;SWP;PIR;GenBank DNA Version;MIPS;MIPS;GenBank DNA Version;', 'N', '', '');
INSERT INTO `orf_info` VALUES (2680, 'YHR052W', ';CIC1;NSA3;', 'Core interacting component 1', ';GO:0030400;', ';GO:0030163;', ';GO:0005837;', 'protease substrate recruitment factor;', 'protein catabolism;', '26S proteasome;', ';GO:0030163;GO:0008152;GO:0008151;', ';protein catabolism;metabolism;cell growth and/or maintenance;', ';488163;6321843;AAB68898.1;CA0020;NP_011919.1;P38779;S46729;U00062.1;YHR052W;YHR052W;', ';Entrez GI;RefSeq GI;Entrez Protein_ID;CandidaDB;RefSeq Version;SWP;PIR;GenBank DNA Version;MIPS;MIPS;', 'N', '', '');
INSERT INTO `orf_info` VALUES (710, 'YCR072C', 'NONE', 'NONE', ';GO:0005554;', ';GO:0000004;', 'NONE', 'molecular_function unknown;', 'biological_process unknown;', 'NONE', ';GO:0000004;', ';biological_process unknown;', ';10383804;1907211;CA0328;CAA42270.1;IPR001680;NP_009997.2;P25382;S19487;X59720.2;YCR072C;YCR072C;', ';RefSeq GI;Entrez GI;CandidaDB;Entrez Protein_ID;InterPro;RefSeq Version;SWP;PIR;GenBank DNA Version;MIPS;MIPS;', 'N', '', '');
INSERT INTO `orf_info` VALUES (4810, 'YMR290C', ';HAS1;', 'Helicase Associated with SET1', ';GO:0003724;', ';GO:0000004;', ';GO:0005635;GO:0005730;', 'RNA helicase;', 'biological_process unknown;', 'nuclear membrane;nucleolus;', ';GO:0000004;', ';biological_process unknown;', ';530347;6323947;CA0554;CAA56799.1;IPR000629;IPR001410;IPR001650;NP_014017.1;Q03532;S47451;U72149.1;X80836.1;YMR290C;YMR290C;', ';Entrez GI;RefSeq GI;CandidaDB;Entrez Protein_ID;InterPro;InterPro;InterPro;RefSeq Version;SWP;PIR;GenBank DNA Version;GenBank DNA Version;MIPS;MIPS;', 'N', '', '');
INSERT INTO `orf_info` VALUES (5999, 'YPL146C', 'NONE', 'NONE', ';GO:0005554;', ';GO:0000004;', ';GO:0005634;GO:0005730;', 'molecular_function unknown;', 'biological_process unknown;', 'nucleus;nucleolus;', ';GO:0000004;', ';biological_process unknown;', ';1370312;6325111;CA4575;CAA97850.1;NP_015179.1;Q12080;S65157;U43703.1;X96770.1;YPL146C;YPL146C;Z73502.1;', ';Entrez GI;RefSeq GI;CandidaDB;Entrez Protein_ID;RefSeq Version;SWP;PIR;GenBank DNA Version;GenBank DNA Version;MIPS;MIPS;GenBank DNA Version;', 'N', '', '');
INSERT INTO `orf_info` VALUES (1110, 'YDR101C', ';ARX1;', 'NONE', ';GO:0005554;', ';GO:0000004;', 'NONE', 'molecular_function unknown;', 'biological_process unknown;', 'NONE', ';GO:0000004;', ';biological_process unknown;', ';6320306;633637;AQ501644.1;CA5051;CAA87677.1;NP_010386.1;Q03862;S51252;YDR101C;YDR101C;Z47746.1;', ';RefSeq GI;Entrez GI;GenBank DNA Version;CandidaDB;Entrez Protein_ID;RefSeq Version;SWP;PIR;MIPS;MIPS;GenBank DNA Version;', 'N', '', '');
INSERT INTO `orf_info` VALUES (5945, 'YPL093W', ';NOG1;', 'Nucleolar G-protein 1; LPG15w  working nomenclature', ';GO:0003924;', ';GO:0000004;', ';GO:0005730;', 'GTPase;', 'biological_process unknown;', 'nucleolus;', ';GO:0000004;', ';biological_process unknown;', ';1151233;6325164;AAB68206.1;CA5682;NP_015232.1;Q02892;S61973;U43281.1;YPL093W;YPL093W;', ';Entrez GI;RefSeq GI;Entrez Protein_ID;CandidaDB;RefSeq Version;SWP;PIR;GenBank DNA Version;MIPS;MIPS;', 'N', '', '');
INSERT INTO `orf_info` VALUES (2370, 'YGR103W', ';NOP7;YPH1;', 'Nucleolar protein present in purified ribosome assembly intermediates. Required for rRNA processing; required for essential steps leading to synthesis of 60S ribosomal subunits.', ';GO:0005554;', ';GO:0042273;', ';GO:0005634;GO:0005730;', 'molecular_function unknown;', 'ribosomal large subunit biogenesis;', 'nucleus;nucleolus;', ';GO:0007046;GO:0016043;GO:0008151;', ';ribosome biogenesis;cell organization and biogenesis;cell growth and/or maintenance;', ';1323159;6321540;CA4326;CAA97106.1;IPR001357;NP_011617.1;P53261;S64410;YGR103W;YGR103W;Z72888.1;', ';Entrez GI;RefSeq GI;CandidaDB;Entrez Protein_ID;InterPro;RefSeq Version;SWP;PIR;MIPS;MIPS;GenBank DNA Version;', 'N', '', '');

#
# Insert data into table `interactions`
#

INSERT INTO `interactions` VALUES (1, '1662', '6065', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (2, '1662', '3479', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (3, '1662', '4854', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (4, '1662', '2680', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (5, '1662', '710', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (6, '1662', '4810', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (7, '1662', '5999', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (8, '1662', '1110', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (9, '1662', '5945', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);
INSERT INTO `interactions` VALUES (10, '1662', '2370', 'Affinity Precipitation', 'Bassler et al', 'Not Reported', 'Not Reported', 'AB', ';11583615;', 'F', NULL);

SELECT COUNT(*) FROM interactions;
SELECT COUNT(*) FROM orf_info;
