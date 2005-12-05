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

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.sql.dao.DaoCPath;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.biopax.RdfUtil;
import org.mskcc.pathdb.schemas.biopax.RdfConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;

/**
 * Custom jps tag for displaying interactions
 *
 * @author Benjamin Gross
 */
public class PathwayInteractionTable extends HtmlTable {

	/**
	 * Reference to XML Element.
	 */
	Element e;

	/**
	 * Reference to XML Root.
	 */
	Element root;

	/**
	 * Reference to XPath Class.
	 */
	XPath xpath;

	/**
	 * Reference to BioPaxConstants Class.
	 */
	BioPaxConstants biopaxConstants;

	/**
	 * Reference to CPathRecord.
	 */
	CPathRecord record;

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    protected void subDoStartTag() throws Exception {

		biopaxConstants = new BioPaxConstants();
		record = (CPathRecord)pageContext.getRequest().getAttribute("RECORD");

		if (biopaxConstants.isPhysicalInteraction(record.getSpecificType())){
			createHeader("Interactions");
			startTable();
			outputRecords();
			endTable();
		}
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

			// display conversion or control information
			if (biopaxConstants.isConversion(record.getSpecificType())){
				outputConversionInformation();
			}
			else if (biopaxConstants.isControl(record.getSpecificType())){
				outputControlInformation();
			}
		}
		catch(Exception e){
			jspError(e);
		}
    }

    /**
     * Output the Conversion Information.
	 *
	 * @throws Exception.
     */
	private void outputConversionInformation() throws Exception {

		// get participants
		Vector leftParticipants = getConversionInformation("/*/bp:LEFT/*/bp:PHYSICAL-ENTITY");
		Vector rightParticipants = getConversionInformation("/*/bp:RIGHT/*/bp:PHYSICAL-ENTITY");

		startTable();
		startRow();
		append("<TD><b>Conversion Information:</b></TD>");
		endRow();

		startRow();
		if (leftParticipants != null){
			append("<TD>Substrates");
			for (int lc = 0; lc < leftParticipants.size(); lc++) {
				if (lc == 0){
					append("<UL>");
				}
				append("<LI>");
				append((String)leftParticipants.get(lc));
			}
			append("</UL>");
			append("</TD>");
		}

		if (rightParticipants != null){
			append("<TD>Products");
			for (int lc = 0; lc < rightParticipants.size(); lc++) {
				if (lc == 0){
					append("<UL>");
				}
				append("<LI>");
				append((String)rightParticipants.get(lc));
			}
			append("</UL>");
			append("</TD>");
		}
		endRow();

		endTable();
	}

	/**
	 * Gets conversion information given query.
	 *
	 * @param query String
	 * @return Vector
	 * @throws JDOMException
	 */
	private Vector getConversionInformation(String query) throws Exception {

		// our list to return
		Vector participantVector = new Vector();

		// interate through "left" participants
		xpath = XPath.newInstance(query);
		xpath.addNamespace("bp", root.getNamespaceURI());
		List list = xpath.selectNodes(root);
		if (list != null && list.size() > 0) {
			for (int lc = 0; lc < list.size(); lc++) {
				e = (Element) list.get(lc);
				Attribute rdfResourceAttribute =
					e.getAttribute(RdfConstants.RESOURCE_ATTRIBUTE, RdfConstants.RDF_NAMESPACE);
				if (rdfResourceAttribute != null) {
					String rdfKey = RdfUtil.removeHashMark
						(rdfResourceAttribute.getValue());
					// create link
					String link = getPhysicalEntityLink(rdfKey);
					// add to record
					if (link != null){
						participantVector.add(link);
					}
				}
			}
		}

		return (participantVector.size() > 0) ? participantVector : null;
	}

    /**
     * Gets Physical Entity Link.
	 *
	 * @param record String
	 * @return String
	 * @throws Exception
     */
	private String getPhysicalEntityLink(String record) throws Exception {

		// String to return
		String link = null;
		
		// get CPathRecord given record id argument
		int indexOfId = record.lastIndexOf("-");
		if (indexOfId == -1){
			throw new Exception("Corrupt Record ID");
		}
		indexOfId += 1;
		String cookedRecord = record.substring(indexOfId);
		Long id = new Long(cookedRecord);
		DaoCPath cPath = DaoCPath.getInstance();
		CPathRecord cPathRecord = cPath.getRecordById(id.longValue());

		// setup xml parsing
		Vector queries = new Vector();
		queries.add(new String("/*/bp:SHORT-NAME"));
		queries.add(new String("/*/bp:NAME"));
		queries.add(new String("/bp:NAME"));
		SAXBuilder builder = new SAXBuilder();
		StringReader reader = new StringReader (cPathRecord.getXmlContent());
		Document bioPaxDoc = builder.build(reader);
		Element root = bioPaxDoc.getRootElement();
		XPath xpath;
		for (int lc = 0; lc < queries.size(); lc++){
			xpath = XPath.newInstance((String)queries.elementAt(lc));
			xpath.addNamespace("bp", root.getNamespaceURI());
			Element e = (Element) xpath.selectSingleNode(root);
			if (e != null) {
				link = new String("<a href=\"record.do?id=" +
								  cookedRecord +
								  "\">" + e.getTextNormalize() +
								  "</a>");
				break;
			}
		}

		// outta here
		return link;
	}

    /**
     * Output the Control Information.
	 *
	 * @throws Exception
     */
	private void outputControlInformation() throws Exception {
	}

    /**
     * Handles error processing.
     */
	private void jspError(Exception e){
		startRow();
		append("<td><font color=\"red\">Exception Thrown: " + e.getMessage() + "</font></td>");
		endRow();
	}
}
