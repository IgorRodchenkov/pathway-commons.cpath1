package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoIdMap;
import org.mskcc.pathdb.sql.transfer.IdMappingService;
import org.mskcc.pathdb.task.ParseIdMappingsTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the ID Mapping Service Class.
 *
 * @author Ethan Cerami.
 */
public class TestIdMappingService extends TestCase {

    /**
     * Tests the ID Mapping Service Class.
     *
     * @throws Exception All Exceptions.
     */
    public void testIdMappingService() throws Exception {
        //  First, store some ID Mappings to the Database
        File file = new File("testData/id_map.txt");
        ParseIdMappingsTask task = new ParseIdMappingsTask(file, false);
        task.parseAndStoreToDb();

        //  Now, query the ID Mapping Service for Equivalent Refs.
        IdMappingService idService = new IdMappingService();

        //  Let's assume we start out with three references
        //  The first and third are already stored in the database.
        //  The second is not stored in the database.
        ExternalReference xrefs[] = new ExternalReference[3];
        xrefs[0] = new ExternalReference("SwissProt", "AAH08943");
        xrefs[1] = new ExternalReference("LocusLink", "ABCDE");
        xrefs[2] = new ExternalReference("RefSeq", "NP_060241");

        //  Now, get a complete list of Equivalent References
        ArrayList hitList = idService.getEquivalenceList(xrefs);

        //  There should be a total of 3 hits
        assertEquals(3, hitList.size());

        //  Verify the Database List
        //  This order is not actually guaranteed, but it is OK for the
        //  unit test.
        ExternalReference xref0 = (ExternalReference) hitList.get(0);
        ExternalReference xref1 = (ExternalReference) hitList.get(1);
        ExternalReference xref2 = (ExternalReference) hitList.get(2);
        assertEquals("External Reference  -->  Database:  [Affymetrix], "
                + "ID:  [1552275_3p_s_at]", xref0.toString());
        assertEquals("External Reference  -->  Database:  [SwissProt], "
                + "ID:  [Q727A4]", xref1.toString());
        assertEquals("External Reference  -->  Database:  [Unigene], "
                + "ID:  [Hs.77646]", xref2.toString());

        //  Delete all records, so that we can rerun this unit test again
        DaoIdMap dao = new DaoIdMap();
        dao.deleteAllRecords();
    }

    public void testHprd() throws Exception {
        IdMappingService idService = new IdMappingService();
        ExternalReference refs[] = new ExternalReference[2];
        refs[0] = new ExternalReference("HPRD", "HPRD_00774");
        refs[1] = new ExternalReference("Ref-Seq", "NP_005336");
        ArrayList list = idService.getEquivalenceList(refs);
        System.out.println("Size of List:  " + list.size());
        for (int i = 0; i < list.size(); i++) {
            ExternalReference ref = (ExternalReference) list.get(i);
            System.out.println(i + ":  " + ref.toString());
            if (ref.getId().trim().length() == 0) {
                System.out.println("****** See above");
            }
        }
    }
}