package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.query.Query;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.xdebug.XDebugMessage;

import java.util.ArrayList;

/**
 * Command Line Tool for Querying Full Text Indexer.
 * <p/>
 * This is a legacy tool, which is not used much anymore.
 * I am keeping it here until I decide what to do with it.
 *
 * @author Ethan Cerami
 */
public class QueryFullText {

    /**
     * Main Method.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            XDebug xdebug = new XDebug();
            xdebug.startTimer();
            StringBuffer terms = new StringBuffer();
            for (int i = 0; i < args.length; i++) {
                terms.append(args[i] + " ");
            }
            System.out.println("Using search terms:  " + terms.toString());
            queryFullText(terms.toString(), xdebug);
            System.out.println("XDebug Log:");
            System.out.println("----------------");
            ArrayList list = xdebug.getDebugMessages();
            for (int i = 0; i < list.size(); i++) {
                XDebugMessage msg = (XDebugMessage) list.get(i);
                System.out.println(msg.getMessage());
            }

            System.out.println("Total Time for Query:  "
                    + xdebug.getTimeElapsed() + " ms");
        } else {
            System.out.println("Command line usage:  admin.pl ft_query"
                    + " search_terms");
        }
    }

    /**
     * Perform Query.
     *
     * @param terms Search Terms.
     */
    private static void queryFullText(String terms, XDebug xdebug) {
        try {
            ProtocolRequest request = new ProtocolRequest();
            request.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
            request.setQuery(terms);
            request.setMaxHits("1");
            Query query = new Query(xdebug);
            XmlAssembly xmlAssembly = query.executeQuery(request, true);
            xdebug.stopTimer();
            if (xmlAssembly.isEmpty()) {
                System.out.println("No Macthing Hits");
            } else {
                String xmlString = xmlAssembly.getXmlString();
                System.out.println("Showing Top Hit Only:");
                System.out.println(xmlString);
            }
        } catch (QueryException e) {
            System.out.println("\n!!!!  An Error Has Occurred!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        }
    }
}