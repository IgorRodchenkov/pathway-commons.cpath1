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