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

import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.core.CacheOptions;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.live.DataServiceBase;
import org.mskcc.dataservices.protocol.GridProtocol;
import org.mskcc.dataservices.services.ReadInteractions;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.dao.DaoException;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Reads Interactions from a Local GRID Database.
 * Information about GRID_LOCAL is available online at:
 * <A HREF="http://biodata.mshri.on.ca/grid/servlet/Index">Grid</A>.
 *
 * @author Ethan Cerami
 */
public class ReadInteractionsFromGrid extends DataServiceBase
        implements ReadInteractions {

    /**
     * Cache Options.
     */
    private CacheOptions cacheOptions;

    /**
     * GRID_LOCAL Interactor Service.
     */
    private ReadInteractorsFromGrid interactorService;

    /**
     * Gets Interaction data for specified GI Number.
     * @param orfName ORF Name.
     * @return ArrayList of Interaction objects.
     * @throws DataServiceException Error connecting to data service.
     */
    public ArrayList getInteractions(String orfName)
            throws DataServiceException {
        try {
            ArrayList interactions = getLiveInteractions(orfName);
            return interactions;
        } catch (EmptySetException e) {
            throw e;
        } catch (DaoException e) {
            throw new DataServiceException(e);
        }
    }

    /**
     * Method Not Supported.
     * @param url URL Object.
     * @return no data is returned.
     * @throws org.mskcc.dataservices.core.DataServiceException Always thrown.
     */
    public ArrayList getInteractions(URL url) throws DataServiceException {
        throw new DataServiceException("Method is not supported");
    }

    /**
     * Method Not Supported.
     * @param file File Object.
     * @return no data is returned.
     * @throws org.mskcc.dataservices.core.DataServiceException Always thrown.
     */
    public ArrayList getInteractions(File file) throws DataServiceException {
        throw new DataServiceException("Method is not supported");
    }

    /**
     * Gets Live Data from GRID_LOCAL.
     * @param orfName ORF Name.
     * @return ArrayList of Interactin objects.
     */
    private ArrayList getLiveInteractions(String orfName)
            throws DataServiceException, DaoException {
        ArrayList interactions = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            // 1.  Get Local ID for ORF Name.
            interactorService =
                    new ReadInteractorsFromGrid();
            interactorService.setLocation(this.getLocation());
            Interactor interactor = interactorService.getInteractor(orfName);

            if (interactor != null) {

                // 2.  Get all interactions for local ID.
                String localId = (String) interactor.getAttribute
                        (InteractorVocab.LOCAL_ID);
                rs = connect(localId);

                // 3.  Iterate through all results.
                interactions = processResults(rs);
            }
            return interactions;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Live Interaction Data from GRID_LOCAL.
     * @param localId Local ID.
     * @return Database Result Set.
     * @throws DaoException Error Retrieving Data.
     */
    private ResultSet connect(String localId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getGridConnection();
            pstmt = con.prepareStatement
                    ("select * from interactions where "
                    + "(geneA = ? or geneB = ?) "
                    + " and (deprecated='F') ORDER BY interaction_id");
            pstmt.setString(1, localId);
            pstmt.setString(2, localId);
            rs = pstmt.executeQuery();
            return rs;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Process Results of GRID_LOCAL Query.
     * @param rs Database Result Set.
     * @return ArrayList of Interaction objects.
     * @throws java.sql.SQLException Error connecting database.
     * @throws DataServiceException Error connecting to data service.
     */
    private ArrayList processResults(ResultSet rs)
            throws SQLException, DataServiceException {
        ArrayList list = new ArrayList();
        while (rs.next()) {
            Interaction interaction = new Interaction();
            String geneALocalId = rs.getString("geneA");
            String geneBLocalId = rs.getString("geneB");
            String expSystem = rs.getString("experimental_system");
            String direction = rs.getString("direction");
            String pubMedId = rs.getString("pubmed_id");
            String owner = rs.getString("owner");
            String[] pubMedIds = GridProtocol.splitString(pubMedId);

            Interactor nodeA = interactorService.getInteractorByLocalId
                    (geneALocalId);
            Interactor nodeB = interactorService.getInteractorByLocalId
                    (geneBLocalId);
            ArrayList interactors = new ArrayList();
            interactors.add(nodeA);
            interactors.add(nodeB);
            interaction.setInteractors(interactors);
            interaction.addAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME,
                    expSystem);
            interaction.addAttribute(InteractionVocab.DIRECTION,
                    direction);
            interaction.addAttribute(InteractionVocab.OWNER, owner);
            if (pubMedIds.length > 0) {
                interaction.addAttribute(InteractionVocab.PUB_MED_ID,
                        pubMedIds[0]);
            }
            list.add(interaction);
        }
        rs.close();
        return list;
    }
}