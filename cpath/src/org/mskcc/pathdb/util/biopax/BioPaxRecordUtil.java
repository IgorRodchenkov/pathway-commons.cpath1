// $Id: BioPaxRecordUtil.java,v 1.29 2007-01-03 16:37:15 cerami Exp $
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

// package
package org.mskcc.pathdb.util.biopax;

// imports

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;
import org.mskcc.pathdb.schemas.biopax.summary.ParticipantSummaryComponent;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.util.rdf.RdfQuery;
import org.mskcc.pathdb.util.rdf.RdfUtil;
import org.mskcc.pathdb.util.cache.EhCache;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;

/**
 * This class contains utilities
 * functions to query biopax docs.
 *
 * @author Benjamin Gross.
 */
public class BioPaxRecordUtil {
    private static Logger log = Logger.getLogger(BioPaxRecordUtil.class);

    /**
     * Creates BioPaxRecordSummary given
     * a CPathRecord.
     *
     * @param record CPathRecord
     * @return BioPaxRecordSummary
     * @throws BioPaxRecordSummaryException Throwable
     */
    public static BioPaxRecordSummary createBioPaxRecordSummary(CPathRecord record)
            throws BioPaxRecordSummaryException {

        // check record for validity
        if (record == null) {
            throw new IllegalArgumentException("Record Argument is Null" + record);
        }
        if (!record.getXmlType().equals(XmlRecordType.BIO_PAX)) {
            throw new IllegalArgumentException("Specified cPath record is not of type "
                    + XmlRecordType.BIO_PAX);
        }

        // setup for queries
        StringReader reader = new StringReader(record.getXmlContent());
        SAXBuilder builder = new SAXBuilder();
        Document bioPaxDoc;
        try {
            bioPaxDoc = builder.build(reader);
        } catch (Throwable throwable) {
            throw new BioPaxRecordSummaryException(throwable);
        }
        Element root = bioPaxDoc.getRootElement();

        // this is object to return
        BioPaxRecordSummary biopaxRecordSummary = new BioPaxRecordSummary();
        biopaxRecordSummary.setCPathRecord(record);

        // set id
        biopaxRecordSummary.setRecordID(record.getId());

        // set type
        String type = record.getSpecificType();
        if (type != null) {
            biopaxRecordSummary.setType(type);
        }

        //  set label
        biopaxRecordSummary.setLabel(record.getName());
        try {
            // set name
			setBioPaxRecordStringAttribute(root,
										   "/*/bp:NAME",
										   "setName",
										   biopaxRecordSummary);
            // set short name
            setBioPaxRecordStringAttribute(root,
										   "/*/bp:SHORT-NAME",
										   "setShortName",
										   biopaxRecordSummary);
            // set synonyms
            setBioPaxRecordListAttribute(root,
										 "/*/bp:SYNONYMS",
										 "setSynonyms",
										 biopaxRecordSummary);
            // set organsim
            setBioPaxRecordStringAttribute(root,
										   "/*/bp:ORGANISM/*/bp:NAME",
										   "setOrganism",
										   biopaxRecordSummary);
            // set data source
            setBioPaxRecordStringAttribute(root,
										   "/*/bp:DATA-SOURCE/*/bp:NAME",
										   "setDataSource",
										   biopaxRecordSummary);
            // availability
            setBioPaxRecordStringAttribute(root,
										   "/*/bp:AVAILABILITY",
										   "setAvailability",
										   biopaxRecordSummary);
            // external links
            DaoExternalLink externalLinker = DaoExternalLink.getInstance();
            ArrayList externalLinks = externalLinker.getRecordsByCPathId(record.getId());
            if (externalLinks.size() > 0) {
                biopaxRecordSummary.setExternalLinks(externalLinks);
            }
            // comment
            setBioPaxRecordStringAttribute(root,
										   "/*/bp:COMMENT",
										   "setComment",
										   biopaxRecordSummary);

            // if physical entity record is a complex, lets get its members
            if (record.getSpecificType() != null
                    && record.getSpecificType().equals(BioPaxConstants.COMPLEX)) {
                BioPaxRecordUtil.setComplexMembers(biopaxRecordSummary, record);
            }

            DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
            if (record.getSnapshotId() >=0) {
                biopaxRecordSummary.setExternalDatabaseSnapshotRecord(dao.getDatabaseSnapshot
                        (record.getSnapshotId()));
            }
        } catch (Throwable throwable) {
            throw new BioPaxRecordSummaryException(throwable);
        }

        // outta here
        return biopaxRecordSummary;
    }

