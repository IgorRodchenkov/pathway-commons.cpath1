package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.controller.*;
import org.mskcc.pathdb.sql.query.ExecuteQuery;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.QueryResult;
import org.mskcc.pathdb.test.util.UriUtil;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Action for cPath Queries.
 *
 * @author Ethan Cerami
 */
public class QueryAction extends BaseAction {

    /**
     * Executes cPath Query.
     * @param mapping Struts ActionMapping Object.
     * @param form Struts ActionForm Object.
     * @param request Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        return processRequest(mapping, request, response, xdebug);
    }

    /**
     * Processes Client Request.
     */
    private ActionForward processRequest(ActionMapping mapping,
            HttpServletRequest request, HttpServletResponse response,
            XDebug xdebug) throws ProtocolException {
        ProtocolRequest protocolRequest = null;
        try {
            HashMap parameterMap = UriUtil.normalizeParameterMap
                    (request.getParameterMap());
            protocolRequest = new ProtocolRequest(parameterMap);
            return processGetInteractions(mapping, protocolRequest, request,
                    response, xdebug);
        } catch (NeedsHelpException e) {
            return mapping.findForward(BaseAction.FORWARD_HELP);
        }
    }

    private ActionForward processGetInteractions
            (ActionMapping mapping, ProtocolRequest protocolRequest,
            HttpServletRequest request, HttpServletResponse response,
            XDebug xdebug) throws ProtocolException, NeedsHelpException {
        if (protocolRequest.getFormat() != null && protocolRequest.getFormat().
                equals(ProtocolConstants.FORMAT_PSI)) {
            return processXmlRequest(mapping, protocolRequest,
                    response, xdebug);
        } else {
            return processHtmlRequest(mapping, protocolRequest,
                    request, xdebug);
        }
    }

    private ActionForward processXmlRequest(ActionMapping mapping,
            ProtocolRequest protocolRequest, HttpServletResponse response,
            XDebug xdebug)
            throws NeedsHelpException {
        String xml = null;
        try {
            ProtocolValidator validator =
                    new ProtocolValidator(protocolRequest);
            validator.validate();
            QueryResult result = executeQuery(xdebug, protocolRequest);
            ArrayList interactions = result.getInteractions();
            if (interactions.size() == 0) {
                throw new ProtocolException
                        (ProtocolStatusCode.NO_RESULTS_FOUND,
                                "No Results Found for:  "
                        + protocolRequest.getQuery());
            }
            xml = result.getXml();
        } catch (ProtocolException e) {
            xml = e.toXml();
        } finally {
            returnXml(response, xml);
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private ActionForward processHtmlRequest(ActionMapping mapping,
            ProtocolRequest protocolRequest, HttpServletRequest request,
            XDebug xdebug)
            throws ProtocolException, NeedsHelpException {
        request.setAttribute(ATTRIBUTE_PROTOCOL_REQUEST, protocolRequest);
        ProtocolValidator validator =
                new ProtocolValidator(protocolRequest);
        validator.validate();
        QueryResult result = executeQuery(xdebug, protocolRequest);
        ArrayList interactions = result.getInteractions();
        request.setAttribute(ATTRIBUTE_INTERACTIONS, interactions);
        if (interactions.size() == 0 && protocolRequest.getCommand().
                equals(ProtocolConstants.
                COMMAND_GET_BY_INTERACTOR_NAME)) {
            return mapping.findForward(BaseAction.FORWARD_FULL_TEXT_SEARCH);
        } else {
            return mapping.findForward(BaseAction.FORWARD_SUCCESS);
        }
    }


    private QueryResult executeQuery(XDebug xdebug,
            ProtocolRequest protocolRequest) throws ProtocolException {
        QueryResult result;
        try {
            ExecuteQuery executeQuery = new ExecuteQuery(xdebug);
            result = executeQuery.executeQuery(protocolRequest, true);
        } catch (QueryException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        }
        return result;
    }

    /**
     * Returns XML Response to Client.
     * Automatically sets the Ds-status header = "ok".
     * @param xmlResponse XML Response Document.
     */
    private void returnXml(HttpServletResponse response, String xmlResponse) {
        try {
            response.setContentType("text/xml");
            ServletOutputStream stream = response.getOutputStream();
            stream.println(xmlResponse);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}