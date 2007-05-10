./initDb.pl
./admin.pl -f ../dbData/externalDb/pathway_commons.xml import
./admin.pl -f $CPATH_HOME/../pathway-commons/ids/cpath_unification_sp2refseq.txt import
./admin.pl -f $CPATH_HOME/../pathway-commons/reactome/version19/Homo_sapiens.owl import
./admin.pl -f $CPATH_HOME/../pathway-commons/humancyc import
python $CPATH_HOME/../pathway-commons/nci/preprocess.py $CPATH_HOME/../pathway-commons/nci/NCI-Nature_Curated.owl $CPATH_HOME/../pathway-commons/nci/NCI-Nature_Curated-Processed.owl 
./admin.pl -f $CPATH_HOME/../pathway-commons/nci/NCI-Nature_Curated-Processed.owl import
./admin.pl -f $CPATH_HOME/../pathway-commons/cellmap import
./admin.pl pop_ref
./admin.pl index
