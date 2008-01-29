#!/bin/sh

# setup the database
./initDb.pl
./admin.pl -f ../dbData/externalDb/pathway_commons.xml import
./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_uniprot2uniprot.txt import
./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_sp2refseq.txt import

# reactome
mv -f $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl" $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl.bak"
iconv -f ISO8859-1 -t UTF-8 $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl.bak" > $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl.iconv"
$CPATH_HOME/../pathway-commons/bin/reactome-cooker.py < $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl.iconv" > $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl"
rm -f $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl.iconv"
./admin.pl -f $CPATH_HOME/../pathway-commons/reactome/version23/"Homo\ sapiens.owl" import
mv -f $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl.bak" $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl"

# humancyc
mv -f $CPATH_HOME/../pathway-commons/humancyc/biopax.owl $CPATH_HOME/../pathway-commons/humancyc/biopax.owl.bak
$CPATH_HOME/../pathway-commons/bin/humancyc-cooker.py < $CPATH_HOME/../pathway-commons/humancyc/biopax.owl.bak > $CPATH_HOME/../pathway-commons/humancyc/biopax.owl
./admin.pl -f $CPATH_HOME/../pathway-commons/humancyc/biopax.owl import
mv -f $CPATH_HOME/../pathway-commons/humancyc/biopax.owl.bak $CPATH_HOME/../pathway-commons/humancyc/biopax.owl

# nci
./admin.pl -f $CPATH_HOME/../pathway-commons/nci/01-28-2008 import

# cellmap
./admin.pl -f $CPATH_HOME/../pathway-commons/cellmap import

# ihop
#./admin.pl -f $CPATH_HOME/../pathway-commons/ihop/sc-june-07/iHOP.owl import

# hprd
$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py hprd-cooker.py $CPATH_HOME/../pathway-commons/hprd/09-01-2007 $CPATH_HOME/../pathway-commons/hprd/09-01-2007/biopax
$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py $CPATH_HOME/../pathway-commons/hprd/09-01-2007/biopax $CPATH_HOME/../pathway-commons/hprd/09-01-2007/biopax
rm -f $CPATH_HOME/../pathway-commons/hprd/09-01-2007/biopax/*.xml
cp $CPATH_HOME/../pathway-commons/hprd/09-01-2007/db.info $CPATH_HOME/../pathway-commons/hprd/09-01-2007/biopax
./admin.pl -f $CPATH_HOME/../pathway-commons/hprd/09-01-2007/biopax import
rm -f $CPATH_HOME/../pathway-commons/hprd/09-01-2007/biopax/{*.owl,db.info}

# intact
$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py intact-cooker.py $CPATH_HOME/../pathway-commons/intact/12-14-2007 $CPATH_HOME/../pathway-commons/intact/12-14-2007/biopax
$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py $CPATH_HOME/../pathway-commons/intact/12-14-2007/biopax $CPATH_HOME/../pathway-commons/intact/12-14-2007/biopax
rm -f $CPATH_HOME/../pathway-commons/intact/12-14-2007/biopax/*.xml
for i in $CPATH_HOME/../pathway-commons/intact/12-14-2007/biopax/*.owl
do
	iconv -f ISO-8859-1 -t UTF-8 $i > $i.iconv;
	mv $i.iconv $i
done
cp $CPATH_HOME/../pathway-commons/intact/12-14-2007/db.info $CPATH_HOME/../pathway-commons/intact/12-14-2007/biopax
./admin.pl -f $CPATH_HOME/../pathway-commons/intact/12-14-2007/biopax import
rm -f $CPATH_HOME/../pathway-commons/intact/12-14-2007/biopax/{*.owl,db.info}

# mint
$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-cooker.py mint-cooker.py $CPATH_HOME/../pathway-commons/mint/12-21-2007 $CPATH_HOME/../pathway-commons/mint/12-21-2007/biopax
$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py $CPATH_HOME/../pathway-commons/mint/12-21-2007/biopax $CPATH_HOME/../pathway-commons/mint/12-21-2007/biopax
rm -f $CPATH_HOME/../pathway-commons/mint/12-21-2007/biopax/*.xml
cp $CPATH_HOME/../pathway-commons/mint/12-21-2007/db.info $CPATH_HOME/../pathway-commons/mint/12-21-2007/biopax
./admin.pl -f $CPATH_HOME/../pathway-commons/mint/12-21-2007/biopax import
rm -f $CPATH_HOME/../pathway-commons/mint/12-21-2007/biopax/{*.owl,db.info}

# fetch publication references
./admin.pl pop_ref

# create lucene index
./admin.pl index
