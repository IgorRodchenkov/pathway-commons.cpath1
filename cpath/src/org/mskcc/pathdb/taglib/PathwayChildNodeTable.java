// $Id: PathwayChildNodeTable.java,v 1.19 2006-02-15 20:07:27 grossb Exp $
//------------------------------------------------------------------------------
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
import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.model.BioPaxInteractionDescriptionMap;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryUtils;

import java.util.ArrayList;

/**
 * Custom jsp tag for displaying pathway child node (1 level deep)
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class PathwayChildNodeTable extends HtmlTable {
    private ArrayList entitySummaryList;
    private String queryString;
    private long cPathId;
    private String currentType;

    /**
     * Receives EntitySummary Object.
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
     * Executes JSP Custom Tag
     */
    protected void subDoStartTag() {
        currentType = null;
        if (entitySummaryList.size() > 0) {
            boolean showAll = (queryString.indexOf("show=ALL") != -1);
            int cnt = (showAll) ? entitySummaryList.size() : (entitySummaryList.size() > 10)
                    ? 10 : entitySummaryList.size();
            String heading = (showAll) ? "Contains the Following Interactions / Pathways" :
                    (entitySummaryList.size() > 10) ?
                            "Contains the Following Interactions / Pathways (first ten shown)"
                            : "Contains the Following Interactions / Pathways";

            createHeader(heading, showAll);

            // start child node output
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
     * Outputs the Table Header.
     */
    private void createHeader(String heading, boolean showAll) {
        append("<DIV CLASS ='h3'>");
        append("<H3>");
        append("<TABLE><TR>");
        append("<TD>" + heading + "</TD>");

        // limited pagination support if necessary
        if (entitySummaryList.size() > 10) {
            // generate link to change number of interactions to display
            if (showAll) {
                String uri = "record.do?id=" + cPathId;
                append("<TD><A HREF=\"" + uri + "\">[display 10]</A></TD>");
            } else {
                String uri = "record.do?id=" + cPathId + "&show=ALL";
                append("<TD><A HREF=\"" + uri + "\">[display all]</A></TD>");
            }
        }
        append("</TR></TABLE>");
        append("</H3>");
        append("</DIV>");
    }

    /**
     * Outputs the EntitySummary Information.
     */
    private void outputRecord(ArrayList entitySummaryList, int index) {
        EntitySummary entitySummary = (EntitySummary) entitySummaryList.get(index);

        String bgColor = "#FFFFFF";
        if (index % 2 == 0) {
            bgColor = "#EEEEEE";
        }

        BioPaxEntityTypeMap map = new BioPaxEntityTypeMap();
        // summary
        String interactionType = entitySummary.getSpecificType();
        String interactionTypeInPlainEnglish = (String) map.get(interactionType);

        if (currentType == null || ! interactionType.equals(currentType)) {
            int count = countRows(entitySummaryList, index);
            append("<td bgcolor=#DDDDDD width=15% rowspan=" + count + ">");
			String interactionTypePopupCode = getInteractionTypePopupCode(interactionType);
			append("<a href=\"javascript:void(0);\"" + interactionTypePopupCode + ">");
            append(interactionTypeInPlainEnglish + "(s)");
			append("</a>");
            append("</td>");
            currentType = entitySummary.getSpecificType();
        }

        if (entitySummary instanceof InteractionSummary) {
            InteractionSummary interactionSummary = (InteractionSummary) entitySummary;
            String interactionString = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
            if (interactionString != null) {
                append("<td bgcolor=" + bgColor + ">" + interactionString + "</td>");
            }
        } else {
            append("<td colspan=2 bgcolor=" + bgColor + ">");
            if (entitySummary != null) {
                append("<a href=\"record.do?id=" + entitySummary.getRecordID() + "\">"
                    + entitySummary.getName() + "</A>");
            }
        }
        // details hyperlink
        if (entitySummary != null) {
            String uri = "record.do?id=" + entitySummary.getRecordID();
            append("<td bgcolor=" + bgColor + " width=15%><a href=\"" + uri + "\">View Details</a></td>");
        }
    }

    private int countRows(ArrayList entitySummaryList, int index) {
        int count = 1;
        EntitySummary entitySummary = (EntitySummary) entitySummaryList.get(index);
        String type = entitySummary.getSpecificType();
        for (int i = index + 1; i < entitySummaryList.size(); i++) {
            entitySummary = (EntitySummary) entitySummaryList.get(i);
            String currentType = entitySummary.getSpecificType();
            if (!currentType.equals(type)) {
                return count;
            }
            count++;
        }
        return count;
    }

	/**
	 * Returns the proper javascript code (as string)
	 * which displays the interaction description for 
	 * the given interactionType in a popup box (aka tooltip).
	 *
	 * @param interactionType String
	 * @return String
	 */
	private String getInteractionTypePopupCode(String interactionType){

		// map used to get interaction descriptions
		BioPaxInteractionDescriptionMap map = new BioPaxInteractionDescriptionMap();

		// set interaction description
		String interactionDescription = (String)map.get(interactionType);

		// outta here
		return " onmouseover=\"return overlib('" + interactionDescription + "');\" onmouseout=\"return nd();\"";
	}
}
