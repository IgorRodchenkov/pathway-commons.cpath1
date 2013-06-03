#! /usr/bin/env python

import elementtree.ElementTree as ET
import os
import sys
import ftplib
import optparse
import MySQLdb
import shutil
import datetime
import re

# makes XML and FTPs the files to NCBI

#if len(sys.argv) != 2:
#   sys.stderr.write("Usage: python %s filename password\n" % sys.argv[0])
#   raise SystemExit(1)
#filename = sys.argv[1]
#password = sys.argv[2]
password = 'PWJy9BQF'

def get_uniprot():
  #first tidy up directory
  filename = './uniprot.txt'
  src = '/tmp/uniprot.txt'
  #old = './uniprot.txt.old'
  #shutil.copyfile(filename,old)
  conn = MySQLdb.connect (host = "localhost", user = "cbio", passwd = "cbio", db = "cpath")
  cur = conn.cursor()
  d = datetime.datetime.now()
  d = d.strftime("%Y%m%dT%H%M%S")
  file = src+"."+str(d)
  select = "select cpath_id, group_concat(linked_to_id) from external_link where external_db_id = 1 group by cpath_id into OUTFILE '" + file + "'"
  cur.execute(select)
  cur.close()
  conn.close()
  shutil.copyfile(file,filename)


def do_ftp(writefile):
  print "FTP files to NCBI"
  s = ftplib.FTP('ftp-private.ncbi.nlm.nih.gov','pathcom',password)
  directory = 'holdings'
  s.cwd(directory)
  ftpfile = writefile+'.xml'
  os.rename(writefile, ftpfile)
  print ftpfile
  s.storbinary('STOR '+ftpfile, open(ftpfile, 'rb'))
  s.quit()
  os.remove(ftpfile)


#tidy up directory
print "tidying up old files"
remove = 'rm pathwaycommons.piece.*.linkouts'
#os.system(remove)
oldfiles = os.path.isfile('filelist')
if oldfiles:
  #check for the files and remove
  for files in open('filelist'):
     file_e = os.path.isfile(files)
     if file_e:
      os.remove(files)
  os.remove('filelist')
# and now it is safe to start
# get data from pc database
get_uniprot()
filename = 'uniprot.txt'
file_exists = os.path.isfile(filename)
if file_exists:
  # NCBI has a filesize limit (15M) so the file is split into chunks
  splitcmd = 'split -l 40000 '+ filename+' '+ 'pathwaycommons.piece.'
  os.system(splitcmd)
  filelistcmd = 'ls -1 pathwaycommons.piece.* > filelist'
  os.system(filelistcmd)
  for files in open('filelist'):
    print files+'\n'
    files=files.rstrip()
    writefile = files+'.linkouts'
    outfile = open(writefile, 'w')
    # XML starts here, fairly self explanatory
    header = '''<?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE LinkSet PUBLIC "-//NLM//DTD LinkOut 1.0//EN"
    "http://www.ncbi.nlm.nih.gov/entrez/linkout/doc/LinkOut.dtd"
    [<!ENTITY base.url "http://www.pathwaycommons.org/pc/stable.do?db=UNIPROT">]>
    '''
    outfile.write(header)
    linkset = ET.Element("LinkSet")
    for eline in open(files):
      eline = eline.rstrip()
      fields = eline.split("\t")
      cpathID = fields[0]
      uniprotIDs = fields[1]
      idList = uniprotIDs.split(",")
      link = ET.SubElement(linkset, "Link")
      linkID = ET.SubElement(link,"LinkId")
      linkID.text = cpathID
      providerID = ET.SubElement(link, "ProviderId")
      providerID.text = "7665"
      objS = ET.SubElement(link, "ObjectSelector")
      db = ET.SubElement(objS, "Database")
      db.text = "Protein"
      objL = ET.SubElement(objS, "ObjectList")
      for ids in idList:
        query = ET.SubElement(objL, "Query")
        query_y = ids +  "[accn]" 
        query.text = query_y
      objU = ET.SubElement(link, "ObjectUrl")
      base = ET.SubElement(objU, "Base")
      base.text = "&base.url;" 
      rule = ET.SubElement(objU, "Rule")
      rule.text =  "&id=&lo.pacc;" 
    ET.ElementTree(linkset).write(outfile)
    outfile.close()
    writefile=writefile.rstrip()
    cmd = './makepretty.sh '+writefile
    print cmd
    os.system(cmd)
    os.remove(files)
    getsize = 'du -m '+ writefile
    filesize = os.system(getsize)
    if filesize < 15 :
      print filesize
      do_ftp(writefile)
    else:
	print writefile +' needs to be less than 15M'
    
  os.remove('filelist')  

