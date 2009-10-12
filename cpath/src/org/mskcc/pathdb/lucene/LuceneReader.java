// $Id: LuceneReader.java,v 1.17 2009-10-12 18:18:44 cerami Exp $
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

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.mskcc.pathdb.sql.query.QueryException;

import java.util.List;
import java.util.Date;
import java.io.IOException;

/**
 * Reads from the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneReader {
    private Logger log = Logger.getLogger(LuceneReader.class);

    /**
     * Index Search Object (for querying Lucene).
     */
    private IndexSearcher reader;
    private BooleanQuery queryToSearch;

    /**
     * Closes the Index Searcher.
     * The close() method must be called by all client code in a finally
     * block.  This ensures that the LuceneSearcher is closed, and all files
     * are released.
     */
    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Executes Search Query
     *
     * @param term Search Term.
     * @return Lucene Hits Object
     * @throws QueryException Error Processing Query. I
     */
    public Hits executeQuery(String term) throws QueryException {
        try {
            log.info("Executing lucene query, using term:  " + term);
            Date start = new Date();
            String dir = LuceneConfig.getLuceneDirectory();
            reader = new IndexSearcher(dir);
            Analyzer analyzer = LuceneConfig.getLuceneAnalyzer();

			// create query on FIELD_NAME, FIELD_ALL, FIELD_SYNONYMS, FIELD_EXTERNAL_REFS
			String fields[] = new String[2];
            fields[0] = LuceneConfig.FIELD_ALL;
            fields[1] = LuceneConfig.FIELD_EXTERNAL_REFS;
			BooleanClause.Occur[] flags = { BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD };
			queryToSearch = (BooleanQuery)MultiFieldQueryParser.parse(term, fields, flags, analyzer);

			// create query on FIELD_GENE_SYMBOL and boost it
			QueryParser nameQueryParser = new QueryParser(LuceneConfig.FIELD_GENE_SYMBOLS, analyzer);
			Query nameQuery = nameQueryParser.parse(term);
			nameQuery.setBoost((float)3.0);

			// add query on FIELD_NAME to search query
			queryToSearch.add(nameQuery, BooleanClause.Occur.SHOULD);

            // create query on FIELD_NAME and boost it
		    nameQueryParser = new QueryParser(LuceneConfig.FIELD_NAME, analyzer);
			nameQuery = nameQueryParser.parse(term);
			nameQuery.setBoost((float)100.0);

			// add query on FIELD_NAME to search query
			queryToSearch.add(nameQuery, BooleanClause.Occur.SHOULD);

			// create query on FIELD_SYNONYMS and boost it
			QueryParser synQueryParser = new QueryParser(LuceneConfig.FIELD_SYNONYMS, analyzer);
			Query synQuery = synQueryParser.parse(term);
			synQuery.setBoost((float)1.75);

			// add query on FIELD_SYNONYMS to search query
			queryToSearch.add(synQuery, BooleanClause.Occur.SHOULD);

			// create query on FIELD_DESCENDENTS and boost it
			QueryParser descQueryParser = new QueryParser(LuceneConfig.FIELD_DESCENDENTS, analyzer);
			Query descQuery = descQueryParser.parse(term);
			descQuery.setBoost((float)1.5);

			// add query on FIELD_DESCENDENT to search query
			queryToSearch.add(descQuery, BooleanClause.Occur.SHOULD);

			// outta here
            Hits hits = reader.search(queryToSearch);
            Date stop = new Date();
            long timeInterval = stop.getTime() - start.getTime();
            log.info("Total time to execute lucene query:  " + timeInterval
                + " ms");
            return hits;
        } catch (IOException e) {
            e.printStackTrace();
            throw new QueryException("IOException:  " + e.getMessage(), e);
        } catch (ParseException e) {
            throw new QueryException("ParseException:  " + e.getMessage(), e);
        }
        //  Why not just call reader.close() here?
        //  Because, to extract data out of the Hits object,
        //  the reader must be open.
        //  Client code is responsibe for calling close in a finally block.
    }

    /**
     * Gets the Index Searcher.
     * @return Index Searcher.
     */
    public IndexSearcher getIndexSearcher() {
        return reader;
    }

    /**
     * Gets the Query Object.
     * @return Query Object.
     */
    public Query getQuery() {
        return queryToSearch;
    }
}
