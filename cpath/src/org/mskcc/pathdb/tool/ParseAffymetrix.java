package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.task.ParseAffymetrixFileTask;

import java.io.File;

/**
 * Command Line Tool for Running Affymetrix Parser.
 *
 * @author Ethan Cerami
 */
public class ParseAffymetrix {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main (String args[]) {
        if (args.length != 2) {
            System.out.println("Usage:  affy.pl input_file output_file");
            System.exit(1);
        }
        try {
            File inFile = new File(args[0]);
            File outFile = new File(args[1]);
            ParseAffymetrixFileTask task =
                    new ParseAffymetrixFileTask (inFile,  outFile);
            task.setVerbose(true);
            task.parse();
        } catch (Exception e) {
            System.out.println("**** Error:  " + e.getMessage());
            e.printStackTrace();
        }
    }
}