package org.mskcc.pathdb.tool;

import gnu.getopt.Getopt;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.ImportException;
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

    //  User Parameters
    private static String userName = null;
    private static String pwd = null;
    private static String fileName = null;
    private static boolean validateExternalReferences = true;
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
                indexer.start();
            } else if (command.equals(COMMAND_IMPORT)) {
                importData();
            } else if (command.equals(COMMAND_PRE_COMPUTE)) {
                LoadPreComputedQueries preCompute =
                        new LoadPreComputedQueries();
                preCompute.preCompute(fileName, xdebug);
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
            System.out.println("**** Error:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Imports a PSI-MI File or an External Reference File.
     */
    private static void importData() throws IOException, DaoException,
            ImportException {
        if (fileName.endsWith("xml") || fileName.endsWith("psi")
                || fileName.endsWith("mif")) {
            System.out.println("Based on the file extension, I am concluding "
                    + "that this is a PSI-MI File.");
            LoadPsi.importDataFile(fileName);
            ImportRecords importer = new ImportRecords();
            importer.transferData(validateExternalReferences);
        } else {
            System.out.println("Based on the file extension, I am concluding "
                    + "that this is a List of External References.");
            LoadExternalReferences loader = new LoadExternalReferences();
            loader.load(fileName);
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
        Getopt g = new Getopt("admin.pl", argv, "u:p:f:xd");
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'u':
                    userName = g.getOptarg();
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
        System.out.println("  -u, -u=name     Database User Name");
        System.out.println("  -p, -p=name     Database Password");
        System.out.println("  -f, -f=filename Name of File");
        System.out.println("  -d,             Shows all Debug/Log Messages");
        System.out.println("  -x              Skips Validation of External "
                + "References");
        System.out.println("\nWhere command is a one of:  ");
        System.out.println("  import          Imports Specified File.");
        System.out.println("                  Used to Import PSI-MI Files "
                + "or External Reference Files.");
        System.out.println("  index           Indexes All Items in cPath");
        System.out.println("  precompute      Precomputes all queries in "
                + "specified config file");
        System.exit(1);
    }
}