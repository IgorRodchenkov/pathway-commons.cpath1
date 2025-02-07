// $Id: PathwayListTable.java,v 1.11 2006-12-22 21:03:48 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

// imports

import org.mskcc.pathdb.model.CPathRecord;

import java.util.ArrayList;

/**
 * Custom JSP Tag for displaying a Pathway List in a Table.
 *
 * @author Benjamin Gross
 */
public class PathwayListTable extends HtmlTable {

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
    private void outputRecords() {

        // get pathway list records
        ArrayList records = null;
        records = (ArrayList) pageContext.getRequest().getAttribute("RECORDS");

        // process records
        if (records != null) {
            if (records.size() == 0) {
                startRow();
                append("<td colspan='5'>No Pathway Records Exist</td>");
                endRow();
            } else {
                for (int lc = 0; lc < records.size(); lc++) {
                    CPathRecord rec = (CPathRecord) records.get(lc);
                    startRow(1);
                    String uri = "record2.do?id=" + rec.getId();
                    outputDataField("<a href=\"" + uri + "\">" + rec.getName() + "</a>");
                    endRow();
                }
            }
        }
    }
}
