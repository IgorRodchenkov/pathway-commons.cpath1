package org.mskcc.pathdb.sql.dao;

import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data Access Object for the GRID Interaction Table.
 *
 * Interaction Identity is based on three rules:
 * 1.  A <--> B or B <--> A
 * 2.  Exerimental System must be identical.
 * 3.  PMID must be identical.
 *
 * @author Ethan Cerami
 */
public class DaoInteraction {
    /**
     * Determines if the interaction exists.
     * See note regarding interaction identity above.
     * @param interaction Interaction Object.
     * @param dbLocation Database Location.
     * @return true or false.
     * @throws DaoException Error Retrieving Data.
     * @throws DataServiceException Error Connecting to data source.
     */
    public boolean interactionExists(Interaction interaction,
            String dbLocation) throws DaoException, DataServiceException {
        int id = getInteractionId(interaction, dbLocation);
        return (id >= 0) ? true : false;
    }

    /**
     * Gets Local ID of Specified Interaction.
     * @param interaction Interaction Object.
     * @param dbLocation Database Location.
     * @return Local ID
     * @throws DaoException Error Retrieving Data.
     * @throws DataServiceException Error Connecting to Data Service.
     */
    public int getInteractionId(Interaction interaction,
            String dbLocation) throws DaoException, DataServiceException {
        HashMap localIdMap = null;
        int id = -1;
        ArrayList interactors = interaction.getInteractors();

        try {
            DaoInteractor daoInteractor = new DaoInteractor();
            localIdMap = daoInteractor.getLocalInteractorIds(interactors);
        } catch (EmptySetException e) {
            return -1;
        }

        Interactor interactor0 = (Interactor) interactors.get(0);
        Interactor interactor1 = (Interactor) interactors.get(1);
        String localId0 = (String) localIdMap.get(interactor0.getName());
        String localId1 = (String) localIdMap.get(interactor1.getName());

        String expSystem = (String) interaction.getAttribute
                (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
        String pmid = getPmids(interaction);

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getGridConnection();
            pstmt = con.prepareStatement
                    ("select interaction_id from interactions where "
                    + "((geneA = ? and geneB = ?) "
                    + " or (geneA = ? and geneB= ?)) and (deprecated='F'"
                    + " and experimental_system = ? and pubmed_id = ?)");
            pstmt.setString(1, localId0);
            pstmt.setString(2, localId1);
            pstmt.setString(3, localId1);
            pstmt.setString(4, localId0);
            pstmt.setString(5, expSystem);
            pstmt.setString(6, pmid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("interaction_id");
            }
            return id;
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
     * Extracts PubMedIds.
     * @param interaction Interaction Object.
     * @return semicolon delimited list of PMIDs.
     */
    public static String getPmids(Interaction interaction) {
        String pmids[] = null;
        Object object = interaction.getAttribute(InteractionVocab.PUB_MED_ID);
        if (object instanceof String) {
            pmids = new String[1];
            pmids[0] = (String) object;
        } else if (object instanceof String[]) {
            pmids = (String[]) object;
        }
        StringBuffer pmidStr = new StringBuffer();
        if (pmids != null && pmids.length > 0) {
            pmidStr.append(";");
            for (int i = 0; i < pmids.length; i++) {
                pmidStr.append(pmids[i] + ";");
            }
        }
        return pmidStr.toString();
    }
}