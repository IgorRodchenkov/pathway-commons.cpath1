package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.sql.DatabaseImport;

import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Import Table.
 *
 * @author Ethan Cerami
 */
public class ImportTable extends HtmlTable {

    /**
     * Executes JSP Custom Tag
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        DatabaseImport dbImport = new DatabaseImport();
        ArrayList records = dbImport.getAllImportRecords();
        String headers[] = {
            "Status", "Creation Time",
            "Update Time", "File Size (kb)", "View"};
        startTable("Database Import Records");
        createTableHeaders(headers);
        outputRecords(records);
        endTable();
    }

    /**
     * Output Import Records.
     */
    private void outputRecords(ArrayList records) {
        for (int i = 0; i < records.size(); i++) {
            append("<TR>");
            ImportRecord record = (ImportRecord) records.get(i);
            byte bytes[] = record.getData().getBytes();
            double size = bytes.length / 1000.0;
            outputDataField(record.getStatus());
            outputDataField(record.getCreateTime());
            outputDataField(record.getUpdateTime());
            outputDataField(new String(size + " kb"));
            append("<TD>");
            this.outputLink("View Contents", "adminDisplay.do?import_id="
                    + record.getImportId());
            append("</TD>");
            append("</TR>");
        }
    }
}