package org.mskcc.pathdb.controller;

import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.pathdb.sql.query.InteractionQuery;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.GetInteractionsByInteractorName;
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
        try {
            if (protocolRequest.getCommand().equals
                    (ProtocolConstants.COMMAND_RETRIEVE_INTERACTIONS)) {
                processGetInteractions(protocolRequest);
            }
        } catch (EmptySetException e) {
            throw new ProtocolException(ProtocolStatusCode.BAD_UID,
                    "UID:  " + protocolRequest.getUid()
                    + " not found in database");
        }
    }

    private void processGetInteractions(ProtocolRequest protocolRequest)
            throws QueryException, IOException, ServletException,
            EmptySetException {
        String uid = protocolRequest.getUid();
        if (protocolRequest.getFormat().equals
                (ProtocolConstants.FORMAT_PSI)) {
            InteractionQuery query = new GetInteractionsByInteractorName(uid);
            String xml = query.getXml();
            this.returnXml(xml);
        } else {
            try {
                //  If the UID contains quotes, skip straight to
                //  text indexer.
                if (uid.indexOf("\"") > -1) {
                    throw new EmptySetException();
                }
                InteractionQuery query = new
                        GetInteractionsByInteractorName(uid);
                ArrayList interactions = query.getInteractions();
                request.setAttribute("interactions", interactions);
            } catch (EmptySetException e) {
                xdebug.logMsg(this, "No Exact Matches Found.  "
                        + "Trying full text search");
                request.setAttribute("doFullTextSearch", "true");
            }
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
     * Sets the correct Ds-status HTTP Header.
     * @param status Status Value.
     */
    private void setHeaderStatus(String status) {
        response.setHeader(ProtocolConstants.DS_HEADER_NAME,
                status);
    }
}