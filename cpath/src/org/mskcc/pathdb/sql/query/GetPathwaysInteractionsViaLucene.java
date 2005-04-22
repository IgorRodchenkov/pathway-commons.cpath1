package org.mskcc.pathdb.sql.query;

import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.ParseException;
import org.mskcc.pathdb.lucene.LuceneReader;
import org.mskcc.pathdb.lucene.RequestAdapter;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.taglib.Pager;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;

/**
 * Searches for BioPAX/PSI-MI Pathways and Interactions via Lucene.
 *
 * @author Ethan Cerami
 */
public class GetPathwaysInteractionsViaLucene {
    private String searchTerms;
    private ProtocolRequest request;
    private XDebug xdebug;
    private int totalNumHits;
    private String fragments[];

    /**
     * Constructor.
     *
     * @param request ProtocolRequest Object.
     */
    public GetPathwaysInteractionsViaLucene(ProtocolRequest request,
            XDebug xdebug) {
        this.request = request;
        this.xdebug = xdebug;
        this.searchTerms = RequestAdapter.getSearchTerms(request);
    }

    /**
     * Executes Query, and returns an Array of cPath Long IDs.
     */
    public long[] executeSearch() throws QueryException,
            IOException, AssemblyException, ParseException {
        xdebug.logMsg(this, "Getting Pathways/Interactions via Lucene. "
                + "Using search term(s):  " + searchTerms);
        LuceneReader indexer = new LuceneReader();
        try {
            Hits hits = executeLuceneSearch(indexer);
            Pager pager = new Pager(request, hits.length());
            long[] cpathIds = QueryUtil.extractHits(xdebug, pager, hits);
            fragments = QueryUtil.exractFragments(searchTerms, pager, hits);
            return cpathIds;
        } finally {
            indexer.close();
        }
    }

    /**
     * Gets Total Number of Hits.
     * @return total number of hits.
     */
    public int getTotalNumHits() {
        return this.totalNumHits;
    }

    /**
     * Gets Text Fragments.
     * @return text fragments, string array.
     */
    public String[] getTextFragments () {
        return this.fragments;
    }

    /**
     * Executes Lucene Search.
     */
    private Hits executeLuceneSearch(LuceneReader indexer)
            throws QueryException {
        Hits hits = indexer.executeQuery(searchTerms);
        xdebug.logMsg(this, "Total Number of Matching Pathways/Interactions "
                + "Found:  " + hits.length());
        this.totalNumHits = hits.length();
        return hits;
    }
}