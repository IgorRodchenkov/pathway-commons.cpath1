Directory Structure:

For each data format (as described below), we provide two directories:

by_source: one file per data source, e.g. one file for Reactome, one for 
NCI-Nature, etc.  by_organism: one file per organism, e.g. one file for human, 
one for mouse, etc.

If you have any questions, please contact us at:

pc-info@pathwaycommons.org


Pathway Commons Data is available in the following file formats:

BioPAX
------

Complete BioPAX (level 2) dump of all pathways and interactions within Pathway 
Commons.

Pathway Commons Gene Set Format
-------------------------------

Similar to the MSigDB format, except that all participants will be micro-encoded
 with multiple identifiers. For example, each participant would be specified as: 
CPATH_ID:RECORD_TYPE:NAME:UNIPROT_ACCESION:GENE_SYMBOL:ENTREZ_GENE_ID. Also 
available for all explicit pathways within Pathway Commons, e.g. all pathways 
from Reactome, Cell Map, and NCI-Nature.  This file format ensures that a gene set 
for a given organism should only contain genes from that species.  Therefore some genes 
from these gene sets are removed.  This format is available for all explicit pathways 
within Pathway Commons, e.g. all pathways from Reoctome, Cell Map, and NCI_Nature.

MSigDB GMT
----------

Gene Matrix Transposed file format (*.gmt) Format: This is the main tab-delimited 
file format specified by the Broad Molecular Signature Database. Each pathway is 
specified on one line. All participants in the pathway are specified as official 
gene symbols (if an official gene symbol is not available, participant will not 
be exported).   We have ensured that all participants for a pathway must come
from the same species as the pathway.  Therefore some participants from these 
pathways are removed.  Exporting to the MSigDB format will enable computational 
biologists to use pathway commons data within gene set enrichment algorithms, 
such as GSEA. Available for all explicit pathways within Pathway Commons, e.g. 
all pathways from Reactome, Cell Map, and NCI-Nature. Full data format details 
are available at: Broad GSEA Wiki, 
http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats.


SIF
---

This is the Simple Interaction Format - SIF, used by Cytoscape:
http://cytoscape.org/cgi-bin/moin.cgi/Cytoscape_User_Manual/Network_Formats.  
All participants will be specified as GENE_SYMBOL (if an official gene symbol is
 not available for all members of the interaction, the interaction will not be 
exported - this is why fewer species may be listed here as compared to other file 
formats). This format is available for all pathways and interactions within 
Pathway Commons.

Tab Delimited Network
---------------------

Tab Delimited Network: Similar to the basic SIF export, except that each 
snapshot will be specified with two files.  The first file will be SIF (using 
CPATH_ID instead of GENE_SYMBOL) plus edge attributes. Current edge attributes 
consist of the Participant-A GENE_SYMBOL, Participant-B GENE_SYMBOL, interaction 
data source and PubMed ID.  The second file will be participant CPATH_ID 
followed by node attributes.  Current node attributes consist of GENE_SYMBOL, 
UNIPROT_ACCESSION, ENTREZ_GENE_ID, CHEBI_ID, NODE_TYPE,  and Organism (NCBI 
taxonomy id).  If an attribute cannot be determined, "NOT SPECIFIED" will be 
used. This format is suitable for Cytoscape - Attribute Table import.  To 
prevent an unsuccessful import into Cytoscape due to missing attribute values, 
users should specify during import that all columns are strings.  This format 
is available for all pathways and interactions within Pathway Commons.
