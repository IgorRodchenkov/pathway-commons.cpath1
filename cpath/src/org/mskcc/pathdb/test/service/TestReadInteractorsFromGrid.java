/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.test.service;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.GoVocab;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.services.ReadInteractors;
import org.mskcc.pathdb.service.ReadInteractorsFromGrid;
import org.mskcc.pathdb.util.CPathConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tests the ReadInteractorsFromGrid Service Class.
 *
 * @author Ethan Cerami
 */
public class TestReadInteractorsFromGrid extends TestCase {
    /**
     * Sample GRID_LOCAL Local ID.
     */
    private static final String SAMPLE_LOCAL_ID = "1662";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_GENE_NAME = "NUG1";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_DESCRIPTION = "NUclear GTPase";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_PROCESS_ID_0 = "0000004";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_PROCESS_NAME_0 =
            "biological_process unknown";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_COMPONENT_ID_0 = "0005634";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_COMPONENT_ID_1 = "0005730";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_COMPONENT_NAME_0 = "nucleus";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_COMPONENT_NAME_1 = "nucleolus";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_FUNCTION_ID_0 = "0005554";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_FUNCTION_NAME_0 =
            "molecular_function unknown";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_SPECIAL_ID_0 = "0000004";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_SPECIAL_NAME_0 =
            "biological_process unknown";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_EXTERNAL_REF_ID_0 = "603598";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_EXTERNAL_REF_NAME_0 = "Entrez GI";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_EXTERNAL_REF_ID_10 = "YER006W";

    /**
     * Expected Value (may need to be updated to reflect changes in GRID_LOCAL.)
     */
    private static final String EXPECTED_EXTERNAL_REF_NAME_10 = "MIPS";

    /**
     * Tests against live GRID_LOCAL Service.
     * @throws Exception Indicates Error
     */
    public void testSqlGridService() throws Exception {
        ReadInteractorsFromGrid service = new ReadInteractorsFromGrid();
        service.setLocation("localhost");
        Interactor interactor = service.getInteractor("YER006W");
        validateNameIdDescription(interactor);
        validateGoTerms(interactor);
        validateExternalReferences(interactor);

        interactor = service.getInteractorByLocalId(SAMPLE_LOCAL_ID);
        validateNameIdDescription(interactor);
    }

    /**
     * Tests against live CBio Service.
     * @throws Exception Indicates Error.
     */
    public void testCbioGridService() throws Exception {
        try {
            DataServiceFactory factory = DataServiceFactory.getInstance();
            ReadInteractors service = (ReadInteractors)
                    factory.getService
                    (CPathConstants.READ_INTERACTORS_FROM_GRID);
            Interactor interactor =
                    service.getInteractor("YER006W");
            validateNameIdDescription(interactor);
            validateGoTerms(interactor);
            validateExternalReferences(interactor);
        } catch (DataServiceException e) {
            Throwable t = e.getCause();
            t.printStackTrace();
        }
    }

    /**
     * Validates Name, ID and Description.
     * @param interactor Interactor object.
     */
    private void validateNameIdDescription(Interactor interactor) {
        //  Validate Gene Name
        ArrayList geneNameList = (ArrayList) interactor.getAttribute
                (InteractorVocab.GENE_NAME);
        assertEquals(EXPECTED_GENE_NAME, (String) geneNameList.get(0));

        //  Validate Description
        String description = interactor.getDescription();
        assertEquals(EXPECTED_DESCRIPTION, description);
    }

    /**
     * Validates all GO Term Categories.
     * @param interactor Interactor object.
     */
    private void validateGoTerms(Interactor interactor) {

        //  Validate GO Process Terms.
        ArrayList goProcess =
                (ArrayList) interactor.getAttribute
                (GoVocab.GO_CATEGORY_PROCESS);
        HashMap goTerm0 = (HashMap) goProcess.get(0);
        assertEquals(EXPECTED_PROCESS_ID_0, goTerm0.get(GoVocab.GO_ID));
        assertEquals(EXPECTED_PROCESS_NAME_0, goTerm0.get(GoVocab.GO_NAME));

        //  Validate GO Component Terms.
        ArrayList goComponent = (ArrayList) interactor.getAttribute
                (GoVocab.GO_CATEGORY_COMPONENT);
        goTerm0 = (HashMap) goComponent.get(0);
        assertEquals(EXPECTED_COMPONENT_ID_0, goTerm0.get(GoVocab.GO_ID));
        assertEquals(EXPECTED_COMPONENT_NAME_0, goTerm0.get(GoVocab.GO_NAME));
        HashMap goTerm1 = (HashMap) goComponent.get(1);
        assertEquals(EXPECTED_COMPONENT_ID_1, goTerm1.get(GoVocab.GO_ID));
        assertEquals(EXPECTED_COMPONENT_NAME_1, goTerm1.get(GoVocab.GO_NAME));

        //  Validate GO Function Terms.
        ArrayList goFunction = (ArrayList) interactor.getAttribute
                (GoVocab.GO_CATEGORY_FUNCTION);
        goTerm0 = (HashMap) goFunction.get(0);
        assertEquals(EXPECTED_FUNCTION_ID_0, goTerm0.get(GoVocab.GO_ID));
        assertEquals(EXPECTED_FUNCTION_NAME_0, goTerm0.get(GoVocab.GO_NAME));

        //  Validate GO Service Terms.
        ArrayList goSpecial = (ArrayList) interactor.getAttribute
                (GoVocab.GO_CATEGORY_SPECIAL);
        goTerm0 = (HashMap) goSpecial.get(0);
        assertEquals(EXPECTED_SPECIAL_ID_0, goTerm0.get(GoVocab.GO_ID));
        assertEquals(EXPECTED_SPECIAL_NAME_0, goTerm0.get(GoVocab.GO_NAME));
    }

    /**
     * Validates External References.
     * @param interactor Interactor object.
     */
    private void validateExternalReferences(Interactor interactor) {
        ExternalReference refs[] = interactor.getExternalRefs();
        assertEquals(EXPECTED_EXTERNAL_REF_ID_0, refs[0].getId());
        assertEquals(EXPECTED_EXTERNAL_REF_NAME_0, refs[0].getDatabase());
        assertEquals(EXPECTED_EXTERNAL_REF_ID_10, refs[10].getId());
        assertEquals(EXPECTED_EXTERNAL_REF_NAME_10, refs[10].getDatabase());
    }
}