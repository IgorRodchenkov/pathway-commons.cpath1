// $Id: ExecuteSearch2.java,v 1.3 2007-01-26 17:31:50 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
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
import org.mskcc.pathdb.lucene.LuceneQuery;
import org.mskcc.pathdb.protocol.*;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.util.security.XssFilter;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.model.GlobalFilterSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.List;

/**
 * Executes Search.
 *
 * @author Benjamin Gross
 */
public class ExecuteSearch2 extends BaseAction {

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
            return processHtmlRequest(mapping, protocolRequest, request, xdebug);
        } catch (NeedsHelpException e) {
            request.removeAttribute(BaseAction.PAGE_IS_SEARCH_RESULT);
            return mapping.findForward(BaseAction.FORWARD_HELP);
        }
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

        try {
			xdebug.logMsg(this, "Branching:  WEB_MODE_BIOPAX");
			return processHtmlRequestBioPaxMode(xdebug, protocolRequest,
												request, mapping);
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
			// grab user selected entity type
			String userSelectedEntityType =
				request.getParameter(GlobalFilterSettings.ENTITY_TYPES_FILTER_NAME);
			// interate through all types, and store query hits by type
			// also set request attributes as appropriate
			HashMap<String, Integer> hitByTypeMap = new HashMap<String, Integer>();
			BioPaxEntityTypeMap typesMap = new BioPaxEntityTypeMap();
			typesMap.put(GlobalFilterSettings.ALL_ENTITY_TYPES_FILTER_VALUE,
						 GlobalFilterSettings.ALL_ENTITY_TYPES_FILTER_VALUE);
			for (String type : (Set<String>)typesMap.keySet()) {
				// set the type
				List<String> typeList = new ArrayList();
				typeList.add(type);
				GlobalFilterSettings globalFilterSettings = (filterSettings == null) ?
					new GlobalFilterSettings() : filterSettings;
				globalFilterSettings.setEntityTypeSelected(typeList);
				// perform the query
				LuceneQuery search = new LuceneQuery(protocolRequest, globalFilterSettings, xdebug);
				long cpathIds[] = search.executeSearch();
				if (userSelectedEntityType.equals(type)) {
					request.setAttribute(BaseAction.ATTRIBUTE_CPATH_IDS, cpathIds);
					request.setAttribute(BaseAction.ATTRIBUTE_TOTAL_NUM_HITS,
										 new Integer(search.getTotalNumHits()));
					request.setAttribute(BaseAction.ATTRIBUTE_TEXT_FRAGMENTS,
										 search.getTextFragments());
					request.setAttribute(BaseAction.ATTRIBUTE_DATA_SOURCE_SET,
										 search.getDataSourceSet());
					request.setAttribute(BaseAction.ATTRIBUTE_DATA_SOURCES,
										 search.getDataSources());
					request.setAttribute(BaseAction.ATTRIBUTE_SCORES,
										 search.getScores());
				}
				int totalNumberHits = search.getTotalNumHits();
				if (totalNumberHits > 0) hitByTypeMap.put(type, totalNumberHits);
			}
			// add hits by record type map to request object
			if (hitByTypeMap.size() > 0) {
				request.setAttribute(BaseAction.ATTRIBUTE_HITS_BY_RECORD_TYPE_MAP, hitByTypeMap);
			}
            return mapping.findForward(CPathUIConfig.BIOPAX);
        } catch (DaoException e) {
            throw new ProtocolException (ProtocolStatusCode.INTERNAL_ERROR, e);
        }
    }
}
