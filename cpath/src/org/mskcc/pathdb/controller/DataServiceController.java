package org.mskcc.pathdb.controller;

import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Data Service Controller.
 * All Client Application/Browser requests travel throught the Data
 * Service Controller.  The Controller forwards requests on to more
 * specific classes, such as the GridController.  It also centralizes
 * all exception handling in one place.
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
     * Protocol Request.
     */
    private ProtocolRequest protocolRequest;

    /**
     * Logger.
     */
    private static Logger log =
            Logger.getLogger(DataServiceController.class.getName());

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
        protocolRequest = new ProtocolRequest(parameterMap);
        ProtocolValidator validator = new ProtocolValidator(protocolRequest);
        validator.validate();
        GridController gridController = new GridController(request,
                response, servletContext);
        gridController.processRequest(protocolRequest);
    }

    /**
     * Returns Error to Client.
     * Automatically sets the Ds-status header = "error".
     * @param exception ProtocolException object.
     */
    private void returnError(ProtocolException exception) {
        setHeaderStatus(ProtocolConstants.DS_ERROR_STATUS);
        try {
            if (protocolRequest.getFormat() != null
                    && protocolRequest.getFormat().equals
                    (ProtocolConstants.FORMAT_HTML)) {
                showHtmlError(exception);
            } else {
                response.setContentType("text/xml");
                ServletOutputStream stream = response.getOutputStream();
                stream.println(exception.toXml());
                stream.flush();
                stream.close();
            }
        } catch (IOException e) {
            log.error("Exception thrown while writing out Error:  "
                    + e.getMessage());
        }
    }

    private void showHtmlError(ProtocolException exception) {
        RequestDispatcher dispatcher =
                servletContext.getRequestDispatcher
                ("/jsp/pages/Master.jsp");
        request.setAttribute("exception", exception);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            log.error("Exception thrown while writing out Error:  "
                    + e.getMessage());
        } catch (IOException e) {
            log.error("Exception thrown while writing out Error:  "
                    + e.getMessage());
        }
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
     * Forwards to the /jsp/protocol.html HTML Page.
     */
    private void showHelp() {
        log.info("Forwarding to Master.jsp page");
        try {
            setHeaderStatus(ProtocolConstants.DS_OK_STATUS);
            RequestDispatcher dispatcher =
                    servletContext.getRequestDispatcher
                    ("/jsp/pages/Master.jsp");
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