package org.mskcc.pathdb.tool;

import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command Line Tool for Loading PSI Data into cPath.
 *
 * @author Ethan Cerami
 */
public class LoadPsi {

    /**
     * Imports PSI-MI Data File.
     *
     * @param fileName File Name.
     * @throws IOException  File Input Error.
     * @throws DaoException Data Access Error.
     */
    public static void importDataFile(String fileName) throws IOException,
            DaoException {
        String description = getDescription();
        System.out.println("Loading data file:  " + fileName);
        System.out.println("Description:  " + description);
        ContentReader reader = new ContentReader();
        String data = reader.retrieveContent(fileName);
        DaoImport dbImport = new DaoImport();
        dbImport.addRecord(description, data);
        System.out.println("XML Document Loaded.  Ready for Import.");
    }

    /**
     * Gets Description of Data File.
     */
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
}