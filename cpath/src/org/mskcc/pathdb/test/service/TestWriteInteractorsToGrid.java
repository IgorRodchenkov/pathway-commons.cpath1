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
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.services.ReadInteractors;
import org.mskcc.dataservices.services.WriteInteractors;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.CPathConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * Tests the WriteInteractorsToGrid Service.
 *
 * @author Ethan Cerami
 */
public class TestWriteInteractorsToGrid extends TestCase {
    private static final String ORF_NAME = "JUNIT";
    private static final String DESCRIPTION = "The JUnit Test Protein";
    private static final String DB_NAME_0 = "NCBI";
    private static final String DB_NAME_1 = "SWISS_PROT";
    private static final String DB_ID_0 = "junit_1";
    private static final String DB_ID_1 = "swiss_junit_1";

    /**
     * Tests Write Facility.
     *
     * @throws Exception All Exceptions.
     */
    public void testWriteInteractors() throws Exception {
        DataServiceFactory factory = DataServiceFactory.getInstance();

        WriteInteractors service = (WriteInteractors)
                factory.getService(CPathConstants.WRITE_INTERACTORS_TO_GRID);

        ArrayList interactors = new ArrayList();

        //  This Interactor Already Exists...
        Interactor interactorA = new Interactor();
        interactorA.setName("YER006W");

        //  This is a new Test Interactor...
        Interactor interactorB = new Interactor();
        interactorB.setName(ORF_NAME);
        interactorB.setDescription(DESCRIPTION);
        ExternalReference refs[] = new ExternalReference[2];
        refs[0] = new ExternalReference(DB_NAME_0, DB_ID_0);
        refs[1] = new ExternalReference(DB_NAME_1, DB_ID_1);
        interactorB.setExternalRefs(refs);
        interactors.add(interactorA);
        interactors.add(interactorB);

        int counter = service.writeInteractors(interactors);

        //  Verify that only 1 record is saved.
        assertEquals(1, counter);

        //  Validate Interactor is actually in database.
        validateInteractor(factory);

        //  Delete Test Interactor.
        deleteJUnitInteractors();
    }

    /**
     * Verify that New Protein Now exists in Database
     */
    private void validateInteractor(DataServiceFactory factory)
            throws DataServiceException {
        ReadInteractors iService = (ReadInteractors)
                factory.getService(CPathConstants.READ_INTERACTORS_FROM_GRID);
        Interactor interactor = iService.getInteractor(ORF_NAME);
        assertEquals(ORF_NAME, interactor.getName());
        assertEquals(DESCRIPTION, interactor.getDescription());
        ExternalReference refs[] = interactor.getExternalRefs();
        assertEquals(DB_NAME_0, refs[0].getDatabase());
        assertEquals(DB_ID_0, refs[0].getId());
        assertEquals(DB_NAME_1, refs[1].getDatabase());
        assertEquals(DB_ID_1, refs[1].getId());
    }

    /**
     * Delete all JUnit Interactors.
     *
     * @throws Exception All Exceptions.
     */
    public static void deleteJUnitInteractors() throws Exception {
        Connection con = JdbcUtil.getGridConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("DELETE FROM ORF_INFO WHERE orf_name = ?");
        pstmt.setString(1, ORF_NAME);
        int rows = pstmt.executeUpdate();
    }
}