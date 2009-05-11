#! /usr/bin/python

# ------------------------------------------------------------------------------
# imports

import os
import sys

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

COMMAND = ("java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -ea -Xmx8192M" +
		   " -cp " + CLASSPATH + " -DCPATH_HOME=" + CPATH_HOME + " org.mskcc.pathdb.tool.ExportAll " + SNAPSHOT_DUMP_DIR)
os.system(COMMAND)

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
