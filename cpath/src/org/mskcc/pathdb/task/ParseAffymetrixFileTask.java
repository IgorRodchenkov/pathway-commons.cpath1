package org.mskcc.pathdb.task;

import org.mskcc.pathdb.util.ConsoleUtil;

import java.io.*;

/**
 * Parses Affymetrix Annotation files, in an attempt to map SWISS-PROT IDs to
 * Affymetrix IDs.
 *
 * @author Ethan Cerami
 */
public class ParseAffymetrixFileTask extends Task {
    private static final String SWISS_PROT = "SwissProt";
    private int swissProtColumn;
    private File inFile;
    private File outFile;
    private FileWriter fileWriter;

    /**
     * Constructor.
     *
     * @param inFile  InputFile.
     * @param outFile OutputFile.
     */
    public ParseAffymetrixFileTask(File inFile, File outFile) {
        super("Parse Affymetrix File");
        this.inFile = inFile;
        this.outFile = outFile;
    }

    /**
     * Constructor.
     *
     * @throws IOException Error Reading File.
     */
    public void parse() throws IOException {
        try {
            outputMsg("Parsing Affymetrix File:  " + inFile);
            outputMsg("Analyzing Input File...");
            int numLines = getNumLines();
            outputMsg("Total Number of Lines in Input File:  " + numLines);
            pMonitor.setMaxValue(numLines);
            pMonitor.setCurValue(1);
            fileWriter = new FileWriter(outFile);
            FileReader fReader = new FileReader(inFile);
            BufferedReader buf = new BufferedReader(fReader);
            String firstLine = buf.readLine();
            swissProtColumn = determineSwissProtColumn(firstLine);
            extractIds(buf, swissProtColumn);
            outputMsg("\nMapping File is now complete:  " + outFile);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    /**
     * Gets Column Number where SWISS-PROT IDs are Specified.
     *
     * @return Column Number, or -1 if not found.
     */
    public int getSwissProtColumn() {
        return this.swissProtColumn;
    }

    /**
     * Determines Column Number for SWISS-PROT ID.
     *
     * @param line Line of comma separated identifiers.
     * @return column number of -1, if not found.
     */
    private int determineSwissProtColumn(String line) {
        String fields[] = line.split("\",");
        for (int i = 0; i < fields.length; i++) {
            String token = stripQuotes(fields[i]);
            if (token.equals(SWISS_PROT)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Extracts AffyIds and All SWISS-PROT IDs.
     *
     * @param buf       BufferedReader.
     * @param swpColumn Location of SWISS-PROT IDs.
     * @throws IOException Error Reading File.
     */
    private void extractIds(BufferedReader buf, int swpColumn)
            throws IOException {
        String lineSeparator = System.getProperty("line.separator");
        fileWriter.write("Swiss-Prot\tAffymetrix" + lineSeparator);
        String line = buf.readLine();
        while (line != null) {
            ConsoleUtil.showProgress(verbose, pMonitor);
            String fields[] = line.split("\",");
            String affyId = stripQuotes(fields[0]);
            String swpIdStr = stripQuotes(fields[swpColumn]);
            String swpIds[] = swpIdStr.split("///");
            for (int i = 0; i < swpIds.length; i++) {
                String swpId = swpIds[i].trim();
                String mappingLine = new String
                        (swpId + "\t" + affyId + lineSeparator);
                fileWriter.write(mappingLine);
            }
            line = buf.readLine();
            pMonitor.incrementCurValue();
        }
    }

    /**
     * Strings Leading and Trailing Quotes (if present).
     *
     * @param token Token, possibly with quotes.
     * @return Token, without any quotes.
     */
    private String stripQuotes(String token) {
        return token.replaceAll("\"", "");
    }

    /**
     * Gets Number of Lines in Input File.
     */
    private int getNumLines() throws IOException {
        int numLines = 0;
        FileReader reader = new FileReader(inFile);
        BufferedReader buffered = new BufferedReader(reader);
        String line = buffered.readLine();
        while (line != null) {
            numLines++;
            line = buffered.readLine();
        }
        return numLines;
    }
}