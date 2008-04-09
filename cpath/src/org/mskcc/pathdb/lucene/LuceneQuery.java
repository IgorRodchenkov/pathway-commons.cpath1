// $Id: LuceneQuery.java,v 1.13 2008-04-09 17:25:26 cerami Exp $
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
import org.mskcc.pathdb.sql.query.QueryUtil;
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
    private List<List<String>> fragments;
	private Set<String> dataSourceSet;
	private Map<Long,Set<String>> dataSources;
	private Map<Long,Float> scores;
    private ArrayList<Integer> numDescendentsList;

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
            long[] cpathIds = QueryUtil.extractHits(xdebug, pager, hits);

            if (queryFromUser != null) {
                //  Extract fragments, based on original query from user
                fragments = QueryUtil.extractFragments(queryFromUser, pager, hits);
				// Extract data sources
				dataSources = QueryUtil.extractDataSources(queryFromUser, pager, hits);
				// Extract scores
				scores = QueryUtil.extractScores(queryFromUser, pager, hits);
            } else {
				fragments = QueryUtil.extractFragments(searchTerms, pager, hits);
                dataSourceSet = QueryUtil.extractDataSourceSet(searchTerms, pager, hits);
                dataSources = QueryUtil.extractDataSources(searchTerms, pager, hits);
				scores = QueryUtil.extractScores(searchTerms, pager, hits);
            }
            numDescendentsList = QueryUtil.extractNumDescendents(pager, hits);
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
     * @return text fragments, list of list of strings.
     */
    public List<List<String>> getTextFragments() {
        return this.fragments;
    }

    /**
     * Gets Data Source Set.
	 * (set of data sources across entire result set).
     *
     * @return Set
     */
    public Set<String> getDataSourceSet() {
        return this.dataSourceSet;
    }

    /**
     * Gets Data Sources.
     * (map of cpath id to data source name)
     *
     * @return Map<Long,Set<String>>
     */
    public Map<Long,Set<String>> getDataSources() {
        return this.dataSources;
    }

    /**
     * Gets query score map.
	 *
	 * The map key is cpath id.
	 * The map value is lucene score (0-1)
     *
     * @return Map<Long,Float>
     */
    public Map<Long,Float> getScores() {
        return this.scores;
    }

    /**
     * Gets Num Descendents List.
     * @return ArrayList of Integer Num Descendents.
     */
    public ArrayList<Integer> getNumDescendentsList() {
        return this.numDescendentsList;
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
