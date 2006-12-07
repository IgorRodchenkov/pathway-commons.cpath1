// $Id: PathwayMembershipTable.java,v 1.18 2006-12-07 19:26:41 grossb Exp $
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
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Comparator;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;

/**
 * Custom jsp tag to generate a table of pathways that a molecule is a member of.
 *
 * @author Benjamin Gross
 */
public class PathwayMembershipTable extends HtmlTable {

    /**
     * Set of Pathways.
     */
    private Set pathwaySet;

	/**
	 * Total number of pathways in db.
	 */
	private Integer totalNumPathways;

    /**
     * HttpServlet Request Object.
     */
    private HttpServletRequest request;

    /**
     * Current cPath Id.
     */
    private long cPathId;


    /**
     * Receives Set Attribute.
     *
     * @param pathwaySet Set.
     */
    public void setPathwaySet(Set pathwaySet) {
        this.pathwaySet = pathwaySet;
    }

    /**
     * Receives totalNumPathways Attribute.
     *
     * @param totalNumPathways
     */
    public void setTotalNumPathways(Integer totalNumPathways) {
        this.totalNumPathways = totalNumPathways;
    }

    /**
     * Receives the Current Request Object.
     *
     * @param request HttpServletRequest Object.
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Receives the Current cPath ID.
     *
     * @param cPathId cPath ID.
     */
    public void setcpathId(long cPathId) {
        this.cPathId = cPathId;
    }

    /**
     * Executes JSP Custom Tag
     */
    protected void subDoStartTag() throws DaoException, BioPaxRecordSummaryException {
        String param = request.getParameter(BioPaxShowFlag.SHOW_FLAG);
        BioPaxShowFlag showFlag = new BioPaxShowFlag(param);
        if (pathwaySet != null && pathwaySet.size() > 0) {
            String title = "Member of the Following Pathways";
            String htmlHeader = BioPaxShowFlag.createHtmlHeader(BioPaxShowFlag.DEFAULT_NUM_RECORDS,
                    totalNumPathways, cPathId, title, showFlag,
                    BioPaxShowFlag.SHOW_ALL_PATHWAYS, "pathway_list");
            append(htmlHeader);
            append("<TABLE WIDTH=100%>");
            outputRecords(showFlag);
            endTable();
        }
    }

    /**
     * Output the Pathways.
     */
    private void outputRecords(BioPaxShowFlag showFlag) throws DaoException, BioPaxRecordSummaryException {

        // sort the pathways
        List pathwayList = new ArrayList(pathwaySet);
        Collections.sort(pathwayList, new RecordSorter());

        int cnt = BioPaxShowFlag.determineEndIndex(BioPaxShowFlag.DEFAULT_NUM_RECORDS, pathwayList.size(),
                showFlag, BioPaxShowFlag.SHOW_ALL_PATHWAYS);

        // interate through list
        for (int lc = 0; lc < cnt; lc++) {
            startRow(lc);
            append("<td>");
			BioPaxRecordSummary biopaxRecordSummary = (BioPaxRecordSummary)pathwayList.get(lc);
			String organism = biopaxRecordSummary.getOrganism();
			String headerString = (organism != null) ?
				(biopaxRecordSummary.getName() + " from " + organism) : biopaxRecordSummary.getName();
            append("<A HREF='record.do?id=" + biopaxRecordSummary.getRecordID()
                + "'>" + headerString + "</A>");
            append("</td>");
            append("<td>");
            //append(DbSnapshotInfo.getDbSnapshotHtml(biopaxRecordSummary.getDataSourceSnapshotId()));
			append(DbSnapshotInfo.getDbSnapshotHtml(biopaxRecordSummary.getExternalDatabaseSnapshotRecord()));
            append("</td>");
			endRow();
        }
    }
}


/**
 * Sorts CPathRecords by Name
 *
 * @author Benjamin Gross
 */
class RecordSorter implements Comparator {

    /**
     * Our implementation of compare.
     */
    public int compare(Object o1, Object o2) {
        BioPaxRecordSummary record1 = (BioPaxRecordSummary) o1;
        BioPaxRecordSummary record2 = (BioPaxRecordSummary) o2;

        return record1.getName().compareToIgnoreCase(record2.getName());
    }
}
