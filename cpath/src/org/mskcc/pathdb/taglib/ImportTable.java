package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.sql.dao.DaoImport;

import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Import Table.
 *
 * @author Ethan Cerami
 */
public class ImportTable extends HtmlTable {

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        DaoImport dbImport = new DaoImport();
        ArrayList records = dbImport.getAllRecords();
        createHeader("cPath currently contains the following imported "
                + "records:");
        this.startTable();
        String headers[] = {
            "Status", "Description", "Upload Time",
            "Update Time", "View"
        };
        createTableHeaders(headers);
        outputRecords(records);
        endTable();
    }

    /**
     * Output Import Records.
     */
    private void outputRecords(ArrayList records) {
        for (int i = 0; i < records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            append("<TR>");
            outputDataField(record.getStatus());
            outputDataField(record.getDescription());
            outputDataField(record.getCreateTime());
            outputDataField(record.getUpdateTime());
            append("<TD>");
            this.outputLink("View Contents",
                    "adminViewImportRecordXml.do?import_id="
                    + record.getImportId());
            append("</TD>");
            append("</TR>");
        }
    }
}