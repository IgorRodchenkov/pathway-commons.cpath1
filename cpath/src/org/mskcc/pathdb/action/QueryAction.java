package org.mskcc.pathdb.action;

import org.apache.lucene.queryParser.ParseException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.pathdb.lucene.PsiInteractorExtractor;
import org.mskcc.pathdb.protocol.*;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.query.Query;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.util.XssFilter;
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
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
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
            HashMap parameterMap = XssFilter.filterAllParameters
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
                equals(ProtocolConstants.FORMAT_XML)) {
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
            XmlAssembly xmlAssembly = executeQuery(xdebug, protocolRequest);
            if (xmlAssembly.isEmpty()) {
                throw new ProtocolException
                        (ProtocolStatusCode.NO_RESULTS_FOUND,
                                "No Results Found for:  "
                        + protocolRequest.getQuery());
            }
            xml = xmlAssembly.getXmlString();
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
        XmlAssembly xmlAssembly = executeQuery(xdebug, protocolRequest);
        request.setAttribute(ATTRIBUTE_XML_ASSEMBLY, xmlAssembly);
        try {
            ArrayList interactorList = extractInteractors(xmlAssembly,
                    protocolRequest, xdebug);
            xdebug.logMsg(this, "Total Number of Interactors for "
                    + "Left Column:  " + interactorList.size());
            if (interactorList != null) {
                request.setAttribute(ATTRIBUTE_INTERACTOR_SET, interactorList);
            }
        } catch (MarshalException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        } catch (ValidationException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        } catch (IOException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        } catch (ParseException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    private ArrayList extractInteractors(XmlAssembly xmlAssembly,
            ProtocolRequest request, XDebug xdebug) throws MarshalException,
            ValidationException, IOException, ParseException {
        EntrySet entrySet = (EntrySet) xmlAssembly.getXmlObject();
        if (entrySet != null) {
            PsiInteractorExtractor interactorExtractor =
                    new PsiInteractorExtractor(entrySet,
                            request.getQuery(), xdebug);
            return interactorExtractor.getSortedInteractors();
        } else {
            return new ArrayList();
        }
    }

    private XmlAssembly executeQuery(XDebug xdebug,
            ProtocolRequest protocolRequest) throws ProtocolException {
        XmlAssembly xmlAssembly;
        try {
            Query executeQuery = new Query(xdebug);
            xmlAssembly = executeQuery.executeQuery(protocolRequest, true);
        } catch (QueryException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        }
        return xmlAssembly;
    }

    /**
     * Returns XML Response to Client.
     * Automatically sets the Ds-status header = "ok".
     *
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