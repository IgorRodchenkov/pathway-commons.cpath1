package org.mskcc.pathdb.lucene;

import org.mskcc.pathdb.protocol.ProtocolRequest;

/**
 * This class takes in a ProtocolRequest object, and outputs its
 * corresponding search terms in Lucene.
 *
 * @author Ethan Cerami
 */
public class RequestAdapter {
    private static final String AND_OPERATOR = "+";
    private static final String FIELD_SEPARATOR = ":";
    private static final String START_PAREN = "(";
    private static final String END_PAREN = ")";

    /**
     * Gets Search Terms.
     * @param request ProtocolRequest Object.
     * @return Search Terms.
     */
    public static String getSearchTerms(ProtocolRequest request) {
        String searchTerms = request.getQuery();
        StringBuffer temp = new StringBuffer();
        String organism = request.getOrganism();
        if (organism != null && organism.length() > 0) {
            if (searchTerms != null) {
                temp.append(AND_OPERATOR + START_PAREN
                        + searchTerms + END_PAREN);
            }
            temp.append(AND_OPERATOR + PsiInteractionToIndex.FIELD_ORGANISM
                    + FIELD_SEPARATOR + organism);
            searchTerms = temp.toString();
        }
        return searchTerms;
    }
}