package org.mskcc.pathdb.sql.query;

import org.apache.log4j.Logger;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract Base Class for all queries which return interactions.
 *
 * @author Ethan Cerami
 */
public abstract class InteractionQuery {
    private ArrayList interactions = new ArrayList();
    private String xml;

    /**
     * Logger.
     */
    protected Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Executes Query.
     * @throws QueryException Error Executing Query.
     */
    public void execute() throws QueryException {
        try {
            executeSub();
        } catch (Exception e) {
            throw new QueryException(e.getMessage(), e);
        }
    }

    /**
     * Must be subclassed.
     * @throws Exception All Exceptions.
     */
    protected abstract void executeSub() throws Exception;

    /**
     * Gets the ArrayList of Interaction Objects.
     * @return ArrayList of Interaction Objects.
     */
    public ArrayList getInteractions() {
        return interactions;
    }

    /**
     * Gets XML Response String.
     * @return XML Response String.
     */
    public String getXml() {
        return this.xml;
    }

    /**
     * Sets the XML Response String.
     * @param xml XML String.
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * Sets the ArrayList of Interaction Objects.
     * @param interactions
     */
    protected void setInteractions(ArrayList interactions) {
        this.interactions = interactions;
    }

    /**
     * Given a List of Interactor Records, retrieve all
     * associated interactions.
     * @param cpathRecords ArrayList of CPath Record Objects
     * containing Interactors.
     * @return ArrayList of CPathRecord Objects containing Interactions.
     * @throws DaoException Error Retrieving Data from Database.
     */
    protected ArrayList extractInteractions(ArrayList cpathRecords)
            throws DaoException {
        ArrayList interactions = new ArrayList();
        DaoInternalLink linker = new DaoInternalLink();
        for (int i = 0; i < cpathRecords.size(); i++) {
            CPathRecord record = (CPathRecord) cpathRecords.get(i);
            ArrayList list = linker.getInternalLinksWithLookup(record.getId());
            interactions.addAll(list);
        }
        return interactions;
    }

    /**
     * Given an Interactor Record, retrieve all associated interactions.
     * @param record CPathRecord Object
     * @return ArrayList of CPathRecord Objects containing Interactions.
     * @throws DaoException Error Retrieving Data from Database.
     */
    protected ArrayList extractInteractions(CPathRecord record)
            throws DaoException {
        ArrayList interactors = new ArrayList();
        interactors.add(record);
        return this.extractInteractions(interactors);
    }

    /**
     * Given a List of Interaction Records, retrieve all associated
     * interactors.
     * @param interactions ArrayList of CPathRecord Objects containing
     * Interactions.
     * @return HashMap of All Interactors, indexed by cpath ID.
     * @throws DaoException Error Retrieving Data from Database.
     */
    protected HashMap extractInteractors(ArrayList interactions)
            throws DaoException {
        HashMap interactorMap = new HashMap();
        DaoInternalLink linker = new DaoInternalLink();
        DaoCPath cpath = new DaoCPath();
        for (int i = 0; i < interactions.size(); i++) {
            CPathRecord record = (CPathRecord) interactions.get(i);
            ArrayList list = linker.getInternalLinks(record.getId());
            for (int j = 0; j < list.size(); j++) {
                InternalLinkRecord link = (InternalLinkRecord) list.get(j);
                Long key = new Long(link.getCpathIdB());
                if (!interactorMap.containsKey(key)) {
                    CPathRecord interactor = cpath.getRecordById
                            (link.getCpathIdB());
                    interactorMap.put(key, interactor);
                }
            }
        }
        return interactorMap;
    }
}