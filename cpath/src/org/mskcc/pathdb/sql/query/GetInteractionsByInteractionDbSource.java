package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gets All Interactions with the specified database source.
 * For example, this query enables you to retrieve all interactions from DIP.
 *
 * @author Ethan Cerami
 */
public class GetInteractionsByInteractionDbSource extends PsiInteractionQuery {
    private String db;
    private int maxHits;

    /**
     * Constructor.
     * @param db Database Source.
     * @param maxHits Max Number of Hits.
     */
    public GetInteractionsByInteractionDbSource(String db, int maxHits) {
        this.db = db;
        this.maxHits = maxHits;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        xdebug.logMsg(this, "Getting Interactions with database source:  "
                + db);
        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference ref = new ExternalReference(db, null);
        ArrayList records = linker.lookUpByExternalRef(ref);

        //  Filter for Interactions Only.
        ArrayList interactions = filterForInteractionsOnly(records);

        xdebug.logMsg(this, "Total Number of Interactions Found:  "
                + interactions.size());

        interactions = truncateResultSet(interactions, maxHits);

        if (interactions.size() > 0) {
            HashMap interactors = this.extractInteractors(interactions);
            createPsi(interactors.values(), interactions);
        }
    }
}