    /**
     * Creates ParticipantSummaryComponent given
     * sequence or physicalEntity participant Element.
     *
     * @param interactionRecord    CPathRecord of interaction
     * @param e                    Element of Participant
     * @param physicalEntityRecord CPathRecord of physicalEntity
     * @return ParticipantSummaryComponent
     * @throws BioPaxRecordSummaryException Throwable
     */
    public static ParticipantSummaryComponent createInteractionSummaryComponent(
            CPathRecord interactionRecord, Element e, CPathRecord physicalEntityRecord)
            throws BioPaxRecordSummaryException {

        // check record for validity
        if (interactionRecord == null) {
            throw new IllegalArgumentException("Record Argument is Null" + interactionRecord);
        }
        if (!interactionRecord.getXmlType().equals(XmlRecordType.BIO_PAX)) {
            throw new IllegalArgumentException("Specified cPath record is not of type "
                    + XmlRecordType.BIO_PAX);
        }

        // success flags
        boolean setCellularLocationSuccess, setFeatureListSuccess, setComplexMembersSuccess;

        // create a biopaxRecordSummary
        BioPaxRecordSummary biopaxRecordSummary = createBioPaxRecordSummary(physicalEntityRecord);

        // this is object to return
        ParticipantSummaryComponent participantSummaryComponent =
                new ParticipantSummaryComponent(biopaxRecordSummary);

        try {
            // get cellular location
            setCellularLocationSuccess =
                    BioPaxRecordUtil.setCellularLocation(participantSummaryComponent,
                            interactionRecord, e);

            // feature list
            setFeatureListSuccess =
                    BioPaxRecordUtil.setFeatureList(participantSummaryComponent,
                            interactionRecord, e);

        } catch (Throwable throwable) {
            throw new BioPaxRecordSummaryException(throwable);
        }

        // made it this far
        return (setCellularLocationSuccess || setFeatureListSuccess)
                ? participantSummaryComponent : null;
    }

