package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.model.Organism;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object to the Organism Table.
 *
 * @author Ethan Cerami.
 */
public class DaoOrganism {

    /**
     * Adds New Organism Record to Database.
     *
     * @param taxonomyId  NCBI Taxonomy ID.
     * @param speciesName Species Name.
     * @param commonName  Common Name.
     * @throws DaoException Error Connecting to Database.
     */
    public synchronized void addRecord(int taxonomyId, String speciesName,
            String commonName) throws DaoException {
        if (speciesName == null) {
            throw new IllegalArgumentException ("Species Name is null");
        }
        if (commonName == null) {
            commonName = new String("");
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO organism (`ncbi_taxonomy_id`, " +
                    "`species_name`, `common_name`)"
                    + " VALUES (?,?,?)");
            pstmt.setInt(1, taxonomyId);
            pstmt.setString(2, speciesName);
            pstmt.setString(3, commonName);
            pstmt.executeUpdate();
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
     * Gets All Organisms in Database.
     * @return ArrayList of Organism Objects.
     * @throws DaoException Error Connecting to Database.
     */
    public ArrayList getAllOrganisms() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList organisms = new ArrayList();
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from organism order by species_name");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int taxonomyId = rs.getInt("ncbi_taxonomy_id");
                String speciesName = rs.getString("species_name");
                String commonName = rs.getString("common_name");
                Organism organism = new Organism(taxonomyId,
                        speciesName, commonName);
                organisms.add(organism);
            }
            return organisms;
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
     * Checks if Record Already Exists in Database.
     * @param taxonomyId NCBI Taxonom ID.
     * @return true or false.
     * @throws DaoException Error Connecting to Database.
     */
    public boolean recordExists(int taxonomyId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select species_name from organism where "
                    + "ncbi_taxonomy_id = ?");
            pstmt.setInt(1, taxonomyId);
            rs = pstmt.executeQuery();
            if (rs.next() == true) {
                return true;
            } else {
                return false;
            }
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
     * Deletes cPath Record with the specified CPATH_ID.
     * This will also delete all external links associated with this record.
     * @param taxonomyId NCBI Taxonomy ID
     * @return returns true if deletion was successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecord (int taxonomyId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM organism WHERE ncbi_taxonomy_id = ?");
            pstmt.setLong(1, taxonomyId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

    }
}