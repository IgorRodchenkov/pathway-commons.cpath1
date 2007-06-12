// $Id: ExecuteSearch.java,v 1.25 2007-06-12 16:55:34 grossben Exp $
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
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.pathdb.lucene.LuceneQuery;
import org.mskcc.pathdb.lucene.PsiInteractorExtractor;
import org.mskcc.pathdb.protocol.*;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.QueryManager;
import org.mskcc.pathdb.sql.query.GetNeighborsCommand;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.util.security.XssFilter;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.model.BioPaxEntityTypeMap;
import org.mskcc.pathdb.form.WebUIBean;
import org.mskcc.pathdb.query.batch.PathwayBatchQuery;
import org.mskcc.pathdb.query.batch.PhysicalEntityWithPathwayList;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Executes Search.
 *
 * @author Ethan Cerami
 */
public class ExecuteSearch extends BaseAction {
    private Logger log = Logger.getLogger(ExecuteSearch.class);

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
        WebUIBean webUiBean = CPathUIConfig.getWebUIBean();
        // valid query
		ProtocolValidator validator = new ProtocolValidator(protocolRequest);
        validator.validate(webUiBean.getWebApiVersion());
		// short circuit if necessary
		if (isSpecialCaseCommand(protocolRequest)) {
			return specialCaseCommandHandler(mapping, protocolRequest, request, response,
                    xdebug);
		}
        if (protocolRequest.getFormat() == null
                || protocolRequest.getFormat()
                .equals(ProtocolConstantsVersion1.FORMAT_HTML)) {
            return processHtmlRequest(mapping, protocolRequest,
                    request, xdebug);
        } else {
            return processXmlRequest(protocolRequest, response, xdebug);
        }
    }

    private ActionForward processXmlRequest(ProtocolRequest protocolRequest,
            HttpServletResponse response,
            XDebug xdebug) throws NeedsHelpException {
        //  Start timer here
        log.info("Received web service request:  " + protocolRequest.getUri());
        Date start = new Date();
        String xml = null;
        XmlAssembly xmlAssembly = null;
        try {
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
            xml = xmlAssembly.getXmlString();

            //  Return Number of Hits Only or Complete XML.
            if (protocolRequest.getFormat().
                    equals(ProtocolConstantsVersion1.FORMAT_COUNT_ONLY)) {
                returnCountOnly(response, xmlAssembly);
            } else {
                returnXml(response, xml);
            }
        } catch (ProtocolException e) {
            xml = e.toXml();
            returnXml(response, xml);
        }
        
        //  Return null here, because we do not want Struts to do any
        //  forwarding.
        Date stop = new Date();
        long timeInterval = stop.getTime() - start.getTime();
        log.info("Total time to execute web service request:  " + timeInterval
            + " ms");
        return null;
    }

    private ActionForward processHtmlRequest(ActionMapping mapping,
            ProtocolRequest protocolRequest, HttpServletRequest request,
            XDebug xdebug)
            throws ProtocolException, NeedsHelpException {
        request.setAttribute(ATTRIBUTE_PROTOCOL_REQUEST, protocolRequest);
        request.setAttribute(BaseAction.PAGE_IS_SEARCH_RESULT, BaseAction.YES);

        try {
            if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_PSI_MI) {
                xdebug.logMsg(this, "Branching based on web mode:  WEB_MODE_PSI_MI");
                return processHtmlRequestPsiMode(xdebug, protocolRequest,
                        request, mapping);
            } else {
                xdebug.logMsg(this, "Branching based on web mode:  WEB_MODE_BIOPAX");
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
                // query by type(s)
                queryByType(xdebug, request, protocolRequest, filterSettings);
                // query by data source
                queryByDataSource(xdebug, request, protocolRequest, filterSettings);
            }
			// outta here
            return mapping.findForward(CPathUIConfig.BIOPAX);
        } catch (DaoException e) {
            throw new ProtocolException (ProtocolStatusCode.INTERNAL_ERROR, e);
		} catch (CloneNotSupportedException e) {
            throw new ProtocolException (ProtocolStatusCode.INTERNAL_ERROR, e);
        }
    }

	private void queryByDataSource(XDebug xdebug,
								   HttpServletRequest request,
								   ProtocolRequest protocolRequest,
								   GlobalFilterSettings globalFilterSettings)
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

	private void queryByType(XDebug xdebug,
							 HttpServletRequest request,
							 ProtocolRequest protocolRequest,
							 GlobalFilterSettings globalFilterSettings)
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
		for (String type : (Set<String>)typesMap.keySet()) {
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
            log.info("Check XML cache flag is set to:  "
                    + protocolRequest.getCheckXmlCache());
            xmlAssembly = queryManager.executeQuery(protocolRequest,
                    protocolRequest.getCheckXmlCache());
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

	/**
	 * Routine which checks if web service command needs special case 
	 * handling.  This routine was motivated by additions to the
	 * pathway commons api which do not conform to the current
	 * execution path of processQuery, like get_neighbors & getPathwayList.
	 *
	 * Note, it is assumed that the ProtocolRequest object has already
	 * been validated.
	 *
	 * @param protocolRequest ProtocolRequest
	 * @return boolean
	 */
	private boolean isSpecialCaseCommand(ProtocolRequest protocolRequest) {

		String command = protocolRequest.getCommand();

		if (command != null) {
			if (command.equals(ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS)) {
				return (protocolRequest.getOutput() != null &&
						protocolRequest.getOutput().equals(ProtocolRequest.ID_LIST));
			} else if (command.equals(ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST)) {
                return true;
            }
		}

		// outta here
		return false;
	}

	/**
	 * Routine which handles select web service api calls.
	 * This routine was motivated by additions to the pathway commons api
	 * which do not conform to the current execution path of processQuery,
	 * like get_neighbors & getPathwayList.
	 *
	 * @param mapping ActionMapping
	 * @param protocolRequest ProtocolRequest
	 * @param request HttpServletRequest
	 * @param xdebug XDebug
	 * @return ActionForward
	 * @throws ProtocolException
	 */
	private ActionForward specialCaseCommandHandler(ActionMapping mapping,
													ProtocolRequest protocolRequest,
													HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    XDebug xdebug) throws ProtocolException {

		if (protocolRequest.getCommand().equals(ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS)) {
			return getNeighborsHandler(mapping, protocolRequest,
									   request, xdebug);
		} else if (protocolRequest.getCommand().equals
                (ProtocolConstantsVersion2.COMMAND_GET_PATHWAY_LIST)) {
            return getPathwayListHandler(mapping, protocolRequest, request, response, xdebug);
        }

		// outta here
		return null;
	}

	/**
	 * Special-Case handler for getNeighbors Command.
	 *
	 * @param mapping ActionMapping
	 * @param protocolRequest ProtocolRequest
	 * @param request HttpServletRequest
	 * @param xdebug XDebug
	 * @return ActionForward
	 * @throws ProtocolException,
	 */
	private ActionForward getNeighborsHandler(ActionMapping mapping,
											  ProtocolRequest protocolRequest,
											  HttpServletRequest request,
											  XDebug xdebug) throws ProtocolException {
		try {
			GetNeighborsCommand cmd = new GetNeighborsCommand(protocolRequest, xdebug);
			Set<String> neighbors = cmd.getNeighbors();
			request.setAttribute(ATTRIBUTE_NEIGHBORS, neighbors);
			return mapping.findForward(ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS);
		}
		catch (DaoException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
		}
		catch (NumberFormatException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
		}
	}

	/**
	 * Special-Case handler for getPathwayList Command.
	 *
	 * @param mapping ActionMapping
	 * @param protocolRequest ProtocolRequest
	 * @param request HttpServletRequest
	 * @param xdebug XDebug
	 * @return ActionForward
	 * @throws ProtocolException
	 */
	private ActionForward getPathwayListHandler(ActionMapping mapping,
        ProtocolRequest protocolRequest, HttpServletRequest request, HttpServletResponse response,
        XDebug xdebug) throws ProtocolException {
		try {
            PathwayBatchQuery batchQuery = new PathwayBatchQuery();
            String ids[] = new String[1];
            ids[0] = protocolRequest.getQuery();
            String dbTerm = protocolRequest.getInputIDType();
            if (dbTerm == null) {
                dbTerm = ExternalDatabaseConstants.INTERNAL_DATABASE;
            }
            String dataSources[] = protocolRequest.getDataSources();
            ArrayList<PhysicalEntityWithPathwayList> list;
            if (dataSources != null) {
                list = batchQuery.executeBatchQuery (ids, dbTerm, dataSources);
            } else {
                list = batchQuery.executeBatchQuery (ids, dbTerm);
            }
            String table = batchQuery.outputTabDelimitedText(list);
            response.setContentType("text/plain");
            PrintWriter writer = response.getWriter();
            writer.println(table);
            writer.flush();
            writer.close();
            return null;
        } catch (DaoException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
		} catch (BioPaxRecordSummaryException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
		} catch (IOException e) {
            throw new ProtocolException(ProtocolStatusCode.INTERNAL_ERROR, e);
        }
	}
}
