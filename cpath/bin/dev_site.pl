#!/usr/bin/perl
# Author:  Ethan Cerami (cerami@cbio.mskcc.org).

use strict;

chdir "/home/cerami/temp" or die;
system "cvs checkout -P sander/cpath";
chdir "sander/cpath" or die;
system "ant prepare_dev_site";
system "maven site:deploy";
