package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gets All Interactions for the specified Interactor CPath ID.
 *
 * @author Ethan Cerami
 */
class GetInteractionsById extends PsiInteractionQuery {
    private long id;

    /**
     * Constructor.
     * @param cpathId CPath ID.
     */
    public GetInteractionsById(long cpathId) {
        this.id = cpathId;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected QueryResult executeSub() throws Exception {
        QueryResult result = new QueryResult();
        DaoCPath cpath = new DaoCPath();
        CPathRecord record = cpath.getRecordById(id);
        ArrayList interactions = new ArrayList();
        if (record != null) {
            if (record.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
                interactions = this.extractInteractions(record);
            } else if (record.getType().equals(CPathRecordType.INTERACTION)) {
                interactions.add(record);
            }
            if (interactions != null) {
                HashMap interactors = this.extractInteractors(interactions);
                result = generateQueryResult (interactors.values(),
                        interactions);
            }
        }
        return result;
    }
}