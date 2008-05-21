// $Id: QueryUtil.java,v 1.23 2008-05-21 17:04:00 cerami Exp $
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
import org.mskcc.pathdb.util.xml.XmlStripper;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Query Utility Class.
 *
 * @author Ethan Cerami
 */
public class QueryUtil {
    public static final String START_TAG = "<b>";
    public static final String END_TAG = "</b>";
    public static final String MEMBER_OF = "is a member of";


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
     * @return List<List<String>> for each document, return list of String fragments
     * @throws IOException    Input/Output Error
     * @throws ParseException Parsing Exception
     */
    public static List<List<String>> extractFragments(String term, Pager pager, Hits hits)
            throws IOException, ParseException {
        int size = pager.getEndIndex() - pager.getStartIndex();
        List<List<String>> fragments = new ArrayList<List<String>>();

		// if query contains multiple terms, surround it with quotes (unless the query already is)
		term = term.trim();
		if (term.matches("^[^\"]*\\s[^\"]*$")) {
			term = "\"" + term + "\"";		
		}

        QueryParser parser = new QueryParser(LuceneConfig.FIELD_ALL,
                new StandardAnalyzer());
        Query luceneQuery = parser.parse(term);

        // Necessary to expand search terms
        IndexReader reader = IndexReader.open
                (new File(LuceneConfig.getLuceneDirectory()));
        luceneQuery = luceneQuery.rewrite(reader);

		// we do our own highlighting since this one will highlight meta-data,
		// like 'NCI_NATURE' or 'AND' or 'pathway' in a query like the following:
		// 'data_source:"NCI_NATURE" AND entity_type:pathway'
        QueryHighlightExtractor highLighter =
                new QueryHighlightExtractor(luceneQuery,
											new StandardAnalyzer(), "","");

        for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
            Document doc = hits.doc(i);
			String fragment = getFragment(doc, highLighter);
			// if fragment is null, assume descendent ?
			if ((fragment == null || fragment.length() == 0) &&
				term.contains(" entity_type:pathway ")){
				String value = doc.getField(LuceneConfig.FIELD_NAME).stringValue();
				if (value != null && value.length() > 0) {
					List<String> listToReturn = new ArrayList<String>();
					listToReturn.add(START_TAG + term + END_TAG + " " + MEMBER_OF);
					fragments.add(listToReturn);
					continue;
                }
			}
			fragments.add(cookFragment(term, fragment));
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
     * Extracts Num Descendnts in specified range of
     * Lucene Hits for the entire result set.
     *
     * @param pager Pager Object for Next/Previous Pages
     * @param hits  Lucene Hits Object
     * @return List of Num Descendents
     * @throws IOException    Input/Output Error
     * @throws ParseException Parsing Exception
     * @throws DaoException   Data Access Exception
     */
    public static ArrayList<Integer> extractNumDescendents(Pager pager, Hits hits)
		throws IOException, ParseException, DaoException {
        ArrayList<Integer> numDescendentsList = new ArrayList<Integer>();

        for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
            Document doc = hits.doc(i);
            Field field = doc.getField(LuceneConfig.FIELD_NUM_DESCENDENTS);
            if (field == null) {
                numDescendentsList.add(0);
            } else {
                try {
                    Integer num = Integer.parseInt(field.stringValue());
                    numDescendentsList.add(num);
                } catch (NumberFormatException e) {
                    numDescendentsList.add(0);
                }
            }
        }
        return numDescendentsList;
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
                if (fieldValue != null && fieldValue.trim().length() > 0) {
                    dataSourcesSet.add(dao.getRecordByTerm(fieldValue).getName());
                }
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

	/**
	 * Grabs fragment of lucene field that matches query term & highlights term.
	 * Method traverses the lucene fields indexed for match.  If match is not found
	 * null is returned.
	 * 
	 * @param doc Lucene Document
	 * @param highLighter QueryHighlightExtractor
	 * @return String
	 * @throws IOException
	 */
	private static String getFragment(Document doc, QueryHighlightExtractor highLighter)
		throws IOException {

		String[] fields = {LuceneConfig.FIELD_ALL,
						   LuceneConfig.FIELD_SYNONYMS,
						   LuceneConfig.FIELD_EXTERNAL_REFS};

		for (String fieldName : fields) {
			Field field = doc.getField(fieldName);
			String value = field.stringValue();
			String fragment = highLighter.getBestFragment(value, 500);
			if (fragment != null && fragment.length() > 0) return fragment;
		}

		// made it here, assume descendent ?
		return null;
	}

	/**
	 * Removes XML element delimiter placed in fragment by XmlStripper.
	 *  Also removes subset(s) of fragments that do not contain query terms.
	 * 
	 * @param term String
	 * @param fragments String
	 * @return List<String>
	 */
	private static List<String> cookFragment(String terms, String fragments) {

		// check fragment args
		if (fragments == null || fragments.length() == 0) return null;

		// to return
		List<String> toReturn = null;

		// create terms regex - used to match terms anywhere in fragment
		boolean haveTerms = false; 
		String termRegex = "^.*(?i)("; // note: (?i) specifies case-insensitive matching
		for (String term : terms.split(" ")) {
			if (validTerm(term)) {
				termRegex += term + "|";
				haveTerms = true;
			}
		}
		termRegex = termRegex.replaceAll("\\|$", ""); // remove trailing '|'
		termRegex += ").*$";

		// to we have valid terms ?  - see addTerm(..) method for more information
		if (!haveTerms) return null;

		Map<String, String> fragmentMap = new HashMap<String,String>();
		for (String fragment : fragments.split(XmlStripper.ELEMENT_DELIMITER)) {
			if (fragment.matches(termRegex)) {
				// don't process duplicate fragments
				if (!fragmentMap.containsKey(fragment)) {
					toReturn = (toReturn == null) ? new ArrayList<String>() : toReturn;
					// do our own highlighting - see note in extractFragments(..)
					String origFragment = fragment;
					for (String term : terms.split(" ")) {
					    if (validTerm(term)) {
						fragment = fragment.replaceAll("(?i)" + "(" + term + ")", START_TAG + "$1" + END_TAG);
					    }
					}
					// lets remove sentences that are part of fragment but don't contain any terms
					String subFragmentsToReturn = "";
					for (String subFragment : fragment.split("\\.")) {
						// try to determine if '.' is not part of title, like Dr.
						if (!subFragment.matches("^.*(?i)dr$") && subFragment.matches(termRegex)) {
							// do we append a '.' after subFragment ?
							int indexOfPeriod = fragment.indexOf(subFragment) + subFragment.length();
							boolean appendPeriod = ((indexOfPeriod <= fragment.length()-1) &&
													(fragment.charAt(indexOfPeriod) == '.'));
							subFragment = subFragment + ((appendPeriod) ? "." : "");
							subFragmentsToReturn += subFragment;
						}
					}
					// fragment may not have contained periods
					subFragmentsToReturn = (subFragmentsToReturn.length() == 0) ? fragment : subFragmentsToReturn;
					toReturn.add(subFragmentsToReturn);
					fragmentMap.put(origFragment,"");
				}
			}
		}

		// outta here
		return toReturn;
	}

	/**
	 * The idea behind this method is that we only should
	 * consider terms in cookFragment(..) to be valid if the term 
	 * contains "free text" entered by the user, ie 'p53'
	 * If the term is a canned lucene query, like
	 * 'data_source:"NCI_NATURE"' or 'entity_type:pathway'
         * or 'AND', the term should not be considered valid.
	 *
	 * @param term String
	 * @return boolean
	 */
	private static boolean validTerm(String term) {

		// see BioPaxToIndex for fields to check here...
		return (term.contains(LuceneConfig.FIELD_ALL + ":") ||
				term.contains(LuceneConfig.FIELD_CPATH_ID + ":") ||
				term.contains(LuceneConfig.FIELD_ENTITY_TYPE + ":") ||
				term.contains(LuceneConfig.FIELD_DATA_SOURCE + ":") ||
				term.contains(LuceneConfig.FIELD_NAME + ":") ||
				term.contains(LuceneConfig.FIELD_ORGANISM + ":") ||
				term.contains(LuceneConfig.FIELD_SYNONYMS + ":") ||
				term.contains(LuceneConfig.FIELD_EXTERNAL_REFS + ":") ||
				term.contains(LuceneConfig.FIELD_DESCENDENTS + ":") ||
				// lucene boolean operators must be capitalized
				term.equalsIgnoreCase("AND") || term.equalsIgnoreCase("OR") || term.equalsIgnoreCase("NOT")) ? false : true;
	}
}
