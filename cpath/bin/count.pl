#!/usr/bin/perl
use strict;
use File::Find;

my $num_lines_code=0;
my $num_lines_jsp=0;
my $num_classes=0;
my $num_jsps=0;

# Recursively Find all Files in src
print "Checking cPath Metrics...\n";
find (\&process_file, "..");

print "\n\nTotal Number of Classes:  $num_classes";
print "\nTotal Number of JSPs:  $num_jsps";
print "\nTotal Number of Lines of Regular Code:  $num_lines_code";
print "\nTotal Number of Lines of JSP Code / HTML Code:  $num_lines_jsp";
print "\n";

# Process All .java Files
sub process_file {
	if (/java$/) { 
		my $curFile = $File::Find::name;
		$num_classes++;
		print ("Checking Java Source File:  $curFile -->  ");
		open(CUR_SOURCE, "< $_") || die "can't read $_";
		my $local_counter =0;
		while (<CUR_SOURCE>) {
			$local_counter++;
		}
		close (CUR_SOURCE);
		print " Lines of Code:  $local_counter\n";
		$num_lines_code += $local_counter;
	}
	if (/\.jsp$/) { 
		my $curFile = $File::Find::name;
		$num_jsps++;
		print ("Checking JSP File:  $curFile-->  ");
		open(CUR_SOURCE, "< $_") || die "can't read $_";
		my $local_counter =0;
		while (<CUR_SOURCE>) {
			$local_counter++;
		}
		close (CUR_SOURCE);
		print " Lines of Code:  $local_counter\n";
		$num_lines_jsp += $local_counter;
	}
}
