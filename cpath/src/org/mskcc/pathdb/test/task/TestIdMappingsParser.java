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
package org.mskcc.pathdb.test.task;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.CPathXRef;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.IdMapRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoIdMap;
import org.mskcc.pathdb.task.ParseIdMappingsTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the ID Mappings Parser.
 *
 * @author Ethan Cerami.
 */
public class TestIdMappingsParser extends TestCase {

    /**
     * Tests the Parser, without saving anything to the database.
     *
     * @throws Exception All Exceptions.
     */
    public void testParserWithOutSave() throws Exception {
        File file = new File("testData/id_map.txt");
        ParseIdMappingsTask task = new ParseIdMappingsTask(file, false);
        ArrayList list = task.parseAndGenerateList();

        //  Validate the first four id mappings.
        validateIdMapRecord(0, list, "Affymetrix", "1552275_3p_s_at",
                "Unigene", "Hs.77646");
        validateIdMapRecord(1, list, "Unigene", "Hs.77646",
                "Swiss-Prot", "AAH08943");
        validateIdMapRecord(2, list, "Unigene", "Hs.77646",
                "Swiss-Prot", "Q727A4");
        validateIdMapRecord(3, list, "Swiss-Prot", "AAH08943",
                "RefSeq", "NP_060241");
    }

    /**
     * Tests the Parser, with saving to the database.
     *
     * @throws Exception All Exceptions.
     */
    public void testParserWithSave() throws Exception {
        File file = new File("testData/id_map.txt");
        ParseIdMappingsTask task = new ParseIdMappingsTask(file, false);
        int numRecordsSaved = task.parseAndStoreToDb();

        //  Validate Number of Records Saved
        assertEquals(5, numRecordsSaved);

        //  Now try saving again;  verify 0 records saved
        numRecordsSaved = task.parseAndStoreToDb();
        assertEquals(0, numRecordsSaved);

        //  Now try getting all equivalent Identifiers;  there should be 4
        DaoIdMap dao = new DaoIdMap();
        CPathXRef xref = new CPathXRef(1, "AAH08943");
        ArrayList equivalenceList = dao.getEquivalenceList(xref);
        CPathXRef match0 = (CPathXRef) equivalenceList.get(0);
        CPathXRef match1 = (CPathXRef) equivalenceList.get(1);
        CPathXRef match2 = (CPathXRef) equivalenceList.get(2);
        CPathXRef match3 = (CPathXRef) equivalenceList.get(3);
        assertEquals("Unigene: Hs.77646", match0.toString());
        assertEquals("RefSeq: NP_060241", match1.toString());
        assertEquals("Affymetrix: 1552275_3p_s_at", match2.toString());
        assertEquals("Swiss-Prot: Q727A4", match3.toString());

        //  Delete all records, so that we can rerun this unit test again
        dao.deleteAllRecords();
    }

    private void validateIdMapRecord(int index, ArrayList idList,
            String expectedDb1, String expectedId1, String expectedDb2,
            String expectedId2) throws DaoException {
        IdMapRecord id = (IdMapRecord) idList.get(index);
        DaoExternalDb dao = new DaoExternalDb();
        int db1 = id.getDb1();
        ExternalDatabaseRecord dbRecord1 = dao.getRecordById(db1);
        int db2 = id.getDb2();
        ExternalDatabaseRecord dbRecord2 = dao.getRecordById(db2);
        assertEquals(expectedDb1, dbRecord1.getName());
        assertEquals(expectedId1, id.getId1());
        assertEquals(expectedDb2, dbRecord2.getName());
        assertEquals(expectedId2, id.getId2());
    }
}
