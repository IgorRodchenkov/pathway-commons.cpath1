package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
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
public class Query {
    private XDebug xdebug;

    /**
     * Constructor.
     * @param xdebug XDebug Object.
     */
    public Query(XDebug xdebug) {
        this.xdebug = xdebug;
    }

    /**
     * Execute Query.
     * @param request ProtocolRequest object.
     * @param checkCache If set to true, method will check the XML cache for
     * pre-computed results.
     * @return XmlAssembly object.
     * @throws QueryException Indicates Query Error.
     */
    public XmlAssembly executeQuery(ProtocolRequest request,
            boolean checkCache) throws QueryException {

        // TODO:  REMOVE THIS LINE
        checkCache = false;

        DaoXmlCache dao = new DaoXmlCache(xdebug);
        XmlAssembly xmlAssembly = null;
        XmlAssembly cachedXml = null;
        try {
            String hashKey = getHashKey(request);
            xdebug.logMsg(this, "Checking cache for pre-computed XML");
            xdebug.logMsg(this, "Using HashKey:  " + hashKey);
            cachedXml = dao.getXmlAssemblyByKey(hashKey);
            if (cachedXml == null) {
                xdebug.logMsg(this, "No Match Found");
            } else {
                xdebug.logMsg(this, "Match Found");
            }
            if (checkCache && cachedXml != null) {
                xdebug.logMsg(this, "Using Cached XML Document");
                xmlAssembly = cachedXml;
            } else {
                xdebug.logMsg(this, "Executing New Interaction Query");
                xmlAssembly = executeQuery(request);
                if (!xmlAssembly.isEmpty()) {
                    if (cachedXml == null) {
                        xdebug.logMsg(this, "Storing XML to Database Cache");
                        dao.addRecord(hashKey, xmlAssembly);
                    } else {
                        xdebug.logMsg(this, "Updating XML in Database Cache");
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
     * @param request ProtocolRequest Object.
     * @return Hash Key.
     * @throws NoSuchAlgorithmException No Such Algorithm Exception
     */
    private String getHashKey(ProtocolRequest request)
            throws NoSuchAlgorithmException {
        String originalFormat = request.getFormat();

        //  Set Format to PSI (no matter what).
        //  This enables us to reuse the same XML Content for requests
        //  for HTML and PSI.
        request.setFormat(ProtocolConstants.FORMAT_PSI);
        String hashKey = Md5Util.createMd5Hash(request.getUri());

        //  Set Back to Original Format.
        request.setFormat(originalFormat);
        return hashKey;
    }

    private XmlAssembly executeQuery(ProtocolRequest request)
            throws QueryException {
        InteractionQuery query = determineQueryType(request);
        return query.execute(xdebug);
    }

    /**
     * Instantiates Correct Query based on Protocol Request.
     */
    private InteractionQuery determineQueryType(ProtocolRequest request) {
        InteractionQuery query = new GetInteractionsViaLucene(request);
        return query;
    }
}