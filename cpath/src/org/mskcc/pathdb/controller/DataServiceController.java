package org.mskcc.pathdb.controller;

import org.mskcc.pathdb.sql.query.*;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        xdebug.logMsg(this, "Entering Data Service Controller");
    }

    /**
     * Processes User Request.
     * @param protocolRequest Protocol Request object.
     * @throws Exception All Exceptions.
     */
    public void processRequest(ProtocolRequest protocolRequest)
            throws Exception {
        processGetInteractions(protocolRequest);
    }

    private void processGetInteractions(ProtocolRequest protocolRequest)
            throws IOException, ServletException, QueryException,
            ProtocolException {
        if (protocolRequest.getFormat().equals(ProtocolConstants.FORMAT_PSI)) {
            InteractionQuery query = determineQueryType(protocolRequest);
            ArrayList interactions = query.getInteractions();
            if (interactions.size() == 0) {
                throw new ProtocolException(ProtocolStatusCode.NO_RESULTS_FOUND,
                        "No Results Found for:  " + protocolRequest.getQuery());
            }
            String xml = query.getXml();
            this.returnXml(xml);
        } else {
            InteractionQuery query = determineQueryType(protocolRequest);
            ArrayList interactions = query.getInteractions();
            request.setAttribute("interactions", interactions);
            if (interactions.size() == 0 && protocolRequest.getCommand().
                    equals(ProtocolConstants.
                    COMMAND_GET_BY_INTERACTOR_NAME)) {
                request.setAttribute("doFullTextSearch", "true");
            }
            forwardToJsp();
        }
    }

    /**
     * Instantiates Correct Query based on Protocol Request.
     */
    private InteractionQuery determineQueryType(ProtocolRequest request)
            throws QueryException {
        String command = request.getCommand();
        String q = request.getQuery();
        InteractionQuery query = null;
        int maxHits = request.getMaxHitsInt();
        if (command.equals(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_ID)) {
            long cpathId = Long.parseLong(q);
            query = new GetInteractionsByInteractorId(cpathId);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME)) {
            query = new GetInteractionsByInteractorName(q);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_TAX_ID)) {
            int taxId = Integer.parseInt(q);
            query = new GetInteractionsByInteractorTaxonomyId(taxId,
                    request.getMaxHitsInt());
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_KEYWORD)) {
            query = new GetInteractionsByInteractorKeyword(q, maxHits);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTION_DB)) {
            query = new GetInteractionsByInteractionDbSource(q, maxHits);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTION_PMID)) {
            query = new GetInteractionsByInteractionPmid(q, maxHits);
        }
        query.execute(xdebug);
        return query;
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
     * Sets the correct Ds-status HTTP Header.
     * @param status Status Value.
     */
    private void setHeaderStatus(String status) {
        response.setHeader(ProtocolConstants.DS_HEADER_NAME,
                status);
    }
}