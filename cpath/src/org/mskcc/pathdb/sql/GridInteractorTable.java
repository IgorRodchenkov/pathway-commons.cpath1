package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.services.ReadInteractors;
import org.mskcc.pathdb.util.CPathConstants;

import java.util.ArrayList;

/**
 * Misc Utility Methods for Accessing the GRID ORF/Interactor Table.
 *
 * @author Ethan Cerami
 */
public class GridInteractorTable {

    /**
     * Looks up local Ids for all Interactors.  We need localIds,
     * because localIds are used to store interactions.
     * Local IDs are stored with the attribute key:  InteractorVocab.LOCAL_ID.
     * @param interactors ArrayList of Interactors.
     * @throws DataServiceException Error Connecting to data service.
     */
    public static void getLocalInteractorIds(ArrayList interactors)
            throws DataServiceException {
        DataServiceFactory factory = DataServiceFactory.getInstance();

        for (int i = 0; i < interactors.size(); i++) {
            Interactor interactor = (Interactor) interactors.get(i);
            String name = interactor.getName();
            ReadInteractors service = (ReadInteractors) factory.getService
                    (CPathConstants.READ_INTERACTORS_FROM_GRID);
            try {
                Interactor dbInteractor = service.getInteractor(name);
                String localId = (String) dbInteractor.getAttribute
                        (InteractorVocab.LOCAL_ID);
                interactor.addAttribute(InteractorVocab.LOCAL_ID, localId);
            } catch (EmptySetException e) {
                interactor.addAttribute(InteractorVocab.LOCAL_ID, "N/A");
            }
        }
    }
}