package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.Organism;

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
     * @throws DaoException Error Connecting to Database.
     */
    public void testAccess() throws DaoException {
        DaoOrganism dao = new DaoOrganism ();

        //  Clear out record (if it already exists)
        dao.deleteRecord(taxId);

        //  Add New Record
        dao.addRecord(taxId, speciesName, commonName);
        assertTrue (dao.recordExists(taxId));
        ArrayList organisms = dao.getAllOrganisms();
        assertTrue (organisms.size() > 0);
        Organism organism = (Organism) organisms.get(0);
        assertEquals (taxId, organism.getTaxonomyId());
        assertEquals (speciesName, organism.getSpeciesName());
        assertEquals (commonName, organism.getCommonName());

        //  Delete Record
        assertTrue (dao.deleteRecord(taxId));
        assertTrue (!dao.recordExists(taxId));
    }
}
