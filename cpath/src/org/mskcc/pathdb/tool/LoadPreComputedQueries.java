package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.protocol.NeedsHelpException;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolValidator;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.sql.query.Query;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.QueryFileReader;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Loads PreComputed Queries.
 *
 * @author Ethan Cerami
 */
public class LoadPreComputedQueries {

    /**
     * Processes all Queries in Specified File.
     *
     * @param file File Name
     * @throws DaoException       Date Access Error.
     * @throws IOException        File Input Error.
     * @throws NeedsHelpException Protocol Error.
     * @throws ProtocolException  Protocol Error.
     * @throws QueryException     Query Error.
     */
    public void preCompute(String file, XDebug xdebug)
            throws DaoException, IOException,
            NeedsHelpException, ProtocolException, QueryException {
        QueryFileReader reader = new QueryFileReader();
        System.out.println("Clearing Database Cache");
        DaoXmlCache dao = new DaoXmlCache(xdebug);
        dao.deleteAllRecords();
        ArrayList list = reader.getProtocolRequests(file);
        for (int i = 0; i < list.size(); i++) {
            ProtocolRequest request = (ProtocolRequest) list.get(i);
            ProtocolValidator validator = new ProtocolValidator(request);
            validator.validate();
            System.out.print("Running Query:  " + request.getUri());
            Query executeQuery = new Query(xdebug);
            executeQuery.executeQuery(request, false);
            System.out.println(" -->  OK");
        }
    }
}