    /**
     * Gets Entity Name, returned as String.
     *
     * @param record String
     * @return String
     * @throws DaoException  Throwable
     * @throws IOException   Throwable
     * @throws JDOMException Throwable
     */
    public static String getPhysicalEntityName(String record)
            throws DaoException, IOException, JDOMException {

        // get CPathRecord given record id argument
        int indexOfId = record.lastIndexOf("-");
        if (indexOfId == -1) {
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
     * @param recordID   long
     * @param xmlContent String
     * @return String
     * @throws IOException   Throwable
     * @throws JDOMException Throwable
     */
    public static String getPhysicalEntityNameAsLink(long recordID, String xmlContent)
            throws IOException, JDOMException {

        // string to return
        String entity = getEntityName(xmlContent);
        return (entity != null)
                ? ("<a href=\"record.do?id=" + String.valueOf(recordID) + "\">" + entity + "</a>")
                : null;
    }

    /**
     * Gets a cpath record given an element whose
     * attribute is an rdf resource attribute is a cpath id.
     *
     * @param e Element
     * @return CPathRecord
     * @throws RuntimeException Throwable
     * @throws DaoException     Throwable
     */
    public static CPathRecord getCPathRecord(Element e) throws RuntimeException, DaoException {

        // get elements attribute
        Attribute rdfResourceAttribute =
                e.getAttribute(RdfConstants.RESOURCE_ATTRIBUTE, RdfConstants.RDF_NAMESPACE);

        // attribute not null
        if (rdfResourceAttribute != null) {
            // get the rdf key
            String rdfKey = RdfUtil.removeHashMark(rdfResourceAttribute.getValue());
            // cook id to save
            int indexOfID = rdfKey.lastIndexOf("-");
            if (indexOfID == -1) {
                throw new RuntimeException("Corrupt Record ID: "
                        + rdfResourceAttribute.getValue());
            }
            indexOfID += 1;
            String cookedKey = rdfKey.substring(indexOfID);
            Long recordID = new Long(cookedKey);
            // get cpath record for this id
            DaoCPath cPath = DaoCPath.getInstance();
            return cPath.getRecordById(recordID.longValue());
        }

        // made it here
        return null;
    }

    /**
     * Gets Entity Name, return as link.
     *
     * @param xmlContent String
     * @return String
     * @throws IOException   Throwable
     * @throws JDOMException Throwable
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
        for (int lc = 0; lc < queries.size(); lc++) {
            xpath = XPath.newInstance((String) queries.get(lc));
            xpath.addNamespace("bp", root.getNamespaceURI());
            Element e = (Element) xpath.selectSingleNode(root);
            if (e != null && e.getTextNormalize().length() > 0) {
                return e.getTextNormalize();
            }
        }
        return null;
    }

    /**
     * Sets cellular location of given component from data within
     * sequence or physicalEntity participant Element.
     *
     * @param participantSummaryComponent ParticipantSummaryComponent
     * @param record                      CPathRecord
     * @param e                           Element
     * @return boolean
     * @throws JDOMException Throwable
     * @throws IOException   Throwable
     */
    private static boolean setCellularLocation(
            ParticipantSummaryComponent participantSummaryComponent, CPathRecord record,
            Element e) throws JDOMException, IOException {

        // setup/perform query
        XPath xpath = XPath.newInstance("bp:CELLULAR-LOCATION");
        xpath.addNamespace("bp", e.getNamespaceURI());
        Element cellularLocation = (Element) xpath.selectSingleNode(e);

        // did we find a cellular location element ?
        if (cellularLocation != null) {

            // does element point to a local id ?
            Attribute rdfResourceAttribute =
                    cellularLocation.getAttribute(RdfConstants.RESOURCE_ATTRIBUTE,
                            RdfConstants.RDF_NAMESPACE);
            if (rdfResourceAttribute != null) {
                // we've got to locate the cellular location via a cpath local id
                String cellularLocationStr = getCellularLocation(record, cellularLocation);
                if (cellularLocationStr != null) {
                    participantSummaryComponent.setCellularLocation(cellularLocationStr);
                    return true;
                } else {
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
            if (cellularLocation != null) {
                participantSummaryComponent.setCellularLocation
                        (cellularLocation.getTextNormalize());
                return true;
            } else {
                // term not available, try for xref id
                xpath = XPath.newInstance("*/bp:XREF/*/bp:ID");
                xpath.addNamespace("bp", e.getNamespaceURI());
                cellularLocation = (Element) xpath.selectSingleNode(e);
                if (cellularLocation != null) {
                    participantSummaryComponent.setCellularLocation(
                            cellularLocation.getTextNormalize());
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
     * @param record              CPathRecord
     * @param cellularLocationRef Element
     * @return String
     * @throws JDOMException Throwable
     * @throws IOException   Throwable
     */
    private static String getCellularLocation(CPathRecord record, Element cellularLocationRef)
            throws JDOMException, IOException {

        // setup for rdf query
        StringReader reader = new StringReader(record.getXmlContent());
        BioPaxUtil bpUtil = new BioPaxUtil(reader);
        RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());

        // try for cellular location term first
        Element cellularLocation = rdfQuery.getNode(cellularLocationRef, "*/TERM");
        if (cellularLocation != null) {
            return cellularLocation.getTextNormalize();
        } else {
            // term not available, try for xref id
            cellularLocation = rdfQuery.getNode(cellularLocationRef, "*/XREF/unificationXref/ID");
            if (cellularLocation != null) {
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
     * @param record                      CPathRecord
     * @param e                           Element
     * @return boolean
     * @throws JDOMException Throwable
     * @throws IOException   Throwable
     */
    private static boolean setFeatureList
            (ParticipantSummaryComponent participantSummaryComponent,
                    CPathRecord record, Element e) throws JDOMException, IOException {

        // vector of features
        HashSet featureSet = new HashSet();

        // setup/perform query
        XPath xpath = XPath.newInstance("bp:SEQUENCE-FEATURE-LIST/*");
        xpath.addNamespace("bp", e.getNamespaceURI());
        List list = xpath.selectNodes(e);

        if (list != null && list.size() > 0) {
            // drill down to feature type - setup for rdf query
            StringReader reader = new StringReader(record.getXmlContent());
            BioPaxUtil bpUtil = new BioPaxUtil(reader);
            RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());
            // loop through feature list
            for (int lc = 0; lc < list.size(); lc++) {
                // reset the list element
                e = (Element) list.get(lc);
                // try to get term
                Element feature = rdfQuery.getNode(e, "FEATURE-TYPE/*/TERM");
                if (feature != null && feature.getTextNormalize().length() > 0) {
                    featureSet.add(feature.getTextNormalize());
                } else {
                    // no term, try to get xref id
                    feature = rdfQuery.getNode(e, "FEATURE-TYPE/*/XREF/*/ID");
                    if (feature != null && feature.getTextNormalize().length() > 0) {
                        featureSet.add(feature.getTextNormalize());
                    } else {
                        // we should have found something, made it here, return false
                        return false;
                    }
                }
            }
        }

        // add list to component - if we have stuff to add
        if (featureSet.size() > 0) {
            participantSummaryComponent.setFeatureList(new ArrayList(featureSet));
        }

        // made it here
        return true;
    }

    /**
     * Sets the member list of a complex.
     *
     * @param bpSummaryComponent BioPaxRecordSummary
     * @param record                      CPathRecord
     * @return boolean
     * @throws JDOMException                Throwable
     * @throws IOException                  Throwable
     * @throws DaoException                 Throwable
     * @throws BioPaxRecordSummaryException Throwable
     * @throws RuntimeException             Throwable
     */
    private static boolean setComplexMembers
            (BioPaxRecordSummary bpSummaryComponent,
            CPathRecord record) throws JDOMException, IOException, DaoException,
            BioPaxRecordSummaryException, RuntimeException {

        // our complex member list
        ArrayList complexMemberList = new ArrayList();

        // setup for rdf query
        StringReader reader = new StringReader(record.getXmlContent());
        BioPaxUtil bpUtil = new BioPaxUtil(reader);
        RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());

        // grab all complex components
        Element root = bpUtil.getRootElement();
        List complexMembers = rdfQuery.getNodes(root, "COMPONENTS/*/PHYSICAL-ENTITY");
        if (complexMembers != null && complexMembers.size() > 0) {
            for (int lc = 0; lc < complexMembers.size(); lc++) {
                Element e = (Element) complexMembers.get(lc);
                CPathRecord memberRecord = getCPathRecord(e);
                if (memberRecord != null) {
                    BioPaxRecordSummary bioPaxRecordSummary =
                            BioPaxRecordUtil.createBioPaxRecordSummary(memberRecord);
                    complexMemberList.add(bioPaxRecordSummary);
                }
            }
            if (complexMemberList.size() > 0) {
                bpSummaryComponent.setComponentList(complexMemberList);
            }
        }

        // outta here
        return true;
    }

    /**
     * Sets a BioPaxRecordSummary string attribute.
     *
     * @param root                Element
     * @param query               String
     * @param methodName          String
     * @param biopaxRecordSummary BiopaxRecordSummary
     * @return boolean
     * @throws JDOMException             Throwable
     * @throws IllegalAccessException    Throwable
     * @throws NoSuchMethodException     Throwable
     * @throws InvocationTargetException Throwable
     */
    private static boolean setBioPaxRecordStringAttribute(Element root,
            String query,
            String methodName,
            BioPaxRecordSummary biopaxRecordSummary)
            throws JDOMException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {

        // setup for query
        XPath xpath = XPath.newInstance(query);
        xpath.addNamespace("bp", root.getNamespaceURI());
        Element element = (Element) xpath.selectSingleNode(root);
        if (element != null && element.getTextNormalize().length() > 0) {
            // get BioPaxRecordSummaryClass
            Class biopaxRecordSummaryClass = biopaxRecordSummary.getClass();
            // get parameters
            Class biopaxRecordSummaryClassMethodParameters[] = {String.class};
            // get method
            Method method =
                    biopaxRecordSummaryClass.getMethod(methodName,
                            biopaxRecordSummaryClassMethodParameters);
            // invoke the method
            Object invokeParameters[] = {element.getTextNormalize()};
            method.invoke(biopaxRecordSummary, invokeParameters);
            return true;
        }

        // made it here
        return false;
    }

    /**
     * Sets a BioPaxRecordSummary list attribute.
     *
     * @param root                Element
     * @param query               String
     * @param methodName          String
     * @param biopaxRecordSummary BiopaxRecordSummary
     * @return boolean
     * @throws JDOMException             Throwable
     * @throws IllegalAccessException    Throwable
     * @throws NoSuchMethodException     Throwable
     * @throws InvocationTargetException Throwable
     */
    private static boolean setBioPaxRecordListAttribute(Element root,
            String query,
            String methodName,
            BioPaxRecordSummary biopaxRecordSummary)
            throws JDOMException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {

        // setup for query
        XPath xpath = XPath.newInstance(query);
        xpath.addNamespace("bp", root.getNamespaceURI());
        List list = xpath.selectNodes(root);
        if (list != null && list.size() > 0) {
            // convert list of elements to list of strings
            ArrayList listOfStrings = new ArrayList();
            for (int lc = 0; lc < list.size(); lc++) {
                Element element = (Element) list.get(lc);
                if (element != null && element.getTextNormalize().length() > 0) {
                    String string = element.getTextNormalize();
                    listOfStrings.add(string);
                }
            }
            if (listOfStrings.size() > 0) {
                // get BioPaxRecordSummaryClass
                Class biopaxRecordSummaryClass = biopaxRecordSummary.getClass();
                // get parameters
                Class biopaxRecordSummaryClassMethodParameters[] = {List.class};
                // get method
                Method method =
                        biopaxRecordSummaryClass.getMethod(methodName,
                                biopaxRecordSummaryClassMethodParameters);
                // invoke the method
                Object invokeParameters[] = {listOfStrings};
                method.invoke(biopaxRecordSummary, invokeParameters);
                return true;
            }
        }

        // made it here
        return false;
    }
}
