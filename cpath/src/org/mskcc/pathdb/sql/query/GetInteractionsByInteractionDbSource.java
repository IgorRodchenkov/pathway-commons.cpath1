package org.mskcc.pathdb.sql.query;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.core.EmptySetException;
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

    /**
     * Constructor.
     * @param db Database Source.
     */
    public GetInteractionsByInteractionDbSource(String db) {
        this.db = db;
    }

    /**
     * Executes Query.
     * @throws Exception All Exceptions.
     */
    protected void executeSub() throws Exception {
        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference ref = new ExternalReference(db, null);
        System.out.println("START:  Look up by External Ref");
        ArrayList records = linker.lookUpByExternalRef(ref);
        System.out.println("STOP:  Look up by External Ref");
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