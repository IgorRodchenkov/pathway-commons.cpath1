// $Id: EntitySummaryParserNoCache.java,v 1.9 2008-03-07 14:59:34 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
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

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.model.Evidence;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.util.rdf.RdfQuery;
import org.mskcc.pathdb.util.biopax.BioPaxUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
class EntitySummaryParserNoCache {

    /**
     * Reference to XML Root.
     */
    private Element root;

    /**
	 * Reference to CPathRecord.
	 */
    private CPathRecord record;

	/**
	 * Reference to RdfQuery
	 */
	private RdfQuery rdfQuery;

    /**
     * Reference to BioPaxConstants Class.
     */
    private BioPaxConstants biopaxConstants;

    /**
     * Entity Summary.
     */
    private EntitySummary entitySummary;

    /**
     * Constructor.
     *
     * @param recordID long
     * @throws DaoException             Throwable
     * @throws IllegalArgumentException Throwable
     * @throws IOException              Throwable
     * @throws JDOMException            Throwable
     */
    public EntitySummaryParserNoCache(long recordID) throws DaoException, IllegalArgumentException, IOException, JDOMException {

        // setup biopax constants
        biopaxConstants = new BioPaxConstants();

        // get cpath record
        DaoCPath cPath = DaoCPath.getInstance();
        record = cPath.getRecordById(recordID);

        // check record for validity
        if (record == null) {
            throw new IllegalArgumentException("cPath ID does not exist: " + recordID);
        }
        if (!record.getXmlType().equals(XmlRecordType.BIO_PAX)) {
            throw new IllegalArgumentException("Specified cPath record is not of type "
                    + XmlRecordType.BIO_PAX);
        }

		BioPaxUtil bpUtil = new BioPaxUtil(new StringReader(record.getXmlContent()));
		rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());
    }

    /**
     * Finds/returns physical interaction information.
     *
     * @return EntitySummary
     * @throws EntitySummaryException Throwable
     */
    public EntitySummary getEntitySummary()
            throws EntitySummaryException {
        entitySummary = null;

        // used for xml parsing
        SAXBuilder builder = new SAXBuilder();
        StringReader reader = new StringReader(record.getXmlContent());
        Document bioPaxDoc;
        try {
            bioPaxDoc = builder.build(reader);
        } catch (Throwable throwable) {
            throw new EntitySummaryException(throwable);
        }

        // get the doc root
        if (bioPaxDoc != null) {
            root = bioPaxDoc.getRootElement();
        }

        try {
            // get interaction information
            if (biopaxConstants.isConversion(record.getSpecificType())) {
                // get conversion info
                ArrayList leftParticipants = getInteractionInformation("LEFT/*");
                ArrayList rightParticipants = getInteractionInformation("RIGHT/*");
                entitySummary =
                        new ConversionInteractionSummary(leftParticipants, rightParticipants);
            } else if (biopaxConstants.isControl(record.getSpecificType())) {
                // get control info
                ArrayList controllers = getInteractionInformation("CONTROLLER/*");
                ArrayList controlled = getInteractionInformation("CONTROLLED");
                String controlType = getControlType("CONTROL-TYPE");
                entitySummary = new ControlInteractionSummary(controlType,
                        controllers, controlled);
            } else if (biopaxConstants.isPhysicalInteraction(record.getSpecificType())) {
                // get physical interaction info
                ArrayList participants = getInteractionInformation
					("PARTICIPANTS/*");
                String interactionType = getInteractionType
                        ("INTERACTION-TYPE/*/TERM");
                entitySummary = new PhysicalInteractionSummary(interactionType,
                        participants);
            } else {
                // get basic interaction info.
                ArrayList participants = getInteractionInformation
                        ("PARTICIPANTS/*");
                entitySummary = new InteractionSummary(participants);
            }

            //  Get external links
            DaoExternalLink externalLinker = DaoExternalLink.getInstance();
            ArrayList externalLinks = externalLinker.getRecordsByCPathId(record.getId());
            if (externalLinks.size() > 0) {
                entitySummary.setExternalLinks(externalLinks);
            }
        } catch (Throwable throwable) {
            throw new EntitySummaryException(throwable);
        }

        // outta here
        if (entitySummary == null) { 
            entitySummary = new EntitySummary();
        }
		else {
			try {
				((InteractionSummary)entitySummary).setEvidence(getInteractionEvidenceInformation(new StringReader(record.getXmlContent())));
			} catch (Throwable throwable) {
				throw new EntitySummaryException(throwable);
			}
		}
        entitySummary.setSpecificType(record.getSpecificType());
        entitySummary.setId(record.getId());
        entitySummary.setName(record.getName());
        entitySummary.setSnapshotId(record.getSnapshotId());

        return entitySummary;
    }

    /**
     * Gets Interaction Participants.
     *
     * @param query String
     * @return ArrayList
     * @throws org.jdom.JDOMException
     * @throws EntitySummaryException
     * @throws DaoException
     * @throws BioPaxRecordSummaryException
     * @throws IOException
     * @throws JDOMException
     */
    private ArrayList getInteractionInformation(String query)
		throws JDOMException, EntitySummaryException, DaoException, BioPaxRecordSummaryException, IOException, JDOMException {

        // we dont process controlled queries as all others
        boolean processingControlled = (query.equals("CONTROLLED"));

        // our list to return
        ArrayList participantArrayList = new ArrayList();

        // perform query
		List<Element> list = rdfQuery.getNodes(root, query);

        // interate through results
        if (list != null) {
            for (Element e : list) {
				// added to fix null ptr exception.  tbd: debug rdfquery.getNodes()
				if (e == null) continue;
                // special processing of controlled
                if (processingControlled) {
                    // we cast return as object because it could be a ParticipantSummaryComponent or
                    // another EntitySummary
                    Object participant = getControlledInteractionType(e);
                    if (participant != null) {
                        participantArrayList.add(participant);
                    }
                } else {
                    // not processing controlled, we need to create a participant summary component
                    // to do this, we need to get physical entity CPathRecord
                    XPath xpath = XPath.newInstance("bp:PHYSICAL-ENTITY");
                    xpath.addNamespace("bp", e.getNamespaceURI());
                    Element physicalEntity = (Element) xpath.selectSingleNode(e);
                    CPathRecord physicalEntityRecord;
                    try {
                        physicalEntityRecord = BioPaxRecordUtil.getCPathRecord(physicalEntity);
                    } catch (RuntimeException exception) {
                        throw new EntitySummaryException(exception);
                    }
                    if (physicalEntityRecord != null) {
                        ParticipantSummaryComponent participantSummaryComponent =
                                BioPaxRecordUtil.createInteractionSummaryComponent(record,
                                        e,
                                        physicalEntityRecord);
                        participantArrayList.add(participantSummaryComponent);
                    }
                }
            }
        }

        // outta here
        return (participantArrayList.size() > 0) ? participantArrayList : null;
    }

    /**
     * Gets Controlled Interaction Information.
     *
     * @param element Element
     * @return Object
     * @throws EntitySummaryException
     * @throws DaoException
     * @throws IOException
     * @throws JDOMException
     */
    private Object getControlledInteractionType(Element element)
            throws EntitySummaryException, DaoException, IOException, JDOMException {

        // this is the object to return, but you didnt need me to tell you that
        Object objectToReturn = null;

        // get the cPath record
        CPathRecord record;
        try {
            record = BioPaxRecordUtil.getCPathRecord(element);
        } catch (RuntimeException e) {
            throw new EntitySummaryException(e);
        }

        if (record != null) {
            // is it an interaction ? if so, we go through the process again
            if (biopaxConstants.isInteraction(record.getSpecificType())) {
                EntitySummaryParser entitySummaryParser = new EntitySummaryParser(record.getId());
                objectToReturn = entitySummaryParser.getEntitySummary();
            } else {
                // this isn't an interaction, just create a top-level EntitySummary
                objectToReturn = new EntitySummary(record.getId(),
                        record.getName(),
                        record.getSpecificType());
            }
        }

        // outta here
        return objectToReturn;
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
        if (record.getSpecificType().equalsIgnoreCase(BioPaxConstants.CATALYSIS)) {
            controlType = BioPaxConstants.ACTIVATION_CATALYSIS;
        } else {
            // lookup control type in xml blob
			Element e = rdfQuery.getNode(root, query);
            if (e != null && e.getTextNormalize().length() > 0) {
                controlType = e.getTextNormalize();
            }
        }

        // outta here
        return controlType;
    }

    /**
     * Gets Interaction Type.
     *
     * @param query String
     * @return String
     * @throws JDOMException
     */
    private String getInteractionType(String query) throws JDOMException {

        // our list to return
        String interactionType = null;

        // lookup control type in xml blob
        Element e = rdfQuery.getNode(root, query);
        if (e != null && e.getTextNormalize().length() > 0) {
            interactionType = e.getTextNormalize();
        }

        // outta here
        return interactionType;
    }

	/**
	 * Method to extract interaction evidence information.
	 *
	 * @return List<Evidence>
	 * @throws IOException
	 * @throws DaoException
	 * @throws JDOMException
	 */
	private List<Evidence> getInteractionEvidenceInformation(StringReader reader) throws JDOMException,
																						 IOException,
																						 DaoException {
		// object to return
		List<Evidence> toReturn = new ArrayList<Evidence>();

        // perform query
        XPath xpath = XPath.newInstance("/*/bp:EVIDENCE/*");
        xpath.addNamespace("bp", root.getNamespaceURI());
        List<Element> elements = xpath.selectNodes(root);

		if (elements != null) {
			BioPaxUtil bpUtil = new BioPaxUtil(reader);
			RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());
			for (Element element : elements) {
				toReturn.add(BioPaxRecordUtil.getEvidence(rdfQuery, element));
			}
		}

		// outta here
		return toReturn;
	}
}
