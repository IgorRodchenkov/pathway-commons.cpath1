package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.taglib.Pager;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.xdebug.XDebug;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.QueryHighlightExtractor;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;
import java.io.File;

public class QueryUtil {
    private static final String START_TAG =
            "<SPAN STYLE='background-color: yellow'>";
    private static final String END_TAG = "</SPAN>";


    /**
     * Extracts cPath IDs associated with the specified range of Lucene Hits.
     *
     * @param xdebug    XDebug Object
     * @param pager     Pager Object for Next/Previous Pages
     * @param hits      Lucene Hits Object
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
     * @param pager     Pager Object for Next/Previous Pages
     * @param hits      Lucene Hits Object
     * @return array of String fragments.
     * @throws IOException Input/Output Error.
     * @throws ParseException Parsing Exception.
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
