#!/usr/bin/perl
# Script Used to Deploy cPath to cBio and restart Tomcat
# Author:  Ethan Cerami (cerami@cbio.mskcc.org).

use strict;

my $tom = "/var/lib/tomcat4/webapps/cpath";

# CD to the cpath tomcat dir
chdir "$tom" or die;

# Delete everything within cpath
system "rm -rf *";

# Update All Code
chdir "/home/cerami/dev/sander/cpath";
system "cvs update -d";

# Create War File
system "ant war";

# Copy War File over
print "Copying war file to: $tom\n";
system "cp dist/cpath.war $tom";

# Expand War File, and set permissions on textIndex dir
chdir "$tom" or die;
print "Unpacking WAR File\n";
system "jar -xvf cpath.war";
system "rm cpath.war";

#  Create Text Index Directory
print "Creating Text Index Directory\n";
chdir "WEB-INF" or die;
system "mkdir textIndex";
system "chmod o+w textIndex";

print "Restarting Tomcat\n";
system "sudo /etc/init.d/tomcat4 restart";

print "Deployment Complete\n";

