#!/usr/bin/perl
print "Loading Bootstrap Data\n";
chdir ("dbData") || die "Cannot cd to dbData directory";
system "mysql -u tomcat --password=kitty < bootstrap.sql";
print "Bootstrap Loading Complete\n";
