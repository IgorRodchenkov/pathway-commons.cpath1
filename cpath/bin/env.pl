#  Set up Environment for Running cPath Java Tools

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
$cp="$cpathHome/build/WEB-INF/classes";
@jar_files = glob ("$cpathHome/lib/*.jar");
foreach my $jar (@jar_files) {
	$cp="$cp$pathDelim$jar"
}

return 1;
