/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.pathdb.lucene.OrganismStats;
import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.query.QueryException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Custom JSP Tag for Displaying Organism Data Plus Links.
 *
 * @author Ethan Cerami
 */
public class OrganismTable extends HtmlTable {
    /**
     * URL Parameter for Sort By Parameter.
     */
    public static final String SORT_BY_PARAMETER = "sortBy";

    /**
     * URL Parameter for Sort Order.
     */
    public static final String SORT_ORDER_PARAMETER = "sortOrder";

    /**
     * Sort By Species Name.
     */
    public static final String SORT_BY_NAME = "name";

    /**
     * Sort By Number of Interactions.
     */
    public static final String SORT_BY_NUM_INTERACTIONS =
            "numInteractions";

    /**
     * Sort Ascending.
     */
    public static final String SORT_ASC = "asc";

    /**
     * Sort Descending.
     */
    public static final String SORT_DESC = "desc";

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    protected void subDoStartTag() throws Exception {
        //  Get User Parameters
        String sortBy = pageContext.getRequest().
                getParameter(SORT_BY_PARAMETER);
        String sortOrder = pageContext.getRequest().
                getParameter(SORT_ORDER_PARAMETER);

        //  By default, sort by Num Interactions, Descending Order
        if (sortBy == null && sortOrder == null) {
            sortBy = SORT_BY_NUM_INTERACTIONS;
            sortOrder = SORT_ASC;
        } else if (sortOrder == null) {
            sortOrder = SORT_ASC;
        }

        createHeader("Organism Information");
        startTable();
        startRow();
        createColumnHeader("Species Name", SORT_BY_NAME, sortBy, sortOrder);
        createColumnHeader("Number of Interactions", SORT_BY_NUM_INTERACTIONS,
                sortBy, sortOrder);
        outputRecords(sortBy, sortOrder);
        endTable();
    }

    private void createColumnHeader(String columnHeading, String targetSortBy,
            String userSortBy, String userSortOrder) {
        StringBuffer url = new StringBuffer
                ("browse.do?" + SORT_BY_PARAMETER + "="
                + targetSortBy + "&" + SORT_ORDER_PARAMETER + "=");
        String title = "Sort Organisms by " + columnHeading;
        if (userSortBy.equals(targetSortBy)) {
            String iconUrl, iconGif;
            append("<td bgcolor=#aaaaaa>");
            if (userSortOrder.equals(SORT_ASC)) {
                iconUrl = new String(url.toString() + SORT_DESC);
                iconGif = "icon_sortup.gif";
            } else {
                iconUrl = new String(url.toString() + SORT_ASC);
                iconGif = "icon_sortdown.gif";
            }
            append("<A TITLE = '"  + title + " 'HREF='" + iconUrl
                    + "'><B>" + columnHeading + "</B></A>");
            append("&nbsp;&nbsp;&nbsp;");
            append("<a TITLE = '" + title + " 'HREF='" + iconUrl + "'>"
                    + "<IMG SRC='jsp/images/" + iconGif + "' border=0></A>");
        } else {
            String colUrl = new String(url.toString() + SORT_DESC);
            append("<td bgcolor=#cccccc>");
            append("<A TITLE = '" + title + "' HREF='" + colUrl
                    + "'><B>" + columnHeading + "</B></A>");
        }
        append("</td>");
    }

    /**
     * Output Organism Records.
     */
    private void outputRecords(String sortBy, String sortOrder)
            throws DaoException, IOException,
            QueryException {
        OrganismStats orgStats = new OrganismStats();
        ArrayList records = null;

        //  Get Records By Sort Parameter
        if (sortBy.equals(SORT_BY_NAME)) {
            records = orgStats.getOrganismsSortedByName();
        } else {
            records = orgStats.getOrganismsSortedByNumInteractions();
        }

        //  Clone the ArrayList Locally
        records = (ArrayList) records.clone();

        //  Sort in Descending Order, if requested
        if (sortOrder.equals(SORT_ASC)) {
            Collections.reverse(records);
        }

        if (records.size() == 0) {
            startRow();
            append("<TD COLSPAN=5>No Organism Data Available</TD>");
            endRow();
        } else {
            NumberFormat formatter = new DecimalFormat("#,###,###");
            for (int i = 0; i < records.size(); i++) {
                Organism organism = (Organism) records.get(i);
                startRow(1);

                ProtocolRequest request = new ProtocolRequest();
                request.setOrganism(Integer.toString(organism.getTaxonomyId()));
                request.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
                request.setFormat(ProtocolConstants.FORMAT_HTML);
                String url = request.getUri();
                outputDataField("<A HREF='" + url + "' TITLE='" +
                        "View All Records for Organism:  "
                        + organism.getSpeciesName() +"'>"
                        + organism.getSpeciesName() + "</A>");
                outputDataField(formatter.format
                        (organism.getNumInteractions()));
                endRow();
            }
        }
    }
}