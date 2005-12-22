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
import java.util.Vector;
import org.mskcc.pathdb.model.PhysicalInteraction;
import org.mskcc.pathdb.model.PhysicalInteractionComponent;

/**
 * Custom jsp tag for displaying interactions
 *
 * @author Benjamin Gross
 */
public class PathwayInteractionTable extends HtmlTable {

    /**
     * Physical Interaction.
     */
    private PhysicalInteraction physicalInteraction;

	/**
	 * Receives PhysicalInteraction Attribute.
	 *
	 * @param physicalInteraction PhysicalInteraction.
	 */
	public void setPhysicalinteraction(PhysicalInteraction physicalInteraction){
		this.physicalInteraction = physicalInteraction;
	}

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    protected void subDoStartTag() throws Exception {

		// here we go
		if (physicalInteraction != null){
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

		int lc, cnt;
		Vector components;
		
		// left side
		components = physicalInteraction.getLeftSideComponents();
		cnt = components.size();
		for (lc = 0; lc < cnt; lc++){
			PhysicalInteractionComponent component = (PhysicalInteractionComponent)components.elementAt(lc);
			String link = new String("<a href=\"record.do?id=" +
									 String.valueOf(component.getRecordID()) +
									 "\">" + component.getName() +
									 "</a>");
			append(link);
			if (lc < cnt-1){
				append(" +");
			}
		}

		// operator
		append(" " + physicalInteraction.getOperator() + " ");

		// right side
		components = physicalInteraction.getRightSideComponents();
		cnt = components.size();
		for (lc = 0; lc < cnt; lc++){
			PhysicalInteractionComponent component = (PhysicalInteractionComponent)components.elementAt(lc);
			String link = new String("<a href=\"record.do?id=" +
									 String.valueOf(component.getRecordID()) +
									 "\">" + component.getName() +
									 "</a>");
			append(link);
			if (lc < cnt-1){
				append(" +");
			}
		}
    }
}
