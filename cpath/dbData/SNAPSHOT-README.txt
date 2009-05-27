Pathway Commons Data is available in the following file formats:

MSigDB GMT
----------

Gene Matrix Transposed file format (*.gmt) Format: This is the main tab-delimited 
file format specified by the Broad Molecular Signature Database. Each pathway is 
specified on one line. All participants in the pathway are specified as official 
gene symbols (if an official gene symbol is not available, participant will not 
be exported). Exporting to the MSigDB format will enable computational 
biologists to use pathway commons data within gene set enrichment algorithms, 
such as GSEA. Available for all explicit pathways within Pathway Commons, e.g. 
all pathways from Reactome, Cell Map, and NCI-Nature. Full data format details 
are available at: Broad GSEA Wiki, 
http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats.

Pathway Commons Gene Set Format
-------------------------------

Similar to the MSigDB format, except that all participants will be micro-encoded
 with multiple identifiers. For example, each participant would be specified as: 
CPATH_ID:RECORD_TYPE:NAME:UNIPROT_ACCESION:GENE_SYMBOL:ENTREZ_GENE_ID. Also 
available for all explicit pathways within Pathway Commons, e.g. all pathways 
from Reactome, Cell Map, and NCI-Nature.

SIF
---

This is the Simple Interaction Format - SIF, used by Cytoscape:
http://cytoscape.org/cgi-bin/moin.cgi/Cytoscape_User_Manual/Network_Formats.  
All participants will be specified as GENE_SYMBOL (if an official gene symbol is
 not available for all members of the interaction, the interaction will not be 
exported). Available for all pathways and interactions in Pathway Commons.

Tab Delimited Network
---------------------

Tab Delimited Network: Similar to the basic SIF export, except that each 
snapshot will be specified with two files.  The first file will be SIF (using 
CPATH_ID instead of GENE_SYMBOL) plus edge attributes. Current edge attributes 
consist of the Participant-A GENE_SYMBOL, Participant-B GENE_SYMBOL, interaction 
data source and PubMed ID.  The second file will be participant CPATH_ID followed
by node attributes.  Current node attributes consist of GENE_SYMBOL,
UNIPROT_ACCESSION, ENTREZ_GENE_ID and Organism (ncbi tax id).  If an attribute
cannot be determined, "NOT SPECIFIED" will be used.

BioPAX
------

Complete BioPAX dump of all pathways and interactions.


Directory Structure:

For each data format, we provide two directories:

by_source: one file per data source, e.g. one file for Reactome, one for 
NCI-Nature, etc.  by_organism: one file per organism, e.g. one file for human, 
one for mouse, etc.

If you have any questions, please contact us at:

pc-info@pathwaycommons.org
