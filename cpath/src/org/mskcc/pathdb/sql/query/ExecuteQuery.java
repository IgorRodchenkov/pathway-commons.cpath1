package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.util.Md5Util;
import org.mskcc.pathdb.xdebug.XDebug;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Executes Interaction Queries.
 *
 * <P>Note:  JUnit testing for this class is performed in:
 * org.mskcc.pathdb.test.sql.TestImportPsiToCPath.
 *
 * @author Ethan Cerami.
 */
public class ExecuteQuery {
    private XDebug xdebug;

    /**
     * Constructor.
     * @param xdebug XDebug Object.
     */
    public ExecuteQuery(XDebug xdebug) {
        this.xdebug = xdebug;
    }

    /**
     * Execute Query.
     * @param request ProtocolRequest object.
     * @param checkCache If set to true, method will check the XML cache for
     *  pre-computed results.
     * @return QueryResult object.
     * @throws QueryException Indicates Query Error.
     */
    public QueryResult executeQuery(ProtocolRequest request,
            boolean checkCache) throws QueryException {
        DaoXmlCache dao = new DaoXmlCache();
        QueryResult result = null;
        String xml = null;
        try {
            String hashKey = Md5Util.createMd5Hash(request.getUri());
            if (checkCache) {
                String format = request.getFormat();
                request.setFormat(ProtocolConstants.FORMAT_PSI);
                xdebug.logMsg(this, "Checking cache for pre-computed XML");
                xdebug.logMsg(this, "Using HashKey:  " + hashKey);
                xml = dao.getXmlByKey(hashKey);
                request.setFormat(format);
            }
            if (xml == null) {
                xdebug.logMsg(this, "No Cached Results Found");
                result = executeQuery(request);

                // Experimental, Dynamic Caching.
                xml = result.getXml();
                if (xml != null) {
                    dao.addRecord(hashKey, xml);
                    xdebug.logMsg(this, "Storing XML in Database");
                }

            } else {
                xdebug.logMsg(this, "Pre-computed XML Document Found.");
                xdebug.logMsg(this, "Using cached XML Document");
                result = new QueryResult();
                ArrayList interactions = mapToInteractions(xml);
                result.setXml(xml);
                result.setInteractions(interactions);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new QueryException(e.getMessage(), e);
        } catch (DaoException e) {
            throw new QueryException(e.getMessage(), e);
        } catch (QueryException e) {
            throw new QueryException(e.getMessage(), e);
        } catch (MapperException e) {
            throw new QueryException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Maps PSI to Data Service Interaction objects.
     */
    private ArrayList mapToInteractions(String xml) throws MapperException {
        ArrayList interactions = new ArrayList();
        MapPsiToInteractions mapper = new MapPsiToInteractions(xml,
                interactions);
        mapper.doMapping();
        return interactions;
    }

    private QueryResult executeQuery(ProtocolRequest request)
            throws QueryException {
        InteractionQuery query = determineQueryType(request);
        return query.execute(xdebug);
    }

    /**
     * Executes Specified Query and Stores Result in Database Cache.
     * @param request ProtocolRequest Object.
     * @throws QueryException Indicates Query Error.
     */
    public void executeAndStoreQuery(ProtocolRequest request)
            throws QueryException {
        request.setFormat(ProtocolConstants.FORMAT_PSI);
        try {
            String hashKey = Md5Util.createMd5Hash(request.getUri());
            QueryResult result = executeQuery(request, false);
            String xml = result.getXml();
            DaoXmlCache dao = new DaoXmlCache();
            String content = dao.getXmlByKey(hashKey);
            if (content == null) {
                dao.addRecord(hashKey, xml);
            } else {
                dao.updateXmlByKey(hashKey, xml);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new QueryException(e.getMessage(), e);
        } catch (DaoException e) {
            throw new QueryException(e.getMessage(), e);
        }
    }

    /**
     * Instantiates Correct Query based on Protocol Request.
     */
    private InteractionQuery determineQueryType(ProtocolRequest request) {
        String command = request.getCommand();
        String q = request.getQuery();
        InteractionQuery query = null;
        int maxHits = request.getMaxHitsInt();
        if (command.equals(ProtocolConstants.COMMAND_GET_BY_INTERACTOR_ID)) {
            long cpathId = Long.parseLong(q);
            query = new GetInteractionsByInteractorId(cpathId);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME)) {
            query = new GetInteractionsByInteractorName(q);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_TAX_ID)) {
            int taxId = Integer.parseInt(q);
            query = new GetInteractionsByInteractorTaxonomyId(taxId,
                    request.getMaxHitsInt());
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_KEYWORD)) {
            query = new GetInteractionsByInteractorKeyword(q, maxHits);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTION_DB)) {
            query = new GetInteractionsByInteractionDbSource(q, maxHits);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTION_PMID)) {
            query = new GetInteractionsByInteractionPmid(q, maxHits);
        }
        return query;
    }
}