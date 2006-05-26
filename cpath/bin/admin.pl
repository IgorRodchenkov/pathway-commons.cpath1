#!/usr/bin/perl

require "env.pl";

system ("java -Xmx2024M -cp $cp -DCPATH_HOME=$cpathHome org.mskcc.pathdb.tool.Admin @ARGV");
