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
if ($numArgs < 1) {
	displayHelp();
}
# Output cPath Home
print ("Using cPath Home:  $cpathHome\n");

my $command = shift (@ARGV);

if ($command eq "psi") {
	system ("java -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.LoadPsi @ARGV");
} elsif ($command eq "import") {
	system ("java -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.ImportRecords @ARGV");
} elsif ($command eq "index") {
	system ("java -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.LoadFullText  @ARGV");
} elsif ($command eq "refs") {
	system ("java -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.LoadExternalReferences  @ARGV");
} elsif ($command eq "precompute") {
	system ("java -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.LoadPreComputedQueries @ARGV");
} elsif ($command eq "ft_query") {
	system ("java -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.QueryFullText @ARGV");
}

sub displayHelp {
	print "-------------------------------------------------------\n";
	print "cPath Admin\n";
	print "-------------------------------------------------------\n";
	print "psi:         Loads PSI-MI data\n";
	print "import:      Imports all new import records to cPath\n";
	print "index:       Indexes all cPath records\n";
	print "refs:        Loads a list of external references\n";
	print "precompute:  Runs Pre-computed queries\n";
	print "ft_query:    Tests the Full Text Query Engine\n";
	exit;
}
