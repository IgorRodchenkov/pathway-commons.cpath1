package org.mskcc.pathdb.tool;

import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command Line Tool for Loading PSI Data to the CPath Import Table.
 *
 * @author Ethan Cerami
 */
public class LoadPsi {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            loadDataFile(args[0]);
        } else {
            System.out.println("Command line usage:  load_psi.sh"
                    + " filename");
        }
    }

    private static String getDescription() throws IOException {
        System.out.print("Please enter a description [minimum 5 chars]:  ");
        BufferedReader input = new BufferedReader
                (new InputStreamReader(System.in));
        String line = input.readLine();
        while (line != null && line.length() <= 4) {
            System.out.print("You must supply a description "
                    + "[miniumum 5 chars]:  ");
            line = input.readLine();
        }
        return line;
    }

    /**
     * Loads Data File.
     * @param fileName File Name.
     */
    private static void loadDataFile(String fileName) {
        try {
            String description = getDescription();
            System.out.println("Loading data file:  " + fileName);
            System.out.println("Description:  " + description);
            File file = new File(fileName);
            ContentReader reader = new ContentReader();
            String data = reader.retrieveContentFromFile(file);
            DaoImport dbImport = new DaoImport();
            dbImport.addRecord(description, data);
            System.out.println("Loading complete.");
        } catch (DaoException e) {
            System.out.println("\n!!!!  Loading of data aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n!!!!  Loading of data aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        }
    }
}
