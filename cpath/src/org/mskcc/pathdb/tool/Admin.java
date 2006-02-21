// $Id: Admin.java,v 1.39 2006-02-21 22:51:11 grossb Exp $
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
package org.mskcc.pathdb.tool;

import gnu.getopt.Getopt;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.references.ParseBackgroundReferencesTask;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.task.*;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.cache.EhCache;
import org.mskcc.pathdb.xdebug.XDebug;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

/**
 * Command Line cPath Administrator.
 *
 * @author Ethan Cerami
 */
public class Admin {
    //  Command Constants
    private static final String COMMAND_INDEX = "index";
    private static final String COMMAND_IMPORT = "import";
    private static final String COMMAND_PRE_COMPUTE = "precompute";
    private static final String COMMAND_COUNT_AFFYMETRIX = "count_affy";
    private static final String COMMAND_VALIDATE = "validate";
    private static final String COMMAND_EXPORT = "export";
    private static final String COMMAND_QUERY = "query";
    private static final int NOT_SET = -9999;

    //  User Parameters
    private static String dbName = null;
    private static String dbHost = null;
    private static String dbUser = null;
    private static String dbPwd = null;
    private static String fileName = null;
    private static boolean validateExternalReferences = true;
    private static int taxonomyId = NOT_SET;
    private static String ftQuery = null;
    private static boolean xdebugFlag = false;
    private static boolean removeAllInteractionXrefs = false;
    private static String command = null;

