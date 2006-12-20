// $Id: DaoExternalDb.java,v 1.30 2006-12-20 18:38:43 grossb Exp $
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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.mskcc.pathdb.model.CvRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.cache.EhCache;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.io.*;

/**
 * Data Access Object to the External Database Table.
 *
 * @author Ethan Cerami.
 */
public class DaoExternalDb {
    private static final String ALL_FIELDS_EXCEPT_ICON =
        "`EXTERNAL_DB_ID`, `NAME`, `DESC`, `DB_TYPE`, `HOME_PAGE_URL`, "
        + "`URL_PATTERN`, `SAMPLE_ID`, `PATH_GUIDE_ID`, `CREATE_TIME`, `UPDATE_TIME`, "
        + "`ICON_FILE_EXTENSION`";

    /**
     * Adds Specified External Database Record to the CPath Database.
     *
     * @param db External Database Record.
     * @return ID of newly generated record.
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized int addRecord(ExternalDatabaseRecord db) throws DaoException {
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
                    ("INSERT INTO external_db (`NAME`,`URL_PATTERN`, `SAMPLE_ID`, "
                            + "`DESC`,`CREATE_TIME`, `DB_TYPE`, "
                            + "`HOME_PAGE_URL`, `PATH_GUIDE_ID`, `ICON_FILE_EXTENSION`) "
                            + "VALUES (?,?,?,?,?,?,?,?,?)");
            pstmt.setString(1, db.getName());
            pstmt.setString(2, db.getUrlPattern());
            pstmt.setString(3, db.getSampleId());
            pstmt.setString(4, db.getDescription());
            java.util.Date now = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(now.getTime());
            pstmt.setDate(5, sqlDate);
            pstmt.setString(6, db.getDbType().toString());
            pstmt.setString(7, db.getHomePageUrl());
            pstmt.setString(8, db.getPathGuideId());
            pstmt.setString(9, db.getIconFileExtension());
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

            //  Get New ID
            pstmt = con.prepareStatement("SELECT MAX(EXTERNAL_DB_ID) from external_db");
            rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Adds Icon for the specified external database.
     * @param file Icon File.
     * @param externalDbId External DB ID.
     * @return true indicates icon successfully added.
     * @throws FileNotFoundException File not found.
     * @throws DaoException Database access error.
     */
    public boolean addIcon (File file, int externalDbId)
        throws DaoException, FileNotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        InputStream in = new FileInputStream (file);

        // If object is already cached, reset it.
        String key = this.getClass().getName() + ".getRecordById."
                + externalDbId;
        CacheManager manager = CacheManager.getInstance();
        Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
        if (cache.get(key) != null) {
            cache.remove(key);
        }

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("UPDATE external_db SET `ICON_BLOB` = ?, "
                            + "`ICON_FILE_EXTENSION` = ? "
                            + "WHERE `EXTERNAL_DB_ID` = ?");
            pstmt.setBinaryStream(1, in, (int) file.length());
            String fileExtension = getFileExtension(file);
            pstmt.setString(2, fileExtension);
            pstmt.setInt(3, externalDbId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Gets Icon for the specified external database.
     * @param externalDbId External DB ID.
     * @return Image Icon.
     * @throws DaoException Database access error.
     */
    public ImageIcon getIcon (int externalDbId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT ICON_BLOB FROM external_db "
                            + "WHERE `EXTERNAL_DB_ID` = ?");
            pstmt.setInt(1, externalDbId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob("ICON_BLOB");
                if (blob != null) {
                    return new ImageIcon(blob.getBytes(1, (int) blob.length()));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Icon for the specified external database.
     * @param externalDbId External DB ID.
     * @return InputStream
     * @throws DaoException Database access error.
     */
    public InputStream getIconBinaryStream (int externalDbId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT ICON_BLOB FROM external_db "
                            + "WHERE `EXTERNAL_DB_ID` = ?");
            pstmt.setInt(1, externalDbId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob("ICON_BLOB");
                if (blob != null) {
                    return blob.getBinaryStream();
                } else {
                    return null;
                }
            } else {
                return null;
            }
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
        CacheManager manager = CacheManager.getInstance();
        Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);

        Element cachedElement = cache.get(key);
        //  If not in cache, get from Database
        if (cachedElement == null) {
            try {
                con = JdbcUtil.getCPathConnection();
                pstmt = con.prepareStatement
                        ("SELECT " + ALL_FIELDS_EXCEPT_ICON
                                + " FROM external_db WHERE EXTERNAL_DB_ID = ?");
                pstmt.setInt(1, id);
                rs = pstmt.executeQuery();
                dbRecord = extractRecord(rs);

                //  Store to Cache
                if (dbRecord != null) {
                    cachedElement = new Element(key, dbRecord);
                    cache.put(cachedElement);
                }
                return dbRecord;
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
                    ("SELECT " + ALL_FIELDS_EXCEPT_ICON
                            + " FROM external_db WHERE NAME = ?");
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            return extractRecord(rs);
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
        CacheManager manager = CacheManager.getInstance();
        Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
        Element cachedElement = cache.get(key);

        //  If not in cache, get from Database
        if (cachedElement == null) {
            DaoExternalDbCv dao = new DaoExternalDbCv();
            dbRecord = dao.getExternalDbByTerm(term);
            if (dbRecord != null) {
                cachedElement = new Element(key, dbRecord);
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
                    ("SELECT " + ALL_FIELDS_EXCEPT_ICON
                            + " FROM external_db");
            rs = pstmt.executeQuery();
            ArrayList records = new ArrayList();
            while (rs.next()) {
                ExternalDatabaseRecord db = this.createBean(rs);
                records.add(db);
            }
            return records;
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
            CacheManager manager = CacheManager.getInstance();
            Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
            String key = this.getClass().getName() + ".getRecordById." + id;
            cache.remove(key);
            return (rows > 0) ? true : false;
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
                            + "`DESC` = ?, `URL_PATTERN` = ?, `SAMPLE_ID` = ?, "
                            + "`UPDATE_TIME` = ? "
                            + "WHERE `EXTERNAL_DB_ID` = ?");
            pstmt.setString(1, db.getName());
            pstmt.setString(2, db.getDescription());
            pstmt.setString(3, db.getUrlPattern());
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
        record.setUrlPattern(rs.getString("URL_PATTERN"));
        record.setHomePageUrl(rs.getString("HOME_PAGE_URL"));
        record.setSampleId(rs.getString("SAMPLE_ID"));
        record.setPathGuideId(rs.getString("PATH_GUIDE_ID"));
        record.setDescription(rs.getString("DESC"));
        record.setIconFileExtension(rs.getString("ICON_FILE_EXTENSION"));
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

    private String getFileExtension (File file) {
        String ext;
        int dotPlace = file.getName().lastIndexOf ('.');
        if (dotPlace >= 0) {
            ext = file.getName().substring(dotPlace + 1);
        } else {
           ext = "";
        }
        return ext;
    }
}