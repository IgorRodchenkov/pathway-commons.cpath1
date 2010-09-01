#!/usr/bin/perl

require "env.pl";

system ("java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5555 -ea -Xmx32768M -cp $cp -DCPATH_HOME=$cpathHome org.mskcc.pathdb.tool.Admin @ARGV");
