// $Id: QueryFullText.java,v 1.20 2009-07-01 14:21:44 cerami Exp $
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
package org.mskcc.pathdb.tool;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.lucene.LuceneReader;
import org.mskcc.pathdb.sql.query.QueryException;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

/**
 * Command Line Tool for Querying Full Text Indexer.
 *
 * @author Ethan Cerami
 */
public class QueryFullText {

    /**
     * Executes Full Text Query.
     *
     * @param term Search Term
     * @throws QueryException Lucene Query Error
     * @throws IOException    I/O Error
     * @throws ParseException Lucene Parsing Error
     */
    public static void queryFullText(String term) throws QueryException,
            IOException, ParseException {
        System.out.println("Using search term:  " + term);
        LuceneReader luceneReader = new LuceneReader();
        Hits hits = luceneReader.executeQuery(term);
        int num = Math.min(10, hits.length());
        System.out.println("Total Number of Hits:  " + hits.length());
        if (hits.length() > 0) {

            //  Standard Analyzer to extract words using a list of English stop words.
            StandardAnalyzer analyzer = new StandardAnalyzer();

            //  Standard Query Parser
            QueryParser queryParser = new QueryParser(LuceneConfig.FIELD_ALL, analyzer);

            // for the usage of highlighting with wildcards
            // Necessary to expand search terms
            IndexReader reader = IndexReader.open (new File(LuceneConfig.getLuceneDirectory()));
            Query luceneQuery = queryParser.parse(term);
            luceneQuery = luceneQuery.rewrite(reader);

            //  Scorer implementation which scores text fragments by the number of
            //  unique query terms found.
            QueryScorer queryScorer = new QueryScorer(luceneQuery);

            //  HTML Formatted surrounds matching text with <B></B> tags.
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();

            //  Highligher Class
            Highlighter highLighter = new Highlighter(htmlFormatter, queryScorer);

            //  XXX Characters Max in Each Fragment
            Fragmenter fragmenter = new SimpleFragmenter(100);
            highLighter.setTextFragmenter(fragmenter);

            System.out.println("Showing hits:  0-" + (num - 1));
            for (int i = 0; i < num; i++) {
                System.out.print ("Hit " + i + ":  ");

                //  Get the Matching Hit
                Document doc = hits.doc(i);

                //  Get the Field of Interest
                Field field = doc.getField(LuceneConfig.FIELD_ALL);

                //  Create the Token Stream
                TokenStream tokenStream = new StandardAnalyzer().tokenStream
                        (LuceneConfig.FIELD_ALL, new StringReader(field.stringValue()));

                //  Get the Best Fragment
                String formattedText = highLighter.getBestFragments(tokenStream,
                        field.stringValue(), 5, "...");
                System.out.println(formattedText);
            }
        }
    }
}
