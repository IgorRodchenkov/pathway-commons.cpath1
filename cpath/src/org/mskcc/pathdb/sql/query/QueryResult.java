package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.schemas.psi.EntrySet;

import java.util.ArrayList;

public class QueryResult {
    private ArrayList interactions = new ArrayList();
    private String xml;
    private EntrySet entrySet;

    public ArrayList getInteractions() {
        return interactions;
    }

    void setInteractions(ArrayList interactions) {
        this.interactions = interactions;
    }

    public String getXml() {
        return xml;
    }

    void setXml(String xml) {
        this.xml = xml;
    }

    public EntrySet getEntrySet() {
        return entrySet;
    }

    void setEntrySet(EntrySet entrySet) {
        this.entrySet = entrySet;
    }
}