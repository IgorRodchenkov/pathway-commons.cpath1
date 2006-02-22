// $Id: ParseBackgroundReferencesTask.java,v 1.10 2006-02-22 22:47:51 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.task.Task;
import org.mskcc.pathdb.util.file.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Parses and Imports Background Reference Files.
 * <P>Background reference files must adhere to the following set of rules:
 * <UL>
 * <LI>The file must be tab-delimited.
 * <LI>The column headings must match controlled vocabulary terms for
 * external databases, as already defined in cPath. For example, "SWP" and
 * "SWISSPROT" are valid terms used for referring to the SWISSPROT databse.
 * <LI>Multiple identifiers can be are specified within a single column by
 * using a space delimeter.
 * <LI>Any data field can be blank.
 * <LI>If the file specifies XXXX_UNIFICATION references, all databases
 * specified in the header must be of type:  XXXX_UNIFICATION.
 * <LI>If the file specifies LINK_OUT references, the first column of data
 * must be of type:  LINK_OUT.  All links will be directional, and will point
 * to this first column of data.  For example, if a data file consists of three
 * columns:  Affymetrix, SwissProt, and LocusLink, two sets of links will
 * be created:  SwissProt --&gt; Affymetrix, and LocusLink --&gt; Affymetrix.
 * </UL>
 * <P>
 * Here is an example PROTEIN_UNIFICATION reference file
 * (see:  testData/unification_refs.txt):
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
 * </UL>
 *
 * @author Ethan Cerami.
 */
public class ParseBackgroundReferencesTask extends Task {
    /**
     * Data File containing Background References.
     */
    private File file;

    /**
     * Flag to save all records to the database.
     * If this is set to false, all records will be stored in an
     * ArrayList, instead of the Database.  Primarily used for JUnit testing
     * purposes only.
     */
    private boolean saveToDatabase = true;

    /**
     * Array List of All BackgroundReference Objects.
     */
    private ArrayList backgroundRefList = new ArrayList();

    /**
     * Number of Protein Unification Databases Referenced in Data File.
     */
    private int numProteinUnificationDbs = 0;

    /**
     * Number of LinkOut Database Reference in Data File.
     */
    private int numLinkOutDbs = 0;

    /**
     * List of database terms, as originally specified in the file header.
     */
    private ArrayList dbTermList = new ArrayList();

    /**
     * Tab Character.
     */
    private static final String TAB_CHAR = "\t";

    /**
     * ReferenceType Object.
     * Stores the type of data in the data file.  Can currently be of type:
     * PROTEIN_UNIFICATION or LINK_OUT.
     */
    private ReferenceType referenceType;

    /**
     * Constructor.
     *
     * @param file        File Containing Background References.
     * @param consoleMode Console Mode.
     */
    public ParseBackgroundReferencesTask(File file, boolean consoleMode) {
        super("Parse Background References File", consoleMode);
        this.file = file;
    }

