# Downloads all Pathways From CellMap.org and saves to pathways directory
import urllib
import xml.dom.minidom as minidom
import os

# Gets text of direct child with target name
def getChild (root, target):
	for node in root.childNodes:
		if node.localName == "NAME":
			return node.firstChild.nodeValue

# Get Complete List of Pathways
print "Getting List of Pathways"
url = "http://cancer.cellmap.org/cellmap/webservice.do?version=1.0&cmd=get_top_level_pathway_list&format=biopax"
filehandle = urllib.urlopen(url)
xml = ""
for line in filehandle:
	xml = xml + line
docRoot = minidom.parseString(xml)
items = docRoot.getElementsByTagName("bp:pathway")
nameList = []
idList = []
for item in items:
	name = getChild (item, "NAME")
	idElement = item.getElementsByTagName("bp:ID")
	id = idElement[1].firstChild.nodeValue
	nameList.append(name)
	idList.append(id)
	print "  Pathway:  " + name + ", cPath ID:  " + id
print "Done.  Got %s pathways" % (len(idList))

# Conditionally create pathways directory
dir = "pathways"
if os.path.isdir(dir):
	pass
else:
	os.mkdir (dir)

# Download all pathways 
for i in range(len(idList)):
	print "Downloading Pathway:  " + nameList[i]
	url = "http://cancer.cellmap.org/cellmap/webservice.do?version=1.0&cmd=get_record_by_cpath_id&format=biopax&q="+idList[i];
	print "Connecting to:  " + url
	fileName = dir + "/" + nameList[i] + ".owl"
	print "Saving to:  " + fileName
	urllib.urlretrieve(url, fileName)
