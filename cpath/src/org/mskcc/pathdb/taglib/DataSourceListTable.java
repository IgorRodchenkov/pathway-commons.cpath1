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
                        append("<img height='40' width='44' src='icon.do?id=" + dbRecord.getId() + "'/>");
                    }
                }
                append ("</td>");
                outputDataField(DbSnapshotInfo.getDbSnapshotHtml(snapshotRecord.getId()));
                endRow();
            }
        }
    }
}