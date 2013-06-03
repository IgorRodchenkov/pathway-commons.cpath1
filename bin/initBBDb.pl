#!/usr/bin/perl
require "env.pl";

print "Script to Initialize bb tables within cPath Database\n";
print "===================================\n\n";

#  Read in build.properties File
open (PROPS, "< $cpathHome/build.properties");
while (<PROPS>) {
    $line = $_;
    if ($line =~ /=/) {
        @fields = split (/=/,$_);
        chomp ($fields[0]);
        chomp ($fields[1]);
        $props {$fields[0]} = $fields[1];
    }
}
close (PROPS);

$db_name = $props{"db.name"};
$db_host = $props{"db.host"};
$db_user = $props{"db.user"};
$db_password = $props{"db.password"};

# Echo out current properties
print ("Using Database Name:      $db_name\n");
print ("Using Database Host:      $db_host\n");
print ("Using Database User:      $db_user\n");
print ("Using Database Password:  $db_password\n");

# create table structure
&importSql ("bb.sql");

# create seed file
my $seed_file = "$cpathHome/dbData/seedBB.sql";
open(my $fh, ">" . "$seed_file") or die "Unable to create {$seed_file}: $!\n";
gen_kegg_seed($fh);
close $fh or die "Unable to close {$seed_file}: $!\n";
# import the seed table
&importSql (substr($seed_file, rindex($seed_file, "\/")+1));
# no need to close seed file, import sql does this for us.
unlink "$seed_file" or die "Unable to remove {$seed_file}: $!\n";

print "Done.  cPath is now ready to go...\n";

# Imports a MySQL File
sub importSql {
    #  Prepare seed.sql
    open (OLD, "< $cpathHome/dbData/$_[0]") or die "cannot open: $cpathHome/dbData/$_[0]";
    open (NEW, "> $cpathHome/dbData/temp.sql") or die "cannot open: $cpathHome/dbData/temp.sql";
    while (<OLD>) {
        $line = $_;
        $line =~ s/db_name__value/$db_name/;
        print NEW $line;
    }
    close (OLD);
    close (NEW);

    #  Indicate what we are doing
    print "Importing MySQL File:  $_[0]\n";

    #  Load up Table Structure
    system "mysql --host=$db_host --user=$db_user --password=$db_password < ../dbData/temp.sql";

    #  Delete Temp File
    unlink ("$cpathHome/dbData/temp.sql");
}

sub gen_kegg_seed($)
{
    my ($seed_fh) = @_;

	print $seed_fh "USE db_name__value;\n\n";

    # interate through kegg files
	my $seed_file_content = "";
    foreach my $kegg_html_file (glob "$cpathHome/testData/pid/kegg/*.html") {
	    # get kegg pathway id, kegg gene file name
		my ($kegg_pathway_id, $type) = split/\.html/,$kegg_html_file;
		my $kegg_gene_file = $kegg_pathway_id . ".gene";
		$kegg_pathway_id = substr($kegg_pathway_id, rindex($kegg_pathway_id, "\/") + 1);
	    # get kegg pathway name
	    my $kegg_pathway_name = get_pathway_name($kegg_html_file);
		# generate bb_pathway table insert
		gen_bb_pathway_table_insert($seed_fh, $kegg_pathway_id, $kegg_pathway_name, "KEGG");
	    # open kegg gene file
	    open (my $kegg_fh, "<$kegg_gene_file") or die "Unable to open {$kegg_gene_file}: $!\n";
		# process gene file
		while(my $line = <$kegg_fh>) {
		  chomp($line);
		  $line =~ /(\d+)\s+(\w+)/;
		  my $entrez_gene_id = $1;
		  my $gene_name = $2;
		  # generate bb_gene table insert
		  gen_bb_gene_table_insert($seed_fh, $entrez_gene_id, $gene_name);
		  # generate bb_internal_link insert
		  gen_bb_internal_link_insert($seed_fh, $kegg_pathway_id, $entrez_gene_id);
        }
        # close gene file
		close $kegg_fh || die "Unable to close {$kegg_gene_file}: $!\n";
    }
    print $seed_fh $seed_file_content;
}

sub get_pathway_name($)
{
    my ($kegg_html_file) = @_;
	my $pathway_name = "";

	# open kegg html file
	open (my $kegg_fh, "<$kegg_html_file") or die "Unable to open {$kegg_html_file}: $!\n";
	# extract pathway name
    while (my $line = <$kegg_fh>) {
	    chomp($line);
		next unless ($line =~ /^DEFINITION\s+((\w|\s)+)- Homo sapiens \(human\)$/);
		$pathway_name = $1;
		break;
	}
	# close kegg html file
	close $kegg_fh || die "Unable to close {$kegg_html_file}: $!\n";

	# zap off white space if necessary
	while (rindex($pathway_name, " ") == length($pathway_name)-1) {
	  chop $pathway_name;
	}

	# outta here
	return $pathway_name;
}

sub gen_bb_pathway_table_insert($$$)
{
    my($fh, $id, $name, $source) = @_;
	
	my $url = "http://www.genome.jp/dbget-bin/show_pathway?" . $id;
	print $fh "INSERT INTO `bb_pathway` (`EXTERNAL_PATHWAY_ID`, `PATHWAY_NAME`, `SOURCE`, `URL`) VALUES ('$id', '$name', '$source', '$url');\n";
}

sub gen_bb_gene_table_insert($$$)
{
    my($fh, $id, $name) = @_;
	print $fh "INSERT INTO `bb_gene` (`ENTREZ_GENE_ID`, `GENE_NAME`) VALUES ('$id', '$name');\n";
}

sub gen_bb_internal_link_insert($$$)
{
    my($fh, $pathway_id, $gene_id) = @_;
	print $fh "INSERT INTO `bb_internal_link` (`EXTERNAL_PATHWAY_ID`, `ENTREZ_GENE_ID`) VALUES ('$pathway_id', '$gene_id');\n";
}
