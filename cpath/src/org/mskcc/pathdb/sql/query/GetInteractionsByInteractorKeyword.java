package org.mskcc.pathdb.sql.query;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gets All Interactions for the specified Interactor CPath ID.
 *
 * @author Ethan Cerami
 */
class GetInteractionsByInteractorKeyword extends PsiInteractionQuery {
    private String searchTerms;
    private int maxHits;

    /**
     * Constructor.
     * @param searchTerms Search term(s) or search phrase.
     * @param maxHits Maximum Number of Hits.
     */
    public GetInteractionsByInteractorKeyword(String searchTerms, int maxHits) {
        this.searchTerms = searchTerms;
        this.maxHits = maxHits;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected QueryResult executeSub() throws Exception {
        QueryResult result = new QueryResult();
        xdebug.logMsg(this, "Getting Interactions for all Interactors with "
                + "search term(s):  " + searchTerms);

        DaoCPath cpath = new DaoCPath();
        LuceneIndexer indexer = new LuceneIndexer();
        try {
            ArrayList records = new ArrayList();
            Hits hits = indexer.executeQuery(searchTerms);
            xdebug.logMsg(this, "Total Number of Matching Interactors "
                    + "Found:  " + hits.length());
            ;
            int max = Math.min(maxHits, hits.length());
            xdebug.logMsg(this, "Showing first " + max + "hits.");
            for (int i = 0; i < max; i++) {
                Document doc = hits.doc(i);
                Field field = doc.getField(LuceneIndexer.FIELD_CPATH_ID);
                long cpathId = Long.parseLong(field.stringValue());
                CPathRecord record = cpath.getRecordById(cpathId);
                records.add(record);
            }
            if (records.size() > 0) {
                ArrayList interactions = this.extractInteractions(records);
                HashMap interactors = this.extractInteractors(interactions);
                result = generateQueryResult(interactors.values(),
                        interactions);
            }
        } finally {
            indexer.closeIndexSearcher();
        }
        return result;
    }
}
