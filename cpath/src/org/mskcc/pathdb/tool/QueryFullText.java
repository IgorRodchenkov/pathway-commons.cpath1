/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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