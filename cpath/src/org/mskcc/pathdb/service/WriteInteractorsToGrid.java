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
package org.mskcc.pathdb.service;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.live.DataServiceBase;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.protocol.GridProtocol;
import org.mskcc.dataservices.services.ReadInteractors;
import org.mskcc.dataservices.services.WriteInteractors;
import org.mskcc.pathdb.util.CPathConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Conditionally Saves Interactors to the GRID ORF_INFO Table.
 * If Interactor already exists within database, no action is taken.
 * Otherwise, new Interactor is saved to database.
 *
 * @author Ethan Cerami
 */
public class WriteInteractorsToGrid extends DataServiceBase
        implements WriteInteractors {

    /**
     * Conditionally saves all specified Interactors to the GRID Database.
     * @param interactors ArrayList of Interactors.
     * @return Number of Interactors Saved.
     * @throws DataServiceException Error connecting to data service.
     */
    public int writeInteractors(ArrayList interactors)
            throws DataServiceException {
        int counter = 0;
        try {
            for (int i = 0; i < interactors.size(); i++) {
                Interactor interactor = (Interactor) interactors.get(i);
                boolean interactorExists = this.interactorExists
                        (interactor.getName());
                if (!interactorExists) {
                    counter += saveInteractor(interactor);
                }
            }
        } catch (Exception e) {
            throw new DataServiceException(e);
        }
        return counter;
    }

    /**
     * Gets Server Response.
     * @return Server Response Message.
     */
    public String getServerResponse() {
        return null;
    }

    /**
     * Saves Specified Interactor.
     * Currently saves name, description and external references.
     * @param interactor Interactor Object.
     * @return Number of Rows of Data added.
     * @throws java.sql.SQLException Error Connecting to Database.
     * @throws java.lang.ClassNotFoundException Error locating database driver.
     */
    private int saveInteractor(Interactor interactor)
            throws SQLException, ClassNotFoundException {
        String name = interactor.getName();
        String desc = interactor.getDescription();
        if (desc == null) {
            desc = "No description available";
        }
        StringBuffer externalNames = new StringBuffer(";");
        StringBuffer externalIds = new StringBuffer(";");
        ExternalReference refs[] = interactor.getExternalRefs();
        if (refs != null) {
            for (int i = 0; i < refs.length; i++) {
                ExternalReference ref = refs[i];
                String dbName = ref.getDatabase();
                String dbId = ref.getId();
                externalNames.append(dbName + ";");
                externalIds.append(dbId + ";");
            }
        }
        Connection connection = GridProtocol.getConnection(this.getLocation());
        PreparedStatement pstmt = connection.prepareStatement
                ("INSERT INTO orf_info (orf_name, description, "
                + "external_names, external_ids, status) "
                + "VALUES (?, ?, ?, ?, ?)");
        pstmt.setString(1, name);
        pstmt.setString(2, desc);
        pstmt.setString(3, externalNames.toString());
        pstmt.setString(4, externalIds.toString());
        pstmt.setString(5, "NEW");
        int rows = pstmt.executeUpdate();
        return rows;
    }

    /**
     * Checks to see if Interactor already exists within the database.
     * @param orfName ORF Name.
     * @return true or false
     * @throws DataServiceException Error Connecting to GRID.
     */
    private boolean interactorExists(String orfName)
            throws DataServiceException {
        DataServiceFactory factory = DataServiceFactory.getInstance();
        try {
            ReadInteractors service = (ReadInteractors) factory.getService
                    (CPathConstants.READ_INTERACTORS_FROM_GRID);
            Interactor knownInteractor = service.getInteractor(orfName);
        } catch (EmptySetException e) {
            return false;
        }
        return true;
    }
}