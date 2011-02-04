#!/bin/sh

############################################################################
# Pathway Commons Import Script
############################################################################

# Log Progress
function logProgress {
	echo "$1 [`date`]"
        echo "$1 [`date`]" >> pc_progress.txt
}

# Dependency Checks
# If any of these dependencies fail, abort.
function checkDependencies {
	logProgress "Checking Dependencies..."
	FRESH_HOME=$CPATH_HOME/../pathway-commons/fresh
	if [ ! -f $FRESH_HOME/ncbi/gene2accession ]; then
		echo "Fresh NCBI data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/uniprot/uniprot_sprot_human.dat ]; then
		echo "Fresh UniProt data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/reactome/db.info ]; then
		echo "Fresh Reactome data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/intact/db.info ]; then
		echo "Fresh Intact data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/mint/db.info ]; then
		echo "Fresh Mint data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/biogrid/db.info ]; then
		echo "Fresh BioGRID data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/hprd/db.info ]; then
		echo "Fresh HPRD data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/humancyc/db.info ]; then
		echo "Fresh HumanCyc data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/metacyc/db.info ]; then
		echo "Fresh MetaCyc data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/sbcny/db.info ]; then
		echo "Fresh SBCNY data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi

	if [ ! -f $FRESH_HOME/nci/db.info ]; then
		echo "Fresh Nature-PID data files not found!  Run pc_get_fresh.sh first. Aborting..."
		exit
	fi
}

function init {
	rm -f pc_progress.txt
	logProgress "Import started."
}

# Initializes the database
function initDB {
	./initDb.pl -f

	# Import the Pathway Commons Data Sources --> Meta Data
	logProgress "Importing Pathway Commons:  Meta Data."
	./admin.pl -f ../dbData/externalDb/pathway_commons.xml import
}

# Import the Human UniProt Protein Info
function importUniProtBackground {
	logProgress "Importing UniProt Background Information."
	./pc_import_uniprot.sh
}

function importEntrezGeneIds {
	# Create the Entrez Gene ID Mapping Files from NCBI Source
	# Currently restricted to Human and Mouse
	logProgress "Creating Entrez Gene ID Mapping Files."
	./entrez_gene.pl $CPATH_HOME/../pathway-commons/fresh/ncbi/gene2accession

	# Import the Entrez Gene Link Out Mapping Files
	logProgress "Importing Entrez Gene ID Mapping Files."
	./admin.pl -f $CPATH_HOME/../pathway-commons/fresh/ncbi/refseq_2_entrez_gene_id.txt import
	./admin.pl -f $CPATH_HOME/../pathway-commons/fresh/ncbi/uniprot_2_entrez_gene_id.txt import
}

# Import the Unification ID Mapping Files - no longer required - comes from UniProt background now
function importUnificationRefs {
	logProgress "Importing UniProt and RefSeq Mapping Files."
	./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_uniprot2uniprot.txt import
	./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_sp2refseq.txt import
}

