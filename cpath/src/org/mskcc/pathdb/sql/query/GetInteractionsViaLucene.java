package org.mskcc.pathdb.sql.query;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.controller.ProtocolRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

/**
 * Gets All Interactions for the specified Interactor CPath ID.
 *
 * @author Ethan Cerami
 */
class GetInteractionsViaLucene extends InteractionQuery {
    private String searchTerms;
    private int maxHits;

    /**
     * Constructor.
     * @param request ProtocolRequest Object.
     */
    public GetInteractionsViaLucene(ProtocolRequest request) {
        this.searchTerms = request.getQuery();
        this.maxHits = request.getMaxHitsInt();
        String organism = request.getOrganism();
        if (organism != null && organism.length() > 0) {
            this.searchTerms = "+("+ searchTerms + ") +organism:" + organism;
        }
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
            Hits hits = indexer.executeQuery(searchTerms);
            xdebug.logMsg(this, "Total Number of Matching Interactions "
                    + "Found:  " + hits.length());
            int max = Math.min(maxHits, hits.length());
            // TODO:  Add Support for Next / Previous Pages
            long cpathIds[] = null;
            cpathIds = new long[max];
            for (int i = 0; i < max; i++) {
                Document doc = hits.doc(i);
                Field field = doc.getField(LuceneIndexer.FIELD_CPATH_ID);
                cpathIds[i] = Long.parseLong(field.stringValue());
            }
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
}