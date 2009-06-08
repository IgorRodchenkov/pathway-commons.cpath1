#! /usr/bin/python

# ------------------------------------------------------------------------------
# imports

import re
import os
import sys

# ------------------------------------------------------------------------------
# sub-routines

#
# for the give biopax file, removed embedded root nodes
#
def process_biopax_file(BIOPAX_FILENAME):

    # open files
    BIOPAX_FILE = open(BIOPAX_FILENAME, 'r')
    COOKED_BIOPAX_FILENAME = BIOPAX_FILENAME + ".tmp"
    COOKED_BIOPAX_FILE = open(COOKED_BIOPAX_FILENAME, 'w')

    dump_root = True
    # interate over biopax
    for line in BIOPAX_FILE:
        # check for embedded <?xml> element
        element = re.match('^\s*<\?xml .*$', line)
        if element is not None:
            if dump_root:
                print >> COOKED_BIOPAX_FILE, line,
            continue
        # check for embedded <rdf:RDF> element
        element = re.match('^\s*<rdf:RDF .*$', line)
        if element is not None:
            if dump_root:
                print >> COOKED_BIOPAX_FILE, line,
            continue
        # check for embedded <owl:Ontology> element
        element = re.match('^\s*<owl:Ontology .*$', line)
        if element is not None:
            if dump_root:
                print >> COOKED_BIOPAX_FILE, line,
            continue
        # check for embedded <owl:imports> element
        element = re.match('^\s*<owl:imports .*$', line)
        if element is not None:
            if dump_root:
                print >> COOKED_BIOPAX_FILE, line,
            continue
        # finally check for </owl:Ontology>, signifies end of what we want to capture
        element = re.match('^\s*</owl:Ontology>', line)
        if element is not None:
            if dump_root:
                print >> COOKED_BIOPAX_FILE, line,
                dump_root = False
            continue
        # remove all </rdf:RDF>
        element = re.match('^\s*</rdf:RDF>', line)
        if element is not None:
            continue
        # made it here, dump the line
        print >> COOKED_BIOPAX_FILE, line,

    # cap cooked file
    print >> COOKED_BIOPAX_FILE, "</rdf:RDF>"

    # close files
    COOKED_BIOPAX_FILE.close()
    BIOPAX_FILE.close()

    # replace biopax file with cooked file
    os.system("mv " + COOKED_BIOPAX_FILENAME + " " + BIOPAX_FILENAME)

#
# for the give file, remove duplicates (using sort -unique)
#
def remove_duplicates(FILE_TO_PROCESS):

    # our temp file
	TEMP_FILE = FILE_TO_PROCESS + ".tmp"

	# we special case tab delimited - sort all lines in file but first (header)
	header = re.match('.*tab_delim_network.*', FILE_TO_PROCESS)
	if header is not None:
		COMMAND = ("head -1 < " + FILE_TO_PROCESS + " > " + TEMP_FILE)
		os.system(COMMAND)
		COMMAND = ("sed 1d < " + FILE_TO_PROCESS + " | sort -u >> " + TEMP_FILE)
		os.system(COMMAND)
	else:
		# execute sort on entire file
		COMMAND = ("sort -u < " + FILE_TO_PROCESS + " > " + TEMP_FILE)
		os.system(COMMAND)

	# replace processed file with temp file
	os.system("mv " + TEMP_FILE + " " + FILE_TO_PROCESS)

#
# runs given function on all files in given directory
#
def process_directory(directory, function):
    for filename in os.listdir(directory):
        filename = directory + "/" + filename
        if os.path.isfile(filename):
            function(filename)

# ------------------------------------------------------------------------------
# check for tcga environment var

CPATH_HOME_FOUND = 0
for key in os.environ.keys():
	if key == "CPATH_HOME":
		CPATH_HOME_FOUND = 1
		break
if not CPATH_HOME_FOUND:
	sys.exit("error: CPATH_HOME environment variable needs to be set")


# ------------------------------------------------------------------------------
# globals

if len(sys.argv) < 2:
	sys.exit("usage ./exportAll.py <snapshot dump location>")
else:
	SNAPSHOT_DUMP_DIR = sys.argv[1]
CPATH_HOME = os.environ['CPATH_HOME']
CLASSPATH = CPATH_HOME + "/build/WEB-INF/classes:";
LIBPATH = CPATH_HOME + "/lib/"
for filename in os.listdir(LIBPATH):
	if filename.endswith(".jar"):
		CLASSPATH = CLASSPATH + LIBPATH + filename + ":"

# ------------------------------------------------------------------------------
# check args

if not os.path.isdir(SNAPSHOT_DUMP_DIR):
	sys.exit("snapshot dump directory: " + SNAPSHOT_DUMP_DIR + " cannot be found!")

# ------------------------------------------------------------------------------
# execute java program

# for profiling
#-agentlib:jprofilerti=port=5005,nowait,id=139,config=/home/grossb/.jprofiler5/config.xml  -Xbootclasspath/a:/home/grossb/local/jprofiler5/bin/agent.jar

COMMAND = ("java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5555 -ea -Xmx8192M" +
		   " -cp " + CLASSPATH + " -DCPATH_HOME=" + CPATH_HOME + " org.mskcc.pathdb.tool.ExportAll " + SNAPSHOT_DUMP_DIR)
os.system(COMMAND)

# ------------------------------------------------------------------------------
# post process biopax files and remove embedded root nodes

process_directory(SNAPSHOT_DUMP_DIR + "/biopax/by_species/", process_biopax_file)
process_directory(SNAPSHOT_DUMP_DIR + "/biopax/by_source/", process_biopax_file)

# ------------------------------------------------------------------------------
# post process sif & tab delimited to remove duplicates

process_directory(SNAPSHOT_DUMP_DIR + "/sif/by_species/", remove_duplicates)
process_directory(SNAPSHOT_DUMP_DIR + "/sif/by_source/", remove_duplicates)
process_directory(SNAPSHOT_DUMP_DIR + "/tab_delim_network/by_species/", remove_duplicates)
process_directory(SNAPSHOT_DUMP_DIR + "/tab_delim_network/by_source/", remove_duplicates)

# ------------------------------------------------------------------------------
# now interate over snapshot dir and zip all files (.owl, .txt, .gmt, .sif)

for file_format_dir in os.listdir(SNAPSHOT_DUMP_DIR):
	file_format_dir = SNAPSHOT_DUMP_DIR + "/" + file_format_dir
	if os.path.isdir(file_format_dir):
		for category_dir in os.listdir(file_format_dir):
			category_dir = file_format_dir + "/" + category_dir
			if os.path.isdir(category_dir):
				for filename in os.listdir(category_dir):
					filename = category_dir + "/" + filename
					if os.path.isfile(filename):
						COMMAND = "zip -jT " + filename + ".zip " + filename + " ; rm -f " + filename
						os.system(COMMAND)

# ------------------------------------------------------------------------------
# lastly, move readme file over
os.system("cp ../dbData/SNAPSHOT-README.txt " + SNAPSHOT_DUMP_DIR + "/README.TXT")
