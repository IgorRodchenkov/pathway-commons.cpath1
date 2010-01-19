#!/bin/sh

# Log Progress
function logProgress {
	echo "$1 [`date`]"
        echo "$1 [`date`]" >> pc_progress.txt
}

FRESH_HOME=$CPATH_HOME/../pathway-commons/fresh

# If $FRESH_HOME/uniprot/biopax already exists, delete it and all its content
if [ -d $FRESH_HOME/uniprot/biopax ]
then
	echo "$FRESH_HOME/uniprot/biopax directory exists"
	echo "Deleting all biopax files in $FRESH_HOME/uniprot/biopax"
	rm -fv $FRESH_HOME/uniprot/biopax/*.*
	echo "Deleting directory:  $FRESH_HOME/uniprot/biopax"
	rmdir $FRESH_HOME/uniprot/biopax
fi
echo "Making directory:  $FRESH_HOME/uniprot/biopax"
mkdir $FRESH_HOME/uniprot/biopax

logProgress "Converting Human UniProt Files to BioPAX"
./uniprot2biopax.pl 2 $FRESH_HOME/uniprot/uniprot_sprot_human.dat

logProgress "Converting Rodent UniProt Files to BioPAX"
./uniprot2biopax.pl 2 $FRESH_HOME/uniprot/uniprot_sprot_rodents.dat

logProgress "Converting Fungi UniProt Files to BioPAX"
./uniprot2biopax.pl 2 $FRESH_HOME/uniprot/uniprot_sprot_fungi.dat

mv $FRESH_HOME/uniprot/*.owl $FRESH_HOME/uniprot/biopax
cp $FRESH_HOME/uniprot/db.info $FRESH_HOME/uniprot/biopax

logProgress "Importing UniProt BioPax Files"
./admin.pl -f $FRESH_HOME/uniprot/biopax import_uniprot_annotation
