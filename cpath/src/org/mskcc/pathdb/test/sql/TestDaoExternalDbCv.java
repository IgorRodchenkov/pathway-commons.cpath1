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
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoExternalDbCv;

import java.util.ArrayList;

/**
 * Tests the DaoExternalDbCv Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoExternalDbCv extends TestCase {
    private static final String TERM1 = "ACME_TEST_1";
    private static final String TERM2 = "ACME_TEST_2";
    private static final String DB_NAME = "ACME_TEST";

    /**
     * Tests Access.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        int dbId = createSampleDb();

        DaoExternalDbCv dao = new DaoExternalDbCv();

        //  Add Two Terms
        boolean success = dao.addRecord(dbId, TERM1);
        assertTrue(success);
        success = dao.addRecord(dbId, TERM2);
        assertTrue(success);

        //  Get Matching Database
        ExternalDatabaseRecord db = dao.getExternalDbByTerm(TERM1);
        assertEquals(DB_NAME, db.getName());
        db = dao.getExternalDbByTerm(TERM2);
        assertEquals(DB_NAME, db.getName());

        //  Get all terms for Database
        ArrayList terms = dao.getTermsByDbId(dbId);
        assertEquals(2, terms.size());
        String term1 = (String) terms.get(0);
        String term2 = (String) terms.get(1);
        assertEquals(TERM1, term1);
        assertEquals(TERM2, term2);

        //  Delete Terms
        success = dao.deleteTermsByDbId(dbId);
        assertTrue(success);
        db = dao.getExternalDbByTerm(TERM1);
        assertTrue(db == null);
        db = dao.getExternalDbByTerm(TERM2);
        assertTrue(db == null);
    }

    private int createSampleDb() throws DaoException {
        DaoExternalDb dao = new DaoExternalDb();
        ExternalDatabaseRecord db = new ExternalDatabaseRecord();
        db.setName(DB_NAME);
        db.setDescription("Test");
        db.setDbType(ReferenceType.PROTEIN_UNIFICATION);
        dao.addRecord(db);
        db = dao.getRecordByName(DB_NAME);
        int dbId = db.getId();
        return dbId;
    }
}