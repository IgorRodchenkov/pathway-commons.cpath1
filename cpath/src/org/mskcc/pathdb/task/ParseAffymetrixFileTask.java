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
package org.mskcc.pathdb.task;

import org.mskcc.pathdb.util.file.FileUtil;
import org.mskcc.pathdb.util.tool.ConsoleUtil;

import java.io.*;

/**
 * Parses Affymetrix Annotation files, in an attempt to map Any ID to
 * Affymetrix IDs.
 *
 * @author Ethan Cerami
 */
public class ParseAffymetrixFileTask extends Task {
    private String columnPrefix;
    private int columnIndex;
    private File inFile;
    private File outFile;
    private FileWriter fileWriter;

    /**
     * Constructor.
     *
     * @param inFile       InputFile.
     * @param outFile      OutputFile.
     * @param columnPrefix Column Prefix.
     * @param consoleMode  Console Mode Flag.
     */
    public ParseAffymetrixFileTask(File inFile, File outFile,
            String columnPrefix, boolean consoleMode) {
        super("Parse Affymetrix File", consoleMode);
        this.inFile = inFile;
        this.outFile = outFile;
        this.columnPrefix = columnPrefix;
    }

    /**
     * Constructor.
     *
     * @throws IOException Error Reading File.
     */
    public void parse() throws IOException {
        try {
            ProgressMonitor pMonitor = this.getProgressMonitor();
            pMonitor.setCurrentMessage("Parsing Affymetrix File:  " + inFile);
            pMonitor.setCurrentMessage("Analyzing Input File...");
            int numLines = FileUtil.getNumLines(inFile);
            pMonitor.setCurrentMessage
                    ("Total Number of Lines in Input File:  " + numLines);
            pMonitor.setMaxValue(numLines);
            pMonitor.setCurValue(1);
            fileWriter = new FileWriter(outFile);
            FileReader fReader = new FileReader(inFile);
            BufferedReader buf = new BufferedReader(fReader);
            String firstLine = buf.readLine();
            columnIndex = determineIdColumn(firstLine);
            extractIds(buf, columnIndex);
            pMonitor.setCurrentMessage("Mapping File is now complete:  "
                    + outFile);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    /**
     * Gets Column Number where IDs are Specified.
     *
     * @return Column Number, or -1 if not found.
     */
    public int getIdColumn() {
        return this.columnIndex;
    }

    /**
     * Determines Column Number for SWISS-PROT ID.
     *
     * @param line Line of comma separated identifiers.
     * @return column number of -1, if not found.
     */
    private int determineIdColumn(String line) {
        String fields[] = line.split("\",");
        for (int i = 0; i < fields.length; i++) {
            String token = stripQuotes(fields[i]);
            if (token.startsWith(columnPrefix)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Extracts AffyIds and All Matching IDs.
     *
     * @param buf       BufferedReader.
     * @param swpColumn Location of SWISS-PROT IDs.
     * @throws IOException Error Reading File.
     */
    private void extractIds(BufferedReader buf, int swpColumn)
            throws IOException {
        String lineSeparator = System.getProperty("line.separator");
        fileWriter.write(columnPrefix + "\tAffymetrix" + lineSeparator);
        String line = buf.readLine();
        while (line != null) {
            ProgressMonitor pMonitor = this.getProgressMonitor();
            ConsoleUtil.showProgress(pMonitor);
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
}