package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.form.WebUIBean;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.protocol.ProtocolRequest;

import java.util.ArrayList;

/**
 * Custom JSP Tag for displaying a list of data sources
 *
 * @author Benjamin Gross
 */
public class DataSourceListTable extends HtmlTable {

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    protected void subDoStartTag() throws Exception {
        startTable();
        outputRecords();
        endTable();
    }

    /**
     * Output the Pathway List.
     */
    private void outputRecords() throws DaoException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllDatabaseSnapshots();
        WebUIBean webUIBean = CPathUIConfig.getWebUIBean();

        // process records
        if (list.size() == 0) {
            startRow();
            append("<TD COLSPAN=2>No Data Sources Available</TD>");
            endRow();
        } else {
            for (int i = 0; i < list.size(); i++) {
                ExternalDatabaseSnapshotRecord snapshotRecord =
                        (ExternalDatabaseSnapshotRecord) list.get(i);
                startRow(1);
                append("<td width=50>");
                if (snapshotRecord.getExternalDatabase() != null) {
                    ExternalDatabaseRecord dbRecord = snapshotRecord.getExternalDatabase();
                    if (dbRecord.getIconFileExtension() != null) {
                        append("<img class='data_source_logo' src='icon.do?id="
                                + dbRecord.getId() + "'/>");
                    }
                }
                append ("</td>");
                append ("<td valign=center>"
                        + DbSnapshotInfo.getDbSnapshotHtml(snapshotRecord.getId()));
                if (snapshotRecord.getExternalDatabase() != null) {
                    ExternalDatabaseRecord dbRecord = snapshotRecord.getExternalDatabase();
                    append ("<br><a href=\"webservice.do?version=");
                    append (webUIBean.getWebApiVersion());
                    append ("&format=html&cmd=get_by_keyword");
					append ("&" + ProtocolRequest.ARG_ENTITY_TYPE + "=ALL_ENTITY_TYPE");
					append ("&" + GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME + "=" + GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL);
					append ("&q=data_source%3A" + dbRecord.getMasterTerm() + "\">");
                    append ("Browse</a>");
                }
                append ("</td>");
                endRow();
            }
        }
    }
}
