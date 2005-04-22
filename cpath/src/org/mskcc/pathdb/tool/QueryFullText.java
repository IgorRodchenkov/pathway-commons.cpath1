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
package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.lucene.LuceneReader;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.sql.query.QueryException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.QueryHighlightExtractor;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.ArrayList;
import java.io.IOException;

/**
 * Command Line Tool for Querying Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class QueryFullText {

    public static void queryFullText(String term) throws QueryException,
            IOException, ParseException {
        System.out.println("Using search term:  " + term);
        LuceneReader luceneReader = new LuceneReader();
        Hits hits = luceneReader.executeQuery(term);
        int num = Math.min (10, hits.length());
        System.out.println("Total Number of Hits:  " + hits.length());
        if (hits.length() > 0) {

            StandardAnalyzer analyzer = new StandardAnalyzer();
            Query query = QueryParser.parse(term, LuceneConfig.FIELD_ALL,
                    analyzer);
            QueryHighlightExtractor highLighter =
                    new QueryHighlightExtractor(query, analyzer, "[", "]");

            System.out.println("Showing hits:  0-" + (num-1));
            for (int i=0; i<num; i++) {
                Document doc = hits.doc(i);
                Field field = doc.getField(LuceneConfig.FIELD_ALL);
                String value = field.stringValue();

                String fragment = highLighter.getBestFragment(value, 70);
                System.out.println(i + ".  " + fragment.trim());
            }
        }
    }
}