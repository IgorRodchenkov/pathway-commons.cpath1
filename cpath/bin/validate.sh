#!/bin/bash
#
#-------------------------------------------------------------------------------

CP="../build/WEB-INF/classes"
LIB_DIR="../lib"

for file in $LIB_DIR/*.jar 
do
#	echo $file
	CP="$CP:$file"
done
java -cp $CP org.mskcc.pathdb.util.XmlValidator $*
