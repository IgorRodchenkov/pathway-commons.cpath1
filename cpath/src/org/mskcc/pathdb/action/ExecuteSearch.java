// $Id: ExecuteSearch.java,v 1.8 2006-11-02 20:37:23 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.action;

import org.apache.lucene.queryParser.ParseException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.lucene.LuceneQuery;
import org.mskcc.pathdb.lucene.PsiInteractorExtractor;
import org.mskcc.pathdb.lucene.LuceneAutoFilter;
import org.mskcc.pathdb.protocol.*;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.QueryManager;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.util.security.XssFilter;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.model.GlobalFilterSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Executes Search.
 *
 * @author Ethan Cerami
 */
public class ExecuteSearch extends BaseAction {

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
            xdebug.logMsg(this, "Executing Web Service API Query:  "
                    + protocolRequest.getUri());
            return processQuery(mapping, protocolRequest, request,
                    response, xdebug);
        } catch (NeedsHelpException e) {
            request.removeAttribute(BaseAction.PAGE_IS_SEARCH_RESULT);
            return mapping.findForward(BaseAction.FORWARD_HELP);
        }
    }

    private ActionForward processQuery
            (ActionMapping mapping, ProtocolRequest protocolRequest,
                    HttpServletRequest request, HttpServletResponse response,
                    XDebug xdebug) throws ProtocolException, NeedsHelpException {
        if (protocolRequest.getFormat() == null
                || protocolRequest.getFormat()
                .equals(ProtocolConstants.FORMAT_HTML)) {
            return processHtmlRequest(mapping, protocolRequest,
                    request, xdebug);
        } else {
            return processXmlRequest(protocolRequest, response, xdebug);
        }
    }

    private ActionForward processXmlRequest(ProtocolRequest protocolRequest,
            HttpServletResponse response,
            XDebug xdebug) throws NeedsHelpException {
        String xml = null;
        XmlAssembly xmlAssembly = null;
        try {
            ProtocolValidator validator =
                    new ProtocolValidator(protocolRequest);
            validator.validate();
            xmlAssembly = executeQuery(xdebug, protocolRequest);
            if (xmlAssembly.isEmpty()) {
                String q = protocolRequest.getQuery();
                if (q == null && protocolRequest.getOrganism() != null) {
                    q = protocolRequest.getOrganism();
                } else if (q == null) {
                    q = protocolRequest.getCommand();
                }
                throw new ProtocolException
                        (ProtocolStatusCode.NO_RESULTS_FOUND,
                                "No Results Found for:  " + q);
            }
            xml = xmlAssembly.getXmlStringWithCPathIdPrefix();

            //  Return Number of Hits Only or Complete XML.
            if (protocolRequest.getFormat().
                    equals(ProtocolConstants.FORMAT_COUNT_ONLY)) {
                returnCountOnly(response, xmlAssembly);
            } else {
                returnXml(response, xml);
            }
        } catch (ProtocolException e) {
            xml = e.toXml();
            returnXml(response, xml);
        } catch (AssemblyException e) {
            xml = e.getMessage();
            returnXml(response, xml);
        }
        //  Return null here, because we do not want Struts to do any
        //  forwarding.
        return null;
    }

    private ActionForward processHtmlRequest(ActionMapping mapping,
            ProtocolRequest protocolRequest, HttpServletRequest request,
            XDebug xdebug)
            throws ProtocolException, NeedsHelpException {
        request.setAttribute(ATTRIBUTE_PROTOCOL_REQUEST, protocolRequest);
        request.setAttribute(BaseAction.PAGE_IS_SEARCH_RESULT, BaseAction.YES);
        ProtocolValidator validator =
                new ProtocolValidator(protocolRequest);
        validator.validate();

        PropertyManager pManager = PropertyManager.getInstance();
        String webMode = pManager.getProperty(BaseAction.PROPERTY_WEB_MODE);
        xdebug.logMsg(this, "Branching based on web mode:  " + webMode);

        try {
            if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) {
                return processHtmlRequestPsiMode(xdebug, protocolRequest,
                        request, mapping);
            } else {
                return processHtmlRequestBioPaxMode(xdebug, protocolRequest,
                        request, mapping);
            }
        } catch (MarshalException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        } catch (ValidationException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        } catch (IOException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        } catch (ParseException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        } catch (QueryException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        } catch (AssemblyException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        }
    }

    private ActionForward processHtmlRequestBioPaxMode
            (XDebug xdebug, ProtocolRequest protocolRequest,
                    HttpServletRequest request, ActionMapping mapping)
            throws QueryException, IOException,
            AssemblyException, ParseException, ProtocolException {
        GlobalFilterSettings filterSettings = null;
        try {
            if (CPathUIConfig.getShowDataSourceDetails()) {
                HttpSession session = request.getSession();
                filterSettings = (GlobalFilterSettings) session.getAttribute
                        (GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
                if (filterSettings == null) {
                    filterSettings = new GlobalFilterSettings();
                    session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS,
                            filterSettings);
                }
                xdebug.logMsg(this, "User has Global Filter Settings");
            }

            LuceneQuery search = new LuceneQuery(protocolRequest, filterSettings, xdebug);
            long cpathIds[] = search.executeSearch();
            request.setAttribute(BaseAction.ATTRIBUTE_CPATH_IDS, cpathIds);
            request.setAttribute(BaseAction.ATTRIBUTE_TOTAL_NUM_HITS,
                    new Integer(search.getTotalNumHits()));
            request.setAttribute(BaseAction.ATTRIBUTE_TEXT_FRAGMENTS,
                    search.getTextFragments());
            return mapping.findForward(CPathUIConfig.BIOPAX);
        } catch (DaoException e) {
            throw new ProtocolException (ProtocolStatusCode.INTERNAL_ERROR, e);
        }
    }

    /**
     * If cPath contains only PSI-MI data and the web_mode is set to:
     * psi_mi_only, proceed as follows.
     */
    private ActionForward processHtmlRequestPsiMode(XDebug xdebug,
            ProtocolRequest protocolRequest, HttpServletRequest request,
            ActionMapping mapping) throws ProtocolException,
            ValidationException, MarshalException, IOException, ParseException {
        XmlAssembly xmlAssembly = executeQuery(xdebug, protocolRequest);
        request.setAttribute(ATTRIBUTE_XML_ASSEMBLY, xmlAssembly);
        ArrayList interactorList = extractInteractors(xmlAssembly,
                protocolRequest, xdebug);
        xdebug.logMsg(this, "Total Number of Interactors for "
                + "Left Column:  " + interactorList.size());
        if (interactorList != null) {
            request.setAttribute(ATTRIBUTE_INTERACTOR_SET, interactorList);
        }
        return mapping.findForward(CPathUIConfig.PSI_MI);
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
            QueryManager queryManager = new QueryManager(xdebug);
            xmlAssembly = queryManager.executeQuery(protocolRequest, true);
        } catch (QueryException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        }
        return xmlAssembly;
    }

    /**
     * Returns XML Response to Client.
     */
    private void returnXml(HttpServletResponse response, String xmlResponse) {
        try {
            response.setContentType("text/xml");
            PrintWriter writer = response.getWriter();
            writer.println(xmlResponse);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns Total Number of Hits, as a single integer value.
     */
    private void returnCountOnly(HttpServletResponse response,
            XmlAssembly xmlAssembly) {
        try {
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.println(xmlAssembly.getNumHits());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
