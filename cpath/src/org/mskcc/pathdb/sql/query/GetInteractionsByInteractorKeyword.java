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
public class GetInteractionsByInteractorKeyword extends PsiInteractionQuery {
    private String searchTerms;

    /**
     * Constructor.
     * @param searchTerms Search term(s) or search phrase.
     */
    public GetInteractionsByInteractorKeyword(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        DaoCPath cpath = new DaoCPath();
        LuceneIndexer indexer = new LuceneIndexer();
        try {
            ArrayList records = new ArrayList();
            Hits hits = indexer.executeQuery(searchTerms);
            for (int i = 0; i < hits.length(); i++) {
                Document doc = hits.doc(i);
                Field field = doc.getField(LuceneIndexer.FIELD_CPATH_ID);
                long cpathId = Long.parseLong(field.stringValue());
                CPathRecord record = cpath.getRecordById(cpathId);
                records.add(record);
            }
            if (records.size() > 0) {
                ArrayList interactions = this.extractInteractions(records);
                HashMap interactors = this.extractInteractors(interactions);
                createPsi(interactors.values(), interactions);
            }
        } finally {
            indexer.closeIndexSearcher();
        }
    }
}
