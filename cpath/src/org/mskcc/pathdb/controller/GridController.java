package org.mskcc.pathdb.controller;

import org.mskcc.pathdb.format.PsiFormatter;
import org.mskcc.pathdb.sql.EmptySetException;
import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.test.TestConstants;
import org.mskcc.pathdb.xml.psi.Entry;

import java.io.StringWriter;
import java.util.ArrayList;

/**
 * GRID Controller.
 *
 * @author Ethan Cerami
 */
public class GridController {

    /**
     * Retrieves Interactions.
     * @param uid UID.
     * @return XML Response String.
     * @throws Exception All Exceptions.
     */
    public String retrieveInteractions(String uid) throws Exception {
        GridInteractionService service = new GridInteractionService
                (TestConstants.DB_HOST, TestConstants.USER,
                        TestConstants.PASSWORD);
        try {
            ArrayList interactions =
                    service.getInteractions(uid);
            PsiFormatter formatter = new PsiFormatter(interactions);
            Entry entry = formatter.getPsiXml();
            StringWriter writer = new StringWriter();
            entry.marshal(writer);
            return writer.toString();
        } catch (EmptySetException e) {
            throw new ProtocolException(ProtocolStatusCode.BAD_UID,
                    "UID:  " + uid + " not found in database");
        }
    }
}