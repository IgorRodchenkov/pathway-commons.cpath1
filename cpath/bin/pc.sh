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

# Import the Unification ID Mapping Files
function importUnificationRefs {
	logProgress "Importing UniProt and RefSeq Mapping Files."
	./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_uniprot2uniprot.txt import
	./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_sp2refseq.txt import
}

# Load intact first, as it has broad coverage, and good protein annotations
function importIntAct {
	logProgress "Loading IntAct."
	local INTACT_HOME="$CPATH_HOME/../pathway-commons/intact/12-14-2007"
	#$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py intact-cooker.py $INTACT_HOME $INTACT_HOME/biopax
	#$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py $INTACT_HOME/biopax $INTACT_HOME/biopax
	#rm -f $INTACT_HOME/biopax/*.xml
	#for i in $INTACT_HOME/biopax/*.owl
	#do
	#	iconv -f ISO-8859-1 -t UTF-8 $i > $i.iconv;
	#	mv $i.iconv $i
	#done
	#cp $INTACT_HOME/db.info $INTACT_HOME/biopax
	./admin.pl -f $INTACT_HOME/biopax import
	#rm -f $INTACT_HOME/biopax/{*.owl,db.info}
}

# Then, mint, as it also has broad coverage, and good protein annotations
function importMint {
	logProgress "Loading MINT."
	local MINT_HOME="$CPATH_HOME/../pathway-commons/mint/12-21-2007"
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py mint-cooker.py $MINT_HOME $MINT_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py $MINT_HOME/biopax $MINT_HOME/biopax
	rm -fv $MINT_HOME/biopax/*.xml
	cp $MINT_HOME/db.info $MINT_HOME/biopax
	./admin.pl -f $MINT_HOME/biopax import
	rm -vf $MINT_HOME/biopax/{*.owl,db.info}
}

# Then hprd, as it also has broad coverage, and good protein annotations
# note to self:  as of 4/29/08:  HPRD incorrectly annotates some proteins as "Mammalia", but annotates
# them with Human UniPROT Accession IDs.
function importHPRD {
	logProgress "Loading HPRD."
	local HPRD_HOME="$CPATH_HOME/../pathway-commons/hprd/09-01-2007"
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py hprd-cooker.py $HPRD_HOME $HPRD_HOME/biopax
	$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py $HPRD_HOME/biopax $HPRD_HOME/biopax
	rm -fv $HPRD_HOME/biopax/*.xml
	cp $HPRD_HOME/db.info $HPRD_HOME/biopax
	./admin.pl -f $HPRD_HOME/biopax import
	rm -fv $HPRD_HOME/biopax/{*.owl,db.info}
}

function importReactome {
	logProgress "Cooking and Char Converting Reactome Human Files."
	local REACTOME_HOME="$FRESH_HOME/reactome/"
	mv -f $REACTOME_HOME/"Homo sapiens.owl" $REACTOME_HOME/Homo_sapiens.owl.bak
	iconv -f ISO8859-1 -t UTF-8 $REACTOME_HOME/Homo_sapiens.owl.bak > $REACTOME_HOME/Homo_sapiens.owl.iconv
	$CPATH_HOME/../pathway-commons/bin/reactome-cooker.py < $REACTOME_HOME/Homo_sapiens.owl.iconv > $REACTOME_HOME/Homo_sapiens.owl
	rm -f $REACTOME_HOME/Homo_sapiens.owl.iconv
	logProgress "Loading Reactome."
	./admin.pl -f $REACTOME_HOME/Homo_sapiens.owl import
	mv -f $REACTOME_HOME/Homo_sapiens.owl.bak $REACTOME_HOME/"Homo sapiens.owl"
}

function importHumanCyc {
	logProgress "Cooking and Char Converting HumanCyc Files."
	local HUMANCYC_HOME="$CPATH_HOME/../pathway-commons/humancyc"
	mv -f $HUMANCYC_HOME/biopax.owl $HUMANCYC_HOME/biopax.owl.bak
	$CPATH_HOME/../pathway-commons/bin/humancyc-cooker.py < $HUMANCYC_HOME/biopax.owl.bak > $HUMANCYC_HOME/biopax.owl
	./admin.pl -f $HUMANCYC_HOME/biopax.owl import
	mv -f $HUMANCYC_PATH/biopax.owl.bak $HUMANCYC_PATH/biopax.owl
}

function importNci {
	logProgress "Loading NCI / Nature PID."
	./admin.pl -f $CPATH_HOME/../pathway-commons/nci/01-28-2008 import
}

function importCellMap {
	logProgress "Loading MSKCC Cell Map."
	./admin.pl -f $CPATH_HOME/../pathway-commons/cellmap import
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
	logProgress "Fetching Pulication References."
	./admin.pl pop_ref
}

function index {
	logProgress "Running Lucene Indexer"
	./admin.pl index
}

function wrapUp {
	logProgress "Import finished."
}

init
checkDependencies
initDB
importUniProtBackground
importEntrezGeneIds
importUnificationRefs
importIntAct
importHPRD
importReactome
importHumanCyc
importNci
importCellMap
fetchPublications
index
wrapUp
