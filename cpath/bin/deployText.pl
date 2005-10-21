#!/usr/bin/perl
# Script Used to Deploy Full Text Index to Tomcat
# Author:  Ethan Cerami (cerami@cbio.mskcc.org).

use strict;

#my $tom = "/var/lib/tomcat4/webapps/cpath/WEB-INF/textIndex";
my $tom = "C:/programming/Tomcat_4.1/webapps/cpath/WEB-INF/textIndex";

printf "Deleting Current Production Index Files\n";
system "rm $tom/*";

printf "Copying Full Text Index to Tomcat\n";
system "cp ../build/WEB-INF/textIndex/*  $tom";

# Set Sticky Bit so that Tomcat can write to this directory
system "chmod 1777 $tom/*";
printf "Done.\n";
