Pathway Commons Data Download
------------------------------

Directory Structure:

For each data format (described below), we provide two directories:

* by_source: one file per data source, e.g. one file for Reactome, one
  for NCI-Nature, etc.

* by_organism: one file per organism, e.g. one file for human, one for
  mouse, etc.

If you have any questions, please contact us at:

pc-info@pathwaycommons.org

Pathway Commons Data is available in the following file formats:

BioPAX OWL (RDF/XML)
--------------------

BioPAX is the native format of Pathway Commons and offers complete
access to all the details that can be stored in the system.  This
format is ideal for users wishing to import all pathway data for an
organism into a local database, or to access specific data not
available in other formats.  Since BioPAX is defined using the standard
OWL XML language, this export can be used with RDF / OWL tools such as
reasoners or triplestores.  All pathways and interactions within
Pathway Commons are available in BioPAX Level 2 (ref BioPAX
paper).  Due to the richness of representation in BioPAX, reading and
using such a large BioPAX document requires knowledge of the format
and software development tools available for processing it, such as
Paxtools, a Java library for working with BioPAX
(http://www.biopax.org/paxtools.php).

Gene Set Enrichment Formats (GSEA - MSigDB GMT)
-----------------------------------------------

Over-representation analysis (ORA) is frequently used to assess the
statistical enrichment of known gene sets (e.g. pathways) in a
discrete or ranked list of genes.  This type of analysis is useful for
summarizing large gene lists and is commonly applied to
genomics data sets.  One popular software for analyzing ranked gene
lists is Gene Set Enrichment Analysis (GSEA).  The Gene sets used by
GSEA are stored for convenience in the Molecular Signature Database
(MSigDB) in the Gene Matrix Transposed file format (*.gmt).  This is
the main tab-delimited file format specified by the Broad Molecular
Signature Database (http://www.broad.mit.edu/gsea/msigdb/).

We provide two versions of this file format.  In the first, all
participants in the pathway are specified as official gene symbols (if
an official gene symbol is not available, the participant will not be
exported).   In the second, all participants are specified as Entrez
Gene IDs (if an Entrez Gene ID is not available, the participant will
not be exported).  All participants for a pathway must come from the
same species as the pathway.  Therefore some participants from
cross-species pathways are removed.  Exporting to the MSigDB format
will enable computational biologists to use pathway commons data
within gene set enrichment algorithms, such as GSEA.  Available for all
pathways within Pathway Commons (only from pathway database sources,
not interaction database sources). Full data format details are
available at: Broad GSEA Wiki,
http://www.broad.mit.edu/cancer/software/gsea/wiki/index.php/Data_formats.

Note:

Issues relating to Reactome gene sets due to incorrect traversal into
sub-pathways during gene set creation has been repaired.  If you have
any questions, please contact us at pc-info@pathwaycommons.org.

Pathway Commons Gene Set Format
-------------------------------

Similar to the MSigDB format (see above), except that all participants
are micro-encoded with multiple identifiers. Each participant is
specified as:
CPATH_ID:RECORD_TYPE:NAME:UNIPROT_ACCESION:GENE_SYMBOL:ENTREZ_GENE_ID.
Also available for all explicit pathways within Pathway Commons (only
from pathway database sources, not interaction database sources).  All
participants for a pathway must come from the same species as the
pathway.  Therefore some participants from cross-species pathways are
removed.

Simple Interaction Format (SIF)
-------------------------------

Many network analysis algorithms require pairwise interaction networks 
as input.  A BioPAX network often contains more complex relationships
with multiple participants, such as biochemical reactions.  To make it
easier to use all of the pathway information in Pathway Commons with
typical network analysis tools, we developed a set of rules to reduce
BioPAX interactions to pairwise relationships.  Since SIF interactions
are always binary it is not possible to fully represent all of BioPAX,
thus this translation is lossy in general.  Nonetheless, the SIF
network is useful for those applications that require pairwise
interaction input.  SIF format can be easily imported into popular
network analysis tools, like Cytoscape
(http://cytoscape.org/cgi-bin/moin.cgi/Cytoscape_User_Manual/Network_Formats).

All participants will be specified as GENE_SYMBOL.  If an official
gene symbol is not available for all members of the interaction, the
interaction will not be exported.  This is why fewer species may be
listed here as compared to other file formats.  This format does not contain
any cross-species interactions and is available for all pathways and
interactions within Pathway Commons.

Tab Delimited Network
---------------------

Similar to the basic SIF export, except that each export is specified
with two files.  Each file is tab-delimited, multi-column.  The first
file is SIF (using CPATH_ID instead of GENE_SYMBOL) plus edge
attributes.  Current edge attributes are the Participant-A GENE_SYMBOL,
Participant-B GENE_SYMBOL, interaction data source and PubMed ID.  The
second file contains participant CPATH_ID followed by node attributes.
Current node attributes are GENE_SYMBOL, UNIPROT_ACCESSION,
ENTREZ_GENE_ID, CHEBI_ID, NODE_TYPE, and Organism (NCBI taxonomy id).
If an attribute cannot be determined, "NOT_SPECIFIED" will be
used. This format is suitable for Cytoscape - Attribute Table import
and loading into Excel.  To prevent an unsuccessful import into
Cytoscape due to missing attribute values, users should specify during
import that all columns are strings.  This format is available for all
pathways and interactions within Pathway Commons.

Availability
------------

Pathway Commons redistributes data from primary databases.  Please make
sure to cite all primary sources you use to support the curation teams
that make this data available.  All data is made available under
original license terms of the primary databases.
