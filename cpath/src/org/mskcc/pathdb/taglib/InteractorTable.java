package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.dataservices.mapper.MapperException;
import org.apache.lucene.search.Hits;

import java.util.*;

public class InteractorTable extends HtmlTable {
    private ProtocolRequest protocolRequest;
    private EntrySet entrySet;
    private HashMap interactors;

    /**
     * Sets Interaction Parameter.
     *
     * @param xmlAssembly XmlAssembly Object
     */
    public void setXmlAssembly(XmlAssembly xmlAssembly) {
        if (!xmlAssembly.isEmpty()) {
            entrySet = (EntrySet) xmlAssembly.getXmlObject();
        } else {
            entrySet = null;
        }
    }

    /**
     * Sets Protocol Request Parameter.
     *
     * @param request Protocol Request
     */
    public void setProtocolRequest(ProtocolRequest request) {
        this.protocolRequest = request;
    }

    /**
     * Start Tag Processing.
     *
     * @throws org.mskcc.pathdb.sql.dao.DaoException
     *          Database Access Error.
     */
    protected void subDoStartTag() throws DaoException, MapperException,
            QueryException {
        interactors = new HashMap();
        protocolRequest.setFormat(ProtocolConstants.FORMAT_PSI);
        extractEntySet();

        if (interactors.size() > 0) {
            append("<div id=\"proteinview\" class=\"toolgroup\">\n" +
                    "<div class=\"label\">\n" +
                    "<strong>Protein View</strong>\n" +
                    "</div>" +
                    "<div class=\"body\">");
            outputInteractors();
            append("</div>\n" +
                    "</div>");
        }
    }

    private void outputInteractors() {
        ArrayList list = new ArrayList(interactors.values());
        Collections.sort(list, new ProteinWithHits());
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            ProteinWithHits proteinWithHits =
                    (ProteinWithHits) list.get(i);
            ProteinInteractorType protein = proteinWithHits.getProtein();
            NamesType names = protein.getNames();
            append("<div>");
            String id = protein.getId();
            String link = getInteractionLink(LuceneIndexer.FIELD_INTERACTOR_ID
                    + ":" + id, ProtocolConstants.FORMAT_HTML);
            String name = names.getFullName().trim();
            if (name.length() > 50) {
                name = name.substring(0, 50) + "...";
            }
            append("<A HREF='" + link + "'>" + name + "</A> [" +
                    proteinWithHits.getNumHits() + "]");
            append("</div>");
        }
    }

    private void extractEntySet() throws QueryException {
        if (entrySet != null) {
            for (int i = 0; i < entrySet.getEntryCount(); i++) {
                Entry entry = entrySet.getEntry(i);
                InteractorList interactorList = entry.getInteractorList();
                extractInteractors(interactorList);
            }
        }
    }

    /**
     * Extracts All Interactors and Places in Global HashMap.
     *
     * @param interactorList List of Interactors
     */
    private void extractInteractors(InteractorList interactorList)
            throws QueryException {
        LuceneIndexer indexer = new LuceneIndexer();
        for (int i = 0; i < interactorList.getProteinInteractorCount(); i++) {
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            String id = protein.getId();
            Hits hits = indexer.executeQuery(LuceneIndexer.FIELD_INTERACTOR_ID
                    + ":" + id);
            ProteinWithHits proteinWithHits = new ProteinWithHits(protein,
                    hits.length());
            interactors.put(id, proteinWithHits);
        }
    }
}

class ProteinWithHits implements Comparator {
    private int numHits;
    private ProteinInteractorType protein;

    public ProteinWithHits() {

    }

    public ProteinWithHits(ProteinInteractorType protein, int numHits) {
        this.protein = protein;
        this.numHits = numHits;
    }

    public int getNumHits() {
        return numHits;
    }

    public ProteinInteractorType getProtein() {
        return protein;
    }

    public int compare(Object object1, Object object2) {
        ProteinWithHits protein1 = (ProteinWithHits) object1;
        ProteinWithHits protein2 = (ProteinWithHits) object2;
        Integer numHits1 = new Integer(protein1.getNumHits());
        Integer numHits2 = new Integer(protein2.getNumHits());
        return numHits1.compareTo(numHits2);
    }
}