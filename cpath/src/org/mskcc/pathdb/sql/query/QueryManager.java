// $Id: QueryManager.java,v 1.7 2007-04-13 14:51:10 cerami Exp $
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
package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.util.security.Md5Util;
import org.mskcc.pathdb.xdebug.XDebug;
import org.apache.log4j.Logger;

import java.security.NoSuchAlgorithmException;

/**
 * Query Manager/Executor.
 * <p/>
 * <P>Note:  JUnit testing for this class is performed in:
 * org.mskcc.pathdb.test.schemas.psi.TestImportPsiToCPath.
 *
 * @author Ethan Cerami.
 */
public class QueryManager {
    private XDebug xdebug;
    private Logger log = Logger.getLogger(QueryManager.class);

    /**
     * Constructor.
     *
     * @param xdebug XDebug Object.
     */
    public QueryManager(XDebug xdebug) {
        this.xdebug = xdebug;
    }

    /**
     * Execute Query.
     *
     * @param request    ProtocolRequest object.
     * @param checkCache If set to true, method will check the XML cache for
     *                   pre-computed results.
     * @return XmlAssembly object.
     * @throws QueryException Indicates Query Error.
     */
    public XmlAssembly executeQuery(ProtocolRequest request,
            boolean checkCache) throws QueryException {
        DaoXmlCache dao = new DaoXmlCache(xdebug);
        XmlAssembly xmlAssembly = null;
        XmlAssembly cachedXml = null;
        try {
            String hashKey = getHashKey(request);
            log.info("Checking cache for pre-computed XML");
            log.info("Using HashKey:  " + hashKey);
            cachedXml = dao.getXmlAssemblyByKey(hashKey);
            if (cachedXml == null) {
                log.info("No Match Found");
            } else {
                log.info("Match Found");
            }
            if (checkCache && cachedXml != null) {
                log.info("Using Cached XML Document");
                xmlAssembly = cachedXml;
            } else {
                log.info("Executing New Interaction/Pathway Query");
                xmlAssembly = executeQuery(request);
                if (!xmlAssembly.isEmpty()) {
                    if (cachedXml == null) {
                        log.info("Storing XML to Database Cache");
                        dao.addRecord(hashKey, request.getUrlParameterString(),
                                xmlAssembly);
                    } else {
                        log.info("Updating XML in Database Cache");
                        dao.updateXmlAssemblyByKey(hashKey, xmlAssembly);
                    }
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new QueryException(e.getMessage(), e);
        } catch (DaoException e) {
            throw new QueryException(e.getMessage(), e);
        } catch (QueryException e) {
            throw new QueryException(e.getMessage(), e);
        }
        return xmlAssembly;
    }

    /**
     * Gets the HashKey for Specified Protocol Request.
     *
     * @param request ProtocolRequest Object.
     * @return Hash Key.
     * @throws NoSuchAlgorithmException No Such Algorithm Exception
     */
    private String getHashKey(ProtocolRequest request)
            throws NoSuchAlgorithmException {
        String hashKey = Md5Util.createMd5Hash(request.getUrlParameterString());
        return hashKey;
    }

    private XmlAssembly executeQuery(ProtocolRequest request)
            throws QueryException {
        Query queryPathway = determineQueryType(request);
        return queryPathway.execute(xdebug);
    }

    /**
     * Instantiates Correct Query based on Protocol Request.
     */
    private Query determineQueryType(ProtocolRequest request) {
        Query query = null;

        //  We currently have two types of queries:
        //  1.  get BioPaxRecord
        //  2.  get top level pathway list
        //  2.  search via Lucene
        if (request.getCommand().equals
                (ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID)) {
            query = new GetBioPaxCommand(request);
        } else if (request.getCommand().equals
                (ProtocolConstants.COMMAND_GET_TOP_LEVEL_PATHWAY_LIST)) {
            query = new GetTopLevelPathwayListCommand(request, xdebug);
        } else {
            query = new SearchCommand(request);
        }
        return query;
    }
}
