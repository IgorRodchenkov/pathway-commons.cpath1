package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.task.ImportReferencesTask;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads External References from text file.
 *
 * @author Ethan Cerami
 */
public class LoadExternalReferences {

    /**
     * Loads External References from Specified File.
     *
     * @param fileName File Name.
     */
    public void load(String fileName) {
        FileReader reader = null;
        try {
            reader = new FileReader(fileName);
            ImportReferencesTask task =
                    new ImportReferencesTask(true, reader);
            task.importReferences();
        } catch (FileNotFoundException e) {
            System.out.println("!!!!  Data loading aborted due to error!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("!!!!  Data loading aborted due to error!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        } catch (DaoException e) {
            System.out.println("!!!!  Data loading aborted due to error!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main Method.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            LoadExternalReferences loader = new LoadExternalReferences();
            loader.load(args[0]);
        } else {
            displayUsage();
        }
    }

    /**
     * Displays Command Line Usage.
     */
    public static void displayUsage() {
        System.out.println("Command line usage:  admin.pl refs filename");
    }
}