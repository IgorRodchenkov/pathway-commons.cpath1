#!/usr/bin/perl

require "env.pl";

system ("java -ea -Xmx1024M -cp $cp -DCPATH_HOME=$cpathHome org.mskcc.pathdb.tool.Admin @ARGV");
