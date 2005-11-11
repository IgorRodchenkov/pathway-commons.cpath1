#!/usr/bin/perl
# Script to checkout cpath, and generate the Maven Developer Site
# Author:  Ethan Cerami (cerami@cbio.mskcc.org).

use strict;

# CD to the temp directory
&debug ("cd to /home/cerami/temp...");
chdir "/home/cerami/temp" or die;

# Delete everything within temp to do a clean build
&debug ("Delete everything in temp...");
system "rm -rvf *";

# Check out all code
&debug ("Check out all code from CVS...");
system "cvs checkout -P sander/cpath";
chdir "sander/cpath" or die;

# Run Prepare_dev_site target
&debug ("Run ant prepare_dev_site...");
system "ant prepare_dev_site";

# Create source distribition
&debug ("Create war file...");
system "ant war";
&debug ("Tar up everything...");
system "tar -cvf cpath.tar *";
&debug ("Gzip everything...");
system "gzip cpath.tar";
&debug ("Deploy to web site...");
system "cp -v cpath.tar.gz /var/www/dev_site/cpath";

# Run Maven
&debug ("Run maven site:generate...");
system "/home/cerami/libraries/maven-1.0-rc1/bin/maven site:generate";
&debug ("Run maven pdf...");
system "/home/cerami/libraries/maven-1.0-rc1/bin/maven pdf";
&debug ("Run maven site:deploy...");
system "/home/cerami/libraries/maven-1.0-rc1/bin/maven site:fsdeploy";

# Run Ant Eater Functional Tests
# Run Ant Eater after source distribution is complete
# There is no need to bundle the AntEater results
&debug ("Run anteater tests...");
system "anteater -f ant_eater/tests.xml";

# Move Ant Eater Reports
&debug ("Move to existing ant eater report directory...");
chdir "/var/www/dev_site/cpath/reports" or die;
&debug ("Delete all existing ant eater reports...");
system "rm -rvf *";
&debug ("Copy over new ant eater reports...");
chdir "/home/cerami/temp/sander/cpath" or die;
system "mv -v ant_eater/reports/* /var/www/dev_site/cpath/reports/";

sub debug {
	print "-----------------------------------------\n";
	print "$_[0]\n";
	print "-----------------------------------------\n";
}
