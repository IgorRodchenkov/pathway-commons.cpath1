package org.mskcc.pathdb.test.schemas.externalDb;

import org.mskcc.pathdb.schemas.externalDb.ExternalDbXmlUtil;
import org.mskcc.pathdb.schemas.externalDb.ExternalDbLinkTester;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * Tests the ExternalDbLinkTester class.
 *
 * @author Ethan Cerami
 */
public class TestExternalDbLinkTester extends TestCase {

    /**
     * Tests the Link Tester.
     * @throws Exception All Exceptions.
     */
    public void testExternalDbLinkTester() throws Exception {
        File file = new File ("testData/externalDb/external_db.xml");
        ExternalDbXmlUtil util = new ExternalDbXmlUtil(file);
        ArrayList dbList = util.getExternalDbList();
        ExternalDatabaseRecord dbRecord =
                (ExternalDatabaseRecord) dbList.get(0);
        int statusCode = ExternalDbLinkTester.checkSampleLink(dbRecord);
        assertEquals (200, statusCode);
    }
}
