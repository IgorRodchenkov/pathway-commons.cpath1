package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.sql.query.ExecuteQuery;
import org.mskcc.pathdb.sql.query.QueryResult;
import org.mskcc.pathdb.xdebug.XDebug;

/**
 * Command Line Tool for Querying CPath.
 *
 * @author Ethan Cerami.
 */
public class QueryCPath {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            queryCPath(args);
        } else {
            System.out.println("Command line usage:  admin.pl ft_query"
                    + " interactor_name");
        }
    }

    /**
     * Executes Query against CPath.
     * @param args Command Line Arguments.
     */
    private static void queryCPath(String[] args) {
        try {
            XDebug xdebug = new XDebug();
            ProtocolRequest request = new ProtocolRequest();
            request.setQuery(args[0]);
            request.setCommand(ProtocolConstants.COMMAND_GET_BY_INTERACTION_DB);
            ExecuteQuery query = new ExecuteQuery(xdebug);
            QueryResult result = query.executeQuery(request, true);
            String xml = result.getXml();
            System.out.println(xml);
        } catch (Exception e) {
            System.out.println("\n!!!!  An Error Has Occurred!");
            System.out.println("-->  " + e.getMessage());
        }
    }
}