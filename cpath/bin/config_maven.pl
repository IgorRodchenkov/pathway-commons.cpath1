#!/usr/bin/perl
use XML::LibXML;
use strict;

my $cpathHome = $ENV{CPATH_HOME};
if ($cpathHome eq "") {
	die "CPATH_HOME Environment Variable is not set.  Please set, and try again.\n";
}

# Get All Jars
chdir ("$cpathHome/lib") or die;
my @jar_files = glob ("*.jar");
my (@names, $over_ride);
foreach my $jar (@jar_files) {
	my ($name, $ext) = split (/\.jar/, $jar);
	$over_ride .= "maven.jar.$name = \${basedir}/lib/$jar\n";
	push (@names, $name);
}
&update_override_file;
&update_project_xml;

# Update Project.properties File
sub update_override_file {
	my $prop_file = "project.properties";
	my $temp_file = "temp.properties";
	chdir ($cpathHome) or die;

	open IN, $prop_file or die;
	open OUT, ">$temp_file" or die;
	while (<IN>) {
		if (! /maven\.jar/) {
			print OUT $_;
		}
		if (/JAR_START/) {
			print OUT "maven.jar.override=on\n";
			print OUT $over_ride;
		}
	}
	rename ($temp_file, $prop_file);
	print "Modified File:  $prop_file\n";
	close IN;
	close OUT;
}

# Update project.xml File
sub update_project_xml {
	my $project_file = "project.xml";
	chdir ($cpathHome) or die;
	my $parser = XML::LibXML->new;
	my $document = $parser->parse_file("$project_file") or die;
	my $root = $document->getDocumentElement();
	my @depend = $root->getElementsByTagName("dependencies");
	my $dependency_root = $depend[0];
	my $name = $dependency_root->nodeName;
	
	# Delete All Existing Dependency Nodes
	$dependency_root->removeChildNodes();

	#  Add New Dependency Node
	foreach $name (@names) {
		my $new_line = $document->createTextNode ("\n");
		$dependency_root->addChild ($new_line);

		my $depend_child = $document->createElement ("dependency");

		# Specify Group ID
		my $group_child = $document->createElement ("groupId");
		my $group_text = $document->createTextNode ("$name");
		$group_child->addChild ($group_text);
		$depend_child->addChild ($group_child);

		# Specify Artifact ID
		my $artifact_child = $document->createElement ("artifactId");
		my $artifact_text = $document->createTextNode ($name);
		$artifact_child->addChild ($artifact_text);
		$depend_child->addChild ($artifact_child);

		$dependency_root->addChild ($depend_child);
	}
	print $document->toString(1);
	$document->toFile ("project_new.xml", 1);
}
