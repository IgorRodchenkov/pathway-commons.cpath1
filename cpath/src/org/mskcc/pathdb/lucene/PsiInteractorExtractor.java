// $Id: PsiInteractorExtractor.java,v 1.11 2006-02-22 22:47:50 grossb Exp $
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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.QueryHighlightExtractor;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.Entry;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.InteractorList;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.pathdb.model.ProteinWithWeight;
import org.mskcc.pathdb.util.xml.XmlStripper;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Extracts Matching Interactors from the PSI-MI Entry Set.
 *
 * @author Ethan Cerami
 */
public class PsiInteractorExtractor {
    private Set interactors;
    private Query query;
    private StandardAnalyzer analyzer;
    private IndexReader reader;
    private QueryHighlightExtractor highLighter;
    private EntrySet entrySet;
    private XDebug xdebug;

    private static final String START_SPAN_TAG =
            "<SPAN STYLE='background-color: ";
    private static final String END_TAG = "'>";
    private static final String COLOR = "yellow";
    private static final String END_SPAN_TAG = "</SPAN>";

    /**
     * Constructor
     *
     * @param entrySet PSI-MI Entry Set Object.
     * @param queryStr Query String.
     * @param xdebug   XDebug Object.
     * @throws IOException         Input Output Exception.
     * @throws ParseException      Parsing Exception.
     * @throws ValidationException Validation Exception.
     * @throws MarshalException    Marshaling Exception.
     */
    public PsiInteractorExtractor(EntrySet entrySet, String queryStr,
            XDebug xdebug) throws IOException, ParseException,
            ValidationException, MarshalException {
        try {
            this.xdebug = xdebug;
            this.entrySet = entrySet;
            interactors = new HashSet();
            analyzer = new StandardAnalyzer();
            reader = IndexReader.open(LuceneConfig.getLuceneDirectory());
            if (queryStr != null) {
                query = QueryParser.parse(queryStr, LuceneConfig.FIELD_ALL,
                        analyzer);
                query = query.rewrite(reader);
                highLighter = new QueryHighlightExtractor(query, analyzer,
                        START_SPAN_TAG + COLOR + END_TAG, END_SPAN_TAG);
                checkAllEntries();
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Checks all Entries for Matching Interactors.
     */
    private void checkAllEntries() throws MarshalException,
            ValidationException, IOException {
        xdebug.logMsg(this, "Checking all interactors for matches:  "
                + query.toString());
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            for (int j = 0;
                 j < interactorList.getProteinInteractorCount(); j++) {
                checkInteractor(interactorList.getProteinInteractor(j));
            }
        }
    }

    /**
     * Checks Specified Interactor.
     */
    private void checkInteractor(ProteinInteractorType protein)
            throws IOException, ValidationException, MarshalException {
        //  Extract Interactor Field Text (with XML Tags Stripped Out)
        StringWriter writer = new StringWriter();
        protein.marshal(writer);
        String interactorText = XmlStripper.stripTags(writer.toString(),
                false);
        xdebug.logMsg(this, "Checking interactor:  " + interactorText);

        if (interactorText != null) {
            String highLight = highLighter.highlightText(interactorText);
            //  If not null, we have a match
            if (highLight != null) {
                xdebug.logMsg(this, "...  Match Found:  " + highLight);
                ProteinWithWeight proteinWithWeight =
                        new ProteinWithWeight(protein, 0);
                interactors.add(proteinWithWeight);
            } else {
                xdebug.logMsg(this, "...  No Match Found");
            }
        }
    }

    /**
     * Gets a Sorted List of Interactors.
     *
     * @return ArrayList of ProteinWithWeight Objects.
     */
    public ArrayList getSortedInteractors() {
        ArrayList list = new ArrayList(interactors);
        Collections.sort(list, new ProteinWithWeight());
        return list;
    }
}
