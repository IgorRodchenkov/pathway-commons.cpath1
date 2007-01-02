// $Id: DaoOrganism.java,v 1.13 2007-01-02 16:56:32 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.Organism;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
            throw new IllegalArgumentException("Species Name is null");
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
                    ("INSERT INTO organism (`ncbi_taxonomy_id`, "
                            + "`species_name`, `common_name`)"
                            + " VALUES (?,?,?)");
            pstmt.setInt(1, taxonomyId);
            pstmt.setString(2, speciesName);
            pstmt.setString(3, commonName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Organism by TaxonomyID
     *
     * @return Organism Object.
     * @throws DaoException Error Connecting to Database.
     */
    public Organism getOrganismByTaxonomyId(int ncbiTaxonomyId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from organism where ncbi_taxonomy_id = ?");
            pstmt.setInt(1, ncbiTaxonomyId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int taxonomyId = rs.getInt("ncbi_taxonomy_id");
                String speciesName = rs.getString("species_name");
                String commonName = rs.getString("common_name");
                Organism organism = new Organism(taxonomyId,
                        speciesName, commonName);
                return organism;
            }
            return null;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets All Organisms in Database.
     *
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
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * @return the number of rows in the organism table, or -1 if
     *         none are found
     * @throws DaoException Error Connecting to Database.
     */
    public int countAllOrganisms() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList organisms = new ArrayList();
        int organismCount = -1;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select count(*) from organism order by species_name");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                organismCount = rs.getInt(1);
            }
            return organismCount;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets All Organisms in Database.
     *
     * @return HashMap of Taxonomy ID String Objects to Organism Objects.
     * @throws DaoException Error Connecting to Database.
     */
    public HashMap getAllOrganismsMap() throws DaoException {
        ArrayList organismList = this.getAllOrganisms();
        HashMap map = new HashMap();
        for (int i = 0; i < organismList.size(); i++) {
            Organism organism = (Organism) organismList.get(i);
            map.put(Integer.toString(organism.getTaxonomyId()), organism);
        }
        return map;
    }

    /**
     * Checks if Record Already Exists in Database.
     *
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
            return rs.next();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes cPath Record with the specified CPATH_ID.
     * This will also delete all external links associated with this record.
     *
     * @param taxonomyId NCBI Taxonomy ID
     * @return returns true if deletion was successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecord(int taxonomyId) throws DaoException {
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
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}
