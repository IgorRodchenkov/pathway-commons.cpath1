package org.mskcc.pathdb.sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mskcc.pathdb.model.ExternalReference;
import org.mskcc.pathdb.model.GoBundle;
import org.mskcc.pathdb.model.GoTerm;
import org.mskcc.pathdb.model.Protein;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Live GRID Protein Service.
 * Connects to the GRID Database.
 * Information about GRID is available online at:
 * <A HREF="http://biodata.mshri.on.ca/grid/servlet/Index">GRID</A>.
 *
 * @author Ethan Cerami
 */
public class GridProteinService extends GridBase {

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     * @param host Database Host Name.
     * @param user Database User Name.
     * @param password Database Password.
     */
    public GridProteinService(String host, String user, String password) {
        super(host, user, password);
    }

    /**
     * Gets Protein object for specified ORF.
     * @param orfName ORF Name.
     * @return Protein object.
     * @throws SQLException Database error.
     * @throws ClassNotFoundException Could not find JDBC Driver.
     * @throws EmptySetException Indicates No Results Found.
     */
    public Protein getProteinByOrf(String orfName)
            throws SQLException, ClassNotFoundException, EmptySetException {
        log.info("Retrieving Protein, using ORFName:  " + orfName);
        Protein protein = getLiveProtein(orfName, GridBase.KEY_ORF);
        return protein;
    }

    /**
     * Gets Protein object for specified Local ID.
     * @param localId Unique Local ID.
     * @return Protein object.
     * @throws SQLException Database error.
     * @throws ClassNotFoundException Could not find JDBC Driver.
     * @throws EmptySetException Indicates No Results Found.
     */
    public Protein getProteinByLocalId(String localId)
            throws SQLException, ClassNotFoundException, EmptySetException {
        log.info("Retrieving Protein, using ORFName:  " + localId);
        Protein protein = getLiveProtein(localId, GridBase.KEY_LOCAL_ID);
        return protein;
    }

    /**
     * Gets Live Protein from GRID, and places in Local Cache.
     * @param uid Unique ID.
     * @param lookUpKey Database LookUp Key.
     * @return Protein objct.
     * @throws SQLException Database error.
     * @throws ClassNotFoundException Could not find JDBC Driver.
     * @throws EmptySetException Indicates No Results Found.
     */
    private Protein getLiveProtein(String uid, String lookUpKey)
            throws SQLException, ClassNotFoundException,
            EmptySetException {
        Protein protein = new Protein();
        ResultSet rs = this.connect(uid, lookUpKey);
        getBasicInformation(rs, protein);
        getGoTerms(rs, protein);
        getExternalRefs(rs, protein);
        return protein;
    }

    /**
     * Gets Live ORF Data from GRID.
     * @param uid Unique ID.
     * @param lookUpKey Database LookUp Key.
     * @return Database Result Set.
     * @throws SQLException Database error.
     * @throws ClassNotFoundException Could not find JDBC Driver.
     * @throws EmptySetException Indicates No Results Found.
     */
    private ResultSet connect(String uid, String lookUpKey)
            throws SQLException, ClassNotFoundException,
            EmptySetException {
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("select * from orf_info where " + lookUpKey + "=?");
        pstmt.setString(1, uid);
        log.info("Executing SQL Query:  " + pstmt.toString());
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        int numRows = rs.getRow();
        if (numRows == 0) {
            throw new EmptySetException ("No results found for id");
        }
        return rs;
    }

    /**
     * Gets Basic information regarding protein.
     * @param rs Database Result Set.
     * @param protein Protein object.
     * @throws SQLException Error Connecting to database.
     */
    private void getBasicInformation(ResultSet rs, Protein protein)
            throws SQLException {
        String orfName = rs.getString("orf_name");
        String localId = rs.getString("id");
        String geneNameList = rs.getString("gene_names");
        String geneNames[] = splitString(geneNameList);
        String description = rs.getString("description");
        protein.setOrfName(orfName);
        protein.setLocalId(localId);
        protein.setGeneNames(geneNames);
        protein.setDescription(description);
    }

    /**
     * Gets the GO (Gene Ontology) Terms.
     * @param rs Database Result Set.
     * @param protein Protein object.
     * @throws SQLException Error Connecting to database.
     */
    private void getGoTerms(ResultSet rs, Protein protein)
            throws SQLException {
        GoBundle goBundle = new GoBundle();
        goBundle.setGoFunctions(getGoTerms(rs, "function"));
        goBundle.setGoProcesses(getGoTerms(rs, "process"));
        goBundle.setGoComponents(getGoTerms(rs, "component"));
        goBundle.setGoSpecial(getGoTerms(rs, "special"));
        protein.setGoBundle(goBundle);
    }

    /**
     * Gets the list of External References.
     * @param rs Database Result Set.
     * @param protein Protein object.
     * @throws SQLException Error Connecting to database.
     */
    private void getExternalRefs(ResultSet rs, Protein protein)
            throws SQLException {
        ExternalReference refs[];
        String dbIds = rs.getString("external_ids");
        String dbNames = rs.getString("external_names");
        String idArray[] = splitString(dbIds);
        String nameArray[] = splitString(dbNames);
        refs = new ExternalReference[idArray.length];
        for (int i = 0; i < idArray.length; i++) {
            refs[i] = new ExternalReference(nameArray[i], idArray[i]);
        }
        protein.setExternalRefs(refs);
    }

    /**
     * Gets GO Terms.
     * @param rs Database Result Set.
     * @param columnPrefix Column Prefix, for example "function".
     * @return Array of GoTerm objects.
     * @throws SQLException Database error.
     */
    private GoTerm[] getGoTerms(ResultSet rs, String columnPrefix)
            throws SQLException {
        String goIds = rs.getString(columnPrefix + "_ids");
        String goNames = rs.getString(columnPrefix + "_names");
        String[] idArray = splitGoIds(goIds);
        String[] nameArray = splitString(goNames);
        GoTerm goTerms[] = new GoTerm[idArray.length];
        for (int i = 0; i < goTerms.length; i++) {
            goTerms[i] = new GoTerm(idArray[i], nameArray[i]);
        }
        return goTerms;
    }

    /**
     * Splits and Massages GO Ids.
     * @param idStr String with multiple GoIDs.
     * @return Array of String Objects.
     */
    private String[] splitGoIds(String idStr) {
        String ids[] = splitString(idStr);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].startsWith(GridBase.GO_PREFIX)) {
                ids[i] = ids[i].substring(GridBase.GO_PREFIX.length());
            }
        }
        return ids;
    }
}