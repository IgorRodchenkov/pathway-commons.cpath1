package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gets All Interactions for the specified Interactor CPath Name.
 *
 * @author Ethan Cerami
 */
public class GetInteractionsByInteractorName extends PsiInteractionQuery {
    private String interactorName;

    /**
     * Constructor.
     * @param interactorName Unique Interactor Name.
     */
    public GetInteractionsByInteractorName(String interactorName) {
        this.interactorName = interactorName;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        DaoCPath cpath = new DaoCPath();
        CPathRecord record = cpath.getRecordByName(interactorName);
        if (record != null) {
            ArrayList interactions = this.extractInteractions(record);
            HashMap interactors = this.extractInteractors(interactions);
            createPsi(interactors.values(), interactions);
        }
    }
}
