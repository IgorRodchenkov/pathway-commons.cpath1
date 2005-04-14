package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.TestCase;
import org.mskcc.pathdb.schemas.biopax.ImportBioPaxToCPath;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.dataservices.util.ContentReader;

/**
 * Tests the ImportBioPaxToCPath Class.
 *
 * @author Ethan Cerami
 */
public class TestImportBioPaxToCPath extends TestCase {

    /**
     * Tests BioPAX Import.
     * @throws Exception All Exceptions.
     */
    public void testImport () throws Exception {
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent
                ("testData/biopax/biopax1_sample1.owl");
        ProgressMonitor pMonitor = new ProgressMonitor();
        ImportBioPaxToCPath importer = new ImportBioPaxToCPath ();
        ImportSummary summary = importer.addRecord(xml, pMonitor);
        assertEquals (1, summary.getNumPathwaysSaved());
        assertEquals (4, summary.getNumInteractionsSaved());
        assertEquals (7, summary.getNumPhysicalEntitiesSaved());
    }
}
