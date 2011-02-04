#!/bin/bash 

if [ -n $1 ]
then 
xmllint --format $1 > $1.xml 2>&1
sed 's/\&amp;base.url;/\&base.url;/g' $1.xml > $1.xml1 2>&1
sed 's/\&amp;lo.pacc;/\&lo.pacc;/g' $1.xml1 > $1.xml2 2>&1
mv $1.xml2 $1
rm $1.xml1 $1.xml
fi

exit 0


