// $Id: DaoExternalDbCv.java,v 1.15 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.pathdb.model.CvRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object to the External Database CV Table.
 *
 * @author Ethan Cerami
 */
public class DaoExternalDbCv {

    /**
     * Adds New ExternalDatabaseRecord CV Term.
     *
     * @param dbId       External Database ID.
     * @param term       Term.
     * @param masterFlag Indicates that this term is a "master" term.
     * @return true indicates success.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean addRecord(int dbId, String term, boolean masterFlag)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO external_db_cv (`EXTERNAL_DB_ID`,`CV_TERM`,"
                    + "`MASTER_FLAG`) VALUES (?,?,?)");
            pstmt.setInt(1, dbId);
            pstmt.setString(2, term.toUpperCase());
            pstmt.setInt(3, masterFlag ? 1 : 0);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }


    /**
     * Normalizes External Reference to FIXED CV_TERM.
     *
     * @param dbTerm External Database CV Term
     * @return Fixed CV Term.
     * @throws DaoException Data Access Exception.
     */
    public String getFixedCvTerm(String dbTerm)
            throws DaoException {
        ExternalDatabaseRecord exDb = getExternalDbByTerm(dbTerm);
        if (exDb == null) {
            throw new DaoException(new NullPointerException
                    ("No matching database found for: " + dbTerm));
        }
        return exDb.getMasterTerm();
    }

    /**
     * Gets Database by Term.
     *
     * @param term Term to search.
     * @return ExternalDatabaseRecord Object.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getExternalDbByTerm(String term)
            throws DaoException {
        //  Convert to Uppercase
        term = term.toUpperCase();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM external_db_cv WHERE CV_TERM = ?");
            pstmt.setString(1, term);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("EXTERNAL_DB_ID");
                DaoExternalDb daoDb = new DaoExternalDb();
                ExternalDatabaseRecord dbRecord = daoDb.getRecordById(id);
                return dbRecord;
            }
            return null;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all Terms Associated with the specified Database.
     *
     * @param dbId External Database Id
     * @return CvTermRecord Controlled Vocabulary Record
     * @throws DaoException Error Retrieving Data.
     */
    public CvRecord getTermsByDbId(int dbId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        CvRecord cvRecord = new CvRecord();
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM external_db_cv WHERE EXTERNAL_DB_ID = ? "
                    + "ORDER BY CV_TERM DESC");
            pstmt.setInt(1, dbId);
            rs = pstmt.executeQuery();
            ArrayList synTerms = new ArrayList();
            while (rs.next()) {
                String term = rs.getString("CV_TERM");
                int masterFlag = rs.getInt("MASTER_FLAG");
                if (masterFlag == 0) {
                    synTerms.add(0, term);
                } else {
                    cvRecord.setMasterTerm(term);
                }
            }
            cvRecord.setSynonymTerms(synTerms);
            return cvRecord;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Term Associated with the specified Db Cv Id.
     *
     * @param cvId External Database CV ID.
     * @return String term.
     * @throws DaoException Error Retrieving Data.
     */
    public String getTermByDbCvId(int cvId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM external_db_cv WHERE CV_ID = ?");
            pstmt.setInt(1, cvId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                return rs.getString("CV_TERM");
            }
            return null;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes All Terms Associated with Specified Database ID.
     *
     * @param dbId Database ID.
     * @return true indicates success.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteTermsByDbId(int dbId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM external_db_cv WHERE EXTERNAL_DB_ID = ?");
            pstmt.setInt(1, dbId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}
