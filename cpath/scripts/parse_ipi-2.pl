#!/usr/bin/perl
use strict;
use Net::FTP;	
#for downloading files from ftp sites

#initialize output files
open(REF, ">cpath_unification_sp2refseq.txt") or die "Error writing into cpath_unification : $!\n";
open(GI, ">cpath_unification_sp2gi.txt") or die "Error writing into cpath_unification : $!\n";
open(SYN_SP, ">cpath_unification_sp2spsyn.txt") or die "Error writing into cpath_unification : $!\n";
open(SYN_TR, ">cpath_unification_sp2trsyn.txt") or die "Error writing into cpath_unification : $!\n";
	
#print the headers of the files
print GI "UNIPROT\tGI\n";
print REF "UNIPROT\tREF_SEQ Protein\n";
print SYN_SP "UNIPROT\tUNIPROT\n";
print SYN_SP "UNIPROT\tUNIPROT\n";

	#download IPI reference file from ebi
	my $ftp = 'ftp.ebi.ac.uk';
	my $filetype = '*.xrefs.gz';
	print "Downloading *.xrefs.gz ... \n";

	#activate Debug to 1 for details
	my $ftp = Net::FTP->new("$ftp", Debug => 0, Passive => 1) or die "Cannot connect to some.host.name: $@";

	$ftp->login("anonymous","-anonymous\@")    or die "Cannot login ", $ftp->message;
	$ftp->binary();
	$ftp->cwd("pub/databases/IPI/current/");

	my @files = $ftp->ls("*.xrefs.gz") or die "ls failed ", $ftp->message;
	for $a (0 .. $#files){

	    #ignore the ipi.gene.X.xrefs.gz files.
	    if($files[$a] =~ /ipi.gene/){
		print "ignoring: $files[$a] \n";
		}
	    else{
		print "downloading: $files[$a] \n";	
             	$ftp->get("$files[$a]")  or die "get failed ", $ftp->message;
	        } 
	}
	$ftp->quit;

	for $a (0 .. $#files){
	    if($files[$a] =~ /ipi.gene/){
		print "ignoring: $files[$a] \n";
		}
	    else{
	       my ($filename, $ending) = split(/.g/,$files[$a]);
	       print "unzipping file: $filename \n";
	       `gunzip "$files[$a]"`;
            
       	       open (XR, "$filename") or print "Error XR: $!\n";
	       while(<XR>){
		
		 chomp;
	
		 my @fields = split(/\t/);
                 if ($fields[0] =~ /(SP)|(TR)/){
                  my $swissprot_acc = $fields[1];
		  
		  #For each refseq get rid of the type (provisional, validated,model...) and output 
		  # swissprot \t refseq to a file.
		  my @refseqs = split(/\;/, $fields[6]);
		  foreach(@refseqs){
                  	my($type, $refseq_id) = split(/\:/, $refseqs[0]);
                        print REF "$swissprot_acc\t$refseq_id\n";
			}
		  #For each gi get rid of the GI: and output 
		  # swissprot \t gi to a file.
		  my @gis = split(/\;/, $fields[15]);
		  for $a (0 .. $#gis){
                  	my($type, $gi) = split(/\:/, $gis[$a]);
                        print GI "$swissprot_acc\t$gi\n";
			}	
	
		  #For each synonym swissprot output 
		  # swissprot \t sp synonym to a file.
		  my @syn_sp = split(/\;/, $fields[3]);
		  for $a (0 .. $#syn_sp){
                        print SYN_SP "$swissprot_acc\t$syn_sp[$a]\n";
			}		

		  #For each synonym trembl output 
		  # swissprot \t sp synonym to a file.
		  my @syn_tr = split(/\;/, $fields[4]);
		  for $a (0 .. $#syn_tr){
                        print SYN_TR "$swissprot_acc\t$syn_tr[$a]\n";
			}		

		}
	    }	
	}
        close(XR);

}

	
	

