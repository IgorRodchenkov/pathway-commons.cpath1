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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.mskcc.pathdb.sql.query.QueryException;

import java.io.IOException;

/**
 * Reads from the Lucene Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class LuceneReader {
    /**
     * Index Search Object (for querying Lucene).
     */
    private IndexSearcher reader;

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
            System.err.println(e);
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
            String dir = LuceneConfig.getLuceneDirectory();
            reader = new IndexSearcher(dir);
            Analyzer analyzer = LuceneConfig.getLuceneAnalyzer();

            //  Create a Multi-Term Query.
            //  By using a multi-term query, hits within FIELD_NAME
            //  have more weight than hits within FIELD_ALL.  This results
            //  in more relevant search results percolating to the top.
            String fields[] = new String[2];
            fields[0] = LuceneConfig.FIELD_NAME;
            fields[1] = LuceneConfig.FIELD_ALL;
            Query query = MultiFieldQueryParser.parse(term, fields, analyzer);
            Hits hits = reader.search(query);
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
}