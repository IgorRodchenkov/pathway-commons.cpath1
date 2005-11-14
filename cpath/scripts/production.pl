#!/usr/bin/perl
# Script Used to Deploy cPath to cBio and restart Tomcat
# Author:  Ethan Cerami (cerami@cbio.mskcc.org).

use strict;

my $tom = "/var/lib/tomcat4/webapps/cpath";
my $home = "/home/cerami/dev/sander/cpath";

# Prompt for Passwords 
print "Enter password for MySQL tomcat user:  ";
chomp (my $mysql_password = <STDIN>);

print "Enter password for cPath Administrator:  ";
chomp (my $admin_password = <STDIN>);

# CD to the cpath tomcat dir
chdir "$tom" or die;

# Delete everything within cpath
print "Deleting existing cpath web app\n";
system "rm -rf *";

# Update All Code from CVS
chdir "$home" or die;
#system "cvs update -d";

# Update web.xml file with user-supplied passwords
print "Updating web.xml File\n";
chdir "web/WEB-INF" or die;
system "sed 's/value\>kitty/value\>$mysql_password/' web.xml > web1.xml";
system "sed 's/value\>cpath/value\>$admin_password/' web1.xml > web2.xml";
system "mv web2.xml web.xml";
system "rm web1.xml";

# Create War File
chdir "$home" or die;
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

