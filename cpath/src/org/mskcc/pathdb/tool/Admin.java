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
package org.mskcc.pathdb.tool;

import gnu.getopt.Getopt;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.sql.transfer.MissingDataException;
import org.mskcc.pathdb.task.CountAffymetrixIdsTask;
import org.mskcc.pathdb.task.IndexLuceneTask;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.*;

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
    private static final int NOT_SET = -9999;

    //  User Parameters
    private static String userName = null;
    private static String pwd = null;
    private static String fileName = null;
    private static boolean validateExternalReferences = true;
    private static int taxonomyId = NOT_SET;
    private static boolean xdebugFlag = false;
    private static String command = null;

    /**
     * Main Method.
     *
     * @param argv Command Line Arguments.
     */
    public static void main(String[] argv) {
        try {
            XDebug xdebug = new XDebug();
            xdebug.startTimer();
            processCommandLineArgs(argv);
            getFromConsole();

            //  Turn on Command Line JDBC Connection
            JdbcUtil.isCommandLineApplication(true);

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
        } catch (Exception e) {
            System.out.println("\n\n-----------------------------------------");
            System.out.println("Fatal Error:  " + e.getMessage());
            if (xdebugFlag) {
                System.out.println("\nFull Details are available in the "
                    + "stack trace below.");
                e.printStackTrace();
            }
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * Imports a PSI-MI File or an External Reference File.
     */
    private static void importData() throws IOException, DaoException,
            ImportException, MissingDataException {
        File file = new File(fileName);
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println("Loading File:  " + files[i].getName());
                importDataFromSingleFile(files[i]);
            }
        } else {
            importDataFromSingleFile(file);
        }
        ImportRecords importer = new ImportRecords();
        importer.transferData(validateExternalReferences);
    }

    private static void importDataFromSingleFile(File file) throws IOException,
            DaoException, MissingDataException {
        String fileName = file.getName();
        if (fileName.endsWith("xml") || fileName.endsWith("psi")
                || fileName.endsWith("mif")) {
            System.out.println("Based on the file extension, I am concluding "
                    + "that this is a PSI-MI File.");
            LoadPsi.importDataFile(file);
        } else {
            System.out.println("Based on the file extension, I am concluding "
                    + "that this is a List of External References.");
            LoadExternalReferences loader = new LoadExternalReferences();
            loader.load(file);
        }
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
        Getopt g = new Getopt("admin.pl", argv, "o:u:p:f:xd");
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'u':
                    userName = g.getOptarg();
                    break;
                case 'o':
                    try {
                        taxonomyId = Integer.parseInt(g.getOptarg());
                    } catch (NumberFormatException e) {
                        taxonomyId = NOT_SET;
                    }
                    break;
                case 'p':
                    pwd = g.getOptarg();
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
        PropertyManager propertyManager = PropertyManager.getInstance();
        if (userName == null) {
            System.out.print("Enter Database User Name: ");
            userName = in.readLine();
            propertyManager.setProperty(PropertyManager.DB_USER, userName);
        }
        if (pwd == null) {
            System.out.print("Enter Database Password: ");
            pwd = in.readLine();
            propertyManager.setProperty(PropertyManager.DB_PASSWORD, pwd);
        }
        if (command.equals(COMMAND_IMPORT) && fileName == null) {
            System.out.print("Enter Path to Import File:  ");
            fileName = in.readLine();
        }
        if (command.equals(COMMAND_PRE_COMPUTE) && fileName == null) {
            System.out.print("Enter Path to Precompute Config File:  ");
            fileName = in.readLine();
        }
        if (command.equals(COMMAND_COUNT_AFFYMETRIX) && taxonomyId == NOT_SET) {
            getTaxonomyId();
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
        System.out.println("Copyright (c) 2004 Memorial Sloan-Kettering "
                + "Cancer Center.");
        System.out.println("\nAdministration Program for the cPath Database");
        System.out.println("Usage:  admin.pl [OPTIONS] command");
        System.out.println("  -f, -f=filename Name of File / Directory");
        System.out.println("  -d,             Shows all Debug/Log Messages/Stack Traces");
        System.out.println("  -u, -u=name     Database User Name");
        System.out.println("  -p, -p=name     Database Password");
        System.out.println("  -x              Skips Validation of External "
                + "References");
        System.out.println("  -o, -o=id       NCBI TaxonomyID");
        System.out.println("\nWhere command is a one of:  ");
        System.out.println("  import          Imports Specified File.");
        System.out.println("                  Used to Import PSI-MI Files "
                + "or External Reference Files.");
        System.out.println("  index           Indexes All Items in cPath");
        System.out.println("  precompute      Precomputes all queries in "
                + "specified config file.");
        System.out.println("  count_affy      Counts Records with Affymetrix "
                + "identifiers.");
        System.exit(1);
    }
}