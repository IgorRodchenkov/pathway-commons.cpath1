package org.mskcc.pathdb.util;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Misc File Utilities.
 *
 * @author Ethan Cerami.
 */
public class FileUtil {

    /**
     * Gets Number of Lines in Specified File.
     * @param file File.
     * @throws IOException Error Reading File.
     */
    public static int getNumLines(File file) throws IOException {
        int numLines = 0;
        FileReader reader = new FileReader(file);
        BufferedReader buffered = new BufferedReader(reader);
        String line = buffered.readLine();
        while (line != null) {
            if (!line.startsWith("#") && line.trim().length() > 0) {
                numLines++;
            }
            line = buffered.readLine();
        }
        reader.close();
        return numLines;
    }
}
