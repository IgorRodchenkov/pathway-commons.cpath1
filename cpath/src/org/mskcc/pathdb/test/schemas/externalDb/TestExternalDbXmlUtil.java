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
        assertEquals (24, dbList.size());
        ExternalDatabaseRecord dbRecord =
                (ExternalDatabaseRecord) dbList.get(0);
        assertEquals ("UniProt", dbRecord.getName());
        assertEquals ("Universal Protein Resource (UniProt)",
                dbRecord.getDescription());
        assertEquals ("http://www.ebi.uniprot.org/entry/%ID%",
                dbRecord.getUrl());
        assertEquals ("P31947", dbRecord.getSampleId());
        assertEquals ("UNIPROT", dbRecord.getFixedCvTerm());

        ArrayList synList = dbRecord.getCvTerms();
        assertEquals (5, synList.size());
        String syn0 = (String) synList.get(0);
        assertEquals ("SWISS-PROT", syn0);
        String syn4 = (String) synList.get(4);
        assertEquals ("SWISS-PROT/TREMBL", syn4);
    }
}
