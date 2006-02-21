// $Id: RecordIndexerManager.java,v 1.3 2006-02-21 23:12:42 grossb Exp $
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
/*
 * Created 04-Jul-2005
 * @author Iain Keddie
 * @author <BR>$Author: grossb $ (last revision)
 * @version $Revision: 1.3 $
 */
package org.mskcc.pathdb.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.task.ProgressMonitor;

/**
 * Class to manage the threaded approach to running Lucene indexing
 * 
 * Class stores a queue of objects to be indexed and starts a number of threads
 * which remove items from the queue to process
 * 
 * @author idk37697
 */
public class RecordIndexerManager {

    private Logger log = Logger.getLogger(RecordIndexerManager.class);

    private LinkedList recordQueue; // collection of records to be indexed

    private RecordIndexer[] indexerList; // collection of threaded indexers

    static final int RECORD_QUEUE_MAX = 5000;

    private static int indexerCount = 4;

    static final String TEMP_DIR_NAME = "_tempIndexDir";

    private long storedRecordCount;

    /**
     * specify the number of indexers used and hence the number of threads
     * defaults to four
     * @param indexers the number of seperate index threads to create
     */
    public static void setIndexerCount(int indexers) {
        indexerCount = indexers;
    }   

    /**
     * initialise all of the indexing process objects
     * 
     * @param pMonitor cpath progress monitor
     * @param resetFlag If Set to True, Existing Index is deleted!
     * @throws IOException if problems creating the index
     */
    public void init(ProgressMonitor pMonitor, boolean resetFlag)
            throws IOException {
        log.debug("initialising RecordIndexerManager...");
        recordQueue = new LinkedList();
        indexerList = new RecordIndexer[indexerCount];

        // create the writers
        for (int i = 0; i < indexerCount; i++) {
            indexerList[i] = new RecordIndexer();
            indexerList[i].init(this, pMonitor, TEMP_DIR_NAME + i, resetFlag);
        }
        log.debug("RecordIndexerManager initialised.");
    }

    /**
     * add a record to the bottom of the queue for processing
     * @param record cpath record to be indexed
     */
    public synchronized void pushRecord(CPathRecord record) {
        recordQueue.addFirst(record);
    }

    /**
     * collect a record from the processing queue
     * @return pop a cpath record off the top of the queue
     */
    public synchronized CPathRecord popRecord() {
        CPathRecord record = null;
        if (recordQueue.size() > 0) {
            try {
                record = (CPathRecord) recordQueue.removeLast();
            } catch (NoSuchElementException e) {
                record = null;
            }
        }
        return record;
    }

    /**
     * @return Lucene Dirs for the index directories used.
     * @throws IOException exception collecting lucene directories
     */
    public Directory[] getLuceneDirs() throws IOException {
        ArrayList dirs = new ArrayList();

        // collect the dir for each index
        for (int i = 0; i < indexerCount; i++) {
            dirs.add(FSDirectory.getDirectory(TEMP_DIR_NAME + i, false));
        }
        return (Directory[]) dirs.toArray(new Directory[indexerCount]);
    }

    /**
     * @return true if the capacity of the queue is reached
     */
    public boolean queueFull() {
        return RECORD_QUEUE_MAX <= recordQueue.size();
    }

    /**
     * start all of the indexing processes
     */
    public void startIndexing() {
        RecordIndexer recordIndexer;
        for (int i = 0; i < indexerList.length; i++) {
            recordIndexer = indexerList[i];
            recordIndexer.start();
        }        
    }

    /**
     * signal to the indexers that they can finish
     */
    public void signalFinish() {
        for (int i = 0; i < indexerList.length; i++) {
            RecordIndexer recordIndexer = indexerList[i];
            recordIndexer.signalFinish();
        }
    }

    /**
     * testing method for single threaded mode
     * 
     * @param record cpath record to be indexed
     * @throws Exception general exception thrown
     */
    public void indexOneRecord(CPathRecord record) throws Exception {
        indexerList[0].indexRecord(record);
    }

    /**
     * join the threads to the main thread so we wait for them to finish
     */
    public void waitForThreads() {
        for (int i = 0; i < indexerList.length; i++) {
            RecordIndexer recordIndexer = indexerList[i];
            try {
                recordIndexer.join();
            } catch (InterruptedException e) {
                // not much we can do in this instance
            }
        }
    }

    /**
     * close all of the indexing threads managed 
     * @throws IOException IOError closing the indexer threads
     */
    public void closeAll() throws IOException {
        for (int i = 0; i < indexerList.length; i++) {
            RecordIndexer recordIndexer = indexerList[i];
            recordIndexer.close();
        }
    }
}
