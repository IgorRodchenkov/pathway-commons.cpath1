// $Id
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
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

import org.mskcc.pathdb.model.BBGeneRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object to the bb_gene table.
 *
 * @author Benjamin Gross
 */
public class DaoBBGene {

    /**
     * Adds a new BBGeneRecord to the db.
     *
     * @param bbGeneRecord BBGeneRecord
     * @throws DaoException
     */
    public void addRecord(BBGeneRecord bbGeneRecord) throws DaoException {

		// check args
		if (bbGeneRecord == null) throw new DaoException("bbGeneRecord is null.");

        //  Make sure record does not already exist.
        BBGeneRecord record = getBBGene(bbGeneRecord.getEntrezGeneID());
        if (record != null) throw new DaoException("Record already exists.");

		// query vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		// perform query
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO bb_gene "
                            + "(`entrez_gene_id`, "
                            + "`gene_name`)"
                            + " VALUES (?,?)");
            pstmt.setString(1, bbGeneRecord.getEntrezGeneID());
            pstmt.setString(2, bbGeneRecord.getGeneName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets the specified bbGeneRecord by ID.
     *
     * @param bbGeneRecordID String
     * @return BBGeneRecord
     * @throws DaoException
     */
    public BBGeneRecord getBBGene(String bbGeneRecordID) throws DaoException {

		// some used vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		// do the query
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from bb_gene where entrez_gene_id = ?");
            pstmt.setString(1, bbGeneRecordID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String geneID = rs.getString(1);
                String geneName = rs.getString(2);
                return new BBGeneRecord(geneID, geneName);
            }
            return null;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}
