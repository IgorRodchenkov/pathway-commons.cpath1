#!/bin/sh

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
echo Retrieving Fresh UniProt Flat Files [Human/Rodent] directly from UniProt
echo Deleting all previous UniProt Files
rm -v $FRESH_HOME/uniprot/*

# Get human data from UniProt
echo Retrieving human data from UniProt...
wget --passive-ftp -P $FRESH_HOME/uniprot ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_human.dat.gz

# Get rodent data from UniProt
echo Retrieving rodent data from UniProt...
wget --passive-ftp -P $FRESH_HOME/uniprot ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_rodents.dat.gz

# Get fungi data
echo Retrieving fungi data from UniProt...
wget --passive-ftp -P $FRESH_HOME/uniprot ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/uniprot_sprot_fungi.dat.gz

echo Unzipping Human UniProt file...
gunzip $FRESH_HOME/uniprot/uniprot_sprot_human.dat.gz

echo Unzipping Rodent UniProt file...
gunzip $FRESH_HOME/uniprot/uniprot_sprot_rodents.dat.gz

echo Unzipping Fungi UniProt file...
gunzip $FRESH_HOME/uniprot/uniprot_sprot_fungi.dat.gz

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

###########################################################
# Retrieve "Fresh" Intact PSI25 directly from EBI
###########################################################
echo Retrieving Fresh Intact PSI25 directly from Intact
echo Deleting all previous Intact Files
find $FRESH_HOME/intact/ -name "*.*" | xargs -i% rm -v %

# Get file from EBI
echo Retrieving file from Intact...
wget --passive-ftp -P $FRESH_HOME/intact ftp://ftp.ebi.ac.uk/pub/databases/intact/current/psi25/pmidMIF25.zip

echo Unzipping Intact file...
unzip -j -d $FRESH_HOME/intact $FRESH_HOME/intact/pmidMIF25.zip
rm -v $FRESH_HOME/intact/pmidMIF25.zip

###########################################################
# Retrieve "Fresh" Mint PSI25 directly from Mint
###########################################################
echo Retrieving Fresh Mint PSI25 directly from Mint
echo Deleting all previous Mint Files
rm -v $FRESH_HOME/mint/*

# Get file from Mint
echo Retrieving file from Mint...
wget --passive-ftp -P $FRESH_HOME/mint ftp://mint.bio.uniroma2.it/pub/release/psi/current/psi25/dataset/full.psi25.zip

echo Unzipping Mint file...
unzip -d $FRESH_HOME/mint $FRESH_HOME/mint/full.psi25.zip
rm -v $FRESH_HOME/mint/full.psi25.zip


###########################################################
# Setup db.info files 
###########################################################
echo
echo -n "Enter version number for UniProt (enter NA if you don't know the version number):  "
read version
echo -n "Enter date for UniProt (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/uniprot/db.info
db_name=UNIPROT
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo -n "Enter version number for Reactome:  "
read version
echo -n "Enter date for Reactome (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/reactome/db.info
db_name=REACTOME
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo -n "Enter version number for Intact:  "
read version
echo -n "Enter date for Intact (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/intact/db.info
db_name=INTACT
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo -n "Enter version number for Mint:  "
read version
echo -n "Enter date for Mint (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/mint/db.info
db_name=MINT
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo Downloads Complete.
echo Check pathway-commons/fresh for new files.
echo

