// $Id: ImportTable.java,v 1.17 2006-06-09 19:22:03 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
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
                "Status", "File Name", "XML Type", "Date/Time Loaded",
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
            outputDataField(record.getXmlType().toString());
            outputDataField(record.getCreateTime());
            append("</TR>");
        }
    }
}
