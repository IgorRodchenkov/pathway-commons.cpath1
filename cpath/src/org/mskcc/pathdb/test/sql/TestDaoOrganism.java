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
package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoOrganism;

import java.util.ArrayList;

/**
 * Tests the DaoOrganism Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoOrganism extends TestCase {
    private int taxId = 4932;
    private String speciesName = "Saccharomyces cerevisiae";
    private String commonName = "baker's yeast";

    /**
     * Tests DaoAccess.
     *
     * @throws DaoException Error Connecting to Database.
     */
    public void testAccess() throws DaoException {
        DaoOrganism dao = new DaoOrganism();

        //  Clear out record (if it already exists)
        dao.deleteRecord(taxId);

        //  Add New Record
        dao.addRecord(taxId, speciesName, commonName);
        assertTrue(dao.recordExists(taxId));
        ArrayList organisms = dao.getAllOrganisms();
        assertTrue(organisms.size() > 0);
        Organism organism = (Organism) organisms.get(0);
        assertEquals(taxId, organism.getTaxonomyId());
        assertEquals(speciesName, organism.getSpeciesName());
        assertEquals(commonName, organism.getCommonName());

        //  Delete Record
        assertTrue(dao.deleteRecord(taxId));
        assertTrue(!dao.recordExists(taxId));
    }
}
