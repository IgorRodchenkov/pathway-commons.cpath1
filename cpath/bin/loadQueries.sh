#!/bin/bash
#
#-------------------------------------------------------------------------------

echo "Using CPATH_HOME:  $CPATH_HOME"
CP="$CPATH_HOME/build/WEB-INF/classes"
LIB_DIR="$CPATH_HOME/lib"

for file in $LIB_DIR/*.jar 
do
#	echo $file
	CP="$CP:$file"
done
#echo $CP
java -cp $CP -DCPATH_HOME="$CPATH_HOME" org.mskcc.pathdb.tool.LoadPreComputedQueries $*
