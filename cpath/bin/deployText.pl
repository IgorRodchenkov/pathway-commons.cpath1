#!/usr/bin/perl
# Script Used to Deploy Full Text Index to Tomcat
# Author:  Ethan Cerami (cerami@cbio.mskcc.org).

use strict;

my $tom = "/var/lib/tomcat4/webapps/cpath/WEB-INF/textIndex";

# CD to the cpath tomcat dir
chdir "$tom" or die "Error:  $!";

printf "Deleting Current Production Index Files\n";
system "rm *";

printf "Copying Full Text Index to Tomcat\n";
system "cp /home/cerami/dev/sander/cpath/build/WEB-INF/textIndex/* .";

# Set Sticky Bit so that Tomcat can write to this directory
system "chmod 1777 $tom";
printf "Done.\n";
