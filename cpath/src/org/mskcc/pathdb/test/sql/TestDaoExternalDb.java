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
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;

import java.util.ArrayList;

/**
 * Tests Data Access for the External Database Table.
 *
 * @author Ethan Cerami
 */
public class TestDaoExternalDb extends TestCase {
    private static final String NAME = "ACME Database";
    private static final String DESC = "ACME Database holds protein"
            + "interactions.";
    private static final String TERM1 = "ACE";
    private static final String TERM2 = "ACME";
    private static final String URL = "http://us.expasy.org/cgi-bin/"
            + "niceprot.pl?%ID%";
    private static final String NEW_NAME = "ACME Improved Database";

    /**
     * Tests Access.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        DaoExternalDb dao = new DaoExternalDb();

        //  Add a Sample Record
        addSampleRecord();

        //  Test getRecordByName() Method.
        ExternalDatabaseRecord record = dao.getRecordByName(NAME);
        validateRecord(record);

        //  Test getRecordById() Method.
        record = dao.getRecordById(record.getId());
        validateRecord(record);

        //  Test getRecordByTerm() Method
        record = dao.getRecordByTerm(TERM1);
        validateRecord(record);
        record = dao.getRecordByTerm(TERM2);
        validateRecord(record);

        //  Update Record;  test updateRecord() Method.
        record.setName(NEW_NAME);
        boolean flag = dao.updateRecord(record);
        assertEquals(true, flag);

        //  Verify Record was updated.
        record = dao.getRecordById(record.getId());
        assertEquals(NEW_NAME, record.getName());

        //  Delete Record.
        flag = dao.deleteRecordById(record.getId());
        assertEquals(true, flag);

        //  Verify Record was deleted
        record = dao.getRecordById(record.getId());
        assertEquals(null, record);
    }

    private void validateRecord(ExternalDatabaseRecord record) {
        assertEquals(NAME, record.getName());
        assertEquals(DESC, record.getDescription());
        ArrayList terms = record.getCvTerms();
        String term1 = (String) terms.get(0);
        String term2 = (String) terms.get(1);
        assertEquals(TERM1, term1);
        assertEquals(TERM2, term2);
        assertEquals(URL, record.getUrl());
    }

    private void addSampleRecord() throws DaoException {
        ExternalDatabaseRecord db = new ExternalDatabaseRecord();
        db.setName(NAME);
        db.setDescription(DESC);
        ArrayList terms = new ArrayList();
        terms.add(TERM1);
        terms.add(TERM2);
        db.setCvTerms(terms);
        db.setUrl(URL);
        DaoExternalDb cpath = new DaoExternalDb();
        cpath.addRecord(db);
    }
}