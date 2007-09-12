// $Id: ExecuteHtmlResponse.java,v 1.4 2007-09-12 14:57:21 cerami Exp $
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
package org.mskcc.pathdb.action.web_api.biopax_mode;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.lucene.queryParser.ParseException;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.lucene.LuceneQuery;
import org.mskcc.pathdb.action.BaseAction;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.MarshalException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * BioPAX Web Mode:  Response is of type HTML.
 *
 * @author Ethan Cerami, Benjamin Gross.
 */
public class ExecuteHtmlResponse {

    /**
     * Processes Client Request.
     *
     * @param xdebug          XDebug Object.
     * @param protocolRequest Protocol Request Object.
     * @param request         Http Servlet Request Object.
     * @param response        Http Servlet Response Object.
     * @param mapping         Struts Action Mapping Object.
     * @return Struts Action Forward Object.
     * @throws QueryException             Query Error.
     * @throws IOException                I/O Error.
     * @throws AssemblyException          XML Assembly Error.
     * @throws ParseException             Lucene Parsing Error.
     * @throws ProtocolException          Protocol Error.
     * @throws DaoException               Database Error.
     * @throws CloneNotSupportedException Cloning Error.
     */
    public ActionForward processRequest(XDebug xdebug, ProtocolRequest protocolRequest,
            HttpServletRequest request, HttpServletResponse response, ActionMapping mapping)
            throws QueryException, IOException, AssemblyException, ParseException, ProtocolException,
            DaoException, CloneNotSupportedException {
        GlobalFilterSettings filterSettings = null;
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
            // query by type(s)
            queryByType(xdebug, request, protocolRequest, filterSettings);
            // query by data source
            queryByDataSource(xdebug, request, protocolRequest, filterSettings);
        }
        // outta here
        return mapping.findForward(CPathUIConfig.BIOPAX);
    }

    /**
     * Query by Data Source(s).
     */
    private void queryByDataSource(XDebug xdebug, HttpServletRequest request,
            ProtocolRequest protocolRequest, GlobalFilterSettings globalFilterSettings)
            throws QueryException, DaoException, IOException,
            AssemblyException, ParseException, CloneNotSupportedException {

        // needed vars
        int totalNumberHits = 0;
        LuceneQuery search = null;
        HashMap<String, Integer> hitByDataSourceMap = new HashMap<String, Integer>();

        // we are going to be modifying global filter settings, lets make a clone
        GlobalFilterSettings filterSettings = globalFilterSettings.clone();

        // grab user selected entity type, and set it in global settings
        String userSelectedEntityType =
                request.getParameter(GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME);
        List<String> typeList = new ArrayList();
        typeList.add(userSelectedEntityType);
        filterSettings.setEntityTypeSelected(typeList);

        // process all (global) data sources
        search = new LuceneQuery(protocolRequest, filterSettings, xdebug);
        search.executeSearch();
        totalNumberHits = search.getTotalNumHits();
        if (totalNumberHits > 0) {
            hitByDataSourceMap.put(GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL,
                    totalNumberHits);
        }

        Set<Long> snapShotIds = filterSettings.getSnapshotIdSet();
        // we override global filter settings when we narrow by specific type
        filterSettings = new GlobalFilterSettings();
        for (Long id : snapShotIds) {
            // set the datasource
            List<Long> dataSourceList = new ArrayList();
            dataSourceList.add(id);
            filterSettings.setSnapshotsSelected(dataSourceList);
            // set the type list
            filterSettings.setEntityTypeSelected(typeList);
            // perform the query
            search = new LuceneQuery(protocolRequest, filterSettings, xdebug);
            search.executeSearch();
            totalNumberHits = search.getTotalNumHits();
            if (totalNumberHits > 0) {
                hitByDataSourceMap.put(String.valueOf(id.intValue()), totalNumberHits);
            }
        }
        // add hits by data source map to request object
        request.setAttribute(BaseAction.ATTRIBUTE_HITS_BY_DATA_SOURCE_MAP, hitByDataSourceMap);
    }

    /**
     * Query by Entity Type.
     */
    private void queryByType(XDebug xdebug, HttpServletRequest request,
            ProtocolRequest protocolRequest, GlobalFilterSettings globalFilterSettings)
            throws QueryException, DaoException, IOException,
            AssemblyException, ParseException, CloneNotSupportedException {

        // grab data source
        String userSelectedDataSource =
                request.getParameter(GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME);
        // grab user selected entity type
        String userSelectedEntityType =
                request.getParameter(GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME);

        // we are going to be modifying global filter settings, lets make a clone
        GlobalFilterSettings filterSettings = globalFilterSettings.clone();

        // setup global filters setting - data source filter
        if (!userSelectedDataSource.equals(GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL)) {
            // user selected is not equal to global, therefore it is equal to a snapshot id
            List<Long> dataSourceList = new ArrayList();
            dataSourceList.add(Long.valueOf(userSelectedDataSource));
            filterSettings.setSnapshotsSelected(dataSourceList);
        }

        // setup types map
        HashMap<String, Integer> hitByTypeMap = new HashMap<String, Integer>();
        BioPaxEntityTypeMap typesMap = new BioPaxEntityTypeMap();
        typesMap.put(GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_VALUE_ALL,
                GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_VALUE_ALL);

        // interate through all types, and store query hits by type
        for (String type : (Set<String>) typesMap.keySet()) {
            // setup global filters setting - types filter
            List<String> typeList = new ArrayList();
            typeList.add(type);
            filterSettings.setEntityTypeSelected(typeList);
            // perform the query
            LuceneQuery search = new LuceneQuery(protocolRequest, filterSettings, xdebug);
            long cpathIds[] = search.executeSearch();
            if (userSelectedEntityType.equals(type)) {
                request.setAttribute(BaseAction.ATTRIBUTE_CPATH_IDS, cpathIds);
                request.setAttribute(BaseAction.ATTRIBUTE_TOTAL_NUM_HITS,
                        new Integer(search.getTotalNumHits()));
                request.setAttribute(BaseAction.ATTRIBUTE_TEXT_FRAGMENTS,
                        search.getTextFragments());
                request.setAttribute(BaseAction.ATTRIBUTE_DATA_SOURCES,
                        search.getDataSources());
                request.setAttribute(BaseAction.ATTRIBUTE_SCORES,
                        search.getScores());
            }
            int totalNumberHits = search.getTotalNumHits();
            if (totalNumberHits > 0) hitByTypeMap.put(type, totalNumberHits);
        }
        // add hits by record type map to request object
        request.setAttribute(BaseAction.ATTRIBUTE_HITS_BY_RECORD_TYPE_MAP, hitByTypeMap);
    }
}