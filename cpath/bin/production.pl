#!/usr/bin/perl
# Script Used to Deploy cPath to cBio and restart Tomcat
# Author:  Ethan Cerami (cerami@cbio.mskcc.org).

use strict;

my $tom = "/var/lib/tomcat4/webapps/cpath";

# Update Passwords with User Input
print "Enter password for MySQL tomcat user:  ";
chomp (my $mysql_password = <STDIN>);

print "Enter password for cPath Administrator:  ";
chomp (my $admin_password = <STDIN>);

# CD to the cpath tomcat dir
#chdir "$tom" or die;

# Delete everything within cpath
#system "rm -rf *";

# Update All Code
chdir "/home/cerami/dev/sander/cpath" or die;
#system "cvs update -d";

# Create War File
#system "ant war";

# Copy War File over
print "Copying war file to: $tom\n";
#system "cp dist/cpath.war $tom";

# Update web.xml file with correct passwords
chdir "web/WEB-INF" or die;
system "sed 's/kitty/$mysql_password/' web.xml";
#system "mv web_new.xml web.xml";

die;

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

