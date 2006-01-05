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

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.Attribute;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.PhysicalInteractionComponent;

/**
 * This class contains utilities
 * functions.
 *
 * @author Benjamin Gross.
 */
public class BioPaxRecordUtil {

    /**
     * Creates PhysicalInteractionComponent given
	 * sequence or physicalEntity participant Element.
	 *
	 * @param Element e
	 * @return PhysicalInteractionComponent
	 * @throws Exception.
     */
	public static PhysicalInteractionComponent createPhysicalInteractionComponent(Element e) throws Exception {

		boolean success;

		// this is object to return
		PhysicalInteractionComponent physicalInteractionComponent = new PhysicalInteractionComponent();

		// first get physical entity
		success = BioPaxRecordUtil.setPhysicalEntity(physicalInteractionComponent, e);
		if (!success){
			return null;
		}

		// get cellular location
		success = BioPaxRecordUtil.setCellularLocation(physicalInteractionComponent, e);
		if (!success){
			return null;
		}

		// feature list
		success = BioPaxRecordUtil.setFeatureList(physicalInteractionComponent, e);
		if (!success){
			return null;
		}

		// made it this far
		return physicalInteractionComponent;
	}

    /**
     * Gets Physical Entity.
	 *
	 * @param record String.
	 * @return String.
	 * @throws Exception.
     */
	public static String getEntity(String record) throws Exception {

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

		return getEntityName(cPathRecord.getXmlContent());
	}

    /**
     * Gets Entity Name, return as link.
	 *
	 * @param recordID long.
	 * @param xmlContent String.
	 * @return String.
	 * @throws Exception.
     */
	public static String getEntity(long recordID, String xmlContent) throws Exception {

		// string to return
		String entity = getEntityName(xmlContent);
		return (entity != null) ?
			new String("<a href=\"record.do?id=" +
					   String.valueOf(recordID) +
					   "\">" + entity +
					   "</a>")
			: null;
	}

	/**
	 * Sets physical entity name and id of given component from
	 * data within sequence or physicalEntity participant Element.
	 *
	 * @param physicalInteractionComponent PhysicalInteractionComponent.
	 * @param e Element.
	 * @return boolean.
	 * @throws Exception.
	 */
	private static boolean setPhysicalEntity(PhysicalInteractionComponent physicalInteractionComponent, Element e) throws Exception {

		// setup/perform query
		XPath xpath = XPath.newInstance("bp:PHYSICAL-ENTITY");
		xpath.addNamespace("bp", e.getNamespaceURI());
		Element physicalEntity = (Element) xpath.selectSingleNode(e);

		// process query
		if (physicalEntity != null) {
			Attribute rdfResourceAttribute =
				physicalEntity.getAttribute(RdfConstants.RESOURCE_ATTRIBUTE, RdfConstants.RDF_NAMESPACE);
			if (rdfResourceAttribute != null) {
				String rdfKey = RdfUtil.removeHashMark(rdfResourceAttribute.getValue());
				// get physical entity
				String physicalEntityString = BioPaxRecordUtil.getEntity(rdfKey);
				physicalInteractionComponent.setName(physicalEntityString);
				// cook id to save
				int indexOfID = rdfKey.lastIndexOf("-");
				if (indexOfID == -1){
					return false;
				}
				indexOfID += 1;
				String cookedKey = rdfKey.substring(indexOfID);
				Long recordID = new Long(cookedKey);
				physicalInteractionComponent.setRecordID(recordID.longValue());
				return true;
			}
		}

		// made it here
		return false;
	}

