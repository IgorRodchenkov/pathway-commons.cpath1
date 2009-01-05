package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Export Utility Class.
 */
public class ExportUtil {

    /**
     * XRef Look up.
     */
    public static HashMap<String, String> getXRefMap(long cpathId) throws DaoException {
        HashMap<String, String> xrefMap = new HashMap<String, String>();
        DaoExternalLink daoExternalLink = DaoExternalLink.getInstance();
        ArrayList<ExternalLinkRecord> xrefList = daoExternalLink.getRecordsByCPathId(cpathId);
        for (ExternalLinkRecord xref : xrefList) {
            String dbMasterTerm = xref.getExternalDatabase().getMasterTerm();
            String xrefId = xref.getLinkedToId();
            xrefMap.put(dbMasterTerm, xrefId);
        }
        return xrefMap;
    }
}
