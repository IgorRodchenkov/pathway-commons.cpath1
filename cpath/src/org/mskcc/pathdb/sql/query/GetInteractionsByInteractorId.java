package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gets All Interactions for the specified Interactor CPath ID.
 *
 * @author Ethan Cerami
 */
public class GetInteractionsByInteractorId extends PsiInteractionQuery {
    private long interactorID;

    /**
     * Constructor.
     * @param cpathId CPath ID.
     */
    public GetInteractionsByInteractorId(long cpathId) {
        this.interactorID = cpathId;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        DaoCPath cpath = new DaoCPath();
        CPathRecord record = cpath.getRecordById(interactorID);
        if (record != null) {
            ArrayList interactions = this.extractInteractions(record);
            HashMap interactors = this.extractInteractors(interactions);
            createPsi(interactors.values(), interactions);
        }
    }
}