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
package org.mskcc.pathdb.sql.references;

import org.mskcc.pathdb.model.BackgroundReferencePair;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.sql.dao.DaoBackgroundReferences;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ConsoleUtil;
import org.mskcc.pathdb.util.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Parses a tab-delimited text file of PROTEIN_UNIFICATION references,
 * and stores the new background references to the database.
 * <P>
 * Here is an example unification reference file
 * (see:  testData/references/unification_refs.txt):
 * <BR>
 * <TABLE>
 * <TR>
 * <TH ALIGN=LEFT>UniProt</TH>
 * <TH ALIGN=LEFT>PIR</TH>
 * <TH ALIGN=LEFT>HUGE</TH>
 * </TR>
 * <TR>
 * <TD>UNIPROT_1234</TD>
 * <TD>PIR_1234 PIR_4321</TD>
 * <TD>HUGE_1234</TD>
 * </TR>
 * <TR>
 * <TD>UNIPROT_XYZ</TD>
 * <TD>PIR_XYZ</TD>
 * <TD>HUGE_XYZ</TD>
 * </TR>
 * <TR>
 * <TD>UNIPROT_1234</TD>
 * <TD></TD>
 * <TD>HUGE_4321</TD>
 * </TR>
 * </TABLE>
 * <P>
 * This class uses a simple "stone-skipping" algorithm to create links
 * between IDs and stores these links within the database.  This is best
 * illustrated with sample data.  For example, the first line of data above
 * generates the following set of background reference links:
 * <UL>
 * <LI>UNIPROT_1234 &lt;---&gt; PIR_1234
 * <LI>PIR_4321 &lt;---&gt; PIR_4321
 * <LI>PIR_4321 &lt;---&gt; HUGE_1234
 * </UL>
 * <P>
 * The parser is capable of handling blank columns.  For example, the third
 * line of sample data above generates just one background reference link:
 * <UL>
 * <LI>UNIPROT_1234 &lt;---&gt; HUGE_4321
 * </UL>
 *
 * @author Ethan Cerami.
 */
public class ImportUnificationRefs {
    private ArrayList dbList;
    private ArrayList backgroundRefList;
    private ProgressMonitor pMonitor;
    private boolean saveToDatabase;

    /**
     * Constructor.
     *
     * @param dbList            ArrayList of External Databases
     * @param backgroundRefList ArrayList of Background References
     * @param pMonitor          Progress Monitor
     * @param saveToDatabase    Flag to Save New Background Reference
     *                          To Database
     */
    public ImportUnificationRefs(ArrayList dbList, ArrayList backgroundRefList,
            ProgressMonitor pMonitor, boolean saveToDatabase) {
        this.dbList = dbList;
        this.backgroundRefList = backgroundRefList;
        this.pMonitor = pMonitor;
        this.saveToDatabase = saveToDatabase;
    }

    /**
     * Parses Data in Buffered Reader Object.
     *
     * @param buf Bufferred Reader Object
     * @return Number of New Background Referenes Stored.
     * @throws IOException  Error Reading Data File.
     * @throws DaoException Error Accessing Database.
     */
    int parseData(BufferedReader buf) throws IOException, DaoException {
        int numRecordsSaved = 0;
        String line;
        //  For each line of data
        while ((line = FileUtil.getNextLine(buf)) != null) {
            ConsoleUtil.showProgress(pMonitor);

            //  Tokenize the Input Line
            TabSpaceTokenizer tokenizer = new TabSpaceTokenizer (line);
            IndexedToken indexedToken = (IndexedToken) tokenizer.nextElement();

            //  Prime the loop by extracting the first ID record.
            ExternalDatabaseRecord dbRecord1 = (ExternalDatabaseRecord)
                    dbList.get (indexedToken.getColumnNumber());
            String id1 = indexedToken.getToken();

            //  Iterate through all remaining records.
            while (tokenizer.hasMoreElements()) {
                pMonitor.incrementCurValue();
                indexedToken = (IndexedToken) tokenizer.nextElement();
                ExternalDatabaseRecord dbRecord2 = (ExternalDatabaseRecord)
                        dbList.get(indexedToken.getColumnNumber());
                String id2 = indexedToken.getToken();

                //  Create Link between record1 and record2
                BackgroundReferencePair idPair = new BackgroundReferencePair
                    (dbRecord1.getId(), id1, dbRecord2.getId(), id2,
                    ReferenceType.PROTEIN_UNIFICATION);

                if (saveToDatabase) {
                    numRecordsSaved += storeToDatabase(idPair);
                } else {
                    backgroundRefList.add(idPair);
                }

                //  record 2 now becomes the initial record.
                dbRecord1 = dbRecord2;
                id1 = id2;
            }
        }
        return numRecordsSaved;
    }

    /**
     * Conditionally Saves Record to Database.
     *
     * @param refPair BackgroundReference Record..
     * @return 1 indicates record was saved;  0 indicates record was not saved.
     */
    private int storeToDatabase(BackgroundReferencePair refPair)
            throws DaoException {
        //  The Database Access Object ensures that the record does not
        //  already exist in the db
        DaoBackgroundReferences dao = new DaoBackgroundReferences();
        boolean success = dao.addRecord(refPair, false);
        return success ? 1 : 0;
    }
}