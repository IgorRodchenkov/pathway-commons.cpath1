package org.mskcc.pathdb.test.tool;

import junit.framework.TestCase;
import org.mskcc.pathdb.tool.ExportGeneSets;
import org.mskcc.pathdb.tool.ExportFileUtil;
import org.mskcc.pathdb.tool.ToolInit;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.CPathRecord;

import java.io.IOException;

/**
 * Tests the ExportGeneSets Util.
 *
 * @author Ethan Cerami.
 */
public class TestExportGeneSets extends TestCase {

    public void testExportGeneSet() throws DaoException, IOException {
        ToolInit.initProps();
        DaoCPath daoCPath = DaoCPath.getInstance();
        CPathRecord record = daoCPath.getRecordByName("mTOR signaling pathway");
        ExportGeneSets exportUtil = new ExportGeneSets();

        //  Verify that the PC Output File Format Works
        String output = exportUtil.exportPathwayRecord(record, ExportFileUtil.PC_OUTPUT);
        assertTrue(output.startsWith("mTOR signaling pathway\tNA\t588:protein:FRAP2:Q9Y4I3:" +
                "EXTERNAL_ID_NOT_FOUND:EXTERNAL_ID_NOT_FOUND"));
    }
}
