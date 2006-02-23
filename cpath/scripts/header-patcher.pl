#!/usr/bin/perl

# packages
use strict;
use Getopt::Long;
use File::Find;

# global - used in process_file, but made global because it can't come throught process_file_via_find
my $copyright_year = undef;

# ----------------------------------------------------------------------

sub usage{
    print STDERR "Usage: $0 -f <file or directory to process> -y <copyright year>\n";
    exit 1;
}

# ----------------------------------------------------------------------

sub print_header($){
	my ($fh) = @_;

	print $fh "// \$Id\$
//------------------------------------------------------------------------------
/** Copyright (c) $copyright_year Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an \"as is\" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
";

}

# ----------------------------------------------------------------------

sub process_file_via_find($){
	my $file = $File::Find::name;
	process_file($file);
}

# ----------------------------------------------------------------------

sub process_file($){
	my ($file) = @_;

	# init some vars
	my $fh;
	my $fhd;
	my $line_ct = 0;

	# is this a java file ?
	if ($file =~ /\.java/){
		# open src file
		open($fh, "<$file") or die "Can't open $file for reading : $!\n";
		# open new destination
		open($fhd, ">$file.new") or die "Can't open $file.new for writing: $!\n";

		# interate through file
		while (my $line = <$fh>){

			# we are working on a new line
			$line_ct += 1;

			# pull off the newline
			chomp($line);

			# look for copyright
			if ($line =~ /^\/\*\* Copyright \(c\) \d\d\d\d Memorial Sloan-Kettering Cancer Center.$/){
				# if this happens to be line one, add cvs id code
				if ($line_ct == 1){
					print $fhd "// \$Id\$\n";
					print $fhd "//------------------------------------------------------------------------------\n";
				}
				# update copyright line (year)
				print $fhd "/** Copyright (c) $copyright_year Memorial Sloan-Kettering Cancer Center.\n";
			}
			elsif( ($line =~ /^package/ || $line =~ /^\/\/ *package/ || $line =~ /^$/) && $line_ct == 1){
				print_header($fhd);
				print $fhd "$line\n";
			}
			else{
				# write line out to destination file
				print $fhd "$line\n";
			}
		}

		# close both files
		close($fh) or die "Can't close $file: $!\n";
		close($fhd) or die "Can't close $file.new: $!\n";

		# move the new src file over the old src file
		rename("$file.new", "$file") || die "Can't rename $file.new to $file: $!\n";
	}
}

# ----------------------------------------------------------------------

# here we go...

# grab file or directory arg
my $potential_directory = undef;
usage() unless (GetOptions("f=s" => \$potential_directory, "y=s" => \$copyright_year) and defined($potential_directory) and defined($copyright_year));

# lets patch the files
if (-d $potential_directory){
	# setup directories arg to find
	my @directories_to_process = ();
	push(@directories_to_process, $potential_directory);
	# traverse the directory
	find({wanted => \&process_file_via_find, no_chdir => 1}, @directories_to_process);
}
else{
	# argument is a file, process it directory
	process_file($potential_directory);
}

# outta here
exit(0);
