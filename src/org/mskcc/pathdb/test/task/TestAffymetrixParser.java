// $Id: TestAffymetrixParser.java,v 1.12 2006-02-22 22:47:51 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.test.task;

import junit.framework.TestCase;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.task.ParseAffymetrixFileTask;

import java.io.File;

/**
 * Tests the AffymetrixParser Task.
 *
 * @author Ethan Cerami.
 */
public class TestAffymetrixParser extends TestCase {

    /**
     * Tests the Affymetrix Parser.
     *
     * @throws Exception Error Reading / Writing Files.
     */
    public void testParser() throws Exception {
        String outFileName = "testData/affymetrix/AffyId_SampleOutput.txt";
        File inFile = new File("testData/affymetrix/HG-U133_Sample.csv");
        File outFile = new File(outFileName);
        ParseAffymetrixFileTask task =
                new ParseAffymetrixFileTask(inFile, outFile, "SwissProt",
                        false);
        task.parse();

        int swpColumn = task.getIdColumn();
        assertEquals(19, swpColumn);

        //  Verify the Output File
        ContentReader reader = new ContentReader();
        String content = reader.retrieveContent(outFileName);
        String lines[] = content.split("\n");

        //  Verify First four identifiers.
        assertEquals("Affymetrix\tSwissProt", lines[0]);
        assertEquals("1007_s_at\tBAC85426", lines[1]);
        assertEquals("1007_s_at\tQ08345", lines[2]);
        assertEquals("1007_s_at\tQ96T61", lines[3]);
        assertEquals("1007_s_at\tQ96T62", lines[4]);

        //  Delete the Sample File
        outFile.delete();
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can properly parse Affymetrix data files";
    }
}
