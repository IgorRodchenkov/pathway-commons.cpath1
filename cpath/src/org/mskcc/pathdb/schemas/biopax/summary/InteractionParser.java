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
package org.mskcc.pathdb.schemas.biopax.summary;


// imports
import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.io.IOException;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.BioPaxControlTypeMap;
import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.RdfConstants;
import org.mskcc.pathdb.schemas.biopax.RdfUtil;
import org.mskcc.pathdb.schemas.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.model.CPathRecord;

/**
 * This class parses interaction data
 * given a cPath record id.  The interaction
 * data is returned in the form a of an InteractionSummary
 * object.  This object is intended to be consumed by component(s)
 * in the presentation layer responsible for displaying interaction
 * summary information.
 *
 * @author Benjamin Gross.
 */
public class InteractionParser {

    /**
     * Reference to XML Root.
     */
    private Element root;

    /*
	 * Reference to CPathRecord.
	 */
    private CPathRecord record;

    /**
     * Reference to BioPaxConstants Class.
     */
    private BioPaxConstants biopaxConstants;

    /**
     * Constructor.
     *
     * @param recordID long
     * @throws DaoException
	 * @throws IllegalArgumentException
     */
    public InteractionParser(long recordID) throws DaoException, IllegalArgumentException {

        // setup biopax constants
        biopaxConstants = new BioPaxConstants();

        // get cpath record
        DaoCPath cPath = DaoCPath.getInstance();
        record = cPath.getRecordById(recordID);
		// check record for validity
		if (record == null){
			throw new IllegalArgumentException("cPath ID does not exist: " + recordID);
		}
		if (!record.getXmlType().equals(XmlRecordType.BIO_PAX)){
			throw new IllegalArgumentException("Specified cPath record is not of type " + XmlRecordType.BIO_PAX);
		}
    }

    /**
     * Finds/returns physical interaction information.
     *
     * @return InteractionSummary
     * @throws IOException
     * @throws JDOMException
     * @throws InteractionSummaryException
     * @throws DaoException
     */
    public InteractionSummary getInteractionSummary()
            throws IOException, JDOMException, InteractionSummaryException, DaoException {

		// check for conversion interaction
		if (biopaxConstants.isConversion(record.getSpecificType())){
            return getInformation("-->", "/*/bp:LEFT/*", "/*/bp:RIGHT/*");
		}
		// check for control interaction
		else if (biopaxConstants.isControl(record.getSpecificType())){
            return getInformation("", "/*/bp:CONTROLLER/*", "/*/bp:CONTROLLED");
		}
		// check for physical interaction last
		else if (biopaxConstants.isPhysicalInteraction(record.getSpecificType())){
            return getInformation("/*/bp:PARTICIPANTS/*");
		}

		// made it here,
		return null;
	}

    /**
     * Finds/returns interaction information.
     *
     * @param participants String
     * @return InteractionSummary
     * @throws IOException
     * @throws JDOMException
     * @throws InteractionSummaryException
     * @throws DaoException
     */
    private InteractionSummary getInformation(String participants)
            throws IOException, JDOMException, InteractionSummaryException, DaoException {

        // used for xml parsing
        SAXBuilder builder = new SAXBuilder();
        StringReader reader = new StringReader (record.getXmlContent());

        Document bioPaxDoc = builder.build(reader);

        if (bioPaxDoc != null){
            root = bioPaxDoc.getRootElement();
        }

        // get type
        String interactionType = getInteractionType();

        // get participants
        ArrayList participantsVector = getPhysicalInteractionInformation(participants);

        // outta here
        return (new InteractionSummary(interactionType, "", participantsVector, null));
    }

