package org.mskcc.pathdb.sql.references;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoBackgroundReferences;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.ConsoleUtil;
import org.mskcc.pathdb.util.FileUtil;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.BackgroundReferencePair;
import org.mskcc.pathdb.model.ReferenceType;

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
 *  <TH ALIGN=LEFT>UniProt</TH>
 *  <TH ALIGN=LEFT>PIR</TH>
 *  <TH ALIGN=LEFT>HUGE</TH>
 * </TR>
 * <TR>
 *  <TD>UNIPROT_1234</TD>
 *  <TD>PIR_1234 PIR_4321</TD>
 *  <TD>HUGE_1234</TD>
 * </TR>
 * <TR>
 *  <TD>UNIPROT_XYZ</TD>
 *  <TD>PIR_XYZ</TD>
 *  <TD>HUGE_XYZ</TD>
 * </TR>
 * <TR>
 *  <TD>UNIPROT_1234</TD>
 *  <TD></TD>
 *  <TD>HUGE_4321</TD>
 * </TR>
 * </TABLE>
 * <P>
 * This class uses a simple "stone-skipping" algorithm to create links
 * between IDs and stores these links within the database.  This is best
 * illustrated with sample data.  For example, the first line of data above
 * generates the following set of background reference links:
 * <UL>
 * <LI>UNIPROT_1234 &lt;---&gt; PIR_1234
 * <LI>UNIPROT_1234 &lt;---&gt; PIR_4321
 * <LI>PIR_1234 &lt;---&gt; HUGE_1234
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
     * @param dbList                ArrayList of External Databases
     * @param backgroundRefList     ArrayList of Background References
     * @param pMonitor              Progress Monitor
     * @param saveToDatabase        Flag to Save New Background Reference
     *                              To Database
    */
    public ImportUnificationRefs (ArrayList dbList, ArrayList backgroundRefList,
            ProgressMonitor pMonitor, boolean saveToDatabase) {
        this.dbList = dbList;
        this.backgroundRefList = backgroundRefList;
        this.pMonitor = pMonitor;
        this.saveToDatabase = saveToDatabase;
    }

    /**
     * Parses Data in Buffered Reader Object.
     * @param buf       Bufferred Reader Object
     * @return          Number of New Background Referenes Stored.
     * @throws IOException  Error Reading Data File.
     * @throws DaoException Error Accessing Database.
     */
    int parseData(BufferedReader buf)
            throws IOException, DaoException {
        int numRecordsSaved = 0;
        String line;
        //  For each line of data
        while ((line = FileUtil.getNextLine(buf)) != null) {
            ConsoleUtil.showProgress(pMonitor);
            ExternalDatabaseRecord dbRecord1 = null;
            String id1 = null;

            //  Split line, based on tab delimiter.
            String tokens[] = line.split("\t");

            //  Process all IDs
            for (int i = 0; i < tokens.length; i++) {
                ExternalDatabaseRecord dbRecord2;
                try {
                    dbRecord2 = (ExternalDatabaseRecord) dbList.get(i);
                } catch (IndexOutOfBoundsException e) {
                    throw e;
                }
                if (dbRecord1 != null && id1 != null
                        && tokens[i].length() > 0) {

                    //  Process Multiple IDs in Each Column
                    String ids[] = tokens[i].split("\\s+");
                    checkForManyIdentifiers(ids, tokens);
                    for (int j = 0; j < ids.length; j++) {
                        BackgroundReferencePair idPair =
                            createBackgroundReference(dbRecord1, id1,
                                    dbRecord2, ids, j);
                        if (saveToDatabase) {
                            numRecordsSaved += storeToDatabase(idPair);
                        } else {
                            backgroundRefList.add(idPair);
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
     * Creates Background Reference Object of type:  PROTEIN_UNIFICATION.
     */
    private BackgroundReferencePair createBackgroundReference
            (ExternalDatabaseRecord dbRecord1, String id1,
            ExternalDatabaseRecord dbRecord2, String[] ids, int j) {
        BackgroundReferencePair idPair =
                new BackgroundReferencePair
                    (dbRecord1.getId(), id1, dbRecord2.getId(), ids[j],
                    ReferenceType.PROTEIN_UNIFICATION);
        return idPair;
    }

    /**
     * Checks for Many Identifiers and Issues Warnings.
     */
    private void checkForManyIdentifiers(String[] ids, String[] tokens) {
        if (ids.length > 50) {
            pMonitor.setCurrentMessage("\nWarning!  "
                    + " Data line beginning with:  "
                    + tokens[0] + " contains " + ids.length
                    + " identifiers");
        }
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