# Download All Reactome Data from Live Reactome.org Web Site
# Ethan Cerami, March 21, 2006
import urllib                                       
import urllib
import os
from sgmllib import SGMLParser

# Bare Bones HTML Parser
class HtmlParser(SGMLParser):
	'''Bare Bones HTML Parser for Parsing a List of Reactome Pathways'''

	# Reset HTML Parser
	def reset(self):                              
		SGMLParser.reset(self)
		self.pathways = []

	# Capture all non-HTML Text
	def handle_data(self, text):
		if text.startswith("[Pathway:"):
			self.pathways.append(text)

# Remove Empty Lines from File
# This is required, b/c some of the Reactome files start with empty lines
# and, this is considered invalid XML
def removeEmptyLines (fileName):
	'''Removes empty lines from the specified file.'''
	infile = open (fileName, 'r')
	outfile = open ("temp.txt", 'w')
	for line in infile.readlines():
		strippedline = line.strip()
		if (len(strippedline) > 0):
			outfile.write(line)
	infile.close()
	outfile.close()
	os.rename("temp.txt", fileName)

# Conditionally create reactome directory
dir = "reactome"
if os.path.isdir(dir):
	pass
else:
	os.mkdir (dir)

#  Get Pathway List
usock = urllib.urlopen("http://www.reactome.org/cgi-bin/instancebrowser?DB=gk_current&ID=170792&")
parser = HtmlParser()
parser.feed(usock.read())         
usock.close()                     
parser.close()

# Download Each Pathway, one at a time
for pathway in parser.pathways:
	print "Downloading Pathway:  " + pathway
	
	# Extract Reactome ID from HTML
	tokens = pathway.split(" ")
	values = tokens[0].split(":")
	id = values[1].strip("]")
	fileName = dir + "/" + id + ".owl"
	url = "http://reactome.org/cgi-bin/biopaxexporter?DB=gk_current&ID=" + id
	
	# Download Specific Pathway, based on Reactome ID
	print "Connecting to:  " + url
	urllib.urlretrieve(url, fileName)
	print "Stored to:  " + fileName
	removeEmptyLines (fileName)