    /**
     * Finds/returns interaction information.
     *
     * @param operator String.
     * @param leftSideParticipants String
     * @param rightSideParticipants String
     * @return InteractionSummary
     * @throws IOException
     * @throws JDOMException
     * @throws InteractionSummaryException
     * @throws DaoException
     */
    private InteractionSummary getInformation(String operator, String leftSideParticipants, String rightSideParticipants)
            throws IOException, JDOMException, InteractionSummaryException, DaoException {

        // used for xml parsing
        SAXBuilder builder = new SAXBuilder();
        StringReader reader = new StringReader (record.getXmlContent());

        Document bioPaxDoc = builder.build(reader);

        if (bioPaxDoc != null){
            root = bioPaxDoc.getRootElement();
        }

        // get type
        String interactionType = getInteractionType();

        // get participants
        ArrayList leftParticipants = getPhysicalInteractionInformation(leftSideParticipants);
        ArrayList rightParticipants = getPhysicalInteractionInformation(rightSideParticipants);

        // get operator
        if (operator.equals("")){
            operator = getControlType();
            if (operator == null){
                operator = "CONTROL-TYPE NOT FOUND";
            }
        }

        // outta here
        return (new InteractionSummary(interactionType, operator, leftParticipants, rightParticipants));
    }

    /**
     * Gets Interaction Type in Plain English.
     *
     * @return String
     */
    private String getInteractionType() {

        // set the control type string
        String interactionType;

        // our biopax entity type 2 plain english hashmap
        BioPaxEntityTypeMap entityTypeMap = new BioPaxEntityTypeMap();
        interactionType = (String)entityTypeMap.get(record.getSpecificType());

        // outta here
        return interactionType;
    }

    /**
     * Get Control Type.
     *
     * @return String
     * @throws JDOMException
     */
    private String getControlType() throws JDOMException {

        // set the control type string
        String controlType;

        BioPaxControlTypeMap controlTypeMap = new BioPaxControlTypeMap();

        controlType = (record.getSpecificType().equalsIgnoreCase("Catalysis")) ?
            "[" + controlTypeMap.get("ACTIVATION") + "]" :
            "[" + controlTypeMap.get(getControlTypeInformation("/*/bp:CONTROL-TYPE")) + "]";

        // outta here
        return controlType;
    }

    /**
     * Gets CONTROL-TYPE.
     *
     * @param query String
     * @return String
     * @throws JDOMException
     */
    private String getControlTypeInformation(String query) throws JDOMException {

        // our list to return
        String controlType = null;

        // perform query
        XPath xpath = XPath.newInstance(query);
        xpath.addNamespace("bp", root.getNamespaceURI());
        Element e = (Element) xpath.selectSingleNode(root);
        if (e != null && e.getTextNormalize().length() > 0) {
            controlType = e.getTextNormalize();
        }
        return controlType;
    }

    /**
     * Gets PhysicalInteractionInformation, given query.
     *
     * @param query String
     * @return ArrayList
     * @throws JDOMException
     * @throws InteractionSummaryException
     * @throws DaoException
     * @throws IOException
     */
    private ArrayList getPhysicalInteractionInformation(String query)
            throws JDOMException, InteractionSummaryException, DaoException, IOException {

        // we dont process controlled queries as all others
        boolean processingControlled = (query.equals("/*/bp:CONTROLLED"));

        // our list to return
        ArrayList participantArrayList = new ArrayList();

        // perform query
        XPath xpath = XPath.newInstance(query);
        xpath.addNamespace("bp", root.getNamespaceURI());
        List list = xpath.selectNodes(root);

        // interate through results - all physicalentity or sequence participants
        if (list != null && list.size() > 0) {
            for (int lc = 0; lc < list.size(); lc++) {
                // get our next element to process
                Element e = (Element) list.get(lc);
                // create new physical interaction component for this participant
                PhysicalInteractionComponent physicalInteractionComponent = null;
                // special processing of controlled
                if (processingControlled){
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
                            throw new InteractionSummaryException("Corrupt Record ID: " + rdfResourceAttribute.getValue());
                        }
                        indexOfID += 1;
                        String cookedKey = rdfKey.substring(indexOfID);
                        Long recordID = new Long(cookedKey);
                        // add to ArrayList
                        if (physicalEntity != null){
                            physicalInteractionComponent = new PhysicalInteractionComponent();
                            physicalInteractionComponent.setName(physicalEntity);
                            physicalInteractionComponent.setRecordID(recordID.longValue());
                        }
                    }
                }
                else{
                    physicalInteractionComponent = BioPaxRecordUtil.createPhysicalInteractionComponent(e);
                }
                // add component to participant ArrayList
                if (physicalInteractionComponent != null){
                    participantArrayList.add(physicalInteractionComponent);
                }
            }
        }

        // outta here
        return (participantArrayList.size() > 0) ? participantArrayList : null;
    }
}
