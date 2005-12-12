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

// package
package org.mskcc.pathdb.schemas.biopax;


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
import org.mskcc.pathdb.model.BioPaxControlTypeMap;
import org.mskcc.pathdb.schemas.biopax.RdfUtil;
import org.mskcc.pathdb.schemas.biopax.RdfConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.model.PhysicalInteraction;

/**
 * This class parses interaction data
 * given a cPath record id.
 *
 * @author Benjamin Gross.
 */
public class InteractionParser {

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
	 * Reference to CPathRecord.
	 */
	CPathRecord record;

	/**
	 * Reference to BioPaxConstants Class.
	 */
	private BioPaxConstants biopaxConstants;

	/**
	 * Constructor.
	 *
	 * @param recordID long.
	 * @throws Exception.
	 */
	public InteractionParser(long recordID) throws Exception {
		
		// setup biopax constants
		biopaxConstants = new BioPaxConstants();

		// get cpath record
		DaoCPath cPath = DaoCPath.getInstance();
		record = cPath.getRecordById(recordID);
	}

	/**
	 * Finds/returns conversion information.
	 *
	 * @return PhysicalInteraction.
	 */
	public PhysicalInteraction getConversionInformation(){

		if (biopaxConstants.isPhysicalInteraction(record.getSpecificType()) &&
			biopaxConstants.isConversion(record.getSpecificType())){
			return getInformation("/*/bp:LEFT/*/bp:PHYSICAL-ENTITY",
								  "/*/bp:RIGHT/*/bp:PHYSICAL-ENTITY");
		}
		return null;
	}

	/**
	 * Finds/returns controller information.
	 *
	 * @return PhysicalInteraction.
	 */
	public PhysicalInteraction getControllerInformation(){

		// is this a physical interaction
		if (biopaxConstants.isPhysicalInteraction(record.getSpecificType()) &&
			biopaxConstants.isControl(record.getSpecificType())){
			return getInformation("/*/bp:CONTROLLER/*/bp:PHYSICAL-ENTITY",
								  "/*/bp:CONTROLLED");
		}
		return null;
	}

	/**
	 * Finds/returns conversion information.
	 *
	 * @param leftSideParticipants String.
	 * @param rightSideParticipants String.
	 * @return PhysicalInteraction.
	 */
	public PhysicalInteraction getInformation(String leftSideParticipants, String rightSideParticipants){

		// used for xml parsing
		SAXBuilder builder = new SAXBuilder();
		StringReader reader = new StringReader (record.getXmlContent());
		if (reader == null){
			return null;
		}

		try{
			Document bioPaxDoc = builder.build(reader);
			
			if (bioPaxDoc != null){
				root = bioPaxDoc.getRootElement();
			}

			// get type
			String interactionType = getInteractionType();

			// get participants
			Vector leftParticipants = getPhysicalInteractionInformation(leftSideParticipants);
			Vector rightParticipants = getPhysicalInteractionInformation(rightSideParticipants);

			return (new PhysicalInteraction(interactionType, leftParticipants, rightParticipants));
		}
		catch(Exception e){
			return null;
		}
	}

	/**
	 * Gets Interaction Type in Plain English.
	 *
	 * @return String
	 * @throws Exception
	 */
	private String getInteractionType() throws Exception {

		// set the control type string
		String interactionType = null;

		// outta here
		return interactionType;
	}

	/**
	 * Gets PhysicalInteractionInformation, given query.
	 *
	 * @param query String
	 * @return Vector
	 * @throws Exception
	 */
	private Vector getPhysicalInteractionInformation(String query) throws Exception {

		// our list to return
		Vector participantVector = new Vector();

		// perform query
		xpath = XPath.newInstance(query);
		xpath.addNamespace("bp", root.getNamespaceURI());
		List list = xpath.selectNodes(root);

		// interate through results
		if (list != null && list.size() > 0) {
			for (int lc = 0; lc < list.size(); lc++) {
				e = (Element) list.get(lc);
				Attribute rdfResourceAttribute =
					e.getAttribute(RdfConstants.RESOURCE_ATTRIBUTE, RdfConstants.RDF_NAMESPACE);
				if (rdfResourceAttribute != null) {
					String rdfKey = RdfUtil.removeHashMark
						(rdfResourceAttribute.getValue());
					// get physical entity
					String physicalEntity = getPhysicalEntity(rdfKey);
					// add to vector
					if (physicalEntity != null){
						participantVector.add(physicalEntity);
					}
				}
			}
		}

		return (participantVector.size() > 0) ? participantVector : null;
	}

    /**
     * Gets Physical Entity.
	 *
	 * @param record String
	 * @return String
     */
	private String getPhysicalEntity(String record) throws Exception {

		// String to return
		String physicalEntity = null;
		
		// get CPathRecord given record id argument
		int indexOfId = record.lastIndexOf("-");
		if (indexOfId == -1){
			return null;
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
				physicalEntity = new String(e.getTextNormalize());
				break;
			}
		}

		// outta here
		return physicalEntity;
	}
}