    /**
     * Parses a Background References File, and automatically stores all new
     * records to the database.
     *
     * @return number of new background reference records saved to database.
     * @throws IOException     Error Reading Data File.
     * @throws DaoException    Error Connecting to Database.
     * @throws ImportException Error importing data.
     */
    public int parseAndStoreToDb() throws IOException, DaoException,
            ImportException {
        this.numLinkOutDbs = 0;
        this.numProteinUnificationDbs = 0;

        //  Initialize Progress Monitor.
        initProgressMonitor();

        //  Open File for Reader
        FileReader fReader = new FileReader(file);
        BufferedReader buf = new BufferedReader(fReader);

        //  Extract Database Headers
        String line = FileUtil.getNextLine(buf);

        //  Verify that the file header is tab delimited.
        if (line.indexOf(TAB_CHAR) == -1) {
            throw new ImportException("Unable to import background "
                    + "reference file.  File must be tab delimited.  Check "
                    + "the file and try again.");
        }

        StringTokenizer tokenizer = tokenize(line);
        ArrayList dbList = extractDatabaseList(tokenizer);

        //  Verify that we have at least two databases in data file.
        if (dbList.size() < 2) {
            throw new ImportException("Unable to import background "
                    + "reference file.  File must contain at least "
                    + "two columns of data.");
        }

        //  Check if these are all UNIFICATION References
        if (numProteinUnificationDbs == dbList.size()) {
            this.referenceType = ReferenceType.PROTEIN_UNIFICATION;
            this.getProgressMonitor().setCurrentMessage
                    ("Saving PROTEIN_UNIFICATION References.");
            ImportUnificationRefs importUnificationRefs = new
                    ImportUnificationRefs(dbList, backgroundRefList,
                            getProgressMonitor(), saveToDatabase);
            return importUnificationRefs.parseData(buf);
        } else {
            ExternalDatabaseRecord db = (ExternalDatabaseRecord)
                    dbList.get(0);
            if (!db.getDbType().equals(ReferenceType.LINK_OUT)) {
                throw new ImportException("Unable to import background "
                        + "reference file of " + ReferenceType.LINK_OUT
                        + " data.  First column must be of type:  "
                        + ReferenceType.LINK_OUT + ".");
            }
            this.referenceType = ReferenceType.LINK_OUT;
            this.getProgressMonitor().setCurrentMessage
                    ("Saving LINK_OUT References.");
            ImportLinkOutRefs imporLinkOutRefs = new ImportLinkOutRefs
                    (dbList, backgroundRefList, getProgressMonitor(),
                            saveToDatabase);
            return imporLinkOutRefs.parseData(buf);
        }
    }

    /**
     * Gets ReferenceType of Data File.
     *
     * @return ReferenceType Object.
     */
    public ReferenceType getReferenceType() {
        return referenceType;
    }

    /**
     * Parses the Background References File, and automatically generates a
     * list of BackgroundReference Records.  No data is saved to the database.
     * This method is primarily used by the JUnit test.
     *
     * @return ArrayList of BackgroundReference Objects.
     * @throws IOException     Error Reading Data File.
     * @throws DaoException    Error Connecting to Database.
     * @throws ImportException Error importing data.
     */
    public ArrayList parseAndGenerateList() throws IOException, DaoException,
            ImportException {
        saveToDatabase = false;
        this.parseAndStoreToDb();
        return this.backgroundRefList;
    }

    /**
     * Initializes the Progress Monitor.
     *
     * @throws IOException Error Reading File.
     */
    private void initProgressMonitor() throws IOException {
        NumberFormat formatter = new DecimalFormat("#,###,###");
        ProgressMonitor pMonitor = this.getProgressMonitor();
        pMonitor.setCurrentMessage("Parsing Background Reference File:  "
                + file);
        int numLines = FileUtil.getNumLines(file);
        pMonitor.setCurrentMessage
                ("Number of Lines of Data in File:  "
                + formatter.format(numLines));
        pMonitor.setMaxValue(numLines);
        pMonitor.setCurValue(1);
    }

    /**
     * Extracts external database records for all those specified in the
     * file header.
     */
    private ArrayList extractDatabaseList(StringTokenizer tokenizer)
            throws DaoException, ImportException {
        ArrayList dbList = new ArrayList();
        DaoExternalDb dao = new DaoExternalDb();
        while (tokenizer.hasMoreTokens()) {
            String dbTerm = tokenizer.nextToken();
            dbTermList.add(dbTerm);
            ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(dbTerm);
            if (dbRecord == null) {
                throw new ImportException("Unable to import background "
                        + "references data file.  Database:  " + dbTerm
                        + " is not known to cPath.  Check the file and "
                        + "try again.");
            } else {
                dbList.add(dbRecord);
                if (dbRecord.getDbType().equals
                        (ReferenceType.PROTEIN_UNIFICATION)) {
                    numProteinUnificationDbs++;
                } else if (dbRecord.getDbType().equals
                        (ReferenceType.LINK_OUT)) {
                    numLinkOutDbs++;
                }
            }
        }
        return dbList;
    }

    /**
     * Tokenize based on tab character.
     *
     * @param line String line.
     * @return String Tokenizer.
     */
    private StringTokenizer tokenize(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, TAB_CHAR);
        return tokenizer;
    }
}