    /**
     * Main Method.
     *
     * @param argv Command Line Arguments.
     */
    public static void main(String[] argv) {
        try {
            EhCache.initCache();

            //  Load build.properties
            String cpathHome = System.getProperty("CPATH_HOME");
            String separator = System.getProperty("file.separator");
            Properties buildProps = new Properties();
            buildProps.load(new FileInputStream (cpathHome
                + separator + "build.properties"));

            dbUser = buildProps.getProperty("db.user");
            dbPwd = buildProps.getProperty("db.password");
            dbName = buildProps.getProperty("db.name");
            dbHost = buildProps.getProperty("db.host");

            //  Process Command Line Arguments
            processCommandLineArgs(argv);

            PropertyManager propertyManager = PropertyManager.getInstance();
            propertyManager.setProperty(PropertyManager.DB_USER, dbUser);
            propertyManager.setProperty(PropertyManager.DB_PASSWORD, dbPwd);
            propertyManager.setProperty(CPathConstants.PROPERTY_MYSQL_DATABASE,
                            dbName);
            propertyManager.setProperty(PropertyManager.DB_LOCATION, dbHost);

            System.out.println("cPath Admin.  cPath Version:  "
                    + CPathConstants.VERSION);
            System.out.println("Copyright (c) 2005 Memorial Sloan-Kettering "
                    + "Cancer Center.\n");

            System.out.println("----------------------------------");
            System.out.println("Database Name:     " + dbName);
            System.out.println("Database Host:     " + dbHost);
            System.out.println("Database User:     " + dbUser);
            System.out.println("Database Password: " + dbPwd);
            System.out.println("----------------------------------\n");

            getFromConsole();
            XDebug xdebug = new XDebug();
            xdebug.startTimer();

            //  Turn on Command Line JDBC Connection
            JdbcUtil.setCommandLineFlag(true);

            if (command.equals(COMMAND_INDEX)) {
                IndexLuceneTask indexer = new IndexLuceneTask(true, xdebug);
                indexer.executeTask();
            } else if (command.equals(COMMAND_IMPORT)) {
                importData();
            } else if (command.equals(COMMAND_PRE_COMPUTE)) {
                LoadPreComputedQueries preCompute =
                        new LoadPreComputedQueries();
                preCompute.preCompute(fileName, xdebug);
            } else if (command.equals(COMMAND_COUNT_AFFYMETRIX)) {
                CountAffymetrixIdsTask task = new CountAffymetrixIdsTask
                        (taxonomyId, true);
            } else if (command.equals(COMMAND_VALIDATE)) {
                ValidateXmlTask validator = new ValidateXmlTask
                        (new File(fileName));
                validator.validate(true);
            } else if (command.equals(COMMAND_EXPORT)) {
                ExportInteractionsToText export =
                        new ExportInteractionsToText(true);
                export.executeTask();
            } else if (command.equals(COMMAND_QUERY)) {
                QueryFullText.queryFullText(ftQuery);
            } else {
                throw new IllegalArgumentException("Command Not Recognized");
            }
            if (xdebugFlag) {
                System.out.println("----------------------------------------");
                System.out.println(xdebug.getCompleteLog());
            }
            xdebug.stopTimer();

            System.out.println("Total Time:  " + xdebug.getTimeElapsed()
                    + " ms");
        } catch (SAXParseException e) {
            System.out.println("\n-----------------------------------------");
            System.out.println("XML Validation Error:  " + e.getMessage());
            System.out.println("Error Located at line:  " + e.getLineNumber()
                    + ", column:  " + e.getColumnNumber());
            System.out.println("-----------------------------------------");
        } catch (SAXException e) {
            System.out.println("\n-----------------------------------------");
            System.out.println("XML Validation Error:  " + e.getMessage());
            System.out.println("-----------------------------------------");
        } catch (Exception e) {
            System.out.println("\n-----------------------------------------");
            if (e instanceof NullPointerException) {
                System.out.println("Fatal Error:  Null Pointer Exception");
            } else {
                System.out.println("Fatal Error:  " + e.getMessage());
            }
            if (xdebugFlag) {
                System.out.println("\nFull Details are available in the "
                        + "stack trace below.");
                e.printStackTrace();
            }
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * Imports a BioPAX, PSI-MI or an External Reference File.
     */
    private static void importData() throws IOException, DaoException,
            ImportException, SAXException,
            DataServiceException {
        if (fileName != null) {
            File file = new File(fileName);
            if (file.isDirectory()) {
                System.out.println("Loading all files in directory:  "
                        + file.getAbsolutePath());
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    //  Avoid loading up hidden files, such as .DS_Store
                    //  files on Mac OS X.
                    if (!files[i].getName().startsWith(".")) {
                        System.out.println(">  Loading File:  "
                                + files[i].getAbsolutePath());
                        importDataFromSingleFile(files[i]);
                    }
                }
            } else {
                importDataFromSingleFile(file);
            }
        }
    }

    private static void importDataFromSingleFile(File file) throws IOException,
            DaoException, SAXException, DataServiceException,
            ImportException {
        String fileName = file.getName();
        long importId = NOT_SET;
        if (fileName.endsWith("xml") || fileName.endsWith("psi")
                || fileName.endsWith("mif")) {
            importId = importPsiMiFile(file);
        } else if (fileName.endsWith("owl")) {
            importId = LoadBioPaxPsi.importDataFile(file,
                    XmlRecordType.BIO_PAX);
        } else if (fileName.endsWith("txt")) {
            ParseBackgroundReferencesTask task =
                    new ParseBackgroundReferencesTask(file, true);
            int numRecordsSaved = task.parseAndStoreToDb();
            NumberFormat formatter = new DecimalFormat("#,###,###");
            System.out.println("\nTotal Number of Background References "
                    + "Stored:  " + formatter.format(numRecordsSaved));
            //  Keep the following code here for future reference:
            //  FileReader reader = new FileReader(file);
            //  ImportReferencesTask task =
            //    new ImportReferencesTask(true, reader);
            //  task.importReferences();
        } else {
            System.out.println("Cannot determine file type.  Skipping...");
        }
        if (importId != NOT_SET) {
            ImportRecordTask importTask = new ImportRecordTask(importId,
                    validateExternalReferences, removeAllInteractionXrefs,
                    true);
            importTask.transferRecord();
        }
    }

    /**
     * Imports PSI-MI File into Import Table.
     */
    private static long importPsiMiFile(File file) throws IOException,
            SAXException, DataServiceException, DaoException {
        ValidateXmlTask validator = new ValidateXmlTask(file);
        boolean isValid = validator.validate(false);
        long importId = NOT_SET;
        if (isValid) {
            importId = LoadBioPaxPsi.importDataFile(file, XmlRecordType.PSI_MI);
        } else {
            System.out.println("\n-------------------------------------");
            System.out.println("Import aborted due to invalid XML.");
            System.out.println("Use the validate command to view "
                    + "a list of XML validation errors.\n"
                    + "For example:  admin.pl"
                    + " -f human_small.xml validate");
            System.out.println("-------------------------------------");
            System.exit(-1);
        }
        return importId;
    }

    /**
     * Processes Command Line Arguments.
     *
     * @param argv Command Line Arguments.
     */
    private static void processCommandLineArgs(String[] argv)
            throws FileNotFoundException {
        if (argv.length == 0) {
            displayHelp();
        }
        
        PropertyManager manager = PropertyManager.getInstance();
        
        Getopt g = new Getopt("admin.pl", argv, "o:u:p:f:h:b:xdr");
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'b':   
                    dbName = g.getOptarg();
                    dbName = dbName.replaceFirst("=", "");
                    break;
                case 'h':
                    dbHost = g.getOptarg();
                    dbHost = dbHost.replaceFirst("=", "");
                    break;
                case 'u':
                    dbUser = g.getOptarg();
                    break;
                case 'o':
                    try {
                        taxonomyId = Integer.parseInt(g.getOptarg());
                    } catch (NumberFormatException e) {
                        taxonomyId = NOT_SET;
                    }
                    break;
                case 'p':
                    dbPwd = g.getOptarg();
                    break;
                case 'f':
                    fileName = g.getOptarg();
                    checkFileExists(fileName);
                    break;
                case 'd':
                    xdebugFlag = true;
                    break;
                case 'x':
                    validateExternalReferences = false;
                    break;
                case 'r':
                    removeAllInteractionXrefs = true;
                    break;
                case 'q':
                    ftQuery = g.getOptarg();
                    break;
            }
        }
        int i = g.getOptind();
        if (i < argv.length) {
            command = argv[g.getOptind()];
        } else {
            throw new IllegalArgumentException("You Must Specify a Command");
        }
    }
 
    /**
     * Verifies that the specified file exists.
     */
    private static void checkFileExists(String fileName)
            throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File Not Found:  " + fileName);
        }
    }

    /**
     * If any data is missing, prompt from console.
     *
     * @throws IOException IO Exception
     */
    private static void getFromConsole() throws IOException {
        BufferedReader in = new BufferedReader
                (new InputStreamReader(System.in));
        if (command.equals(COMMAND_PRE_COMPUTE) && fileName == null) {
            System.out.print("Enter Path to Precompute Config File:  ");
            fileName = in.readLine();
        }
        if (command.equals(COMMAND_COUNT_AFFYMETRIX) && taxonomyId == NOT_SET) {
            getTaxonomyId();
        }
        if (command.equals(COMMAND_VALIDATE) && fileName == null) {
            System.out.print("Enter Path to XML File:  ");
            fileName = in.readLine();
        }
        if (command.equals(COMMAND_QUERY) && ftQuery == null) {
            System.out.print("Enter Full Text Query Term:  ");
            ftQuery = in.readLine();
        }
    }

    /**
     * Prompts for an NCBI Taxonomy ID.
     */
    private static void getTaxonomyId() throws IOException {
        BufferedReader in = new BufferedReader
                (new InputStreamReader(System.in));
        System.out.print("Enter NCBI Taxonomy Identifier:  ");
        while (taxonomyId == NOT_SET) {
            String line = in.readLine();
            try {
                taxonomyId = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please Try Again.  Enter NCBI Taxonomy "
                        + "Identifier:  ");
            }
        }
    }

    /**
     * Displays Help Message.
     */
    private static void displayHelp() {
        System.out.println("cPath Admin.  cPath Version:  "
                + CPathConstants.VERSION);
        System.out.println("Copyright (c) 2005 Memorial Sloan-Kettering "
                + "Cancer Center.");
        System.out.println("\nAdministration Program for the cPath Database");
        System.out.println("Usage:  admin.pl [OPTIONS] command");
        System.out.println("  -f, -f=filename Name of File / Directory");
        System.out.println("  -d,             Shows all Debug/Log "
                + "Messages/Stack Traces");
        System.out.println("  -u, -u=name     Database User Name "
            + "(overrides build.properties)");
        System.out.println("  -p, -p=name     Database Password "
            +"(overrides build.properties)");
        System.out.println ("  -h, -h=hostname Database Server Name "
            + "(overrides build.properties)");
        System.out.println("  -b, -b=database Database name "
            + "(overrides build.properties)");
        System.out.println("  -x              Skips Validation of External "
                + "References");
        System.out.println("  -o, -o=id       NCBI TaxonomyID");
        System.out.println("  -q, -q=term     Full Text Query Term");
        System.out.println("\nWhere command is a one of:  ");
        System.out.println("  import          Imports Specified File.");
        System.out.println("                  Used to Import BioPAX Files, "
                + "PSI-MI Files");
        System.out.println("                  ID Mapping Files, or External "
                + "Database files");
        System.out.println("  index           Indexes All Items in cPath");
        System.out.println("  precompute      Precomputes all queries in "
                + "specified config file.");
        System.out.println("  count_affy      Counts Records with Affymetrix "
                + "identifiers.");
        System.out.println("  validate        Validates the specified XML "
                + "file.");
        System.out.println("  query           Executes Full Text Query");
        System.out.println("\nExtra Options (not guaranteed to be available "
                + "in future versions of cPath)");
        System.out.println("  -r              Removes all Interaction PSI-MI "
                + "xrefs  (not recommended)");
        System.out.println("                  Used to temporarily import "
                + "buggy HRPD PSI-MI Files.");
        System.exit(1);
    }
}
