#!/usr/bin/perl
print "Reseting Production Database\n";
chdir ("dbData") || die "Cannot cd to dbData directory";
system "mysql -u tomcat --password=kitty < bootProd.sql";
print "Bootstrap Loading Complete\n";
