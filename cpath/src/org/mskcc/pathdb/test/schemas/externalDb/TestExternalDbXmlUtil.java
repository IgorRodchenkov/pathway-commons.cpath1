package org.mskcc.pathdb.test.schemas.externalDb;

import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;

import org.mskcc.pathdb.schemas.externalDb.ExternalDbXmlUtil;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

/**
 * Tests the Parsing of an External Database XML File.
 *
 * @author Ethan Cerami.
 */
public class TestExternalDbXmlUtil extends TestCase {

    /**
     * Tests the Parsing of an External Database XML File.
     * @throws Exception All Exceptions.
     */
    public void testXmlParsing() throws Exception {
        File file = new File ("testData/externalDb/external_db.xml");
        ExternalDbXmlUtil util = new ExternalDbXmlUtil(file);
        ArrayList dbList = util.getExternalDbList();
        assertEquals (2, dbList.size());
        ExternalDatabaseRecord dbRecord =
                (ExternalDatabaseRecord) dbList.get(0);
        assertEquals ("Yahoo", dbRecord.getName());
        assertEquals ("Yahoo Search",
                dbRecord.getDescription());
        assertEquals ("http://search.yahoo.com/search?p=%ID%",
                dbRecord.getUrl());
        assertEquals ("test", dbRecord.getSampleId());
        assertEquals ("YAHOO", dbRecord.getMasterTerm());

        ArrayList synList = dbRecord.getSynonymTerms();
        assertEquals (1, synList.size());
        String syn0 = (String) synList.get(0);
        assertEquals ("YHO", syn0);
    }
}
