package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;

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
     * Does the specified Interaction Exist?
     * See note regarding interaction identity above.
     * @param interaction Interaction Object.
     * @param dbLocation Database Location.
     * @return true or false.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     * @throws DataServiceException Error Connecting to data source.
     */
    public static boolean interactionExists(Interaction interaction,
            String dbLocation)
            throws SQLException, ClassNotFoundException, DataServiceException {
        int id = getInteractionId(interaction, dbLocation);
        return (id >= 0) ? true : false;
    }

    /**
     * Gets Local ID of Specified Interaction.
     * @param interaction Interaction Object.
     * @param dbLocation Database Location.
     * @return Local ID
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error locating Database Driver.
     * @throws DataServiceException Error Connecting to Data Service.
     */
    public static int getInteractionId(Interaction interaction,
            String dbLocation)
            throws SQLException, ClassNotFoundException, DataServiceException {
        HashMap localIdMap = null;
        int id = -1;
        ArrayList interactors = interaction.getInteractors();

        try {
            localIdMap = DaoInteractor.getLocalInteractorIds(interactors);
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

        Connection con = JdbcUtil.getGridConnection();
        PreparedStatement pstmt = con.prepareStatement
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
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            id = rs.getInt("interaction_id");
        }
        return id;
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