package org.mskcc.pathdb.tool;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDbCv;
import org.mskcc.pathdb.sql.transfer.UpdatePsiInteractor;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;

/**
 * Loads External References from text file.
 *
 * @author Ethan Cerami
 */
public class LoadExternalReferences {
    private static boolean verbose = true;
    private static final String EMPTY_FLAG = "---";
    private String db1, db2;
    private int numReferences = 0;
    private int numNonEmptyReferences = 0;
    private int numMatching = 0;
    private int numUpdates = 0;
    private int numLines = -1;

    /**
     * Loads External References from Specified File.
     * @param fileName File Name.
     */
    public void load(String fileName) {
        FileReader reader = null;
        try {
            countLines(fileName);
            reader = new FileReader(fileName);
            BufferedReader buffered = new BufferedReader(reader);
            readData(buffered);
            System.out.println("\nTotal Number of References:  "
                    + numReferences);
            System.out.println("Total Number of NonEmpty Refs:  "
                    + numNonEmptyReferences);
            System.out.println("Total Number of Matching Interactors:  "
                    + numMatching);
            System.out.println("Total Number of Refs Saved:  "
                    + numUpdates);
        } catch (FileNotFoundException e) {
            System.out.println("!!!!  Data loading aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        } catch (IOException e) {
            System.out.println("!!!!  Data loading aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        } catch (DaoException e) {
            System.out.println("!!!!  Data loading aborted due to error!");
            System.out.println("-->  " + e.getMessage());
        }
    }

    /**
     * Count Total Number of Lines in the File.
     */
    private void countLines(String fileName) throws IOException {
        FileReader reader = new FileReader(fileName);
        BufferedReader buffered = new BufferedReader(reader);
        String line = buffered.readLine();
        while (line != null) {
            this.numLines++;
            line = buffered.readLine();
        }
    }

    /**
     * Read In File Data.
     */
    private void readData(BufferedReader buffered) throws IOException,
            DaoException {
        String line = buffered.readLine();
        boolean dbNamesSet = false;
        while (line != null) {
            StringTokenizer tokenizer = new StringTokenizer(line, " \t");
            String token1 = (String) tokenizer.nextElement();
            String token2 = (String) tokenizer.nextElement();
            if (!dbNamesSet) {
                setDbNames(token1, token2);
                dbNamesSet = true;
            } else {
                ExternalReference ref1 =
                        new ExternalReference(db1, token1.trim());
                ExternalReference ref2 =
                        new ExternalReference(db2, token2.trim());
                updateRefs(ref1, ref2);
            }
            line = buffered.readLine();
        }
    }

    /**
     * Sets Global Database Names.
     */
    private void setDbNames(String token1, String token2) throws DaoException {
        DaoExternalDbCv daoCv = new DaoExternalDbCv();
        db1 = daoCv.getFixedCvTerm(token1);
        db2 = daoCv.getFixedCvTerm(token2);
    }

    /**
     * Conditionally Add External References.
     */
    private void updateRefs(ExternalReference ref1, ExternalReference ref2)
            throws DaoException {
        numReferences++;
        System.out.print(".");
        if (numReferences % 50 == 0) {
            displayProgress();
        }
        int index1 = ref1.getId().indexOf(EMPTY_FLAG);
        int index2 = ref2.getId().indexOf(EMPTY_FLAG);
        if (index1 == -1 && index2 == -1) {
            numNonEmptyReferences++;
            UpdatePsiInteractor updater = new UpdatePsiInteractor
                    (ref1, ref2, true);
            boolean needsUpdating = updater.needsUpdating();
            long id = updater.getcPathId();
            if (id > -1) {
                this.numMatching++;
            }
            if (needsUpdating) {
                updater.doUpdate();
                numUpdates++;
            }
        }
    }

    /**
     * Display Progress Meter.
     */
    private void displayProgress() {
        double percent = (numReferences / (double) numLines);
        NumberFormat format = DecimalFormat.getPercentInstance();
        String perc = format.format(percent);
        System.out.println("-->  " + numReferences + " / "
                + numLines + "  [" + perc + "]");
    }

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        XDebug xdebug = new XDebug();
        xdebug.startTimer();

        if (args.length > 0) {
            LoadExternalReferences loader = new LoadExternalReferences();
            loader.load(args[0]);
        } else {
            displayUsage();
        }
        xdebug.stopTimer();
        outputMsg("Total Loading Time:  "
                + xdebug.getTimeElapsed() + " ms");
    }

    /**
     * Displays Command Line Usage.
     */
    public static void displayUsage() {
        System.out.println("Command line usage:  loadRefs.sh filename");
    }

    /**
     * Conditionally Output Message to System.out.
     * @param msg User Message.
     */
    public static void outputMsg(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }
}
