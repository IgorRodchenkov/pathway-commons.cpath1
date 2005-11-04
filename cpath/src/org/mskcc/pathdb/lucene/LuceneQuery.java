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

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.QueryUtil;
import org.mskcc.pathdb.taglib.Pager;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;

/**
 * Lucene Query Class.
 *
 * @author Ethan Cerami
 */
public class LuceneQuery {
    private String searchTerms;
    private ProtocolRequest request;
    private XDebug xdebug;
    private int totalNumHits;
    private String fragments[];

    /**
     * Constructor.
     *
     * @param request ProtocolRequest Object.
     * @param xdebug  XDebug Object.
     */
    public LuceneQuery(ProtocolRequest request,
            XDebug xdebug) {
        this.request = request;
        this.xdebug = xdebug;
        this.searchTerms = RequestAdapter.getSearchTerms(request);
    }

    /**
     * Executes Query, and returns an Array of cPath Long IDs.
     *
     * @return array of long cPath IDs.
     * @throws org.mskcc.pathdb.sql.query.QueryException
     *                           Query Error.
     * @throws IOException       I/O Error.
     * @throws AssemblyException XML Assembly Error.
     * @throws ParseException    Lucene Parsing Error.
     */
    public long[] executeSearch() throws QueryException,
            IOException, AssemblyException, ParseException {
        xdebug.logMsg(this, "Searching Full Text Index, "
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
     *
     * @return total number of hits.
     */
    public int getTotalNumHits() {
        return this.totalNumHits;
    }

    /**
     * Gets Text Fragments.
     *
     * @return text fragments, string array.
     */
    public String[] getTextFragments() {
        return this.fragments;
    }

    /**
     * Executes Lucene Search.
     */
    private Hits executeLuceneSearch(LuceneReader indexer)
            throws QueryException {
        Hits hits = indexer.executeQuery(searchTerms);
        xdebug.logMsg(this, "Total Number of Hits Found:  " + hits.length());
        this.totalNumHits = hits.length();
        return hits;
    }
}