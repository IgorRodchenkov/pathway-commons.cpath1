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
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ConsoleUtil;
import org.mskcc.pathdb.util.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Parses a tab-delimited text file of LINK_OUT references,
 * and stores the new background references to the database.
 * <P>
 * Here are the first few lines from a sample link_out reference file
 * (see:  testData/references/link_out_refs.txt):
 * <P>
 * <table>
 * <tr>
 * <th>Affymetrix</th>
 * <th>Entrez_Gene</th>
 * <th>UniProt</th>
 * <th>UniGene</th>
 * <th>Ensembl</th>
 * </tr>
 * <tr>
 * <td>1000_at</td>
 * <td>5595</td>
 * <td>P27361 Q8NHX0 Q8NHX1 Q7Z3H5 Q9BWJ1</td>
 * <td>Hs.861</td>
 * <td>ENSG00000102882</td>
 * </tr>
 * <tr>
 * <td>1001_at</td>
 * <td >7075</td>
 * <td>P35590 Q9HBS4</td>
 * <td>Hs.78824</td>
 * <td>ENSG00000066056</td>
 * </tr>
 * <tr>
 * <td>1002_f_at</td>
 * <td>1557</td>
 * <td>P33261 Q16743</td>
 * <td>Hs.282409</td>
 * <td>ENSG00000165841</td>
 * </tr>
 * <tr>
 * <td>1003_s_at</td>
 * <td>643</td>
 * <td></td>
 * <td>Hs.113916</td>
 * <td>ENSG00000160683</td>
 * </tr>
 * <tr>
 * <td>1004_at</td>
 * <td></td>
 * <td></td>
 * <td></td>
 * <td>ENSG00000160683</td>
 * </tr>
 * </table>
 * <p/>
 * This class uses a simple "spoke" algorithm to create links between
 * IDs and stores these links within the database.  It's call "spoke"
 * because directional links are created from all IDs to the primary ID
 * specified in column 1.  In the sample data file above, Affymetrix IDs
 * serve as the hub of the spoke, and all other identifers point to hub.
 * <P>
 * This is best illustrated with sample data.
 * For example, the first line of data above generates the following
 * set of background reference links:
 * <p/>
 * <P>
 * The parser is capable of handling blank columns.  For example, the third
 * line of sample data above generates just one background reference link:
 * <UL>
 * <LI>UNIPROT_1234 &lt;---&gt; HUGE_4321
 * </UL>
 *
 * @author Ethan Cerami.
 */
public class ImportLinkOutRefs {
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
    public ImportLinkOutRefs(ArrayList dbList, ArrayList backgroundRefList,
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
     * @throws IOException     Error Reading Data File.
     * @throws DaoException    Error Accessing Database.
     * @throws ImportException Error Importing Data.
     */
    int parseData(BufferedReader buf)
            throws IOException, DaoException, ImportException {
        int numRecordsSaved = 0;
        String line;

        //  First, determine the LINK_OUT database
        ExternalDatabaseRecord linkOutDb = (ExternalDatabaseRecord)
                dbList.get(0);

        //  For each line of data
        while ((line = FileUtil.getNextLine(buf)) != null) {
            ConsoleUtil.showProgress(pMonitor);

            //  Split line, based on tab delimiter.
            String tokens[] = line.split("\t");

            //  Extract the LINK_OUT ID.
            String linkOutId = tokens[0];

            if (linkOutId.trim().length() > 0) {

                //  Process all IDs
                for (int i = 1; i < tokens.length; i++) {
                    ExternalDatabaseRecord dbRecord =
                            (ExternalDatabaseRecord) dbList.get(i);

                    //  Handle Blank Columns, and
                    //  Process Multiple IDs in Each Column
                    if (tokens[i].trim().length() > 0) {
                        String ids[] = tokens[i].split("\\s+");
                        for (int j = 0; j < ids.length; j++) {
                            BackgroundReferencePair idPair =
                                    createBackgroundReference(dbRecord, ids, j,
                                            linkOutDb, linkOutId);
                            if (saveToDatabase) {
                                numRecordsSaved += storeToDatabase
                                        (idPair);
                            } else {
                                backgroundRefList.add(idPair);
                            }
                        }
                    }
                }
                pMonitor.incrementCurValue();
            }
        }
        return numRecordsSaved;
    }

    /**
     * Creates Background Reference Object of type:  LINK_OUT.
     */
    private BackgroundReferencePair createBackgroundReference
            (ExternalDatabaseRecord dbRecord1, String ids1[], int index,
            ExternalDatabaseRecord dbRecord2, String id2) {
        BackgroundReferencePair idPair =
                new BackgroundReferencePair
                        (dbRecord1.getId(), ids1[index], dbRecord2.getId(),
                                id2, ReferenceType.LINK_OUT);
        return idPair;
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