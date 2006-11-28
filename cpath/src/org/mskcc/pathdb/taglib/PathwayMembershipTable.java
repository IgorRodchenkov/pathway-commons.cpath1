// $Id: PathwayMembershipTable.java,v 1.14 2006-11-28 21:41:26 grossb Exp $
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

import java.util.*;

/**
 * Custom jsp tag to generate a table of pathways that a molecule is a member of.
 *
 * @author Benjamin Gross
 */
public class PathwayMembershipTable extends HtmlTable {

    /**
     * HashSet of Pathways.
     */
    private HashSet pathwaySet;

    /**
     * Receives HashSet Attribute.
     *
     * @param pathwaySet HashSet.
     */
    public void setPathwaySet(HashSet pathwaySet) {
        this.pathwaySet = pathwaySet;
    }

    /**
     * Executes JSP Custom Tag
     */
    protected void subDoStartTag() throws DaoException, BioPaxRecordSummaryException {

        // here we go
        if (pathwaySet != null && pathwaySet.size() > 0) {
            outputRecords();
        }
    }

    /**
     * Output the Pathways.
     */
    private void outputRecords() throws DaoException, BioPaxRecordSummaryException {

        // sort the pathways
        CPathRecord[] pathways = (CPathRecord[]) pathwaySet.toArray(new CPathRecord[0]);
        List pathwayList = Arrays.asList(pathways);
        Collections.sort(pathwayList, new RecordSorter());

        // render the table
        startRow();

        // interate through list
        int cnt = pathwayList.size();
        for (int lc = 0; lc < cnt; lc++) {
            startRow(lc);
            append("<td>");
            CPathRecord pathwayRecord = (CPathRecord) pathwayList.get(lc);
			BioPaxRecordSummary biopaxRecordSummary =
				BioPaxRecordUtil.createBioPaxRecordSummary(pathwayRecord);
			String organism = biopaxRecordSummary.getOrganism();
			String headerString = (organism != null) ?
				(pathwayRecord.getName() + " from " + organism) : pathwayRecord.getName();
            append("<A HREF='record.do?id=" + pathwayRecord.getId()
                + "'>" + headerString + "</A>");
            append("</td>");
            append("<td>");
            append(DbSnapshotInfo.getDbSnapshotHtml(pathwayRecord.getSnapshotId()));
            append("</td>");
        }

        // end the row
        endRow();
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
        CPathRecord record1 = (CPathRecord) o1;
        CPathRecord record2 = (CPathRecord) o2;

        return record1.getName().compareToIgnoreCase(record2.getName());
    }
}
