#!/bin/bash
#
# Runs cpath import from the command line 
#-------------------------------------------------------------------------------

CP="../build/WEB-INF/classes"
LIB_DIR="../lib"

for file in $LIB_DIR/*.jar 
do
#	echo $file
	CP="$CP:$file"
done
#echo $CP
java -cp $CP org.mskcc.pathdb.tool.LoadPsi $*
