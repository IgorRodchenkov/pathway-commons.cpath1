package org.mskcc.pathdb.util;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This Utility Class parses Affymetrix Annotation files, in an attempt
 * to map Affymetrix IDs to SWISS-PROT IDs.
 *
 * @author Ethan Cerami
 */
public class AffymetrixParser {
    private static final String QUOTE = "\"";

    public AffymetrixParser (File file) throws IOException {
        FileReader fReader = new FileReader(file);
        BufferedReader buf = new BufferedReader (fReader);
        String firstLine = buf.readLine();
        int swissProtColumn = determineSwissProtColumn (firstLine);

    }

    private int determineSwissProtColumn (String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            token = stripQuotes (token);
            System.out.println(token);
        }
        return 0;
    }

    private String stripQuotes (String token) {
        StringBuffer buffer = new StringBuffer(token);
        if (token.startsWith(QUOTE)) {
            buffer.deleteCharAt(0);
        }
        if (token.endsWith(QUOTE)) {
            buffer.deleteCharAt(buffer.length()-1);
        }
        return buffer.toString();
    }

}
