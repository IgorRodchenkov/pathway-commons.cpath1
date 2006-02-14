// $Id: PathwayChildNodeTable.java,v 1.16 2006-02-14 17:24:44 cerami Exp $
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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.jdom.JDOMException;

import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryUtils;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummaryException;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary;

/**
 * Custom jsp tag for displaying pathway child node (1 level deep)
 *
 * @author Benjamin Gross, Ethan Cerami.
 */
public class PathwayChildNodeTable extends HtmlTable {
    private EntitySummary entitySummary;

    /**
     * Receives EntitySummary Object.
     *
     * @param entitySummary EntitySummary
     */
    public void setEntitySummary (EntitySummary entitySummary){
        this.entitySummary = entitySummary;
    }

    /**
     * Executes JSP Custom Tag
     */
    protected void subDoStartTag() {
        startRow();
        outputRecord();
        endRow();
    }

    /**
     * Output the EntitySummary Information.
     */
    private void outputRecord()  {
        BioPaxEntityTypeMap map = new BioPaxEntityTypeMap();
        // summary
        if (entitySummary instanceof InteractionSummary) {
            InteractionSummary interactionSummary = (InteractionSummary) entitySummary;
            String interactionString = InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
            if (interactionString != null) {
                String interactionType = (String) map.get(entitySummary.getSpecificType());
                append("<td>" + interactionType + "</td>");
                append("<td>" + interactionString + "</td>");
            }
        } else {
            append("<td>Not yet supported:   " + entitySummary.getSpecificType() + "</td>");
        }
        // details hyperlink
        String uri = "record.do?id=" + entitySummary.getRecordID();
        append("<td><a href=\"" + uri + "\">View Details</a></td>");
    }
}