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
package org.mskcc.pathdb.bb.sql.dao;

import org.mskcc.pathdb.model.BBInternalLinkRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object to the Kegg Internal Link table.
 *
 * @author Benjamin Gross
 */
public class DaoBBInternalLink {

    /**
     * Creates internal link between kegg pathway and kegg gene records.
     *
     * @param links ArrayList<BBInternalLinkRecord>
     * @throws DaoException
     */
    public void addRecords(ArrayList<BBInternalLinkRecord> links) throws DaoException {

		// vars used
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		// perform the insert
        try {

            //  Use batch mode for faster performance
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO bb_internal_link (`external_pathway_id`,`entrez_gene_id`)"
                            + " VALUES (?,?)");
            for (BBInternalLinkRecord record : links) {
                pstmt.setString(1, record.getPathwayID());
                pstmt.setString(2, record.getEntrezGeneID());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets an arraylist of BBInternalLinkRecord given a pathway id
     *
     * @param bbPathwayRecordID String
     * @return ArrayList<BBInternalLinkRecord>
     * @throws DaoException
     */
    public ArrayList<BBInternalLinkRecord> getBBPathwayByPathwayID(String bbPathwayRecordID)
		throws DaoException {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		try {
            con = JdbcUtil.getCPathConnection();
			pstmt = con.prepareStatement
				("select * from bb_internal_link where external_pathway_id = ?");
			pstmt.setString(1, bbPathwayRecordID);
			return getBBInternalLinkRecords(con, pstmt, rs);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
			JdbcUtil.closeAll(con, null, null);
        }
	}

    /**
     * Gets an arraylist of BBInternalLinkRecord given an entrez gene id
     *
     * @param bbGeneRecordID String
     * @return ArrayList<BBInternalLinkRecord>
     * @throws DaoException
     */
    public ArrayList<BBInternalLinkRecord> getBBPathwayByGeneID(String bbGeneRecordID)
		throws DaoException {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		try {
            con = JdbcUtil.getCPathConnection();
			pstmt = con.prepareStatement
				("select * from bb_internal_link where entrez_gene_id = ?");
			pstmt.setString(1, bbGeneRecordID);
			return getBBInternalLinkRecords(con, pstmt, rs);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
			JdbcUtil.closeAll(con, null, null);
        }
	}

    /**
     * Gets an arraylist of BBInternalLinkRecord given a pathway id
     *
	 * @param con Connection
     * @param pstmt PreparedStatement
	 * @param rs ResultSet
     * @return ArrayList<BBInternalLinkRecord>
     * @throws DaoException
     */
    private ArrayList<BBInternalLinkRecord> getBBInternalLinkRecords(Connection con,
																	 PreparedStatement pstmt,
																	 ResultSet rs) throws DaoException {
		// do the query
        try {
            rs = pstmt.executeQuery();
			ArrayList<BBInternalLinkRecord> list = new ArrayList<BBInternalLinkRecord>();
            while (rs.next()) {
                String externalPathwayID = rs.getString(1);
                String entrezGeneID = rs.getString(2);
                list.add(new BBInternalLinkRecord(externalPathwayID, entrezGeneID));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
