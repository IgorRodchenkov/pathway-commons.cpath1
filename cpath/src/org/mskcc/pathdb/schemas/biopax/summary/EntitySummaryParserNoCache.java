package org.mskcc.pathdb.schemas.biopax.summary;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;

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

    /*
	 * Reference to CPathRecord.
	 */
    private CPathRecord record;

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
     */
    public EntitySummaryParserNoCache(long recordID) throws DaoException,
            IllegalArgumentException {

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
                ArrayList leftParticipants = getInteractionInformation("/*/bp:LEFT/*");
                ArrayList rightParticipants = getInteractionInformation("/*/bp:RIGHT/*");
                entitySummary =
                        new ConversionInteractionSummary(leftParticipants, rightParticipants);
            } else if (biopaxConstants.isControl(record.getSpecificType())) {
                // get control info
                ArrayList controllers = getInteractionInformation("/*/bp:CONTROLLER/*");
                ArrayList controlled = getInteractionInformation("/*/bp:CONTROLLED");
                String controlType = getControlType("/*/bp:CONTROL-TYPE");
                entitySummary = new ControlInteractionSummary(controlType,
                        controllers, controlled);
            } else if (biopaxConstants.isPhysicalInteraction(record.getSpecificType())) {
                // get physical interaction info
                ArrayList participants = getInteractionInformation
                        ("/*/bp:PARTICIPANTS/*");
                String interactionType = getInteractionType
                        ("/*/bp:INTERACTION-TYPE/*/bp:TERM");
                entitySummary = new PhysicalInteractionSummary(interactionType,
                        participants);
            } else {
                // get conversion info
                ArrayList leftParticipants = getInteractionInformation("/*/bp:LEFT/*");
                ArrayList rightParticipants = getInteractionInformation("/*/bp:RIGHT/*");
                entitySummary =
                        new ConversionInteractionSummary(leftParticipants, rightParticipants);
            }
        } catch (Throwable throwable) {
            throw new EntitySummaryException(throwable);
        }

        // outta here
        if (entitySummary == null) {
            entitySummary = new EntitySummary();
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
     */
    private ArrayList getInteractionInformation(String query)
            throws JDOMException,
            EntitySummaryException,
            DaoException,
            BioPaxRecordSummaryException {

        // we dont process controlled queries as all others
        boolean processingControlled = (query.equals("/*/bp:CONTROLLED"));

        // our list to return
        ArrayList participantArrayList = new ArrayList();

        // perform query
        XPath xpath = XPath.newInstance(query);
        xpath.addNamespace("bp", root.getNamespaceURI());
        List list = xpath.selectNodes(root);

        // interate through results
        if (list != null && list.size() > 0) {
            for (int lc = 0; lc < list.size(); lc++) {
                // get our next element to process
                Element e = (Element) list.get(lc);
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
                    xpath = XPath.newInstance("bp:PHYSICAL-ENTITY");
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
     */
    private Object getControlledInteractionType(Element element)
            throws EntitySummaryException, DaoException {

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
            if (biopaxConstants.isPhysicalInteraction(record.getSpecificType())) {
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
            controlType = BioPaxConstants.ACTIVATION.toUpperCase();
        } else {
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
        XPath xpath = XPath.newInstance(query);
        xpath.addNamespace("bp", root.getNamespaceURI());
        Element e = (Element) xpath.selectSingleNode(root);
        if (e != null && e.getTextNormalize().length() > 0) {
            interactionType = e.getTextNormalize();
        }

        // outta here
        return interactionType;
    }
}