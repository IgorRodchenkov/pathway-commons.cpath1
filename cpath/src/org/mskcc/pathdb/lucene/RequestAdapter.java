package org.mskcc.pathdb.lucene;

import org.mskcc.pathdb.controller.ProtocolRequest;

/**
 * This class takes in a ProtocolRequest object, and outputs its
 * corresponding search terms in Lucene.
 *
 * @author Ethan Cerami
 */
public class RequestAdapter {
    private static String AND_OPERATOR = "+";
    private static String FIELD_SEPARATOR = ":";
    private static String START_PAREN = "(";
    private static String END_PAREN = ")";

    // TODO:  Write Unit Test
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