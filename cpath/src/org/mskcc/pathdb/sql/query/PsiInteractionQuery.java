package org.mskcc.pathdb.sql.query;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.mapper.MapPsiToInteractions;
import org.mskcc.dataservices.mapper.MapperException;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.util.PsiBuilder;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract Base Class for all queries which return PSI-MI Formatted Results.
 *
 * @author Ethan Cerami
 */
abstract class PsiInteractionQuery extends InteractionQuery {

    /**
     * Must be subclassed.
     * @throws Exception All Exceptions.
     */
    protected abstract QueryResult executeSub() throws Exception;

    /**
     * Creates PSI XML Document.
     * @param interactors ArrayList of Interactors.
     * @param interactions ArrayList of Interactions.
     * @throws ValidationException Document is not valid.
     * @throws MarshalException Could not Marshal Document to XML.
     */
    protected QueryResult generateQueryResult(Collection interactors,
            Collection interactions) throws ValidationException,
            MarshalException, MapperException {
        QueryResult result = new QueryResult();
        xdebug.logMsg(this, "Creating Final PSI Document");
        PsiBuilder psiBuilder = new PsiBuilder();
        EntrySet entrySet = psiBuilder.generatePsi(interactors, interactions);
        String xml = generateXml(entrySet);
        ArrayList list = mapToInteractions(xml);
        result.setXml(xml);
        result.setInteractions(list);
        result.setEntrySet(entrySet);
        return result;
    }

    /**
     * Generates XML from Entry Set Object.
     */
    private String generateXml(EntrySet set) throws ValidationException,
            MarshalException {
        StringWriter writer = new StringWriter();
        set.marshal(writer);
        String xml = writer.toString();
        return xml;
    }

    /**
     * Maps PSI to Data Service Interaction objects.
     */
    private ArrayList mapToInteractions(String xml) throws MapperException {
        ArrayList interactions = new ArrayList();
        MapPsiToInteractions mapper = new MapPsiToInteractions(xml,
                interactions);
        mapper.doMapping();
        return interactions;
    }

    /**
     * Given a list of CPath Records, retrieve INTERACTION records only,
     * and filter out all the rest.
     * @param records ArrayList of CPath Records.
     * @return ArrayList of CPath Records.
     */
    protected ArrayList filterForInteractionsOnly(ArrayList records) {
        ArrayList interactions = new ArrayList();
        for (int i = 0; i < records.size(); i++) {
            CPathRecord record = (CPathRecord) records.get(i);
            if (record.getType() == CPathRecordType.INTERACTION) {
                interactions.add(record);
            }
        }
        return interactions;
    }
}