#!/usr/bin/perl

require "env.pl";

system ("java -ea -Xmx2048M -cp $cp -DCPATH_HOME=$cpathHome org.mskcc.pathdb.tool.ExportAll @ARGV");
