#!/usr/bin/perl
print "Initializing cPath Database\n";
print "! Running this command will delete all existing data !\n";
print "Are you sure you wish to proceed (Y/N):  ";
chomp (my $answer = <STDIN>);

if ($answer eq 'Y' || $answer eq 'y') {

    # Prompt for user input
    print "Enter database name:  ";
    chomp (my $db_name = <STDIN>);

    print "Enter user name:  ";
    chomp (my $name = <STDIN>);

    print "Enter user password:  ";
    chomp (my $password = <STDIN>);

    #  Prepare cpath.sql
    open (OLD, "< ../dbData/cpath.sql");
    open (NEW, "> ../dbData/temp1.sql") or die;
    while (<OLD>) {
        $line = $_;
        $line =~ s/db_name/$db_name/;
        print NEW $line;
    }
    close (OLD);
    close (NEW);

    #  Prepare seed.sql
    open (OLD, "< ../dbData/seed.sql");
    open (NEW, "> ../dbData/temp2.sql") or die;
    while (<OLD>) {
        $line = $_;
        $line =~ s/db_name/$db_name/;
        print NEW $line;
    }
    close (OLD);
    close (NEW);

    #  Load up Table Structure
    system "mysql -u $name --password=$password < ../dbData/temp1.sql";

    #  Load up Seed Data
    system "mysql -u $name --password=$password < ../dbData/temp2.sql";

    #  Delete Temp Files
    unlink ("../dbData/temp1.sql");
    unlink ("../dbData/temp2.sql");

    #  All is done
    print "Done.  cPath is now ready to go...\n";
} else {
    print "Command cancelled...\n";
}

