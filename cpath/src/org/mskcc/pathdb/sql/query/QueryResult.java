package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.schemas.psi.EntrySet;

import java.util.ArrayList;

/**
 * Encapsulates Result for Executed Query.
 *
 * @author Ethan Cerami
 */
public class QueryResult {
    private ArrayList interactions = new ArrayList();
    private String xml;
    private EntrySet entrySet;

    /**
     * Gets All Data Service Interactions.
     * @return ArrayList of Interaction Objects.
     */
    public ArrayList getInteractions() {
        return interactions;
    }

    /**
     * Sets Data Service Interactions.
     * @param interactions ArrayList of Interaction Objects.
     */
    void setInteractions(ArrayList interactions) {
        this.interactions = interactions;
    }

    /**
     * Gets XML Document.
     * @return XML Document.
     */
    public String getXml() {
        return xml;
    }

    /**
     * Sets XML Document.
     * @param xml XML Document.
     */
    void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * Gets PSI-MI Entry Set Object.
     * @return PSI-MI Entry Set Object.
     */
    public EntrySet getEntrySet() {
        return entrySet;
    }

    /**
     * Sets PSI-MI Entry Set Object.
     * @param entrySet PSI-MI Entry Set Object.
     */
    void setEntrySet(EntrySet entrySet) {
        this.entrySet = entrySet;
    }
}