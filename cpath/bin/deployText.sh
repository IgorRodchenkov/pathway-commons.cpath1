#!/bin/bash
#
#-------------------------------------------------------------------------------

echo Copying Full Text Index to Tomcat
cp -R /home/cerami/dev/sander/cpath/textIndex/* /var/lib/tomcat4/webapps/textIndex
echo Done.
