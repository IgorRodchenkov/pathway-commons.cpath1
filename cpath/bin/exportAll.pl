#!/usr/bin/perl

require "env.pl";

$path = $ARGV[0];

system ("rm -rf $path/*");
system ("java -ea -Xmx8192M -cp $cp -DCPATH_HOME=$cpathHome org.mskcc.pathdb.tool.ExportAll @ARGV");
