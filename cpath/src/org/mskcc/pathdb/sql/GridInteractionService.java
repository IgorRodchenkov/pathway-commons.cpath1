package org.mskcc.pathdb.sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mskcc.pathdb.model.Interaction;
import org.mskcc.pathdb.model.Protein;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Live GRID Interaction Service.
 * Connects to the GRID Database.
 * Information about GRID is available online at:
 * <A HREF="http://biodata.mshri.on.ca/grid/servlet/Index">GRID</A>.
 *
 * @author Ethan Cerami
 */
public class GridInteractionService extends GridBase {
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * GRID Protein Service.
     */
    private GridProteinService proteinService;

    /**
     * Constructor.
     * @param host Database Host Name.
     * @param user Database User.
     * @param password Password.
     */
    public GridInteractionService(String host, String user, String password) {
        super(host, user, password);
    }

    /**
     * Gets Interaction data for specified GI Number.
     * @param orfName ORF Name.
     * @return ArrayList of Interaction objects.
     * @throws SQLException Error connecting to data base.
     * @throws ClassNotFoundException Could not located JDBC Driver.
     * @throws EmptySetException Indicates No Results Found.
     */
    public ArrayList getInteractions(String orfName)
            throws SQLException, ClassNotFoundException, EmptySetException {
        log.info("Getting Interactions for:  " + orfName);
        ArrayList interactions = getLiveInteractions(orfName);
        return interactions;
    }

    /**
     * Gets Live Data from GRID.
     * @param orfName ORF Name.
     * @return ArrayList of Interactin objects.
     * @throws SQLException Error connecting to data base.
     * @throws ClassNotFoundException Could not located JDBC Driver.
     * @throws EmptySetException Indicates No Results Found.
     */
    private ArrayList getLiveInteractions(String orfName)
            throws SQLException, ClassNotFoundException, EmptySetException {
        ArrayList interactions;
        // 1.  Get Local ID for ORF Name.
        proteinService =
                new GridProteinService(getHost(), getUser(), getPassword());
        Protein protein = proteinService.getProteinByOrf(orfName);

        // 2.  Get all interactions for local ID.
        ResultSet rs = connect(protein.getLocalId());

        // 3.  Iterate through all results.
        interactions = processResults(rs);
        return interactions;
    }

    /**
     * Gets Live Interaction Data from GRID.
     * @param localId Local ID.
     * @return Database Result Set.
     * @throws java.sql.SQLException Database error.
     * @throws ClassNotFoundException Could not find JDBC Driver.
     */
    private ResultSet connect(String localId)
            throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("select * from interactions where (geneA = ? or geneB = ?) "
                + " and (deprecated='F')");
        pstmt.setString(1, localId);
        pstmt.setString(2, localId);

        log.info("Executing SQL Query:  " + pstmt.toString());
        ResultSet rs = pstmt.executeQuery();
        return rs;
    }

    /**
     * Process Results of SQL Query.
     * @param rs Database Result Set.
     * @return ArrayList of Interaction objects.
     * @throws SQLException Error connecting database.
     * @throws ClassNotFoundException Could not located JDBC Driver.
     * @throws EmptySetException Indicates No Results Found.
     */
    private ArrayList processResults(ResultSet rs)
            throws SQLException, ClassNotFoundException, EmptySetException {
        ArrayList list = new ArrayList();
        while (rs.next()) {
            Interaction interaction = new Interaction();
            String geneALocalId = rs.getString("geneA");
            String geneBLocalId = rs.getString("geneB");
            String expSystem = rs.getString("experimental_system");
            String direction = rs.getString("direction");
            String pubMedId = rs.getString("pubmed_id");
            String owner = rs.getString("owner");
            String[] pubMedIds = splitString(pubMedId);

            Protein nodeA = proteinService.getProteinByLocalId(geneALocalId);
            Protein nodeB = proteinService.getProteinByLocalId(geneBLocalId);
            interaction.setNodeA(nodeA);
            interaction.setNodeB(nodeB);
            interaction.setExperimentalSystem(expSystem);
            interaction.setDirection(direction);
            interaction.setOwner(owner);
            interaction.setPubMedIds(pubMedIds);
            list.add(interaction);
        }
        return list;
    }
}