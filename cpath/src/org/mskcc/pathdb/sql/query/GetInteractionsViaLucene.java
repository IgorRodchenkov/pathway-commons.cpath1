package org.mskcc.pathdb.sql.query;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.lucene.RequestAdapter;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.taglib.Pager;

import java.io.IOException;

/**
 * Gets All Interactions for the specified Interactor CPath ID.
 *
 * @author Ethan Cerami
 */
class GetInteractionsViaLucene extends InteractionQuery {
    private String searchTerms;
    private ProtocolRequest request;

    /**
     * Constructor.
     *
     * @param request ProtocolRequest Object.
     */
    public GetInteractionsViaLucene(ProtocolRequest request) {
        this.request = request;
        this.searchTerms = RequestAdapter.getSearchTerms(request);
    }

    /**
     * Executes Query.
     */
    protected XmlAssembly executeSub() throws QueryException,
            IOException, AssemblyException {
        xdebug.logMsg(this, "Getting Interactions via Lucene. "
                + "Using search term(s):  " + searchTerms);
        LuceneIndexer indexer = new LuceneIndexer();
        XmlAssembly xmlAssembly;
        try {
            Hits hits = executeLuceneSearch(indexer);
            Pager pager = new Pager(request, hits.length());

            long[] cpathIds = extractHits(pager, hits);

            if (cpathIds != null && cpathIds.length > 0) {
                xmlAssembly =
                        XmlAssemblyFactory.createXmlAssembly(cpathIds,
                                hits.length(), xdebug);
            } else {
                xmlAssembly = XmlAssemblyFactory.createEmptyXmlAssembly(xdebug);
            }
            xmlAssembly.setNumHits(hits.length());
        } finally {
            indexer.closeIndexSearcher();
        }
        return xmlAssembly;
    }

    /**
     * Extracts Lucene Hits in Specified Range.
     */
    private long[] extractHits(Pager pager, Hits hits) throws IOException {
        int size = pager.getEndIndex() - pager.getStartIndex();
        long cpathIds[] = new long[size];
        int index = 0;
        xdebug.logMsg(this, "Extracting hits:  " + pager.getStartIndex()
                + " - " + pager.getEndIndex());

        for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
            Document doc = hits.doc(i);
            Field field = doc.getField(LuceneIndexer.FIELD_INTERACTION_ID);
            cpathIds[index++] = Long.parseLong(field.stringValue());
        }
        return cpathIds;
    }

    /**
     * Executes Lucene Search.
     */
    private Hits executeLuceneSearch(LuceneIndexer indexer)
            throws QueryException {
        Hits hits = indexer.executeQuery(searchTerms);
        xdebug.logMsg(this, "Total Number of Matching Interactions "
                + "Found:  " + hits.length());
        return hits;
    }
}