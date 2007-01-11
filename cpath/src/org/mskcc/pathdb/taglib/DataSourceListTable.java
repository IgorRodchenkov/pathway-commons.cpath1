package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;

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
                    append ("<br><a href=\"webservice.do?version=1.0&format=html&cmd=get_by_keyword");
                    append ("&q=data_source%3A%22" + dbRecord.getName() + "%22+AND+entity_type%3Apathway\">");
                    append ("Browse</a>");
                }
                append ("</td>");
                endRow();
            }
        }
    }
}