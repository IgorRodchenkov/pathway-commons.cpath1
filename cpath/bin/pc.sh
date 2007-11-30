# setup the database
./initDb.pl
./admin.pl -f ../dbData/externalDb/pathway_commons.xml import
./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_sp2refseq.txt import

# reactome
mv -f $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.owl" $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.bak.owl"
$CPATH_HOME/../pathway-commons/bin/reactome-cooker.py < $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.bak.owl" > $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.owl"
./admin.pl -f $CPATH_HOME/../pathway-commons/reactome/version21/"Homo\ sapiens.owl" import
mv -f $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.bak.owl" $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.owl"

# humancyc
./admin.pl -f $CPATH_HOME/../pathway-commons/humancyc import

# nci
./admin.pl -f $CPATH_HOME/../pathway-commons/nci/ import

# cellmap
./admin.pl -f $CPATH_HOME/../pathway-commons/cellmap import

# ihop
#./admin.pl -f $CPATH_HOME/../pathway-commons/ihop/sc-june-07/iHOP.owl import

# hprd
$CPATH_HOME/../pathway-commons/bin/hprd-cooker.py < $CPATH_HOME/../pathway-commons/hprd/09-01-07/HPRD_SINGLE_PSIMI_090107.xml > $CPATH_HOME/../pathway-commons/hprd/09-01-07/HPRD_SINGLE_PSIMI_090107.xml.cooked
$CPATH_HOME/../pathway-commons/bin/psi-mi-converter.sh $CPATH_HOME/../pathway-commons/hprd/09-01-07/HPRD_SINGLE_PSIMI_090107.xml.cooked $CPATH_HOME/../pathway-commons/hprd/09-01-07/biopax/HPRD_SINGLE_PSIMI_090107.owl
./admin.pl -f $CPATH_HOME/../pathway-commons/hprd/09-01-07/biopax/HPRD_SINGLE_PSIMI_090107.owl import

# intact
$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py $CPATH_HOME/../pathway-commons/intact/10-27-2007 $CPATH_HOME/../pathway-commons/intact/10-27-2007/biopax
./admin.pl -f $CPATH_HOME/../pathway-commons/intact/10-27-2007/biopax import

# mint
$CPATH_HOME/../pathway-commons/bin/psi-mi-batch-converter.py $CPATH_HOME/../pathway-commons/mint/07-01-2007 $CPATH_HOME/../pathway-commons/mint/07-01-2007/biopax
./admin.pl -f $CPATH_HOME/../pathway-commons/mint/07-01-2007/biopax import

# fetch publication references
./admin.pl pop_ref

# create lucene index
./admin.pl index
