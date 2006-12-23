// $Id: OrganismTable.java,v 1.24 2006-12-23 04:29:26 cerami Exp $
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

import net.sf.ehcache.CacheException;
import org.mskcc.pathdb.action.BaseAction;
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
     * URL Value for Referrer.
     */
    private String referer = "HOME";

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
     * Receives Tag Attribute.
     *
     * @param referer Referer String.
     */
    public void setReferer(String referer) {
        this.referer = referer;
    }

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

        startTable();
        startRow();
        createColumnHeader("Species Name", SORT_BY_NAME, sortBy, sortOrder);
        createColumnHeader("Number of Records*",
                SORT_BY_NUM_INTERACTIONS, sortBy, sortOrder);
        endRow();
        try {
            outputRecords(sortBy, sortOrder);
        } catch (QueryException e) {
            startRow();
            append("<td>No organism data currrently available</td>");
            endRow();
        }
        endTable();
    }

    private void createColumnHeader(String columnHeading, String targetSortBy,
            String userSortBy, String userSortOrder) {
        StringBuffer url = new StringBuffer
                ("browse.do?" + BaseAction.REFERER + "=" + referer + "&amp;"
                        + SORT_BY_PARAMETER + "="
                        + targetSortBy + "&amp;" + SORT_ORDER_PARAMETER + "=");
        String title = "Sort Organisms by " + columnHeading;
        if (userSortBy.equals(targetSortBy)) {
            String iconUrl, iconGif;
            append("<th width=\"50%\">");
            if (userSortOrder.equals(SORT_ASC)) {
                iconUrl = new String(url.toString() + SORT_DESC);
                iconGif = "icon_sortup.gif";
            } else {
                iconUrl = new String(url.toString() + SORT_ASC);
                iconGif = "icon_sortdown.gif";
            }
            append("<a title = '" + title + " 'href='" + iconUrl
                    + "'><b>" + columnHeading + "</b></a>");
            append("&nbsp;&nbsp;&nbsp;");
            append("<a title = '" + title + " 'href='" + iconUrl + "'>"
                    + "<img src='jsp/images/" + iconGif + "' border=\"0\" alt=\"Sort\" /></a>");
        } else {
            String colUrl = new String(url.toString() + SORT_DESC);
            append("<th>");
            append("<a title = '" + title + "' href='" + colUrl
                    + "'><b>" + columnHeading + "</b></a>");
        }
        append("</th>");
    }

    /**
     * Output Organism Records.
     */
    private void outputRecords(String sortBy, String sortOrder)
            throws DaoException, IOException, QueryException, CacheException {
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
            append("<td colspan=\"5\">No Organism Data Available</td>");
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
                outputDataField("<a href='" + url + "' title='"
                        + "View All Records for Organism:  "
                        + organism.getSpeciesName() + "'>"
                        + organism.getSpeciesName() + "</a>");
                outputDataField(formatter.format
                        (organism.getNumInteractions()));
                endRow();
            }
        }
    }
}
