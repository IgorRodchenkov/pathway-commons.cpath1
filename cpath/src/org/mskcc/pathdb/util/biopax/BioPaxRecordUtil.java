// $Id: BioPaxRecordUtil.java,v 1.11 2006-02-14 16:23:20 grossb Exp $
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

// package
package org.mskcc.pathdb.util.biopax;

// imports
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.StringReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.Attribute;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.util.rdf.RdfUtil;
import org.mskcc.pathdb.util.rdf.RdfQuery;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.ParticipantSummaryComponent;

/**
 * This class contains utilities
 * functions to query biopax docs.
 *
 * @author Benjamin Gross.
 */
public class BioPaxRecordUtil {

    /**
     * Creates BioPaxRecordSummary given
     * a CPathRecord.
     *
     * @param record CPathRecord
     * @return BioPaxRecordSummary
     * @throws IllegalArgumentException
     * @throws JDOMException
     * @throws IOException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws DaoException
     */
    public static BioPaxRecordSummary createBioPaxRecordSummary(CPathRecord record)
		throws IllegalArgumentException, JDOMException, IOException, IllegalAccessException,
			   NoSuchMethodException, InvocationTargetException, DaoException {

		// check record for validity
		if (record == null){
			throw new IllegalArgumentException("Record Argument is Null" + record);
		}
		if (!record.getXmlType().equals(XmlRecordType.BIO_PAX)){
			throw new IllegalArgumentException("Specified cPath record is not of type " + XmlRecordType.BIO_PAX);
		}

		// some flags to determine return
		boolean setTypeSuccess, setNameSuccess, setShortNameSuccess, setSynonymSuccess, setOrganismSuccess;
		boolean setDataSourceSuccess, setAvailabilitySuccess, setExternalLinksSuccess, setCommentSuccess;

		// setup for queries
		StringReader reader = new StringReader (record.getXmlContent());
		SAXBuilder builder = new SAXBuilder();
		Document bioPaxDoc = builder.build(reader);
		Element root = bioPaxDoc.getRootElement();

        // this is object to return
        BioPaxRecordSummary biopaxRecordSummary = new BioPaxRecordSummary();

		// set type
		String type = record.getSpecificType();
		if (type != null){
			biopaxRecordSummary.setType(type);
			setTypeSuccess = true;
		}
		else{
			setTypeSuccess = false;
		}
		// set name
		setNameSuccess = setBioPaxRecordStringAttribute(root, "/*/bp:NAME", "setName", biopaxRecordSummary);
		// set short name
		setShortNameSuccess = setBioPaxRecordStringAttribute(root, "/*/bp:SHORT-NAME", "setShortName", biopaxRecordSummary);
		// set synonyms
		setSynonymSuccess = setBioPaxRecordRecordListAttribute(root, "/*/bp:SYNONYMS", "setSynonyms", biopaxRecordSummary);
		// set organsim
		setOrganismSuccess = setBioPaxRecordStringAttribute(root, "/*/bp:ORGANISM/*/bp:NAME", "setOrganism", biopaxRecordSummary);
		// set data source
		setDataSourceSuccess = setBioPaxRecordStringAttribute(root, "/*/bp:DATA-SOURCE/*/bp:NAME", "setDataSource", biopaxRecordSummary);
		// availability
		setAvailabilitySuccess = setBioPaxRecordStringAttribute(root, "/*/bp:AVAILABILITY", "setAvailability", biopaxRecordSummary);
		// external links
		DaoExternalLink externalLinker = DaoExternalLink.getInstance();
		ArrayList externalLinks = externalLinker.getRecordsByCPathId(record.getId());
		if (externalLinks.size() > 0) {
			biopaxRecordSummary.setExternalLinks(externalLinks);
			setExternalLinksSuccess = true;
		}
		else{
			setExternalLinksSuccess = false;
		}
		// comment
		setCommentSuccess = setBioPaxRecordStringAttribute(root, "/*/bp:COMMENT", "setComment", biopaxRecordSummary);

		// outta here
		return (setTypeSuccess || setNameSuccess || setShortNameSuccess ||
				setSynonymSuccess || setOrganismSuccess || setDataSourceSuccess ||
				setAvailabilitySuccess || setExternalLinksSuccess || setCommentSuccess) ? biopaxRecordSummary : null;
	}

