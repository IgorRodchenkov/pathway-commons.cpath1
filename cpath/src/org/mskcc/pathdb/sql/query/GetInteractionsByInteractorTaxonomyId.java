package org.mskcc.pathdb.sql.query;

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
    private int maxHits;

    /**
     * Constructor.
     * @param taxonomyId NCBI Taxonomy ID.
     * @param maxHits Maximum Number of Hits.
     */
    public GetInteractionsByInteractorTaxonomyId(int taxonomyId, int maxHits) {
        this.taxonomyId = taxonomyId;
        this.maxHits = maxHits;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        xdebug.logMsg(this, "Getting Interactions for all interactors with "
                + " NCBI Taxonomy ID:  " + taxonomyId);
        DaoCPath cpath = new DaoCPath();
        ArrayList records = cpath.getRecordByTaxonomyID
                (CPathRecordType.PHYSICAL_ENTITY, taxonomyId);
        xdebug.logMsg(this, "Total Number of Interactors Found:  "
                + records.size());
        records = truncateResultSet(records, maxHits);
        if (records.size() > 0) {
            ArrayList interactions = this.extractInteractions(records);
            HashMap interactorMap = this.extractInteractors(interactions);
            createPsi(interactorMap.values(), interactions);
        }
    }
}
