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
     * @param file File.
     * @throws IOException  File Input Error.
     * @throws DaoException Data Access Error.
     */
    public static void importDataFile(File file) throws IOException,
            DaoException {
        String description = file.getName();
        System.out.println("Loading data file:  " + file.getName());
        System.out.println("Description:  " + description);
        String data = retrieveContentFromFile(file);
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
     *
     * @param file File Object
     * @return File contents.
     * @throws java.io.IOException Error Retrieving file.
     */
    private static String retrieveContentFromFile(File file)
            throws IOException {
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