package org.mskcc.pathdb.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Data Service Controller.
 *
 * @author Ethan Cerami
 */
public class DataServiceController {
    /**
     * Servlet Request.
     */
    private HttpServletRequest request;

    /**
     * Servlet Response.
     */
    private HttpServletResponse response;

    /**
     * Servlet Context.
     */
    private ServletContext servletContext;

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     * @param request Servlet Request.
     * @param response Servlet Response.
     * @param servletContext Servlet Context object.
     */
    public DataServiceController(HttpServletRequest request,
            HttpServletResponse response, ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    /**
     * Executes the Controller.
     */
    public void execute() {
        try {
            processRequest();
        } catch (NeedsHelpException e) {
            showHelp();
        } catch (ProtocolException e) {
            returnError(e);
        } catch (Exception e) {
            ProtocolException exception = new ProtocolException
                    (ProtocolStatusCode.INTERNAL_ERROR, e);
            returnError(exception);
        }
    }

    /**
     * Processes Client Request.
     * @throws Exception All Exceptions.
     */
    private void processRequest() throws Exception {
        HashMap parameterMap = getParameterMap(request);
        ProtocolRequest protocolRequest = new ProtocolRequest(parameterMap);
        ProtocolValidator validator = new ProtocolValidator(protocolRequest);
        validator.validate();
        GridController gridController = new GridController();
        String xmlResponse = gridController.retrieveInteractions
                (protocolRequest);
        returnXml(xmlResponse);
    }

    /**
     * Returns Error to Client.
     * @param exception ProtocolException object.
     */
    private void returnError(ProtocolException exception) {
        response.setContentType("text/xml");
        setHeaderStatus(ProtocolConstants.DS_ERROR_STATUS);
        try {
            PrintWriter out = response.getWriter();
            out.println(exception.toXml());
        } catch (IOException e) {
            log.error("Exception thrown while writing out XML Error:  "
                    + e.getMessage());
        }
    }

    /**
     * Returns XML Response to Client.
     * @param xmlResponse XML Response Document.
     * @throws IOException Error writing to client.
     */
    private void returnXml(String xmlResponse) throws IOException {
        PrintWriter out = response.getWriter();
        setHeaderStatus(ProtocolConstants.DS_OK_STATUS);
        response.setContentType("text/xml");
        out.println(xmlResponse);
    }

    /**
     * Get Parameter Map of all Client Name/value pairs.
     * @param request HttpServletRequest request.
     * @return HashMap of all Client Name/value pairs.
     */
    private HashMap getParameterMap(HttpServletRequest request) {
        HashMap map = new HashMap();
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = request.getParameter(name);
            map.put(name, value);
        }
        return map;
    }

    /**
     * Shows Help Page.
     */
    private void showHelp() {
        try {
            setHeaderStatus(ProtocolConstants.DS_OK_STATUS);
            RequestDispatcher dispatcher =
                    servletContext.getRequestDispatcher("/jsp/protocol.html");
            dispatcher.forward(request, response);
        } catch (IOException e) {
            log.error("IOException thrown while writing out Help page:  "
                    + e.getMessage());
        } catch (ServletException e) {
            log.error("ServletException thrown while writing out Help page:  "
                    + e.getMessage());
        }
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