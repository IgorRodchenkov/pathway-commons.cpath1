package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.bio.ExternalReference;
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
    private int maxHits;

    /**
     * Constructor.
     * @param pmid PMID.
     * @param maxHits Max Hits.
     */
    public GetInteractionsByInteractionPmid(String pmid, int maxHits) {
        this.pmid = pmid;
        this.maxHits = maxHits;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        xdebug.logMsg(this, "Getting Interactions for PMID:  " + pmid);
        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference ref = new ExternalReference("PMID", pmid);
        ArrayList records = linker.lookUpByExternalRef(ref);

        //  Filter for Interactions Only.
        ArrayList interactions = filterForInteractionsOnly(records);

        xdebug.logMsg(this, "Getting Interactions for PMID:  " + pmid);

        xdebug.logMsg(this, "Total Number of Interactions Found:  "
                + interactions.size());

        interactions = truncateResultSet(records, maxHits);

        if (interactions.size() > 0) {
            HashMap interactors = this.extractInteractors(interactions);
            createPsi(interactors.values(), interactions);
        }
    }
}