package org.mskcc.pathdb.test.schemas.biopax;

import org.mskcc.pathdb.schemas.biopax.BioPaxUtil;
import org.mskcc.pathdb.schemas.biopax.TransformBioPaxToCPathRecords;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.FileReader;
import java.util.ArrayList;

import junit.framework.TestCase;

public class TestBioPaxTransformToCPathRecords extends TestCase {

    public void testTransformation() throws Exception {
        FileReader file = new FileReader
                ("testData/biopax/biopax1_sample1.owl");
        ProgressMonitor pMonitor = new ProgressMonitor();
        BioPaxUtil util = new BioPaxUtil(file, pMonitor);
        ArrayList pathwayList = util.getPathwayList();

        //  Try with a Pathway
        TransformBioPaxToCPathRecords transformer = new
                TransformBioPaxToCPathRecords (pathwayList);
        ArrayList cPathRecordList = transformer.getcPathRecordList();
        ArrayList idList = transformer.getIdList();

        //  Validate the cPath Record
        CPathRecord record = (CPathRecord) cPathRecordList.get(0);
        assertEquals ("glycolysis", record.getName());
        assertEquals ("Glycolysis Pathway", record.getDescription());
        assertEquals (562, record.getNcbiTaxonomyId());
        assertEquals (XmlRecordType.BIO_PAX, record.getXmlType());
        assertEquals (CPathRecordType.PATHWAY , record.getType());
        assertEquals (BioPaxConstants.PATHWAY, record.getSpecificType());

        //  Validate the RDF ID
        String id = (String) idList.get(0);
        assertEquals ("pathway50", id);
    }
}
