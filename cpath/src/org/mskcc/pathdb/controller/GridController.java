package org.mskcc.pathdb.controller;

import org.mskcc.pathdb.format.PsiFormatter;
import org.mskcc.pathdb.sql.EmptySetException;
import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.sql.GridProteinService;
import org.mskcc.pathdb.util.PropertyManager;
import org.mskcc.pathdb.xml.psi.Entry;

import java.io.StringWriter;
import java.util.ArrayList;

/**
 * GRID Controller.
 * Processes all client requests for GRID specific data.
 *
 * @author Ethan Cerami
 */
public class GridController {

    /**
     * Retrieves GRID Data.
     * @param request ProtocolRequest object.
     * @return XML Response String.
     * @throws Exception All Exceptions.
     */
    public String retrieveData(ProtocolRequest request)
            throws Exception {
        try {
            String xml = getXmlData(request);
            return xml;
        } catch (EmptySetException e) {
            throw new ProtocolException(ProtocolStatusCode.BAD_UID,
                    "UID:  " + request.getUid() + " not found in database");
        }
    }

    /**
     * Gets XML Data.
     * @param request ProtocolRequest object.
     * @return XML String data.
     * @throws Exception All Exceptions.
     */
    private String getXmlData(ProtocolRequest request)
            throws Exception {
        String xml = null;
        if (request.getCommand().equals
                (ProtocolConstants.COMMAND_RETRIEVE_INTERACTIONS)) {
            xml = getInteractions(request.getUid());
        } else if (request.getCommand().equals
                (ProtocolConstants.COMMAND_RETRIEVE_GO)) {
            xml = getGo(request.getUid());
        }
        return xml;
    }

    /**
     * Gets Interactions.
     * @param uid UID.
     * @return XML Response String.
     * @throws Exception All Exceptions.
     */
    private String getInteractions(String uid) throws Exception {
        PropertyManager manager = PropertyManager.getInstance();
        GridInteractionService service = new GridInteractionService
                (manager.getGridHost(), manager.getGridUser(),
                        manager.getGridPassword());
        ArrayList interactions =
                service.getInteractions(uid);
        PsiFormatter formatter = new PsiFormatter(interactions);
        Entry entry = formatter.getPsiXml();
        StringWriter writer = new StringWriter();
        entry.marshal(writer);
        return writer.toString();
    }

    /**
     * Get Go Terms.
     * @param uid UID
     * @return XML Response String.
     * @throws Exception All Exceptions.
     */
    private String getGo(String uid) throws Exception {
        PropertyManager manager = PropertyManager.getInstance();
        GridProteinService service = new GridProteinService
                (manager.getGridHost(), manager.getGridUser(),
                        manager.getGridPassword());
        String xml = service.getProteinXmlByOrf(uid);
        return xml;
    }
}