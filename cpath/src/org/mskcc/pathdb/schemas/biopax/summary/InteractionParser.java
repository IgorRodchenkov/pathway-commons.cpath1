// $Id: InteractionParser.java,v 1.8 2006-01-26 23:10:35 grossb Exp $
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
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.util.rdf.RdfUtil;
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

		// ref to return
		InteractionSummary interactionSummary = null;

        // used for xml parsing
        SAXBuilder builder = new SAXBuilder();
        StringReader reader = new StringReader (record.getXmlContent());
        Document bioPaxDoc = builder.build(reader);

		// get the doc root
        if (bioPaxDoc != null){
            root = bioPaxDoc.getRootElement();
        }

		// get interaction information
		ArrayList leftParticipants, rightParticipants;
		if (biopaxConstants.isConversion(record.getSpecificType())){
			// get conversion info
			leftParticipants = getInteractionInformation("/*/bp:LEFT/*");
			rightParticipants = getInteractionInformation("/*/bp:RIGHT/*");
			interactionSummary = new ConversionInteractionSummary(leftParticipants, rightParticipants);
		}
		else if (biopaxConstants.isControl(record.getSpecificType())){
			// get control info
			leftParticipants = getInteractionInformation("/*/bp:CONTROLLER/*");
			rightParticipants = getInteractionInformation("/*/bp:CONTROLLED");
			String controlType = getControlType("/*/bp:CONTROL-TYPE");
			interactionSummary = new ControlInteractionSummary(controlType, leftParticipants, rightParticipants);
		}
		else if (biopaxConstants.isPhysicalInteraction(record.getSpecificType())){
			// get physical interaction info
			leftParticipants = getInteractionInformation("/*/bp:PARTICIPANTS/*");
			interactionSummary = new PhysicalInteractionSummary(leftParticipants);
		}

		// outta here
		return interactionSummary;
	}

    /**
     * Gets Interaction Participants.
     *
     * @param query String
     * @return ArrayList
     * @throws JDOMException
     * @throws InteractionSummaryException
     * @throws DaoException
     * @throws IOException
     */
    private ArrayList getInteractionInformation(String query)
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
                InteractionSummaryComponent interactionSummaryComponent = null;
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
                            interactionSummaryComponent = new InteractionSummaryComponent();
                            interactionSummaryComponent.setName(physicalEntity);
                            interactionSummaryComponent.setRecordID(recordID.longValue());
                        }
                    }
                }
                else{
                    interactionSummaryComponent = BioPaxRecordUtil.createInteractionSummaryComponent(record, e);
                }
                // add component to participant ArrayList
                if (interactionSummaryComponent != null){
                    participantArrayList.add(interactionSummaryComponent);
                }
            }
        }

        // outta here
        return (participantArrayList.size() > 0) ? participantArrayList : null;
    }

    /**
     * Gets CONTROL-TYPE.
     *
     * @param query String
     * @return String
     * @throws JDOMException
     */
    private String getControlType(String query) throws JDOMException {

        // our list to return
        String controlType = null;

		// if record type is catalysis, the control type is operator
		if (record.getSpecificType().equalsIgnoreCase(BioPaxConstants.CATALYSIS)){
			controlType = BioPaxConstants.ACTIVATION.toUpperCase();
		}
		else{
			// lookup control type in xml blob
			XPath xpath = XPath.newInstance(query);
			xpath.addNamespace("bp", root.getNamespaceURI());
			Element e = (Element) xpath.selectSingleNode(root);
			if (e != null && e.getTextNormalize().length() > 0) {
				controlType = e.getTextNormalize();
			}
		}

		// outta here
        return controlType;
    }
}