# Load intact first, as it has broad coverage, and good protein annotations
function importIntAct {
	logProgress "Loading IntAct."
	local INTACT_HOME="$FRESH_HOME/intact"
	mkdir $INTACT_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py intact-cooker.py $INTACT_HOME $INTACT_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py 2 $INTACT_HOME/biopax $INTACT_HOME/biopax
	find $INTACT_HOME/biopax/ -name *.xml | xargs -i% rm -f %
	for i in $INTACT_HOME/biopax/*.owl
	do
		iconv -f ISO-8859-1 -t UTF-8 $i > $i.iconv;
		mv $i.iconv $i
	done
	cp $INTACT_HOME/db.info $INTACT_HOME/biopax
	./admin.pl -f $INTACT_HOME/biopax import
	rm -rfv $INTACT_HOME/biopax
}

# Then, mint, as it also has broad coverage, and good protein annotations
function importMint {
	logProgress "Loading MINT."
	local MINT_HOME="$FRESH_HOME/mint"
	mkdir $MINT_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py mint-cooker.py $MINT_HOME $MINT_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py 2 $MINT_HOME/biopax $MINT_HOME/biopax
	rm -fv $MINT_HOME/biopax/*.xml
	cp $MINT_HOME/db.info $MINT_HOME/biopax
	./admin.pl -f $MINT_HOME/biopax import
	rm -rfv $MINT_HOME/biopax
}

# Then hprd, as it also has broad coverage, and good protein annotations
# note to self:  as of 4/29/08:  HPRD incorrectly annotates some proteins as "Mammalia", but annotates
# them with Human UniPROT Accession IDs.  Lookout for OMIM/UNIPROT secondary refs that have erroneous ids, like id="-" or
# secondary refs with multiple accessions: id="Q9Y4L1,Q6IN67,A8C1Z0".
function importHPRD {
	logProgress "Loading HPRD."
	local HPRD_HOME="$FRESH_HOME/hprd"
	mkdir $HPRD_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py hprd-cooker.py $HPRD_HOME $HPRD_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py 2 $HPRD_HOME/biopax $HPRD_HOME/biopax
	find $HPRD_HOME/biopax/ -name *.xml | xargs -i% rm -f %
	cp $HPRD_HOME/db.info $HPRD_HOME/biopax
	./admin.pl -f $HPRD_HOME/biopax import
	rm -rfv $HPRD_HOME/biopax
}

function importReactome {
	logProgress "Cooking and Char Converting Reactome Human Files."
	local REACTOME_HOME="$FRESH_HOME/reactome/"
	mv -f $REACTOME_HOME/"Homo sapiens.owl" $REACTOME_HOME/Homo_sapiens.owl.bak
	iconv -f ISO8859-1 -t UTF-8 $REACTOME_HOME/Homo_sapiens.owl.bak > $REACTOME_HOME/Homo_sapiens.owl.iconv
	$CPATH_HOME/../pathway-commons/bin/reactome-cooker.py < $REACTOME_HOME/Homo_sapiens.owl.iconv > $REACTOME_HOME/Homo_sapiens_to_fix.owl
	rm -f $REACTOME_HOME/Homo_sapiens.owl.iconv
    $CPATH_HOME/../pathway-commons/bin/reactome-fix.sh $REACTOME_HOME/Homo_sapiens_to_fix.owl $REACTOME_HOME/Homo_sapiens.owl
    rm -f $REACTOME_HOME/Homo_sapiens_to_fix.owl
	logProgress "Loading Reactome."
	./admin.pl -f $REACTOME_HOME/Homo_sapiens.owl import
	rm -f $REACTOME_HOME/Homo_sapiens.owl
	mv -f $REACTOME_HOME/Homo_sapiens.owl.bak $REACTOME_HOME/"Homo sapiens.owl"
}

function importHumanCyc {
	logProgress "Loading HumanCyc."
	local HUMANCYC_HOME="$FRESH_HOME/humancyc"
	#mv -vf $HUMANCYC_HOME/biopax.owl $HUMANCYC_HOME/biopax.owl.bak
	#$CPATH_HOME/../pathway-commons/bin/humancyc-cooker.py < $HUMANCYC_HOME/biopax.owl.bak > $HUMANCYC_HOME/biopax.owl
	./admin.pl -f $HUMANCYC_HOME import
	#mv -vf $HUMANCYC_HOME/biopax.owl.bak $HUMANCYC_HOME/biopax.owl
}

function importMetaCyc {
	logProgress "Loading MetaCyc."
	local METACYC_HOME="$FRESH_HOME/metacyc"
	#mv -vf $METACYC_HOME/biopax.owl $METACYC_HOME/biopax.owl.bak
	#$CPATH_HOME/../pathway-commons/bin/metacyc-cooker.py < $METACYC_HOME/biopax.owl.bak > $METACYC_HOME/biopax.owl
	./admin.pl -f $METACYC_HOME import
	#mv -vf $METACYC_HOME/biopax.owl.bak $METACYC_HOME/biopax.owl
}

function importNci {
	logProgress "Loading NCI / Nature PID."
	local NCI_HOME="$FRESH_HOME/nci"
	./admin.pl -f $NCI_HOME import
}

function importCellMap {
	logProgress "Loading MSKCC Cell Map."
	./admin.pl -f $CPATH_HOME/../pathway-commons/cellmap import
}

function importBioGRID {
	logProgress "Loading BioGRID."
	local BIOGRID_HOME="$FRESH_HOME/biogrid"
	mkdir $BIOGRID_HOME/biopax
	#$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py biogrid-cooker.py $BIOGRID_HOME $BIOGRID_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py 2 $BIOGRID_HOME/ $BIOGRID_HOME/biopax
	rm -fv $BIOGRID_HOME/biopax/*.xml
	cp $BIOGRID_HOME/db.info $BIOGRID_HOME/biopax
	./admin.pl -f $BIOGRID_HOME/biopax/ import
	rm -rfv $BIOGRID_HOME/biopax
}

function importIMID {
	logProgress "Loading IMID."
	local SBCNY_HOME="$FRESH_HOME/sbcny"
	./admin.pl -f $SBCNY_HOME import
}

function importiHop {
	#echo "Importing iHOP file..."
	local IHOP_HOME="$CPATH_HOME/../pathway-commons/ihop/03-14-2008/"
	#$CPATH_HOME/../pathway-commons/bin/ihop-converter.sh < $IHOPE_HOME/iHOP_network_gene_chem.txt > $IHOP_HOME/biopax/iHOP.owl
	#cp $IHOP_HOME/db.info $IHOP_HOME/biopax
	#./admin.pl -f $IHOP_HOME/biopax/iHOP.owl import
	#rm -fv $IHOP_HOME/biopax/{*.owl,db.info}
}

# fetch publication references
function fetchPublications {
	logProgress "Fetching Publication References."
	./admin.pl pop_ref
}

function index {
	logProgress "Running Lucene Indexer."
	./admin.pl index
}

function neighborhoodMaps {
	logProgress "Running Neighborhood Map Precomputations."
	./admin.pl compute_neighborhood_map
}

function wrapUp {
	logProgress "Import finished."
}

init
checkDependencies

initDB

importUniProtBackground
importEntrezGeneIds

importIntAct
importMint
importHPRD

importReactome
importHumanCyc
importMetaCyc
importNci
importCellMap
importIMID

importBioGRID

fetchPublications
index
neighborhoodMaps
wrapUp
