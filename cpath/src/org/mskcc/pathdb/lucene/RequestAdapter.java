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
package org.mskcc.pathdb.lucene;

import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;

/**
 * This class takes in a ProtocolRequest object, and outputs its
 * corresponding search terms in Lucene.
 *
 * @author Ethan Cerami
 */
public class RequestAdapter {
    private static final String AND_OPERATOR = "+";
    private static final String START_PAREN = "(";
    private static final String END_PAREN = ")";
    private static final String SPACE = " ";
    private static final String COLON = ":";
    private static final String QUOTE = "\"";

    /**
     * Gets Revised Search Terms.
     *
     * @param request ProtocolRequest Object.
     * @return Search Terms.
     */
    public static String getSearchTerms(ProtocolRequest request) {
        String query = request.getQuery();
        String command = request.getCommand();
        if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_ID)) {
            query = addField(LuceneConfig.FIELD_INTERACTOR_ID, query);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_INTERACTOR_NAME_XREF)) {
            query = addField(PsiInteractionToIndex.FIELD_INTERACTOR, query);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_ORGANISM)) {
            query = addField(PsiInteractionToIndex.FIELD_ORGANISM, query);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_DATABASE)) {
            query = addField(PsiInteractionToIndex.FIELD_DATABASE, query);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_PMID)) {
            query = addField(PsiInteractionToIndex.FIELD_PMID, query);
        } else if (command.equals
                (ProtocolConstants.COMMAND_GET_BY_EXPERIMENT_TYPE)) {
            query = addField(PsiInteractionToIndex.FIELD_EXPERIMENT_TYPE,
                    query);
        }
        query = reviseByOrganism(request, query);
        return query;
    }

    private static String addField(String field, String query) {
        query = new String(field + COLON + query);
        return query;
    }

    /**
     * Checks Organism Field and Modifies Query Term(s) as Needed.
     */
    private static String reviseByOrganism(ProtocolRequest request,
            String query) {
        StringBuffer revisedQuery = new StringBuffer();
        String organism = request.getOrganism();
        if (organism != null && organism.length() > 0) {
            if (query != null && query.length() > 0) {
                revisedQuery.append(AND_OPERATOR + START_PAREN
                        + query + END_PAREN + SPACE);
            }
            revisedQuery.append(AND_OPERATOR
                    + PsiInteractionToIndex.FIELD_ORGANISM
                    + COLON + organism);
        } else {
            revisedQuery.append(query);
        }
        return revisedQuery.toString();
    }
}