/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
/*
 * Created 04-Jul-2005
 * @author Iain Keddie
 * @author <BR>$Author: keddie $ (last revision)
 * @version $Revision: 1.1 $
 */
package org.mskcc.pathdb.lucene;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.tool.ConsoleUtil;
import org.mskcc.pathdb.xdebug.XDebug;

/**
 * threaded class to carry out a single indexing task this class monitors a que
 * of records and if there are records in it, they will be removed and indexed
 * <br>
 * This continues until it is signaled to stop
 *
 * @author Iain Keddie
 */
public class RecordIndexer extends Thread {

    private Logger log = Logger.getLogger(RecordIndexer.class);

    private LuceneWriter indexWriter;

    private ProgressMonitor pMonitor;

    private RecordIndexerManager recordManager;

    private boolean continuing = true;

    /**
     * empty constructor
     */
    public RecordIndexer() {
    }

    /**     
     * @return the directory used by the index writer
     */
    public String getDirectory() {
        return indexWriter.getDirectory();
    }


    /**
     * initialise this object
     * @param recordManager the containing record manager
     * @param pMonitor the cpath progress monitor
     * @param dir directory containing the index
     * @param resetFlag If Set to True, Existing Index is deleted!
     * @throws IOException if there are problems creating the index
     */
    public void init(RecordIndexerManager recordManager,
            ProgressMonitor pMonitor, String dir, boolean resetFlag)
            throws IOException {
        this.pMonitor = pMonitor;
        this.recordManager = recordManager;
        indexWriter = new LuceneWriter(dir, resetFlag);
    }

    /**
     * Method to kick of the indexing
     */
    public void run() {
        CPathRecord record;
        log.debug("Starting Indexing thread");
        continuing = true;

        try {
            record = getRecord(recordManager);

            // keep going until we've been told
            while (continuing || record != null) {
                if (record != null) {
                    indexRecord(record);
                    record = null;
                } else {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        // no need to do anything here
                    }
                }
                // check again for new records
                record = getRecord(recordManager);
            }
        } catch (Exception e) {
            System.out.println("Unexpected error throw by indexing thread ["
                    + e.getMessage() + "]");
            e.printStackTrace();
        }
        log.debug("Stopping Indexing thread");
    }

    /**
     * collect the items to be indexed, then pass them to the indexer
     * @param record record to be indexed
     * @throws SQLException Database Error
     * @throws IOException I/O Error
     * @throws ImportException Import Error
     * @throws AssemblyException Assembly Error
     * @throws JDOMException JDOM Error
     */
    public void indexRecord(CPathRecord record) throws SQLException,
            IOException, ImportException, AssemblyException, JDOMException {

        pMonitor.incrementCurValue();
        ConsoleUtil.showProgress(pMonitor);

        // Create XML Assembly Object of Specified Record
        XmlAssembly xmlAssembly = XmlAssemblyFactory.createXmlAssembly(record
                .getId(), 1, new XDebug());

        //  Determine which fields to index
        ItemToIndex item = IndexFactory.createItemToIndex(record.getId(),
                xmlAssembly);

        // Then, index all fields in Lucene
        indexWriter.addRecord(item);

        item = null;
        xmlAssembly = null;
    }

    /**
     * collect a record from the manager
     * @param recordManager
     * @return the record
     */
    private CPathRecord getRecord(RecordIndexerManager recordManager) {
        return recordManager.popRecord();
    }

    /**
     * signal to finish up indexing     
     */
    public void signalFinish() {
        continuing = false;
    }

    /**
     *  close the indexWriter
     * @throws IOException if there are problems closing the indexer
     */
    public void close() throws IOException {
        indexWriter.closeWriter();
    }
}
