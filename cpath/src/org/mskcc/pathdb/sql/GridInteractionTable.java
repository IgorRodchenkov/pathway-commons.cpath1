package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.protocol.GridProtocol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Misc Utility Classes for Accessing the GRID Interaction Table.
 *
 * @author Ethan Cerami
 */
public class GridInteractionTable {

    /**
     * Does the specified Interaction Exist?
     * Interaction Identity is currently very simple.
     * If an interaction between A <--> B or B <--> A exists,
     * then we consider that the interaction already exists.
     * This may not be sufficient for future use cases, but for me,
     * it simplifies the work.
     * @param interaction Interaction Object.
     * @return true or false.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating JDBC Driver.
     * @throws DataServiceException Error Connecting to data source.
     */
    public static boolean interactionExists(Interaction interaction)
            throws SQLException, ClassNotFoundException, DataServiceException {
        int id = getInteractionId(interaction);
        return (id >= 0) ? true : false;
    }

    /**
     * Gets Local ID of Specified Interaction.
     * @param interaction Interaction Object.
     * @return Local ID
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error locating Database Driver.
     * @throws DataServiceException Error Connecting to Data Service.
     */
    public static int getInteractionId(Interaction interaction)
            throws SQLException, ClassNotFoundException, DataServiceException {
        int id = -1;
        ArrayList interactors = interaction.getInteractors();
        GridInteractorTable.getLocalInteractorIds(interactors);
        Interactor interactor0 = (Interactor) interactors.get(0);
        Interactor interactor1 = (Interactor) interactors.get(1);
        String localId0 = (String) interactor0.getAttribute
                (InteractorVocab.LOCAL_ID);
        String localId1 = (String) interactor1.getAttribute
                (InteractorVocab.LOCAL_ID);

        Connection con = GridProtocol.getConnection("localhost");
        PreparedStatement pstmt = con.prepareStatement
                ("select interaction_id from interactions where "
                + "(geneA = ? and geneB = ?) "
                + " or (geneA = ? and geneB= ?) and (deprecated='F')");
        pstmt.setString(1, localId0);
        pstmt.setString(2, localId1);
        pstmt.setString(3, localId1);
        pstmt.setString(4, localId0);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            id = rs.getInt("interaction_id");
        }
        return id;
    }
}