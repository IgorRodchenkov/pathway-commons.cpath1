###########################################################
# Retrieve "Fresh" Gene 2 Accession File directly from NCBI
###########################################################

FRESH_HOME=$CPATH_HOME/../pathway-commons/fresh
echo "Using Fresh Home:  $FRESH_HOME"
echo Retrieving Fresh Gene 2 Accession file directly from NCBI
echo Deleting all previous NCBI Files
rm -v $FRESH_HOME/ncbi/*

# Get file from NCBI
echo Retrieving file from NCBI...
wget --passive-ftp -P $FRESH_HOME/ncbi ftp://ftp.ncbi.nih.gov/gene/DATA/gene2accession.gz

# Unzip
echo Unzipping NCBI file...
gunzip $FRESH_HOME/ncbi/gene2accession.gz

###########################################################
# Retrieve "Fresh" UNIPROT Flat File directly from UniProt
###########################################################
echo Retrieving Fresh UniProt Flat Files [Human] directly from UniProt
echo Deleting all previous UniProt Files
rm -v $FRESH_HOME/uniprot/*

# Get file from UniProt 
echo Retrieving file from UniProt...
wget --passive-ftp -P $FRESH_HOME/uniprot ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_human.dat.gz 

echo Unzipping UniProt file...
gunzip $FRESH_HOME/uniprot/uniprot_sprot_human.dat.gz

###########################################################
# Retrieve "Fresh" Reactome BioPAX directly from Reactome
###########################################################
echo Retrieving Fresh Reactome BioPAX directly from Reactome
echo Deleting all previous Reactome Files
rm -v $FRESH_HOME/reactome/*

# Get file from Reactome
echo Retrieving file from Reactome...
wget --passive-ftp -P $FRESH_HOME/reactome http://www.reactome.org/download/current/biopax.zip

echo Unzipping Reactome file...
unzip -d $FRESH_HOME/reactome $FRESH_HOME/reactome/biopax.zip

echo
echo -n "Enter version number for Reactome:  "
read version
echo -n "Enter date for Reactome (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/reactome/db.info
db_name=Reactome
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo Downloads Complete.
echo Check pathway-commons/fresh for new files.
echo
