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
     *
     * @param verbose  Verbose Flag
     * @param pMonitor ProgressMonitor Object.
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
