package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gets All Interactions for the specified Interactor NCBI Taxonomy ID.
 * For example, this query enables you to retrieve all YEAST interactions.
 *
 * @author Ethan Cerami
 */
public class GetInteractionsByInteractorTaxonomyId extends PsiInteractionQuery {
    private int taxonomyId;

    /**
     * Constructor.
     * @param taxonomyId NCBI Taxonomy ID.
     */
    public GetInteractionsByInteractorTaxonomyId(int taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        DaoCPath cpath = new DaoCPath();
        CPathRecord record = cpath.getRecordByTaxonomyID
                (CPathRecordType.PHYSICAL_ENTITY, taxonomyId);
        if (record != null) {
            ArrayList interactions = this.extractInteractions(record);
            HashMap interactors = this.extractInteractors(interactions);
            createPsi(interactors.values(), interactions);
        } else {
            throw new EmptySetException();
        }
    }
}