    /**
     * Creates ParticipantSummaryComponent given
     * sequence or physicalEntity participant Element.
     *
	 * @param record CPathRecord
     * @param e Element
     * @return ParticipantSummaryComponent
     * @throws DaoException
     * @throws JDOMException
     * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
     */
    public static ParticipantSummaryComponent createInteractionSummaryComponent(CPathRecord record, Element e)
            throws DaoException, JDOMException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		// check record for validity
		if (record == null){
			throw new IllegalArgumentException("Record Argument is Null" + record);
		}
		if (!record.getXmlType().equals(XmlRecordType.BIO_PAX)){
			throw new IllegalArgumentException("Specified cPath record is not of type " + XmlRecordType.BIO_PAX);
		}

		// success flags
        boolean setPhysicalEntitySuccess, setCellularLocationSuccess, setFeatureListSuccess;

		// create a biopaxRecordSummary
		BioPaxRecordSummary biopaxRecordSummary = createBioPaxRecordSummary(record);

        // this is object to return
        ParticipantSummaryComponent participantSummaryComponent = new ParticipantSummaryComponent(biopaxRecordSummary);

        // first get physical entity
        setPhysicalEntitySuccess = BioPaxRecordUtil.setPhysicalEntity(participantSummaryComponent, e);

        // get cellular location
        setCellularLocationSuccess = BioPaxRecordUtil.setCellularLocation(participantSummaryComponent, record, e);

        // feature list
        setFeatureListSuccess = BioPaxRecordUtil.setFeatureList(participantSummaryComponent, record, e);

