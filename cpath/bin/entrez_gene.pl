#!/usr/bin/perl
require "../bin/env.pl";

system ("$JAVA_HOME/bin/java -Xmx1524M -cp $cp -DCPATH_HOME='$cpathHome' org.mskcc.pathdb.tool.EntrezGeneAccessionParser @ARGV");
