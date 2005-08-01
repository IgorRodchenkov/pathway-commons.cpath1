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
package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.TestCase;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.schemas.biopax.ImportBioPaxToCPath;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.util.ArrayList;

/**
 * Tests the ImportBioPaxToCPath Class.
 *
 * @author Ethan Cerami
 */
public class TestImportBioPaxToCPath extends TestCase {

    /**
     * Tests BioPAX Import.
     *
     * @throws Exception All Exceptions.
     */
    public void testImport() throws Exception {
        DaoOrganism daoOrganism = new DaoOrganism();
        ArrayList organismList = daoOrganism.getAllOrganisms();

        //  Before import, we have 0 organisms
        int numOrganisms = organismList.size();

        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent
                ("testData/biopax/biopax1_sample1.owl");
        ProgressMonitor pMonitor = new ProgressMonitor();
        ImportBioPaxToCPath importer = new ImportBioPaxToCPath();
        ImportSummary summary = importer.addRecord(xml, pMonitor);
        assertEquals(1, summary.getNumPathwaysSaved());
        assertEquals(4, summary.getNumInteractionsSaved());
        assertEquals(7, summary.getNumPhysicalEntitiesSaved());

        //  After import, we have 1 organism
        organismList = daoOrganism.getAllOrganisms();
        assertEquals(numOrganisms + 1, organismList.size());

        //  Try Saving Again
        importer = new ImportBioPaxToCPath();
        summary = importer.addRecord(xml, pMonitor);

        //  Because the pathway has a unification xref, it should not
        //  be saved again.
        assertEquals(0, summary.getNumPathwaysSaved());
        assertEquals(4, summary.getNumInteractionsSaved());
        assertEquals(7, summary.getNumPhysicalEntitiesSaved());
    }

    /**
     * Gets Name of Test.
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can import BioPAX records into cPath";
    }
}
