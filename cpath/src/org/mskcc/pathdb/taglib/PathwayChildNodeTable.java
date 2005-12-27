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
import java.util.List;
import java.util.Vector;
import java.io.StringReader;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.sql.dao.DaoCPath;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.PhysicalInteraction;
import org.mskcc.pathdb.model.PhysicalInteractionComponent;
import org.mskcc.pathdb.schemas.biopax.InteractionParser;

/**
 * Custom jsp tag for displaying pathway child node (1 level deep)
 *
 * @author Benjamin Gross
 */
public class PathwayChildNodeTable extends HtmlTable {

	/**
	 * Reference to XML Element.
	 */
	private Element e;

	/**
	 * Reference to XML Root.
	 */
	private Element root;

	/**
	 * Reference to XPath Class.
	 */
	private XPath xpath;

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
	 * @param long recid.
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
     */
    private void outputRecords() {

		// used for xml parsing
		SAXBuilder builder = new SAXBuilder();
		StringReader reader = new StringReader (record.getXmlContent());
		if (reader == null){
			jspError(new Exception("Cannot initialize Reader"));
			return;
		}

		try{
			Document bioPaxDoc = builder.build(reader);

			if (bioPaxDoc != null){
				root = bioPaxDoc.getRootElement();
			}

			// name
			append("<td>" + record.getName() + "</td>");

			// short name
			xpath = XPath.newInstance("/*/bp:SHORT-NAME");
			xpath.addNamespace("bp", root.getNamespaceURI());
			e = (Element) xpath.selectSingleNode(root);
			String shortName = null;
			if (e != null) {
				shortName = e.getTextNormalize();
				if (!shortName.equals(record.getName())){
					append("<td>" + shortName + "</td>");
				}
			}

			// interaction summary
			String interactionString = getInteractionSummary(record.getId());
			append("<td>" + interactionString + "</td>");

			// details hyperlink
			String uri = "record.do?id=" + recID;
			append("<td><a href=\"" + uri + "\">View Details</a></td>");
		}
		catch(Exception e){
			jspError(e);
		}
    }

    /**
     * Gets Interaction Summary string.
	 *
	 * @param recordID long.
	 * @return String.
	 * @throws Exception.
     */
	private String getInteractionSummary(long recordID) throws Exception {
		// get interaction parser
		InteractionParser interactionParser = new InteractionParser(recordID);

		// get physical interacittion
		PhysicalInteraction physicalInteraction = interactionParser.getConversionInformation();
		if (physicalInteraction == null){
			physicalInteraction = interactionParser.getControllerInformation();
		}
		if (physicalInteraction == null){
			return "";
		}

		return createInteractionSummaryString(physicalInteraction);
	}

    /**
     * Creates the interaction summary string.
	 *
	 * @param physicalInteraction PhysicalInteractiong.
	 * @return String.
     */
	private String createInteractionSummaryString(PhysicalInteraction physicalInteraction){

		int lc, cnt;
		Vector components;
		String summaryString = new String();
		
		// left side
		components = physicalInteraction.getLeftSideComponents();
		cnt = components.size();
		for (lc = 0; lc < cnt; lc++){
			PhysicalInteractionComponent component = (PhysicalInteractionComponent)components.elementAt(lc);
			String link = new String("<a href=\"record.do?id=" +
									 String.valueOf(component.getRecordID()) +
									 "\">" + component.getName() +
									 "</a>");
			summaryString = link;
			if (lc < cnt-1){
				summaryString += " + ";
			}
		}

		// operator
		summaryString += (" " + physicalInteraction.getOperator() + " ");

		// right side
		components = physicalInteraction.getRightSideComponents();
		cnt = components.size();
		for (lc = 0; lc < cnt; lc++){
			PhysicalInteractionComponent component = (PhysicalInteractionComponent)components.elementAt(lc);
			String link = new String("<a href=\"record.do?id=" +
									 String.valueOf(component.getRecordID()) +
									 "\">" + component.getName() +
									 "</a>");
			summaryString += link;
			if (lc < cnt-1){
				summaryString += " + ";
			}
		}
		
		// outta here
		return summaryString;
	}

    /**
     * Handles error processing.
	 *
	 * @param e Exception.
     */
	private void jspError(Exception e){
		startRow();
		append("<td><font color=\"red\">Exception Thrown: " + e.getMessage() + "</font></td>");
		endRow();
	}
}
