package org.mskcc.pathdb.util;

import org.mskcc.pathdb.task.ProgressMonitor;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Misc Utility Methods for Console Applications.
 *
 * @author Ethan Cerami
 */
public class ConsoleUtil {

    /**
     * Outputs Progress Messages to Console.
     * @param verbose   Verbose Flag
     * @param pMonitor  ProgressMonitor Object.
     */
    public static void showProgress(boolean verbose, ProgressMonitor pMonitor) {
        if (verbose) {
            int currentValue = pMonitor.getCurValue();
            System.out.print(".");
            if (currentValue % 100 == 0) {
                NumberFormat format = DecimalFormat.getPercentInstance();
                System.out.println("\nPercentage Complete:  "
                    + format.format(pMonitor.getPercentComplete()));
            }
        }
    }
}
