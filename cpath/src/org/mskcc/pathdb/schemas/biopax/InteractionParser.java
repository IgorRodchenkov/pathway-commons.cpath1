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

import org.mskcc.pathdb.schemas.biopax.RdfUtil;
import org.mskcc.pathdb.schemas.biopax.RdfConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.model.BioPaxControlTypeMap;
import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.model.PhysicalInteraction;
import org.mskcc.pathdb.model.PhysicalInteractionComponent;
import org.mskcc.pathdb.model.CPathRecord;

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

	/*
	 * Reference to CPathRecord.
	 */
	private CPathRecord record;

	/**
	 * Reference to BioPaxConstants Class.
	 */
	private BioPaxConstants biopaxConstants;

	/**
	 * Reference to SAXBuilder.
	 */
	private SAXBuilder builder;

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

		// init our builder
		builder = new SAXBuilder();
	}

	/**
	 * Finds/returns physical interaction information.
	 *
	 * @return PhysicalInteraction.
	 */
	public PhysicalInteraction getPhysicalInteractionInformation(){

		if (biopaxConstants.isPhysicalInteraction(record.getSpecificType()) &&
			!biopaxConstants.isConversion(record.getSpecificType()) &&
			!biopaxConstants.isControl(record.getSpecificType())){
			return getInformation("/*/bp:PARTICIPANTS/*/bp:PHYSICAL-ENTITY");
		}
		return null;
	}

	/**
	 * Finds/returns conversion information.
	 *
	 * @return PhysicalInteraction.
	 */
	public PhysicalInteraction getConversionInformation(){

		if (biopaxConstants.isPhysicalInteraction(record.getSpecificType()) &&
			biopaxConstants.isConversion(record.getSpecificType())){
			return getInformation("-->",
								  "/*/bp:LEFT/*/bp:PHYSICAL-ENTITY",
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
			return getInformation("",
								  "/*/bp:CONTROLLER/*/bp:PHYSICAL-ENTITY",
								  "/*/bp:CONTROLLED");
		}
		return null;
	}

	/**
	 * Finds/returns interaction information.
	 *
	 * @param participants String.
	 * @return PhysicalInteraction.
	 */
	private PhysicalInteraction getInformation(String participants){

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
			Vector participantsVector = getPhysicalInteractionInformation(participants);

			// outta here
			return (new PhysicalInteraction(interactionType, "", participantsVector, null));
		}
		catch(Exception e){
			return null;
		}
	}

	/**
	 * Finds/returns interaction information.
	 *
	 * @param operator String.
	 * @param leftSideParticipants String.
	 * @param rightSideParticipants String.
	 * @return PhysicalInteraction.
	 */
	private PhysicalInteraction getInformation(String operator, String leftSideParticipants, String rightSideParticipants){

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

			// get operator
			if (operator.equals("")){
				operator = getControlType();
				if (operator == null){
					operator = "CONTROL-TYPE NOT FOUND";
				}
			}

			// outta here
			return (new PhysicalInteraction(interactionType, operator, leftParticipants, rightParticipants));
		}
		catch(Exception e){
			return null;
		}
	}

	/**
	 * Gets Interaction Type in Plain English.
	 *
	 * @return String
	 */
	private String getInteractionType() {

		// set the control type string
		String interactionType = null;

		// our biopax entity type 2 plain english hashmap
		BioPaxEntityTypeMap entityTypeMap = new BioPaxEntityTypeMap();
		interactionType = (String)entityTypeMap.get(record.getSpecificType());

		// outta here
		return interactionType;
	}

	/**
	 * Get Control Type.
	 *
	 * @return String.
	 */
	private String getControlType() {

		// set the control type string
		String controlType = null;

		// our biopax entity type 2 plain english hashmap
		BioPaxEntityTypeMap entityTypeMap = new BioPaxEntityTypeMap();
		BioPaxControlTypeMap controlTypeMap = new BioPaxControlTypeMap();
		if (record.getSpecificType().equalsIgnoreCase("Catalysis")){
			controlType = new String ("[" + controlTypeMap.get("ACTIVATION") + "]");
		}
		else{
			try{
				controlType =
					new String ("[" + controlTypeMap.get(getControlTypeInformation("/*/bp:CONTROL-TYPE")) + "]");
			}
			catch(Exception e){
			}
	    }

		// outta here
		return controlType;
	}

	/**
	 * Gets CONTROL-TYPE.
	 *
	 * @param query String.
	 * @return String.
	 * @throws Exception
	 */
	private String getControlTypeInformation(String query) throws Exception {

		// our list to return
		String controlType = null;

		// perform query
		xpath = XPath.newInstance(query);
		xpath.addNamespace("bp", root.getNamespaceURI());
		Element e = (Element) xpath.selectSingleNode(root);
		if (e != null) {
			controlType = e.getTextNormalize();
		}
		return controlType;
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
					String physicalEntity = BioPaxRecordUtil.getEntity(rdfKey);
					// cook id to save
					int indexOfID = rdfKey.lastIndexOf("-");
					if (indexOfID == -1){
						throw new Exception("Corrupt Record ID");
					}
					indexOfID += 1;
					String cookedKey = rdfKey.substring(indexOfID);
					Long recordID = new Long(cookedKey);
					// add to vector
					if (physicalEntity != null){
						participantVector.add(new PhysicalInteractionComponent(physicalEntity, recordID.longValue()));
					}
				}
			}
		}

		return (participantVector.size() > 0) ? participantVector : null;
	}
}
