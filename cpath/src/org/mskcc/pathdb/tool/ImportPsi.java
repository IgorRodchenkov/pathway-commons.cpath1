package org.mskcc.pathdb.tool;

import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.model.ImportSummary;

import java.io.File;
import java.io.IOException;

/**
 * Command Line Tool for Importing PSI Data directly to cPath.
 *
 * @author Ethan Cerami
 */
public class ImportPsi {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            importData(args);
        } else {
            System.out.println("Command line usage:  import_psi.sh"
                + " filename");
        }
    }

    /**
     * Imports Single PSI-MI File to cPath.
     * @param args Command Line Arguments.
     */
    private static void importData(String[] args) {
        File file = new File(args[0]);
        ContentReader contentReader = new ContentReader();
        String xml = null;
        try {
            xml = contentReader.retrieveContentFromFile(file);
            ImportPsiToCPath importer = new ImportPsiToCPath();
            ImportSummary summary = importer.addRecord(xml);
            outputSummary(summary);
        } catch (IOException e) {
            System.out.println("\n!!!!  Import aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        } catch (ImportException e) {
            System.out.println("\n!!!!  Import aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        }
    }

    /**
     * Displays Summary of Import.
     * @param summary ImportSummary object.
     */
    private static void outputSummary (ImportSummary summary) {
        System.out.println();
        System.out.println ("Import Summary:  ");
        System.out.println ("-----------------------------------------------");
        System.out.println ("# of Matching Interactors found in DB:  "
                + summary.getNumInteractorsFound());
        System.out.println ("# of Interactors saved to DB:           "
                + summary.getNumInteractorsSaved());
        System.out.println ("# of Interactions saved to DB:          "
                + summary.getNumInteractionsSaved());
        System.out.println ("-----------------------------------------------");
    }
}
