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
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * GRID Controller.
 * Processes all client requests for GRID specific data.
 *
 * @author Ethan Cerami
 */
public class GridController {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    /**
     * Logger.
     */
    private static Logger log =
            Logger.getLogger(DataServiceController.class.getName());

    /**
     * Constructor.
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @param servletContext ServletContext Object.
     */
    public GridController (HttpServletRequest request, HttpServletResponse
            response, ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    /**
     * Processes User Request.
     * @param protocolRequest Protocol Request object.
     * @throws MarshalException Problem using Castor.
     * @throws ValidationException XML Document is not valid.
     * @throws DataServiceException Problem accessing data.
     * @throws ProtocolException Problem with service.
     * @throws IOException Problem writing out data.
     * @throws ServletException Problem writing to servlet.
     */
    public void processRequest (ProtocolRequest protocolRequest)
            throws MarshalException, ValidationException, DataServiceException,
            ProtocolException, IOException, ServletException {
        try {
            if (protocolRequest.getCommand().equals
                    (ProtocolConstants.COMMAND_RETRIEVE_INTERACTIONS)) {
                processGetInteractions(protocolRequest);
            } else if (protocolRequest.getCommand().equals
                (ProtocolConstants.COMMAND_RETRIEVE_GO)) {
                String xml = getGo(protocolRequest.getUid());
                returnXml(xml);
            }
        } catch (EmptySetException e) {
            throw new ProtocolException(ProtocolStatusCode.BAD_UID,
                    "UID:  " + protocolRequest.getUid()
                    + " not found in database");
        }
    }

    private void processGetInteractions(ProtocolRequest protocolRequest)
            throws DataServiceException, MarshalException, ValidationException,
            ServletException, IOException {
        ArrayList interactions =
                getInteractions(protocolRequest.getUid());
        if (protocolRequest.getFormat().equals
                (ProtocolConstants.FORMAT_PSI)) {
            MapInteractionsToPsi mapper =
                    new MapInteractionsToPsi (interactions);
            mapper.doMapping();
            EntrySet entrySet = mapper.getPsiXml();
            StringWriter writer = new StringWriter();
            entrySet.marshal(writer);
            String xml = writer.toString();
            this.returnXml(xml);
        } else {
            request.setAttribute("interactions", interactions);
            request.setAttribute("protocol_request", protocolRequest);
            forwardToJsp();
        }
    }

    private void forwardToJsp() throws ServletException, IOException {
        RequestDispatcher dispatcher =
            servletContext.getRequestDispatcher
                ("/jsp/pages/Master.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Returns XML Response to Client.
     * Automatically sets the Ds-status header = "ok".
     * @param xmlResponse XML Response Document.
     * @throws IOException Error writing to client.
     */
    private void returnXml(String xmlResponse) throws IOException {
        setHeaderStatus(ProtocolConstants.DS_OK_STATUS);
        response.setContentType("text/xml");
        ServletOutputStream stream = response.getOutputStream();
        stream.println(xmlResponse);
        stream.flush();
        stream.close();
    }

    /**
     * Gets Interactions.
     */
    private ArrayList getInteractions(String uid) throws DataServiceException,
            EmptySetException {
        log.info("Retrieving Interactions from GRID for UID:  " + uid);
        DataServiceFactory factory = DataServiceFactory.getInstance();
        InteractionService service =
                (InteractionService) factory.getService
                (LiveConstants.GRID_INTERACTION_SERVICE);
        ArrayList interactions = service.getInteractions(uid);
        interactions = this.filterInteractionList(interactions);
        return interactions;
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
        DataServiceFactory factory = DataServiceFactory.getInstance();
        InteractorService service =
                (InteractorService) factory.getService
                (LiveConstants.GRID_INTERACTOR_SERVICE);
        Interactor interactor = service.getInteractor(uid);

        String xml = (String) interactor.getAttribute
                (InteractorVocab.XML_RESULT_SET);
        return xml;
    }

    /**
     * Sets the correct Ds-status HTTP Header.
     * @param status Status Value.
     */
    private void setHeaderStatus(String status) {
        response.setHeader(ProtocolConstants.DS_HEADER_NAME,
                status);
    }
}