#!/usr/bin/perl
print "Loading Bootstrap Data\n";
chdir ("dbData") || die "Cannot cd to dbData directory";
system "mysql -u tomcat --password=kitty < reset.sql";
system "mysql -u tomcat --password=kitty < unit_test.sql";
print "Bootstrap Loading Complete\n";
