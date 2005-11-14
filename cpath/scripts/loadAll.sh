# A script that loads up all of Reactome and IOB
# This is not really useful for general use, and will probably only work on Ethan's machine
admin.pl -f ~/dev/cpath_data/affy/idMap/swissprot/human/ import -u tomcat -p kitty
admin.pl -f ~/dev/cpath_data/affy/idMap/refseq/human/ import -u tomcat -p kitty
admin.pl -f ~/dev/cpath_data/reactome import -u tomcat -p kitty
admin.pl -f ~/dev/cpath_data/iob import -u tomcat -p kitty
