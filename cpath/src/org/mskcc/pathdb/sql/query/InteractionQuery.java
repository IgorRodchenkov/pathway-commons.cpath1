package org.mskcc.pathdb.sql.query;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.DaoCPath;
import org.mskcc.pathdb.sql.DaoInternalLink;
import org.mskcc.pathdb.util.PsiBuilder;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Performs an Interaction Query.
 * Description of Query:  Retrieve all interactions for "YER006W".
 *
 * @author Ethan Cerami
 */
public class InteractionQuery {
    private EntrySet entrySet;
    private ArrayList interactions;

    /**
     * Constructor.
     * @param interactorName Unique Interactor Name.
     * @throws ValidationException XML String is invalid.
     * @throws MarshalException Error Marshaling Object.
     * @throws ClassNotFoundException JDBC Driver not found.
     * @throws SQLException Error Connecting to Database.
     * @throws MapperException Error Mapping to Data Service objects.
     */
    public InteractionQuery (String interactorName)
        throws ClassNotFoundException, SQLException, MarshalException,
        ValidationException, MapperException {
        interactions = new ArrayList();
        HashMap interactorMap = new HashMap();
        DaoCPath cpath = new DaoCPath();
        DaoInternalLink linker = new DaoInternalLink();
        CPathRecord record = cpath.getRecordByName(interactorName);
        if (record != null) {
            entrySet = generateXml(record, linker, interactorMap);
            mapToInteractions();
        }
    }

    /**
     * Gets the EntrySet Object or Results.
     * @return PSI-MI Entry Set Castor Object.
     */
    public EntrySet getEntrySet() {
        return entrySet;
    }

    /**
     * Gets the ArrayList of Interaction Results.
     * @return ArrayList of Interaction Objects.
     */
    public ArrayList getInteractions() {
        return interactions;
    }

    private void mapToInteractions() throws MarshalException,
            ValidationException, MapperException {
        StringWriter writer = new StringWriter();
        entrySet.marshal(writer);
        String xml = writer.toString();
        interactions = new ArrayList();
        MapPsiToInteractions mapper = new MapPsiToInteractions(xml,
                interactions);
        mapper.doMapping();
    }


    /**
     * Generates PSI XML.
     */
    private EntrySet generateXml(CPathRecord record, DaoInternalLink linker,
            HashMap interactorMap) throws ClassNotFoundException,
            SQLException, MarshalException, ValidationException {
        PsiBuilder psiBuilder = new PsiBuilder();
        long id = record.getId();
        ArrayList interactions = linker.getInternalLinksWithLookup(id);
        for (int i = 0; i < interactions.size(); i++) {
            CPathRecord intxRecord = (CPathRecord) interactions.get(i);
            long intxId = intxRecord.getId();
            ArrayList interactors =
                    linker.getInternalLinksWithLookup(intxId);
            addInteractorsToMap(interactorMap, interactors);
        }
        EntrySet entrySet = psiBuilder.generatePsi(interactorMap.values(),
                interactions);
        return entrySet;
    }

    /**
     * Adds List of Interactors to Non-redundant Interactor Map.
     */
    private void addInteractorsToMap(HashMap interactorMap, ArrayList
            interactors) {
        for (int i = 0; i < interactors.size(); i++) {
            CPathRecord record = (CPathRecord) interactors.get(i);
            long id = record.getId();
            interactorMap.put(Long.toString(id), record);
        }
    }
}