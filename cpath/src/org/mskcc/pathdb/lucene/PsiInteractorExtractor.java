package org.mskcc.pathdb.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.QueryHighlightExtractor;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.util.XmlStripper;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.model.ProteinWithWeight;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.MarshalException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;

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

    public PsiInteractorExtractor(EntrySet entrySet, String queryStr,
            XDebug xdebug) throws IOException, ParseException,
            ValidationException, MarshalException {
        this.xdebug = xdebug;
        this.entrySet = entrySet;
        interactors = new HashSet();
        analyzer = new StandardAnalyzer();
        LuceneIndexer indexer = new LuceneIndexer();
        reader = IndexReader.open(indexer.getDirectory());
        if (queryStr != null) {
            query = QueryParser.parse(queryStr, LuceneIndexer.FIELD_ALL,
                    analyzer);
            query = query.rewrite(reader);
            highLighter = new QueryHighlightExtractor (query, analyzer,
                   START_SPAN_TAG+COLOR+END_TAG, END_SPAN_TAG);
            checkAllEntries();
        }
    }

    private void checkAllEntries () throws MarshalException,
            ValidationException, IOException {
        xdebug.logMsg(this, "Checking all interactors for matches:  " +
                query.toString());
        for (int i=0; i< entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            for (int j=0; j<interactorList.getProteinInteractorCount(); j++) {
                checkInteractor(interactorList.getProteinInteractor(j));
            }
        }
    }

    private void checkInteractor(ProteinInteractorType protein)
            throws IOException, ValidationException, MarshalException {
        //  Extract Interactor Field Text (with XML Tags Stripped Out)
        StringWriter writer = new StringWriter();
        protein.marshal(writer);
        String interactorText = XmlStripper.stripTags(writer.toString());
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
     * @return ArrayList of ProteinWithWeight Objects.
     */
    public ArrayList getSortedInteractors() {
        ArrayList list = new ArrayList (interactors);
        Collections.sort(list, new ProteinWithWeight());
        return list;
    }
}