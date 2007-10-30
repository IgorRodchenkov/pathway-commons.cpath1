./initDb.pl
./admin.pl -f ../dbData/externalDb/pathway_commons.xml import
./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_sp2refseq.txt import
mv -f $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.owl" $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.bak.owl"
/usr/bin/python $CPATH_HOME/../pathway-commons/bin/reactome-cooker.py < $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.bak.owl" > $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.owl"
./admin.pl -f $CPATH_HOME/../pathway-commons/reactome/version21/"Homo\ sapiens.owl" import
mv -f $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.bak.owl" $CPATH_HOME/../pathway-commons/reactome/version21/"Homo sapiens.owl"
./admin.pl -f $CPATH_HOME/../pathway-commons/humancyc import
./admin.pl -f $CPATH_HOME/../pathway-commons/nci/ import
./admin.pl -f $CPATH_HOME/../pathway-commons/cellmap import
./admin.pl pop_ref
./admin.pl index