        // made it this far
        return (setPhysicalEntitySuccess ||
				setCellularLocationSuccess ||
				setFeatureListSuccess) ? participantSummaryComponent : null;
    }

    /**
     * Gets Entity Name, returned as String.
     *
     * @param record String
     * @return String
     * @throws DaoException
     * @throws IOException
     * @throws JDOMException
     */
    public static String getPhysicalEntityName(String record) throws DaoException, IOException, JDOMException {

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
     * Gets Entity Name, return as Link.
     *
     * @param recordID long
     * @param xmlContent String
     * @return String
     * @throws IOException
     * @throws JDOMException
     */
    public static String getPhysicalEntityNameAsLink(long recordID, String xmlContent) throws IOException, JDOMException {

        // string to return
        String entity = getEntityName(xmlContent);
        return (entity != null) ?
            ("<a href=\"record.do?id=" + String.valueOf(recordID) + "\">" + entity + "</a>") :
            null;
    }

    /**
     * Gets Entity Name, return as link.
     *
     * @param xmlContent String
     * @return String
     * @throws IOException
     * @throws JDOMException
     */
    private static String getEntityName(String xmlContent) throws IOException, JDOMException {

        // setup xml parsing
        ArrayList queries = new ArrayList();
        queries.add("/*/bp:SHORT-NAME");
        queries.add("/*/bp:NAME");
        queries.add("/bp:NAME");
        SAXBuilder builder = new SAXBuilder();
        StringReader reader = new StringReader(xmlContent);
        Document bioPaxDoc = builder.build(reader);
        Element root = bioPaxDoc.getRootElement();
        XPath xpath;
        for (int lc = 0; lc < queries.size(); lc++){
            xpath = XPath.newInstance((String)queries.get(lc));
            xpath.addNamespace("bp", root.getNamespaceURI());
            Element e = (Element) xpath.selectSingleNode(root);
            if (e != null && e.getTextNormalize().length() > 0) {
                return e.getTextNormalize();
            }
        }
        return null;
    }

    /**
     * Gets physical entity name and id of given component from
     * data within sequence or physicalEntity participant Element.
     *
     * @param participantSummaryComponent ParticipantSummaryComponent
     * @param e Element
     * @return boolean
     * @throws JDOMException
     * @throws DaoException
     * @throws IOException
     */
    private static boolean setPhysicalEntity(ParticipantSummaryComponent participantSummaryComponent, Element e)
            throws JDOMException, DaoException, IOException {

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
                String physicalEntityString = BioPaxRecordUtil.getPhysicalEntityName(rdfKey);
                participantSummaryComponent.setName(physicalEntityString);
                // cook id to save
                int indexOfID = rdfKey.lastIndexOf("-");
                if (indexOfID == -1){
                    return false;
                }
                indexOfID += 1;
                String cookedKey = rdfKey.substring(indexOfID);
                Long recordID = new Long(cookedKey);
                ((BioPaxRecordSummary)participantSummaryComponent).setRecordID(recordID.longValue());
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
     * @param participantSummaryComponent ParticipantSummaryComponent
	 * @param record CPathRecord
     * @param e Element
     * @return boolean
     * @throws JDOMException
	 * @throws IOException
     */
    private static boolean setCellularLocation(ParticipantSummaryComponent participantSummaryComponent, CPathRecord record, Element e)
            throws JDOMException, IOException {

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
				// we've got to locate the cellular location via a cpath local id
				String cellularLocationStr = getCellularLocation(record, cellularLocation);
				if (cellularLocationStr != null){
					participantSummaryComponent.setCellularLocation(cellularLocationStr);
					return true;
				}
				else{
					return false;
				}
            }

            // we should now set our element to cellular location and look from here.
            e = cellularLocation;

            // setup/perform query
            xpath = XPath.newInstance("*/bp:TERM");
            xpath.addNamespace("bp", e.getNamespaceURI());
            cellularLocation = (Element) xpath.selectSingleNode(e);

            // process query
            if (cellularLocation != null){
                participantSummaryComponent.setCellularLocation(cellularLocation.getTextNormalize());
                return true;
            }
            // term not available, try for xref id
            else{
                xpath = XPath.newInstance("*/bp:XREF/*/bp:ID");
                xpath.addNamespace("bp", e.getNamespaceURI());
                cellularLocation = (Element) xpath.selectSingleNode(e);
                if (cellularLocation != null){
                    participantSummaryComponent.setCellularLocation(cellularLocation.getTextNormalize());
                    return true;
                }
            }
        }

        // made it here
        return false;
    }

    /**
	 * Gets the cellular location (term or id) from an
	 * xml blob (retrieved from record arg) and reference
	 * to cpath local record contained within cellularLocationRef
	 * arg.
	 *
	 * @param record CPathRecord
	 * @param cellularLocationRef Element
	 * @return String
	 * @throws JDOMException
	 * @throws IOException
	 */
	private static String getCellularLocation(CPathRecord record, Element cellularLocationRef) throws JDOMException, IOException {

		// setup for rdf query
        //SAXBuilder builder = new SAXBuilder();
        StringReader reader = new StringReader (record.getXmlContent());
		BioPaxUtil bpUtil = new BioPaxUtil(reader);
		RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());

		// try for cellular location term first
        Element cellularLocation = rdfQuery.getNode(cellularLocationRef, "*/TERM");
		if (cellularLocation != null){
			return cellularLocation.getTextNormalize();
		}
		// term not available, try for xref id
		else{
			cellularLocation = rdfQuery.getNode(cellularLocationRef, "*/XREF/unificationXref/ID");
			if (cellularLocation != null){
				return cellularLocation.getTextNormalize();
			}
		}

		// outta here
		return null;
	}

    /**
     * Sets feature list of given component from data within
     * sequence or physicalEntity participant Element.
     *
     * @param participantSummaryComponent ParticipantSummaryComponent
     * @param record CPathRecord
     * @param e Element
     * @return boolean
     * @throws JDOMException
     */
    private static boolean setFeatureList(ParticipantSummaryComponent participantSummaryComponent, CPathRecord record, Element e)
            throws JDOMException, IOException {

        // vector of features
		HashSet featureSet = new HashSet();

        // setup/perform query
        XPath xpath = XPath.newInstance("bp:SEQUENCE-FEATURE-LIST/*");
        xpath.addNamespace("bp", e.getNamespaceURI());
        List list = xpath.selectNodes(e);

        if (list != null && list.size() > 0) {
			// drill down to feature type - setup for rdf query
			StringReader reader = new StringReader (record.getXmlContent());
			BioPaxUtil bpUtil = new BioPaxUtil(reader);
			RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());
			// loop through feature list
            for (int lc = 0; lc < list.size(); lc++) {
                // reset the list element
                e = (Element)list.get(lc);
                // try to get term
				Element feature = rdfQuery.getNode(e, "FEATURE-TYPE/*/TERM");
                if (feature != null && feature.getTextNormalize().length() > 0){
                    featureSet.add(feature.getTextNormalize());
                }
				// no term, try to get xref id
                else{
					feature = rdfQuery.getNode(e, "FEATURE-TYPE/*/XREF/*/ID");
                    if (feature != null && feature.getTextNormalize().length() > 0){
                        featureSet.add(feature.getTextNormalize());
                    }
					// we should have found something, made it here, return false
                    else{
                        return false;
					}
                }
            }
        }

        // add list to component - if we have stuff to add
        if (featureSet.size() > 0){
            participantSummaryComponent.setFeatureList(new ArrayList(featureSet));
        }

        // made it here
        return true;
    }

    /**
     * Sets a BioPaxRecordSummary string attribute.
     *
	 * @param root Element
	 * @param query String
	 * @param methodName String
     * @param biopaxRecordSummary BiopaxRecordSummary
     * @return boolean
     * @throws JDOMException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
     */
    private static boolean setBioPaxRecordStringAttribute(Element root, String query, String methodName, BioPaxRecordSummary biopaxRecordSummary)
            throws JDOMException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		// setup for query
		XPath xpath = XPath.newInstance(query);
		xpath.addNamespace("bp", root.getNamespaceURI());
		Element element = (Element) xpath.selectSingleNode(root);
		if (element != null && element.getTextNormalize().length() > 0) {
			// get BioPaxRecordSummaryClass
			Class biopaxRecordSummaryClass = biopaxRecordSummary.getClass();
			// get parameters
			Class biopaxRecordSummaryClassMethodParameters[] = { String.class };
			// get method
			Method method = biopaxRecordSummaryClass.getMethod(methodName, biopaxRecordSummaryClassMethodParameters);
			// invoke the method
			Object invokeParameters[] = { element.getTextNormalize() };
			method.invoke(biopaxRecordSummary, invokeParameters);
			return true;
		}

		// made it here
		return false;
	}

    /**
     * Sets a BioPaxRecordSummary list attribute.
     *
	 * @param root Element
	 * @param query String
	 * @param methodName String
     * @param biopaxRecordSummary BiopaxRecordSummary
     * @return boolean
     * @throws JDOMException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
     */
    private static boolean setBioPaxRecordRecordListAttribute(Element root, String query, String methodName, BioPaxRecordSummary biopaxRecordSummary)
            throws JDOMException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		// setup for query
		XPath xpath = XPath.newInstance(query);
		xpath.addNamespace("bp", root.getNamespaceURI());
		List list = xpath.selectNodes(root);
		if (list != null && list.size() > 0){
			// convert list of elements to list of strings
			ArrayList listOfStrings =  new ArrayList();
			for (int lc = 0; lc < list.size(); lc++){
				Element element = (Element)list.get(lc);
				if (element != null && element.getTextNormalize().length() > 0){
					String string = element.getTextNormalize();
					listOfStrings.add(string);
				}
			}
			if (listOfStrings.size() > 0){
				// get BioPaxRecordSummaryClass
				Class biopaxRecordSummaryClass = biopaxRecordSummary.getClass();
				// get parameters
				Class biopaxRecordSummaryClassMethodParameters[] = { List.class };
				// get method
				Method method = biopaxRecordSummaryClass.getMethod(methodName, biopaxRecordSummaryClassMethodParameters);
				// invoke the method
				Object invokeParameters[] = { listOfStrings };
				method.invoke(biopaxRecordSummary, invokeParameters);
				return true;
			}
		}

		// made it here
		return false;
	}
}
