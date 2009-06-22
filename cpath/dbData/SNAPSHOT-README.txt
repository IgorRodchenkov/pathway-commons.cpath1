Pathway Commons Data Download
------------------------------

Directory Structure:

For each data format (described below), we provide two directories:

* by_source: one file per data source, e.g. one file for Reactome, one for
NCI-Nature, etc.

* by_organism: one file per organism, e.g. one file for human, one for mouse, etc.

If you have any questions, please contact us at:

pc-info@pathwaycommons.org

Pathway Commons Data is available in the following file formats:

BioPAX
------

BioPAX is a data exchange format for biological pathway data.  All pathways 
and interactions within Pathway Commons are available in BioPAX Level 2.  
For more information on the BioPAX exchange format, visit: http://www.biopax.org.

GSEA (MSigDB GMT)
-----------------

Gene Matrix Transposed file format (*.gmt) Format: This is the main tab-delimited
file format specified by the Broad Molecular Signature Database
(http://www.broad.mit.edu/gsea/msigdb/). We provide two versions of this file format.
In the first, all participants in the pathway are specified as official
gene symbols (if an official gene symbol is not available, the participant will not
be exported).   In the second, all participants are specified as Entrez Gene IDs
(if an Entrez Gene ID is not available, the participant will not be exported).  
All participants for a pathway must come from the same species as the pathway.  
Therefore some participants from cross-species pathways are removed.  Exporting 
to the MSigDB format will enable computational biologists to use pathway commons 
data within gene set enrichment algorithms, such as GSEA. Available for all 
pathways within Pathway Commons (only from pathway database sources, not 
interaction database sources). Full data format details are available at: Broad 
GSEA Wiki, http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats.

Pathway Commons Gene Set Format
-------------------------------

Similar to the MSigDB format (see above), except that all participants are
micro-encoded with multiple identifiers. Each participant is specified 
as: CPATH_ID:RECORD_TYPE:NAME:UNIPROT_ACCESION:GENE_SYMBOL:ENTREZ_GENE_ID. 
Also available for all explicit pathways within Pathway Commons (only from 
pathway database sources, not interaction database sources).  All participants 
for a pathway must come from the same species as the pathway.  Therefore some 
participants from cross-species pathways are removed.

SIF
---

Simple Interaction Format - SIF, used by Cytoscape:
http://cytoscape.org/cgi-bin/moin.cgi/Cytoscape_User_Manual/Network_Formats.  
All participants will be specified as GENE_SYMBOL. If an official gene symbol is
not available for all members of the interaction, the interaction will not be 
exported. This is why fewer species may be listed here as compared to other file 
formats. This format is available for all pathways and interactions within 
Pathway Commons.

Tab Delimited Network
---------------------

Tab Delimited Network: Similar to the basic SIF export, except that each 
export is specified with two files. Each file is tab-delimited, multi-column.  
The first file is SIF (using CPATH_ID instead of GENE_SYMBOL) plus edge 
attributes. Current edge attributes are the Participant-A GENE_SYMBOL, 
Participant-B GENE_SYMBOL, interaction data source and PubMed ID.  The second 
file contains participant CPATH_ID followed by node attributes.  Current node 
attributes are GENE_SYMBOL, UNIPROT_ACCESSION, ENTREZ_GENE_ID, CHEBI_ID, 
NODE_TYPE, and Organism (NCBI taxonomy id).  If an attribute cannot be 
determined, "NOT SPECIFIED" will be used. This format is suitable for Cytoscape 
- Attribute Table import and loading into Excel.  To prevent an unsuccessful 
import into Cytoscape due to missing attribute values, users should specify 
during import that all columns are strings.  This format is available for all 
pathways and interactions within Pathway Commons.

Availability
------------
Pathway Commons redistributes data from primary databases. Please make sure to 
cite all primary sources you use to support the curation teams that make this 
data available.  All data is made available under original license terms of the 
primary databases.
