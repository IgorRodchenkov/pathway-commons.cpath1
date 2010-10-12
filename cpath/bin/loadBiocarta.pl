#!/usr/bin/perl

require "env.pl";

system ("$JAVA_HOME/bin/java -ea -Xmx1024M -cp $cp -DCPATH_HOME=$cpathHome org.mskcc.pathdb.tool.LoadBioCartaPid @ARGV");
