// $Id: DaoExternalDb.java,v 1.23 2006-03-06 17:28:29 cerami Exp $
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

import org.mskcc.pathdb.model.CvRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.cache.EhCache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * Data Access Object to the External Database Table.
 *
 * @author Ethan Cerami.
 */
public class DaoExternalDb {

    /**
     * Adds Specified External Database Record to the CPath Database.
     *
     * @param db External Database Record.
     * @return true if record was successfully added.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean addRecord(ExternalDatabaseRecord db) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String masterTerm = db.getMasterTerm();

        if (db.getMasterTerm() == null) {
            throw new IllegalArgumentException("ExternalDatabaseRecord "
                    + " masterTerm attribute is null.");
        }

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO external_db (`NAME`,`URL`, `SAMPLE_ID`, "
                    + "`DESC`,`CREATE_TIME`, `DB_TYPE`) VALUES (?,?,?,?,?,?)");
            pstmt.setString(1, db.getName());
            pstmt.setString(2, db.getUrl());
            pstmt.setString(3, db.getSampleId());
            pstmt.setString(4, db.getDescription());
            java.util.Date now = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(now.getTime());
            pstmt.setDate(5, sqlDate);
            pstmt.setString(6, db.getDbType().toString());
            int rows = pstmt.executeUpdate();

            // Save the Controlled Vocabulary Terms.
            ArrayList terms = db.getSynonymTerms();
            DaoExternalDbCv dao = new DaoExternalDbCv();
            db = getRecordByName(db.getName());
            if (terms != null) {
                for (int i = 0; i < terms.size(); i++) {
                    dao.addRecord(db.getId(), (String) terms.get(i), false);
                }
            }

            //  Save the Master Term
            dao.addRecord(db.getId(), masterTerm, true);

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
     * Retrieves the External Database Record with the specified Primary ID.
     *
     * @param id External Database ID.
     * @return External Database Record.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getRecordById(int id) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ExternalDatabaseRecord dbRecord = null;

        // First Check Cache
        String key = this.getClass().getName() + ".getRecordById." + id;
        CacheManager manager = CacheManager.create();
        Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);

        Element cachedElement = cache.get(key);

        //  If not in cache, get from Database
        if (cachedElement == null) {
            try {
                con = JdbcUtil.getCPathConnection();
                pstmt = con.prepareStatement
                        ("SELECT * FROM external_db WHERE EXTERNAL_DB_ID = ?");
                pstmt.setInt(1, id);
                rs = pstmt.executeQuery();
                dbRecord = extractRecord(rs);

                //  Store to Cache
                if (dbRecord != null) {
                    cachedElement = new Element (key, dbRecord);
                    cache.put(cachedElement);
                }
                return dbRecord;
            } catch (ClassNotFoundException e) {
                throw new DaoException(e);
            } catch (SQLException e) {
                throw new DaoException(e);
            } finally {
                JdbcUtil.closeAll(con, pstmt, rs);
            }
        } else {
            dbRecord = (ExternalDatabaseRecord) cachedElement.getValue();
            return dbRecord;
        }
    }

    /**
     * Retrieves the External Database Record with the specified name.
     *
     * @param name External Database Name
     * @return External Database Record.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getRecordByName(String name)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //  Verify name is not null or empty;  part of bug #0000508
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Database name is null "
                    + "or empty.");
        }

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM external_db WHERE NAME = ?");
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            return extractRecord(rs);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Retrieves the External Database Record with the matching pattern.
     *
     * @param term String Term
     * @return External Database Record.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalDatabaseRecord getRecordByTerm(String term)
            throws DaoException {
        ExternalDatabaseRecord dbRecord = null;

        //  Verify term is not null or empty;  part of bug #0000508
        if (term == null || term.length() == 0) {
            throw new IllegalArgumentException("Database name is null "
                    + "or empty.");
        }

        // First Check Global Cache
        String key = this.getClass().getName() + ".getRecordByTerm." + term;
        CacheManager manager = CacheManager.create();
        Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
        Element cachedElement = cache.get(key);

        //  If not in cache, get from Database
        if (cachedElement == null) {
            DaoExternalDbCv dao = new DaoExternalDbCv();
            dbRecord = dao.getExternalDbByTerm(term);
            if (dbRecord != null) {
                cachedElement = new Element (key, dbRecord);
                cache.put(cachedElement);
            }
        } else {
            dbRecord = (ExternalDatabaseRecord) cachedElement.getValue();
        }
        return dbRecord;
    }

    /**
     * Gets a Complete List of All External Database Records.
     *
     * @return ArrayList of External Database object.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM external_db");
            rs = pstmt.executeQuery();
            ArrayList records = new ArrayList();
            while (rs.next()) {
                ExternalDatabaseRecord db = this.createBean(rs);
                records.add(db);
            }
            return records;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Delete External Database Record with the Specified ID.
     *
     * @param id External Database ID.
     * @return true indicates record was successfully deleted.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(int id) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM external_db WHERE EXTERNAL_DB_ID = ?");
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            //  Delete all associated terms.
            DaoExternalDbCv dao = new DaoExternalDbCv();
            dao.deleteTermsByDbId(id);

            //  Remove from global cache
            CacheManager manager = CacheManager.create();
            Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
            String key = this.getClass().getName() + ".getRecordById." + id;
            cache.remove(key);
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
     * Update External Database Record with the Specified ID.
     *
     * @param db External Database Record.
     * @return true indicates record was successfully update.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean updateRecord(ExternalDatabaseRecord db) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("UPDATE external_db SET `NAME` = ?, "
                    + "`DESC` = ?, `URL` = ?, `SAMPLE_ID` = ?, "
                    + "`UPDATE_TIME` = ? "
                    + "WHERE `EXTERNAL_DB_ID` = ?");
            pstmt.setString(1, db.getName());
            pstmt.setString(2, db.getDescription());
            pstmt.setString(3, db.getUrl());
            pstmt.setString(4, db.getSampleId());
            java.util.Date now = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(now.getTime());
            pstmt.setDate(5, sqlDate);
            pstmt.setInt(6, db.getId());
            int rows = pstmt.executeUpdate();

            //  Delete all associated terms.
            DaoExternalDbCv dao = new DaoExternalDbCv();
            dao.deleteTermsByDbId(db.getId());

            // Save CV Terms.
            ArrayList terms = db.getSynonymTerms();
            db = getRecordByName(db.getName());
            for (int i = 0; i < terms.size(); i++) {
                dao.addRecord(db.getId(), (String) terms.get(i), false);
            }
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
     * Extracts Database Record into Java Bean.
     */
    private ExternalDatabaseRecord extractRecord(ResultSet rs)
            throws SQLException, DaoException {
        if (rs.next()) {
            return createBean(rs);
        } else {
            return null;
        }
    }

    /**
     * Creates External Database Java Bean.
     */
    private ExternalDatabaseRecord createBean(ResultSet rs)
            throws SQLException, DaoException {
        ExternalDatabaseRecord record = new ExternalDatabaseRecord();
        record.setId(rs.getInt("EXTERNAL_DB_ID"));
        record.setName(rs.getString("NAME"));
        record.setUrl(rs.getString("URL"));
        record.setSampleId(rs.getString("SAMPLE_ID"));
        record.setDescription(rs.getString("DESC"));
        record.setCreateTime(rs.getDate("CREATE_TIME"));
        record.setUpdateTime(rs.getDate("UPDATE_TIME"));
        ReferenceType type = ReferenceType.getType
                (rs.getString("DB_TYPE"));
        record.setDbType(type);

        DaoExternalDbCv dao = new DaoExternalDbCv();
        CvRecord cvRecord = dao.getTermsByDbId(record.getId());
        record.setMasterTerm(cvRecord.getMasterTerm());
        record.setSynonymTerms(cvRecord.getSynonymTerms());
        return record;
    }
}
