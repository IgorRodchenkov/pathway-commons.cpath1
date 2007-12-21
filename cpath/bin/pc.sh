# setup the database
./initDb.pl
./admin.pl -f ../dbData/externalDb/pathway_commons.xml import
./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_uniprot2uniprot.txt import
./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_sp2refseq.txt import

# reactome
mv -f $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl" $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.bak.owl"
$CPATH_HOME/../pathway-commons/bin/reactome-cooker.py < $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.bak.owl" > $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl"
./admin.pl -f $CPATH_HOME/../pathway-commons/reactome/version23/"Homo\ sapiens.owl" import
mv -f $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.bak.owl" $CPATH_HOME/../pathway-commons/reactome/version23/"Homo sapiens.owl"

# humancyc
./admin.pl -f $CPATH_HOME/../pathway-commons/humancyc import

# nci
./admin.pl -f $CPATH_HOME/../pathway-commons/nci/12-11-2007 import

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
