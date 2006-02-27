// $Id: PathwayInteractionTable.java,v 1.23 2006-02-27 21:51:11 grossb Exp $
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

import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary;
import org.mskcc.pathdb.schemas.biopax.summary.InteractionSummaryUtils;
import org.mskcc.pathdb.schemas.biopax.summary.EntitySummaryException;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.jdom.JDOMException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Custom jsp tag for displaying interactions
 *
 * @author Benjamin Gross
 */
public class PathwayInteractionTable extends HtmlTable {

    /**
     * Physical Interaction.
     */
    private InteractionSummary interactionSummary;

    /**
     * Receives InteractionSummary Attribute.
     *
     * @param interactionSummary InteractionSummary.
     */
    public void setInteractionSummary(InteractionSummary interactionSummary) {
        this.interactionSummary = interactionSummary;
    }

    /**
     * Executes JSP Custom Tag
     *
     * @throws DaoException
     * @throws IOException
     * @throws EntitySummaryException
     * @throws JDOMException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected void subDoStartTag()
            throws DaoException, IOException, EntitySummaryException, JDOMException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // here we go
        if (interactionSummary != null) {
            startRow();
            append("<td>");
            outputRecords();
            append("</td>");
            endRow();
        }
    }

    /**
     * Output the Interaction Information.
     */
    private void outputRecords() {

        //easy huh ?
        String interactionSummaryString =
                InteractionSummaryUtils.createInteractionSummaryString(interactionSummary);
        if (interactionSummaryString != null) {
            append(interactionSummaryString);
        }
    }
}
