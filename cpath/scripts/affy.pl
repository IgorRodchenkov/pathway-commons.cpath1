#!/usr/bin/perl
require "../bin/env.pl";

system ("java -Xmx1524M -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.ParseAffymetrix @ARGV");
