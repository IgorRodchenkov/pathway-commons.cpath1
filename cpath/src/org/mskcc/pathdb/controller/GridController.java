package org.mskcc.pathdb.controller;

import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.live.LiveConstants;
import org.mskcc.dataservices.services.InteractionService;
import org.mskcc.dataservices.services.InteractorService;
import org.mskcc.dataservices.mapper.MapInteractionsToPsi;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.util.PropertyManager;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.StringWriter;
import java.util.ArrayList;

/**
 * GRID Controller.
 * Processes all client requests for GRID specific data.
 *
 * @author Ethan Cerami
 */
public class GridController {
    /**
     * Logger.
     */
    private static Logger log =
            Logger.getLogger(DataServiceController.class.getName());

    /**
     * Retrieves GRID Data.
     * @param request ProtocolRequest object.
     * @return XML Response String.
     */
    public String retrieveData(ProtocolRequest request)
            throws MarshalException, ValidationException, DataServiceException, ProtocolException {
        try {
            String xml = getXmlData(request);
            return xml;
        } catch (EmptySetException e) {
            throw new ProtocolException(ProtocolStatusCode.BAD_UID,
                    "UID:  " + request.getUid() + " not found in database");
        }
    }

    /**
     * Gets XML Data.
     * @param request ProtocolRequest object.
     */
    private String getXmlData(ProtocolRequest request) throws MarshalException,
            ValidationException, DataServiceException, EmptySetException {
        String xml = null;
        if (request.getCommand().equals
                (ProtocolConstants.COMMAND_RETRIEVE_INTERACTIONS)) {
            xml = getInteractions(request.getUid());
        } else if (request.getCommand().equals
                (ProtocolConstants.COMMAND_RETRIEVE_GO)) {
            xml = getGo(request.getUid());
        }
        return xml;
    }

    /**
     * Gets Interactions.
     */
    private String getInteractions(String uid) throws DataServiceException,
            MarshalException, ValidationException, EmptySetException {
        log.info("Retrieving Interactions from GRID for UID:  " + uid);
        DataServiceFactory factory = DataServiceFactory.getInstance();
        InteractionService service =
                (InteractionService) factory.getService
                (LiveConstants.GRID_INTERACTION_SERVICE);
        ArrayList interactions = service.getInteractions(uid);

        interactions = this.filterInteractionList(interactions);
        MapInteractionsToPsi mapper = new MapInteractionsToPsi (interactions);
        mapper.doMapping();
        EntrySet entrySet = mapper.getPsiXml();
        StringWriter writer = new StringWriter();
        entrySet.marshal(writer);
        return writer.toString();
    }

    /**
     * Filter our specific types of interactions.
     * From Gary: "PSI has only been designed for protein-protein interactions
     * and GRID has both protein-protein interactions and genetic interactions,
     * so PSI does not cover GRID completely.  So, we shouldn't really include
     * genetic interactions in PSI output from GRID."
     * @param interactions ArrayList of Interaction objects.
     */
    private ArrayList filterInteractionList(ArrayList interactions) {
        ArrayList filteredList = new ArrayList();
        for (int i = 0; i < interactions.size(); i++) {
            boolean filterOut = false;
            Interaction interaction = (Interaction) interactions.get(i);
            String expSystem = (String) interaction.getAttribute
                    (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
            if (expSystem.equals("Synthetic Lethality")
                    || expSystem.equals("Synthetic Rescue")
                    || expSystem.equals("Dosage Lethality")) {
                filterOut = true;
            }
            if (!filterOut) {
                filteredList.add(interaction);
            }
        }
        return filteredList;
    }


    /**
     * Get Go Terms.
     */
    private String getGo(String uid) throws DataServiceException,
            EmptySetException {
        log.info("Retrieving Interactor Data from GRID for UID:  " + uid);
        PropertyManager manager = PropertyManager.getInstance();
        DataServiceFactory factory = DataServiceFactory.getInstance();
        InteractorService service =
                (InteractorService) factory.getService
                (LiveConstants.GRID_INTERACTOR_SERVICE);
        Interactor interactor = service.getInteractor(uid);

        String xml = (String) interactor.getAttribute
                (InteractorVocab.XML_RESULT_SET);
        return xml;
    }
}