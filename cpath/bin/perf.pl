#!/usr/bin/perl
use strict;

my $cpathHome = $ENV{CPATH_HOME};
if ($cpathHome eq "") {
	die "CPATH_HOME Environment Variable is not set.  Please set, and try again.\n";
}

# Set up Classpath to use all JAR files in lib dir.
my $cp="$cpathHome/build/WEB-INF/classes";
my @jar_files = glob ("$cpathHome/lib/*.jar");
foreach my $jar (@jar_files) {
	$cp="$cp:$jar"
}

my $numArgs = scalar (@ARGV);

# Output cPath Home
# print ("Using cPath Home:  $cpathHome\n");

system ("java -Xmx512M -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.PerfTest @ARGV");
