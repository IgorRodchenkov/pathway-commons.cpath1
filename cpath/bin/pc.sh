./initDb.pl
./initBBDb.pl
./admin.pl -f ../dbData/externalDb/pathway_commons.xml import
./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_sp2refseq.txt import
#./admin.pl -f $CPATH_HOME/../pathway-commons/reactome/version19/Homo_sapiens.owl import
#./admin.pl -f $CPATH_HOME/../pathway-commons/humancyc import
./admin.pl -f $CPATH_HOME/../pathway-commons/nci import
#./admin.pl -f $CPATH_HOME/../pathway-commons/cellmap import
./admin.pl pop_ref
./admin.pl index
