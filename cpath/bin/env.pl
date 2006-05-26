#  Set up Environment for Running cPath Java Tools

# Check to see if CPATH_HOME is set via command line arguments
if ($#ARGV >= 0) {
	$arg0 = $ARGV[0];
	$index = index($arg0, "CPATH_HOME");
	if ($index >= 0) {
		$home = substr($arg0, 11);
		$ENV{CPATH_HOME}=$home;
	}
}

$cpathHome = $ENV{CPATH_HOME};
my $osCheck = $ENV{OS};
my $pathDelim;

if( $osCheck =~ /win/i){
    $pathDelim=";";
}else{
    $pathDelim=":";
}

if ($cpathHome eq "") {
	die "CPATH_HOME Environment Variable is not set.  Please set, and try again.\n";
}

# Set up Classpath to use all JAR files in lib dir.
print "Using CPATH_HOME $cpathHome\n";
$cp="$cpathHome/build/WEB-INF/classes";
@jar_files = glob ("$cpathHome/lib/*.jar");
foreach my $jar (@jar_files) {
	$cp="$cp$pathDelim$jar"
}

return 1;
