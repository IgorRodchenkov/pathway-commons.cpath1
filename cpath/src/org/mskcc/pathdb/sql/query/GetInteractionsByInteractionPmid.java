package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gets All Interactions with the specified PMID.
 * For example, this query enables you to retrieve all interactions
 * defined by or reported in a specific journal article.
 *
 * @author Ethan Cerami
 */
public class GetInteractionsByInteractionPmid extends PsiInteractionQuery {
    private String pmid;

    /**
     * Constructor.
     * @param pmid PMID.
     */
    public GetInteractionsByInteractionPmid(String pmid) {
        this.pmid = pmid;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference ref = new ExternalReference("PMID", pmid);
        ArrayList records = linker.lookUpByExternalRef(ref);

        //  Filter for Interactions Only.
        ArrayList interactions = filterForInteractionsOnly(records);

        if (interactions.size() > 0) {
            HashMap interactors = this.extractInteractors(interactions);
            createPsi(interactors.values(), interactions);
        } else {
            throw new EmptySetException();
        }
    }
}