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
# Retrieve "Fresh" BioGRID PSI25 directly from BioGRID
###########################################################
echo Retrieving Fresh BioGRID PSI25 directly from BioGRID
echo Deleting all previous BioGRID Files
if [ -d $FRESH_HOME/biogrid ]; then
	rm -v $FRESH_HOME/biogrid/*
else 
	mkdir $FRESH_HOME/biogrid
fi

#echo -n "Enter file to retrieve from BioGRID, e.g., BIOGRID-ORGANISM-2.0.60.psi25.zip:  "
#read biogrid_file
biogrid_file=BIOGRID-ORGANISM-3.1.76.psi25.zip

# Get file from BioGRID
echo Retrieving file $biogrid_file from BioGRID...
wget -O $biogrid_file http://thebiogrid.org/downloads/archives/Release%20Archive/BIOGRID-3.1.76/$biogrid_file
mv $biogrid_file $FRESH_HOME/biogrid

#echo Unzipping BioGRID file...
unzip -d $FRESH_HOME/biogrid $FRESH_HOME/biogrid/$biogrid_file
rm -v $FRESH_HOME/biogrid/$biogrid_file

###########################################################
# Retrieve "Fresh" HPRD PSI25 directly from HPRD
# note single file causes problems with intact psi-mi lib, use multiple file version
###########################################################
echo Retrieving Fresh HPRD PSI25 directly from HPRD
echo Deleting all previous HPRD Files
rm -v $FRESH_HOME/hprd/*

#echo -n "Enter file to retrieve from HPRD without the extension, e.g., HPRD_SINGLE_PSIMI_070609:  "
#read hprd_file
hprd_file=HPRD_SINGLE_PSIMI_041310.xml.tar.gz

# Get file from HPRD
echo Retrieving file $hprd_file from HPRD...
#wget -P $FRESH_HOME/hprd http://www.hprd.org/edownload/$hprd_file
wget -P $FRESH_HOME/hprd http://www.hprd.org/RELEASE9/$hprd_file

echo Unzipping HPRD file...
tar -xvzf $FRESH_HOME/hprd/$hprd_file -C $FRESH_HOME/hprd
# use following if single file is used
#find $FRESH_HOME/hprd -name *.xml | xargs -i% mv % .
#rm -rf $FRESH_HOME/hprd/PSIMI_XML
rm -v $FRESH_HOME/hprd/*.gz


###########################################################
# Retrieve "Fresh" HumanCyc BioPAX directly from HumanCyc
###########################################################
echo Retrieving Fresh HumanCyc BioPAX directly from HumanCyc
echo Deleting all previous HumanCyc Files
rm -v $FRESH_HOME/humancyc/*

# Get file from HumanCyc
echo Retrieving file from HumanCyc...
wget -P $FRESH_HOME/humancyc http://brg.ai.sri.com/ecocyc/dist/flatfiles-52983746/human.zip

echo Unzipping HumanCyc file...
unzip -j -d $FRESH_HOME/humancyc $FRESH_HOME/humancyc/human.zip
find $FRESH_HOME/humancyc -type f ! -name "biopax-level2.owl" -exec rm -f {} \;

#!/bin/sh
FRESH_HOME=$CPATH_HOME/../pathway-commons/fresh

###########################################################
# Retrieve "Fresh" MetaCyc BioPAX directly from MetaCyc
###########################################################
echo Retrieving Fresh MetaCyc BioPAX directly from MetaCyc
echo Deleting all previous MetaCyc Files
rm -v $FRESH_HOME/metacyc/*

# Get file from MetaCyc
echo Retrieving file from MetaCyc...
wget -P $FRESH_HOME/metacyc http://brg.ai.sri.com/ecocyc/dist/flatfiles-52983746/meta.zip

echo Unzipping MetaCyc file...
unzip -j -d $FRESH_HOME/metacyc $FRESH_HOME/metacyc/meta.zip
find $FRESH_HOME/metacyc -type f ! -name "biopax-level2.owl" -exec rm -f {} \;

#!/bin/sh
FRESH_HOME=$CPATH_HOME/../pathway-commons/fresh

###########################################################
# Retrieve "Fresh" SBCNY BioPAX directly from SBCNY
###########################################################
echo Retrieving Fresh SBCNY BioPAX directly from SBCNY
echo Deleting all previous SBCNY Files
rm -v $FRESH_HOME/sbcny/*

# Get file from SBCNY
echo Retrieving files from SBCNY...
wget -P $FRESH_HOME/sbcny http://www.sbcny.org/datasets/neuronal_signaling.owl
wget -P $FRESH_HOME/sbcny http://www.sbcny.org/datasets/presynaptome.owl

###########################################################
# Retrieve "Fresh" Nature PID BioPAX directly from NCI
###########################################################
echo Retrieving Fresh Nature PID BioPAX directly from NCI
echo Deleting all previous Nature PID Files
rm -v $FRESH_HOME/nci/*

# Get files from NCI
echo Retrieving files from NCI...
wget --passive-ftp -P $FRESH_HOME/nci ftp://ftp1.nci.nih.gov/pub/PID/BioPAX_Level_2/NCI-Nature_Curated.bp2.owl.gz

echo Unzipping Nature-PID files...
gunzip $FRESH_HOME/nci/*.gz

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
echo -n "Enter version number for BioGRID:  "
read version
echo -n "Enter date for BioGRID (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/biogrid/db.info
db_name=BIOGRID
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo -n "Enter version number for HPRD:  "
read version
echo -n "Enter date for HPRD (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/hprd/db.info
db_name=HPRD
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo -n "Enter version number for HumanCyc:  "
read version
echo -n "Enter date for HumanCyc (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/humancyc/db.info
db_name=HUMANCYC
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo -n "Enter version number for MetaCyc:  "
read version
echo -n "Enter date for MetaCyc (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/metacyc/db.info
db_name=METACYC
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo -n "Enter version number for SBCNY:  "
read version
echo -n "Enter date for SBCNY (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/sbcny/db.info
db_name=SBCNY
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo -n "Enter version number for Nature PID:  "
read version
echo -n "Enter date for Nature PID (MM/DD/YYYY):  "
read date
cat << END > $FRESH_HOME/nci/db.info
db_name=NCI_NATURE
db_snapshot_version=$version
db_snapshot_date=$date
END

echo
echo Downloads Complete.
echo Check pathway-commons/fresh for new files.
echo
