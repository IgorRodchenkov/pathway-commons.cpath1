#!/bin/bash
#
#-------------------------------------------------------------------------------

echo Copying Full Text Index to Tomcat
cp -R /home/cerami/dev/sander/cpath/build/WEB-INF/textIndex/* /var/lib/tomcat4/webapps/cpath/WEB-INF/textIndex
# Set Sticky Bit so that Tomcat can write to this directory
chmod 1777 /var/lib/tomcat4/webapps/cpath/WEB-INF/textIndex 
echo Done.
