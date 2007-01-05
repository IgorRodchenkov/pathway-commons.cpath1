./initDb.pl
./admin.pl -f ../dbData/externalDb/pathway_commons.xml import
./admin.pl -f ~/dev/sander/pathway-commons/reactome/version19/Homo_sapiens.owl import
./admin.pl -f ~/dev/sander/pathway-commons/humancyc import
./admin.pl -f ~/dev/sander/pathway-commons/nci import
./admin.pl -f ~/dev/sander/pathway-commons/cellmap import
./admin.pl pop_ref
./admin.pl index
