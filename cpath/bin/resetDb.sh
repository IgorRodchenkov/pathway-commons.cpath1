#!/usr/bin/perl
print "Reseting Database\n";
chdir ("../dbData") || die "Cannot cd to dbData directory";
system "mysql -u tomcat --password=kitty < reset.sql";
print "Done.  Go!  Go!  Go!\n";
