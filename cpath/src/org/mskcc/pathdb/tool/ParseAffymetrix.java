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
     *
     * @param args Command Line Arguments.
     */
    public static void main(String args[]) {
        if (args.length != 3) {
            System.out.println("Usage:  affy.pl input_file output_file "
                    + "column_prefix");
            System.out.println("\nThis command line utility "
                    + "parses Affymetrix annotation files, and extracts "
                    + "two columns of data:  Probe Set Affymetrix ID "
                    + "(always assumed to be the zeroth column), and "
                    + "a second column of the user's choosing.");
            System.out.println("\nFor example, this command:");
            System.out.println("affy.pl HG-U133_Plus_2_annot.csv temp.txt "
                    + "SwissProt");
            System.out.println("\nwill create a mapping file between Probe"
                    + "Set Affymetrix ID and SwissProt IDs that will look "
                    + "like this:  ");
            System.out.println("Affymetrix      SwissProt\n"
                    + "1007_s_at       BAC85426\n"
                    + "1007_s_at       Q08345\n"
                    + "1007_s_at       Q96T61\n"
                    + "1007_s_at       Q96T62\n"
                    + "...");
            System.out.println("\nThe resulting output file can then "
                    + "be imported into cPath.");
            System.exit(1);
        }
        try {
            File inFile = new File(args[0]);
            File outFile = new File(args[1]);
            String columnPrefix = args[2];
            ParseAffymetrixFileTask task =
                    new ParseAffymetrixFileTask(inFile, outFile, columnPrefix,
                            true);
            task.parse();
        } catch (Exception e) {
            System.out.println("**** Error:  " + e.getMessage());
            e.printStackTrace();
        }
    }
}