	/**
	 * Sets cellular location of given component from data within
	 * sequence or physicalEntity participant Element.
	 *
	 * @param physicalInteractionComponent PhysicalInteractionComponent.
	 * @param e Element.
	 * @return boolean.
	 * @throws Exception.
	 */
	private static boolean setCellularLocation(PhysicalInteractionComponent physicalInteractionComponent, Element e) throws Exception {

		// setup/perform query
		XPath xpath = XPath.newInstance("bp:CELLULAR-LOCATION");
		xpath.addNamespace("bp", e.getNamespaceURI());
		Element cellularLocation = (Element) xpath.selectSingleNode(e);

		// did we find a cellular location element ?
		if (cellularLocation != null){

			// does element point to a local id ?
			Attribute rdfResourceAttribute =
				cellularLocation.getAttribute(RdfConstants.RESOURCE_ATTRIBUTE, RdfConstants.RDF_NAMESPACE);
			if (rdfResourceAttribute != null){
				//physicalInteractionComponent.setCellularLocation("undetermined");
				return true;
			}
			
			// we should now set our element to cellular location and look from here.
			e = cellularLocation;

			// setup/perform query
			xpath = XPath.newInstance("*/bp:TERM");
			xpath.addNamespace("bp", e.getNamespaceURI());
			cellularLocation = (Element) xpath.selectSingleNode(e);

			// process query
			if (cellularLocation != null){
				physicalInteractionComponent.setCellularLocation(cellularLocation.getTextNormalize());
				return true;
			}
			// term not available, try for xref id
			else{
				xpath = XPath.newInstance("*/bp:XREF/*/bp:ID");
				xpath.addNamespace("bp", e.getNamespaceURI());
				cellularLocation = (Element) xpath.selectSingleNode(e);
				if (cellularLocation != null){
					physicalInteractionComponent.setCellularLocation(cellularLocation.getTextNormalize());
					return true;
				}
			}
		}

		// made it here
		return false;
	}

	/**
	 * Sets feature list of given component from data within
	 * sequence or physicalEntity participant Element.
	 *
	 * @param physicalInteractionComponent PhysicalInteractionComponent.
	 * @param e Element.
	 * @return boolean.
	 * @throws Exception.
	 */
	private static boolean setFeatureList(PhysicalInteractionComponent physicalInteractionComponent, Element e) throws Exception {

		// vector of features
		Vector featureList = new Vector();

		// setup/perform query
		XPath xpath = XPath.newInstance("bp:SEQUENCE-FEATURE-LIST/*");
		xpath.addNamespace("bp", e.getNamespaceURI());
		List list = xpath.selectNodes(e);

		if (list != null && list.size() > 0) {
			for (int lc = 0; lc < list.size(); lc++) {
				// reset the list element
				e = (Element)list.get(lc);
				// try to get term
				xpath = XPath.newInstance("bp:FEATURE-TYPE/*/bp:TERM");
				xpath.addNamespace("bp", e.getNamespaceURI());
				Element feature = (Element) xpath.selectSingleNode(e);
				if (feature != null && feature.getTextNormalize().length() > 0){
					featureList.add(feature.getTextNormalize());
				}
				else{
					xpath = XPath.newInstance("bp:FEATURE-TYPE/*/bp:XREF/*/bp:ID");
					xpath.addNamespace("bp", e.getNamespaceURI());
					feature = (Element) xpath.selectSingleNode(e);
					if (feature != null && feature.getTextNormalize().length() > 0){
						featureList.add(feature.getTextNormalize());
					}
					else{
						return false;
					}
				}
			}
		}
		
		// add list to component - if we have stuff to add
		if (featureList.size() > 0){
			physicalInteractionComponent.setFeatureList(featureList);
		}

		// made it here
		return true;
	}

    /**
     * Gets Entity Name, return as link.
	 *
	 * @param xmlContent String.
	 * @return String.
	 * @throws Exception.
     */
	private static String getEntityName(String xmlContent) throws Exception {

		// setup xml parsing
		Vector queries = new Vector();
		queries.add(new String("/*/bp:SHORT-NAME"));
		queries.add(new String("/*/bp:NAME"));
		queries.add(new String("/bp:NAME"));
		SAXBuilder builder = new SAXBuilder();
		StringReader reader = new StringReader(xmlContent);
		Document bioPaxDoc = builder.build(reader);
		Element root = bioPaxDoc.getRootElement();
		XPath xpath;
		for (int lc = 0; lc < queries.size(); lc++){
			xpath = XPath.newInstance((String)queries.elementAt(lc));
			xpath.addNamespace("bp", root.getNamespaceURI());
			Element e = (Element) xpath.selectSingleNode(root);
			if (e != null && e.getTextNormalize().length() > 0) {
				return new String(e.getTextNormalize());
			}
		}
		return null;
	}

	/**
	 * Return CellularName Element given record id.
	 *
	 * @param record String.
	 * @return Element.
	 * @throws Exception.
	 */
	private static Element getCellularNameElement(String record) throws Exception {

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

		SAXBuilder builder = new SAXBuilder();
		StringReader reader = new StringReader(cPathRecord.getXmlContent());
		Document bioPaxDoc = builder.build(reader);
		Element root = bioPaxDoc.getRootElement();

		// outta here
		return root;
	}
}
