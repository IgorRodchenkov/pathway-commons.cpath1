package org.mskcc.pathdb.controller;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.mapper.MapInteractionsToPsi;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.services.ReadInteractions;
import org.mskcc.dataservices.services.ReadInteractors;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * DataService Controller.
 * Processes all client requests for DataService specific data.
 *
 * @author Ethan Cerami
 */
public class DataServiceController {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;
    private XDebug xdebug;

    /**
     * Constructor.
     * @param request HttpServletRequest.
     * @param response HttpServletResponse.
     * @param servletContext ServletContext Object.
     * @param xdebug XDebug Object.
     */
    public DataServiceController(HttpServletRequest request, HttpServletResponse
            response, ServletContext servletContext, XDebug xdebug) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
        this.xdebug = xdebug;
        xdebug.logMsg(this, "Entering Grid Controller");
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
    public void processRequest(ProtocolRequest protocolRequest)
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
        xdebug.logMsg(this, "Number of Interactions Found:  "
                + interactions.size());
        if (protocolRequest.getFormat().equals
                (ProtocolConstants.FORMAT_PSI)) {
            MapInteractionsToPsi mapper =
                    new MapInteractionsToPsi(interactions);
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
        String page = "/jsp/pages/Master.jsp";
        xdebug.logMsg(this, "Forwarding to JSP Page:  " + page);
        xdebug.stopTimer();
        RequestDispatcher dispatcher =
                servletContext.getRequestDispatcher(page);
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
        xdebug.logMsg(this, "Retrieving Interactions from GRID for UID:  "
                + uid);
        DataServiceFactory factory = DataServiceFactory.getInstance();
        ReadInteractions service =
                (ReadInteractions) factory.getService
                (CPathConstants.READ_INTERACTIONS_FROM_GRID);
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
        xdebug.logMsg(this, "Retrieving Interactor Data from GRID for UID:  "
                + uid);
        DataServiceFactory factory = DataServiceFactory.getInstance();
        ReadInteractors service =
                (ReadInteractors) factory.getService
                (CPathConstants.READ_INTERACTORS_FROM_GRID);
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