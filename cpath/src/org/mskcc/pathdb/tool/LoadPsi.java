package org.mskcc.pathdb.tool;

import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;

import java.io.*;

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
        String data = retrieveContentFromFile(new File(fileName));
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


    /**
     * Retrieves Content from local File System.
     * @param file File Object
     * @return File contents.
     * @throws java.io.IOException Error Retrieving file.
     */
    private static String retrieveContentFromFile(File file) throws IOException {
        StringBuffer content = new StringBuffer();
        FileReader reader = new FileReader(file);
        BufferedReader buffered = new BufferedReader(reader);
        long len = file.length();
        char cbuf[] = new char[(int) len];
        while (buffered.read(cbuf) != -1) {
            content.append(cbuf);
        }
        return content.toString();
    }
}