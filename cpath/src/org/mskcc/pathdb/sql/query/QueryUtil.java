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
package org.mskcc.pathdb.sql.query;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.QueryHighlightExtractor;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.taglib.Pager;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.File;
import java.io.IOException;

/**
 * Query Utility Class.
 *
 * @author Ethan Cerami
 */
public class QueryUtil {
    private static final String START_TAG = "<B>";
    private static final String END_TAG = "</B>";


    /**
     * Extracts cPath IDs associated with the specified range of Lucene Hits.
     *
     * @param xdebug XDebug Object
     * @param pager  Pager Object for Next/Previous Pages
     * @param hits   Lucene Hits Object
     * @return array of cPath Ids.
     * @throws IOException Input/Output Error.
     */
    public static long[] extractHits(XDebug xdebug, Pager pager, Hits hits)
            throws IOException {
        int size = pager.getEndIndex() - pager.getStartIndex();
        long cpathIds[] = new long[size];
        int index = 0;
        xdebug.logMsg(QueryUtil.class, "Extracting hits:  "
                + pager.getStartIndex()
                + " - " + pager.getEndIndex());

        for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
            Document doc = hits.doc(i);
            Field field = doc.getField(LuceneConfig.FIELD_CPATH_ID);
            cpathIds[index++] = Long.parseLong(field.stringValue());
        }
        return cpathIds;
    }

    /**
     * Extracts Text Fragments associated with the specified range of
     * Lucene Hits.
     *
     * @param term  Query Term String
     * @param pager Pager Object for Next/Previous Pages
     * @param hits  Lucene Hits Object
     * @return array of String fragments
     * @throws IOException    Input/Output Error
     * @throws ParseException Parsing Exception
     */
    public static String[] exractFragments(String term, Pager pager, Hits hits)
            throws IOException, ParseException {
        int size = pager.getEndIndex() - pager.getStartIndex();
        String fragments[] = new String[size];

        QueryParser parser = new QueryParser(LuceneConfig.FIELD_ALL,
                new StandardAnalyzer());
        Query luceneQuery = parser.parse(term);

        // Necessary to expand search terms
        IndexReader reader = IndexReader.open
                (new File(LuceneConfig.getLuceneDirectory()));
        luceneQuery = luceneQuery.rewrite(reader);

        QueryHighlightExtractor highLighter =
                new QueryHighlightExtractor(luceneQuery,
                        new StandardAnalyzer(), START_TAG, END_TAG);

        int index = 0;
        for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
            Document doc = hits.doc(i);
            Field field = doc.getField(LuceneConfig.FIELD_ALL);
            String value = field.stringValue();
            fragments[index++] = highLighter.getBestFragment(value, 125);
        }
        reader.close();
        return fragments;
    }
}
