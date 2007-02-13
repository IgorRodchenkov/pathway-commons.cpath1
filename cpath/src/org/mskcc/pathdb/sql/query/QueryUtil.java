// $Id: QueryUtil.java,v 1.10 2007-02-13 17:22:11 grossb Exp $
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
import org.mskcc.pathdb.lucene.BioPaxToIndex;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.taglib.Pager;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * Query Utility Class.
 *
 * @author Ethan Cerami
 */
public class QueryUtil {
    private static final String START_TAG = "<b>";
    private static final String END_TAG = "</b>";


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
    public static String[] extractFragments(String term, Pager pager, Hits hits)
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

    /**
     * Extracts Data Sources associated with the specified range of
     * Lucene Hits for the entire result set.
     *
     * @param term  Query Term String
     * @param pager Pager Object for Next/Previous Pages
     * @param hits  Lucene Hits Object
     * @return Set of data sources.
     * @throws IOException    Input/Output Error
     * @throws ParseException Parsing Exception
     * @throws DaoException   Data Access Exception
     */
    public static Set<String> extractDataSourceSet(String term, Pager pager, Hits hits)
		throws IOException, ParseException, DaoException {
        int size = pager.getEndIndex() - pager.getStartIndex();
        Set<String> dataSources = new HashSet<String>();
        DaoExternalDb dao = new DaoExternalDb();

        //for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
		for (int i = 0; i < hits.length(); i++) {
            Document doc = hits.doc(i);
            Field field = doc.getField(LuceneConfig.FIELD_DATA_SOURCE);
			for (String fieldValue : field.stringValue().split(" ")) {
				ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(fieldValue);
				dataSources.add(dbRecord.getName());
			}
        }
        return dataSources;
    }

    /**
     * Extracts Datasources associated with the specified range of
     * Lucene Hits for each record.
     *
     * @param term  Query Term String
     * @param pager Pager Object for Next/Previous Pages
     * @param hits  Lucene Hits Object
     * @return Map of cpath ids to data source names. Map<Long,Set<String>>
     * @throws IOException    Input/Output Error
     * @throws ParseException Parsing Exception
     */
    public static Map<Long,Set<String>> extractDataSources(String term, Pager pager, Hits hits)
		throws IOException, ParseException, DaoException {
        int size = pager.getEndIndex() - pager.getStartIndex();
        Map<Long,Set<String>> dataSources = new HashMap<Long,Set<String>>();
        DaoExternalDb dao = new DaoExternalDb();

        int index = 0;
        for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
            Document doc = hits.doc(i);
			Field cpathIdField = doc.getField(LuceneConfig.FIELD_CPATH_ID);
            Field dataSourceField = doc.getField(LuceneConfig.FIELD_DATA_SOURCE);
			HashSet<String> dataSourcesSet = new HashSet<String>();
			for (String fieldValue : dataSourceField.stringValue().split(" ")) {
				dataSourcesSet.add(dao.getRecordByTerm(fieldValue).getName());
			}
			dataSources.put(Long.parseLong(cpathIdField.stringValue()), dataSourcesSet);
        }
        return dataSources;
    }

    /**
     * Extracts Scores associated with the specified range of
     * Lucene Hits.
     *
     * @param term  Query Term String
     * @param pager Pager Object for Next/Previous Pages
     * @param hits  Lucene Hits Object
     * @return Map of cpath ids to lucene scores (0-1). Map<Long,Float>
     * @throws IOException    Input/Output Error
     * @throws ParseException Parsing Exception
     */
    public static Map<Long,Float> extractScores(String term, Pager pager, Hits hits)
            throws IOException, ParseException {
        int size = pager.getEndIndex() - pager.getStartIndex();
        Map<Long,Float> scores = new HashMap<Long,Float>();

        int index = 0;
        for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
            Document doc = hits.doc(i);
			Field cpathIdField = doc.getField(LuceneConfig.FIELD_CPATH_ID);
			scores.put(Long.parseLong(cpathIdField.stringValue()), new Float(hits.score(i)));
        }
        return scores;
    }
}
