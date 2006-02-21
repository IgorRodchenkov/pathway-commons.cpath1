// $Id: LuceneWriter.java,v 1.10 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.mskcc.pathdb.sql.transfer.ImportException;

import java.io.IOException;

/**
 * Write to the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneWriter {
    private String dir = LuceneConfig.getLuceneDirectory();
    private Analyzer analyzer = LuceneConfig.getLuceneAnalyzer();
    private IndexWriter writer = null;
    private Logger log = Logger.getLogger(LuceneWriter.class);
    
    /**
     * Constructor
     * @param dir directory to create index
     * @param resetFlag If Set to True, Existing Index is deleted!
     * @throws IOException IO Error.
     */
    public LuceneWriter(String dir, boolean resetFlag) throws IOException {
        this.dir = dir;
        init(resetFlag);       
    }

    /**
     * Constructor.
     *
     * @param resetFlag If Set to True, Existing Index is deleted!
     * @throws IOException IO Error.
     */
    public LuceneWriter(boolean resetFlag) throws IOException {
        init(resetFlag);
    }

    /**
     * initialises this object
     * @param resetFlag If Set to True, Existing Index is deleted!
     * @throws IOException IO Error.
     */
    public void init(boolean resetFlag) throws IOException {
        log.debug("Createing index on: " + dir);
        writer = new IndexWriter(dir, analyzer, resetFlag);

        //  Set CompoundFile to True
        //  From Lucene Javadoc:  When on, multiple files for each segment
        //  are merged into a single file once the segment creation is
        //  finished.  Setting to true is one of the recommended solutions
        //  for resolving the "too many open files" error which occurs
        //  on Linux.
        writer.setUseCompoundFile(true);

        //  Increase the Merge Factor.
        //  Results in faster indexing.  For details, refer to:
        //  http://www.onjava.com/pub/a/onjava/2003/03/05/lucene.html
        writer.mergeFactor = 100;
        
        // see recomendation from 
        // http://wiki.apache.org/jakarta-lucene/PainlessIndexing
        writer.minMergeDocs = 1000;
    }

    /**
     * Adds New Record to the Lucene Index.
     *
     * @param item ItemToIndex.
     * @throws ImportException Error Adding New Record to Lucene.
     */
    public void addRecord(ItemToIndex item) throws ImportException {
        try {
            //  Index all Fields in ItemToIndex
            Document document = new Document();
            int numFields = item.getNumFields();
            for (int i = 0; i < numFields; i++) {
                Field field = item.getField(i);
                document.add(field);
            }
            //  Add New Document to Lucene
            writer.addDocument(document);
        } catch (IOException e) {
            throw new ImportException(e);
        }
    }

    /**
     * add the indexes from the supplied directories to this one.
     * @param dirs array of directories to be added
     * @throws IOException IO exception
     */
    public void addIndexes(Directory[] dirs)throws IOException {
        writer.addIndexes(dirs);
    }
    
    /**
     * Optimizes Lucene Index Files.
     * For details:  http://www.onjava.com/pub/a/onjava/2003/03/05/lucene.html.
     *
     * @throws IOException File Error.
     */
    public void optimize() throws IOException {
        writer.optimize();
    }

    /**
     * Closes the Indexer Writer.
     *
     * @throws IOException File Error.
     */
    public void closeWriter() throws IOException {
        writer.close();
    }
    /**
     * @return Returns the dir.
     */
    public String getDirectory() {
        return dir;
    }
}
