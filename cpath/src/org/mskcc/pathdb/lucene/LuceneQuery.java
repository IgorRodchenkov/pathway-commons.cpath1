// $Id: LuceneQuery.java,v 1.16 2008-07-10 21:05:48 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
import org.mskcc.pathdb.lucene.LuceneResults;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.taglib.Pager;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.model.GlobalFilterSettings;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Lucene Query Class.
 *
 * @author Ethan Cerami
 */
public class LuceneQuery {
    private String queryFromUser;
    private String searchTerms;
    private ProtocolRequest request;
    private XDebug xdebug;
    private int totalNumHits;
    private LuceneResults luceneResults;

    /**
     * Constructor.
     *
     * @param request ProtocolRequest Object.
     * @param xdebug  XDebug Object.
     */
    public LuceneQuery(ProtocolRequest request, GlobalFilterSettings globalFilterSettings,
            XDebug xdebug) throws DaoException {
        this.request = request;
        this.xdebug = xdebug;
        this.searchTerms = RequestAdapter.getSearchTerms(request);
        if (globalFilterSettings != null) {
            this.queryFromUser = request.getQuery ();
            this.searchTerms = LuceneAutoFilter.addFiltersToQuery
                (request.getQuery(), globalFilterSettings);
        }

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
     * @throws DaoException      Data Access Exception.
     */
    public long[] executeSearch() throws QueryException,
		IOException, AssemblyException, ParseException, DaoException {
        xdebug.logMsg(this, "Searching Full Text Index, "
                + "Using search term(s):  " + searchTerms);
        LuceneReader indexer = new LuceneReader();
        try {
            Hits hits = executeLuceneSearch(indexer);
            Pager pager = new Pager(request, hits.length());

            if (queryFromUser != null) {
                luceneResults = new LuceneResults(pager, hits, queryFromUser);
            } else {
                luceneResults = new LuceneResults(pager, hits, searchTerms);
            }
            return luceneResults.getCpathIds();
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
     * Gets Lucene Results Object.
     * @return Lucene Results Object.
     */
    public LuceneResults getLuceneResults() {
        return luceneResults;
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