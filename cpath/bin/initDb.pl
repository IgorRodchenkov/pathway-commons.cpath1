#!/usr/bin/perl
require "env.pl";

print "Script to Initialize cPath Database\n";
print "===================================\n\n";

$arg0 = $ARGV[0];

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

$unitTestFlag = 0;
if ($arg0 eq "load_test_data") {
    $unitTestFlag = 1;
} else {
    print "\n! Running this command will delete all existing data !\n";
    print "Are you sure you wish to proceed (Type: YES):  ";
    chomp (my $answer = <STDIN>);
}

if ($answer eq 'YES' || $unitTestFlag == 1) {

    #  Prepare cpath.sql
    open (OLD, "< $cpathHome/dbData/cpath.sql");
    open (NEW, "> $cpathHome/dbData/temp1.sql") or die;
    while (<OLD>) {
        $line = $_;
        $line =~ s/db_name/$db_name/;
        print NEW $line;
    }
    close (OLD);
    close (NEW);

    #  Prepare seed.sql
    open (OLD, "< $cpathHome/dbData/seed.sql");
    open (NEW, "> $cpathHome/dbData/temp2.sql") or die;
    while (<OLD>) {
        $line = $_;
        $line =~ s/db_name/$db_name/;
        print NEW $line;
    }
    close (OLD);
    close (NEW);

    #  Load up Table Structure
    system "mysql --host=$db_host --user=$db_user --password=$db_password < ../dbData/temp1.sql";

    #  Load up Seed Data
    system "mysql --host=$db_host --user=$db_user --password=$db_password < ../dbData/temp2.sql";

    #  Delete Temp Files
    unlink ("$cpathHome/dbData/temp1.sql");
    unlink ("$cpathHome/dbData/temp2.sql");

    if ($unitTestFlag ==1) {
        #  Prepare cpath.sql
        open (OLD, "< $cpathHome/dbData/unit_test.sql");
        open (NEW, "> $cpathHome/dbData/temp3.sql") or die;
        while (<OLD>) {
            $line = $_;
            $line =~ s/db_name/$db_name/;
            print NEW $line;
        }
        close (OLD);
        close (NEW);

        #  Load up Test Data
        system "mysql --host=$db_host --user=$db_user --password=$db_password < ../dbData/temp3.sql";
    }

    #  All is done
    print "Done.  cPath is now ready to go...\n";
} else {
    print "Command cancelled...\n";
}

