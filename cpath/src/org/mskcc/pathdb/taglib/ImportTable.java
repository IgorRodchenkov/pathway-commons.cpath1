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
    private boolean adminView = false;

    /**
     * Sets the Admin View.
     * @param adminView Admin View.
     */
    public void setAdminView(String adminView) {
        if (adminView.equalsIgnoreCase("yes")) {
            this.adminView = true;
        }
    }

    /**
     * Executes JSP Custom Tag
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        DaoImport dbImport = new DaoImport();
        ArrayList records = dbImport.getAllRecords();
        startTable("cPath contains the following imported records:");
        if (adminView) {
            String headers[] = {
                "Status", "Description", "Upload Time",
                "Update Time", "File Size (kb)", "View"
            };
            createTableHeaders(headers);
        } else {
            String headers[] = {"Description", "Upload Time"};
            createTableHeaders(headers);
        }
        outputRecords(records);
        endTable();
    }

    /**
     * Output Import Records.
     */
    private void outputRecords(ArrayList records) {
        for (int i = 0; i < records.size(); i++) {
            ImportRecord record = (ImportRecord) records.get(i);
            String status = record.getStatus();
            if (!status.equals(ImportRecord.STATUS_TRANSFERRED)
                    && !adminView) {
                continue;
            }
            append("<TR>");
            if (adminView) {
                outputDataField(record.getStatus());
            }
            outputDataField(record.getDescription());
            outputDataField(record.getCreateTime());
            if (adminView) {
                outputDataField(record.getUpdateTime());
                append("<TD>");
                this.outputLink("View Contents",
                        "adminViewImportRecordXml.do?import_id="
                        + record.getImportId());
                append("</TD>");
            }
            append("</TR>");
        }
    }
}