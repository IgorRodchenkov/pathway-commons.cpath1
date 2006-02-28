// $Id: BioPaxParentChildTable.java,v 1.12 2006-02-28 17:21:31 cerami Exp $
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

import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.model.BioPaxInteractionDescriptionMap;

import java.util.ArrayList;

import org.mskcc.pathdb.schemas.biopax.summary.EntitySummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryUtils;
import org.mskcc.pathdb.schemas.biopax.summary.*;

/**
 * Custom JSP tag for displaying a list of BioPAX Parent Elements
 * or Children Elements.
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class BioPaxParentChildTable extends HtmlTable {
    private ArrayList entitySummaryList;
    private String queryString;
    private long cPathId;
    private String currentType;
    private BioPaxEntityTypeMap map = new BioPaxEntityTypeMap();
    private int mode;
    private static final int DEFAULT_NUM_RECORDS = 20;

    /**
     * Alternating Colors for Table Rows:  Color 1.
     */
    private static final String COLOR_1 = "#FFFFFF";

    /**
     * Alternating Colors for Table Rows:  Color 2.
     */
    private static final String COLOR_2 = "#EEEEEE";

    /**
     * Receives List of EntitySummary Objects
     *
     * @param entitySummaryList EntitySummaryList.
     */
    public void setEntitySummaryList(ArrayList entitySummaryList) {
        this.entitySummaryList = entitySummaryList;
    }

    /**
     * Receives the Current Query String.
     *
     * @param queryString Query String.
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
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
     * Sets the Mode:  Show Parents or Show Children.
     *
     * @param mode int.
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Executes JSP Custom Tag
     */
    protected void subDoStartTag() {
        currentType = null;
        if (entitySummaryList.size() > 0) {
            boolean showAll = (queryString.indexOf("show=all_children") != -1);
            int cnt = (showAll) ? entitySummaryList.size()
                    : (entitySummaryList.size() > DEFAULT_NUM_RECORDS)
                    ? DEFAULT_NUM_RECORDS : entitySummaryList.size();
            String heading = getHeader(showAll, entitySummaryList);
            createHeader(heading, showAll);

            // start record output
            startTable();
            for (int lc = 0; lc < cnt; lc++) {
                startRow();
                outputRecord(entitySummaryList, lc);
                endRow();
            }
            endTable();
        }
    }

    /**
     * Determines the Correct Table Header.
     *
     * @param showAll Whether or not to show all.
     * @return Header String
     */
    private String getHeader(boolean showAll, ArrayList entitySummaryList) {
        String heading;
        int start = 1;
        int end;
        int all;
        if (mode == SummaryListUtil.MODE_GET_CHILDREN) {
            heading = "Contains the Following Interactions / Pathways ";
            if (showAll) {
                end = entitySummaryList.size();
                all = entitySummaryList.size();
            } else {
                if (entitySummaryList.size() > DEFAULT_NUM_RECORDS) {
                    end = DEFAULT_NUM_RECORDS;
                    all = entitySummaryList.size();
                } else {
                    end = entitySummaryList.size();
                    all = entitySummaryList.size();
                }
            }
        } else {
            heading = "Member of the Following Interactions / Complexes";
            end = entitySummaryList.size();
            all = entitySummaryList.size();
        }
        heading += " (Showing " + start + " - " + end
                + " of " + all + ")";
        return heading;
    }

    /**
     * Outputs the Table Header.
     */
    private void createHeader(String heading, boolean showAll) {
        append("<DIV CLASS ='h3'>");
        append("<H3>");
        append(heading);

        // limited pagination support if necessary
        if (entitySummaryList.size() > DEFAULT_NUM_RECORDS) {
            // generate link to change number of interactions to display
            if (showAll) {
                String uri = "record.do?id=" + cPathId;
                append("&nbsp;&nbsp;<A HREF=\"" + uri + "\">[display 1- "
                        + DEFAULT_NUM_RECORDS + "]</A>");
            } else {
                String uri = "record.do?id=" + cPathId + "&show=all_children";
                append("&nbsp;&nbsp;<A HREF=\"" + uri + "\">[display all]</A>");
            }
        }
        append("</H3>");
        append("</DIV>");
    }

    /**
     * Outputs the EntitySummary Information.
     */
    private void outputRecord(ArrayList entitySummaryList, int index) {
        EntitySummary entitySummary = (EntitySummary)
                entitySummaryList.get(index);

        String bgColor = getRowColor(index);
        String type = entitySummary.getSpecificType();
        String typeInPlainEnglish = (String) map.get(type);

        // Output Type Header
        if (currentType == null || !type.equals(currentType)) {
            append("<td colspan=2 class='table_head2'>");
            String interactionTypePopupCode =
                    getInteractionTypePopupCode(type);
            append("<a href=\"javascript:void(0);\""
                    + interactionTypePopupCode + ">");
            append(typeInPlainEnglish + "(s)");
            append("</a>");
            append("</td>");
            append("</tr>");
            append("<tr>");
            currentType = entitySummary.getSpecificType();
        }

        if (entitySummary != null) {
            String uri = "record.do?id=" + entitySummary.getRecordID();
            append("<td bgcolor=" + bgColor + " width=15%><a href=\""
                    + uri + "\">View Details</a></td>");
        }
        
        if (entitySummary instanceof InteractionSummary) {
            InteractionSummary interactionSummary =
                    (InteractionSummary) entitySummary;
            String interactionString =
                    InteractionSummaryUtils.createInteractionSummaryString
                            (interactionSummary);
            if (interactionString != null) {
                append("<td bgcolor=" + bgColor + ">"
                        + interactionString + "</td>");
            }
        } else {
            append("<td bgcolor=" + bgColor + ">");
            if (entitySummary != null) {
                append("<a href=\"record.do?id="
                        + entitySummary.getRecordID() + "\">"
                        + entitySummary.getName() + "</a></td>");
            }
        }
        // details hyperlink
    }

    /**
     * Gets the Background Color for the Current Row.
     *
     * @param index Index value.
     * @return String Color, in HEX.
     */
    private String getRowColor(int index) {
        String bgColor = COLOR_1;
        if (index % 2 == 0) {
            bgColor = COLOR_2;
        }
        return bgColor;
    }

    /**
     * Returns the proper javascript code (as string)
     * which displays the interaction description for
     * the given interactionType in a popup box (aka tooltip).
     *
     * @param interactionType String
     * @return String
     */
    private String getInteractionTypePopupCode(String interactionType) {

        // map used to get interaction descriptions
        BioPaxInteractionDescriptionMap map =
                new BioPaxInteractionDescriptionMap();

        // set interaction description
        String interactionDescription = (String) map.get(interactionType);

        // outta here
        return " onmouseover=\"return overlib('" + interactionDescription
                + "');\" onmouseout=\"return nd();\"";
    }
}
