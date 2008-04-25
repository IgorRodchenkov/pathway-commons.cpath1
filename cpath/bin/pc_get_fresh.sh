###########################################################
# Retrieve "Fresh" Gene 2 Accession File directly from NCBI
###########################################################

echo Retrieving Fresh Gene 2 Accession file directly from NCBI
echo Deleting all previous NCBI Files
rm $CPATH_HOME/../pathway-commons/fresh/ncbi/*.*

# Get file from NCBI
echo Retrieving file from NCBI...
wget --passive-ftp -P $CPATH_HOME/../pathway-commons/fresh/ncbi ftp://ftp.ncbi.nih.gov/gene/DATA/gene2accession.gz

# Unzip
echo Unzipping NCBI file...
gunzip $CPATH_HOME/../pathway-commons/fresh/ncbi/gene2accession.gz

###########################################################
# Retrieve "Fresh" UNIPROT Flat File directly from UniProt
###########################################################
echo Retrieving Fresh UniProt Flat Files [Human] directly from UniProt
echo Deleting all previous UniProt Files
rm $CPATH_HOME/../pathway-commons/fresh/uniprot/*.*

# Get file from UniProt 
echo Retrieving file from UniProt...
wget --passive-ftp -P $CPATH_HOME/../pathway-commons/fresh/uniprot ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_human.dat.gz 

echo Unzipping UniProt file...
gunzip $CPATH_HOME/../pathway-commons/fresh/uniprot/uniprot_sprot_human.dat.gz

echo
echo Downloads Complete.
echo Check pathway-commons/fresh for new files.
echo
