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
package org.mskcc.pathdb.task;

import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.IdMapRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoIdMap;
import org.mskcc.pathdb.util.ConsoleUtil;
import org.mskcc.pathdb.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Parses a tab-delimited text file of ID mappings, and stores the new ID
 * mappings to the database.
 * <P>
 * Here is an example ID mappings file (see:  testData/id_map.txt):
 * <PRE>
 * Affymetrix       UniGene        LocusLink       SwissProt       RefSeq
 * 1552275_3p_s_at  Hs.77646                       AAH08943 Q727A4 NP_060241
 * 1552275_3p_s_at                                                 NP_06024
 * </PRE>
 * <P>A few items to note:
 * <UL>
 * <LI>The file is tab-delimited.
 * <LI>The column headings must match controlled vocabulary terms for
 * external databases, as already defined in cPath. If an input file contains
 * invalid heading names, this class will immediately halt processing and
 * report the error to the end user.
 * <LI>Multiple IDs can be are specified within a single column by using a
 * space delimeter. For example, see the SwissProt column above.
 * <LI>Any field can be blank, and this class will take appropriate action to
 * process blank fields.
 * </UL>
 * <P>
 * This class uses a simple "stone-skipping" algorithm to create links
 * between IDs and stores these links within the database.  This is best
 * illustrated with sample data.  For example, the first line of data above
 * generates the following set of internal links:
 * <P>
 * <IMG SRC="doc-files/link_graph.png"/>
 * <P>
 * @author Ethan Cerami.
 */
public class ParseIdMappingsTask extends Task {
    /**
     * File containing ID Mappings.
     */
    private File file;

    /**
     * Flag to save all IdMapRecord objects to the database.
     * If this is set to false, all IdMapRecords will be stored in an
     * ArrayList, instead of the Database.
     */
    private boolean saveToDatabase = true;

    /**
     * Array List of All IdMapRecord Objects.
     */
    private ArrayList idList = new ArrayList();

    /**
     * Constructor.
     *
     * @param file        File Containing ID Mappings.
     * @param consoleMode Console Mode.
     */
    public ParseIdMappingsTask(File file, boolean consoleMode) {
        super("Parse ID Mapping File", consoleMode);
        this.file = file;
    }

    /**
     * Parses the ID Mappings File, and automatically stores all new
     * ID mappings to the database.
     *
     * @return number of new id mapping records saved to database.
     * @throws IOException  Error Reading Data File.
     * @throws DaoException Error Connecting to Database.
     */
    public int parseAndStoreToDb() throws IOException, DaoException {
        //  Initialize Number of Records Saved To Database
        int numRecordsSaved = 0;

        //  Initialize Progress Monitor.
        initProgressMonitor();

        //  Open File for Reader
        FileReader fReader = new FileReader(file);
        BufferedReader buf = new BufferedReader(fReader);

        //  Extract Database Headers
        String line = getNextLine(buf);
        StringTokenizer tokenizer = tokenize(line);
        ArrayList dbList = extractDatabaseList(tokenizer);

        //  For each line of data
        while ((line = getNextLine(buf)) != null) {
            ProgressMonitor pMonitor = this.getProgressMonitor();
            ConsoleUtil.showProgress(pMonitor);
            ExternalDatabaseRecord dbRecord1 = null;
            String id1 = null;

            //  Split line, based on tab delimiter.
            String tokens[] = line.split("\t");

            //  Process all IDs
            for (int i = 0; i < tokens.length; i++) {
                ExternalDatabaseRecord dbRecord2 =
                        (ExternalDatabaseRecord) dbList.get(i);
                if (dbRecord1 != null && id1 != null
                        && tokens[i].length() > 0) {

                    //  Process Multiple IDs in Each Column
                    String ids[] = tokens[i].split("\\s+");
                    for (int j = 0; j < ids.length; j++) {
                        IdMapRecord idRecord = new IdMapRecord
                                (dbRecord1.getId(), id1, dbRecord2.getId(),
                                        ids[j]);
                        if (saveToDatabase) {
                            numRecordsSaved += storeToDatabase(idRecord);
                        } else {
                            idList.add(idRecord);
                        }
                    }
                }

                //  Update Current DB/ID Pair
                if (tokens[i].length() > 0) {
                    dbRecord1 = dbRecord2;
                    String ids[] = tokens[i].split("\\s+");
                    id1 = ids[0];
                }
            }
            pMonitor.incrementCurValue();
        }
        return numRecordsSaved;
    }

    /**
     * Parses the ID Mappings File, and automatically generates a list of
     * IdMapRecords.  No data is saved to the database.
     * This method is primarily used by the JUnit test.
     *
     * @return ArrayList of IdMapRecord Objects.
     * @throws IOException  Error Reading Data File.
     * @throws DaoException Error Connecting to Database.
     */
    public ArrayList parseAndGenerateList() throws IOException, DaoException {
        saveToDatabase = false;
        this.parseAndStoreToDb();
        return this.idList;
    }

    /**
     * Conditionally Saves Record to Database.
     *
     * @param idRecord IdMapRecord.
     * @return 1 indicates record was saved;  0 indicates record was not saved.
     */
    private int storeToDatabase(IdMapRecord idRecord) throws DaoException {
        //  The Database Access Object ensures that the record does not
        //  already exist in the db
        DaoIdMap dao = new DaoIdMap();
        boolean success = dao.addRecord(idRecord, false);
        return success ? 1 : 0;
    }

    /**
     * Initializes the Progress Monitor.
     *
     * @throws IOException Error Reading File.
     */
    private void initProgressMonitor() throws IOException {
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Parsing ID Mappings File:  " + file);
        int numLines = FileUtil.getNumLines(file);
        pMonitor.setCurrentMessage
                ("Number of Lines of Data in File:  " + numLines);
        pMonitor.setMaxValue(numLines);
        pMonitor.setCurValue(1);
    }

    /**
     * Extracts database records for all those specified in the file header.
     */
    private ArrayList extractDatabaseList(StringTokenizer tokenizer)
            throws DaoException {
        ArrayList dbList = new ArrayList();
        DaoExternalDb dao = new DaoExternalDb();
        while (tokenizer.hasMoreTokens()) {
            String dbTerm = tokenizer.nextToken();
            ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(dbTerm);
            if (dbRecord == null) {
                throw new DaoException("Database:  " + dbTerm
                        + " is not known.");
            } else {
                dbList.add(dbRecord);
            }
        }
        return dbList;
    }

    /**
     * Gets Next Line of Input.  Filters out Empty Lines and Comments.
     */
    private String getNextLine(BufferedReader buf) throws IOException {
        String line = buf.readLine();
        while (line != null && (line.trim().length() == 0
                || line.trim().startsWith("#"))) {
            line = buf.readLine();
        }
        return line;
    }

    /**
     * Tokenize based on default white space delimeters
     *
     * @param line String line.
     * @return String Tokenizer.
     */
    private StringTokenizer tokenize(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        return tokenizer;
    }
}