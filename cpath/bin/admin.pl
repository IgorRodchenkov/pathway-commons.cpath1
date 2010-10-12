#!/usr/bin/perl

require "env.pl";

system ("$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5555 -ea -Xmx65536M -cp $cp -DCPATH_HOME=$cpathHome org.mskcc.pathdb.tool.Admin @ARGV");
