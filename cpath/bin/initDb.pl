#!/usr/bin/perl
require "env.pl";

print "Script to Initialize cPath Database\n";
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

$arg0 = $ARGV[0];
$arg1 = $ARGV[1];

$unitTestFlag = 0;
$foceFlag = 0;
$answer = 0;

if  ($arg0 eq "-f") {
    $forceFlag = 1;
}
if ($arg1 eq "load_test_data") {
    $unitTestFlag = 1;
}

if ($forceFlag == 0) {
    print "\n! Running this command will delete all existing data !\n";
    print "Are you sure you wish to proceed (Type: YES):  ";
    chomp ($answer = <STDIN>);
}

if ($answer eq 'YES' || $forceFlag == 1) {
    &importSql ("cpath.sql");
    &importSql ("seed.sql");
    &importSql ("reference.sql");

    if ($unitTestFlag ==1) {
        &importSql ("unit_test.sql");
    }

    #  All is done
    print "Done.  cPath is now ready to go...\n";
} else {
    print "Command cancelled...\n";
}

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
