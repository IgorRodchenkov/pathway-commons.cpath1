#!/bin/bash
#
#-------------------------------------------------------------------------------

echo Copying Full Text Index to Tomcat
cp -R /home/cerami/dev/sander/cpath/textIndex/* /var/lib/tomcat4/webapps/textIndex
# Set Sticky Bit so that Tomcat can write to this directory
chmod 1777 /home/cerami/dev/sander/cpath/textIndex
echo Done.
