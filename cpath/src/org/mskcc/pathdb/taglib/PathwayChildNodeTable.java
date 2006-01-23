/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
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
import java.io.IOException;
import org.jdom.JDOMException;

import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryUtils;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryException;

/**
 * Custom jsp tag for displaying pathway child node (1 level deep)
 *
 * @author Benjamin Gross
 */
public class PathwayChildNodeTable extends HtmlTable {

    /**
     * Record ID.
     */
    private long recID;

    /**
     * Reference to CPathRecord.
     */
    CPathRecord record;

    /**
     * Receives Record ID Attribute.
     *
     * @param recID long.
     */
    public void setRecid(long recID){
        this.recID = recID;
    }

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    protected void subDoStartTag() throws Exception {

        // get record using ID attribute
        DaoCPath cPath = DaoCPath.getInstance();
        record = cPath.getRecordById(recID);

        // is this a physical interaction
        startRow();
        outputRecords();
        endRow();
    }

    /**
     * Output the Interaction Information.
     * @throws DaoException
     * @throws IOException
     * @throws InteractionSummaryException
     * @throws JDOMException
     */
    private void outputRecords() throws DaoException, IOException, InteractionSummaryException, JDOMException {

        // interaction summary
        String interactionString = InteractionSummaryUtils.getInteractionSummary(record.getId());
		if (interactionString != null){
			append("<td>" + interactionString + "</td>");

			// details hyperlink
			String uri = "record.do?id=" + recID;
			append("<td><a href=\"" + uri + "\">View Details</a></td>");
		}
    }
}
