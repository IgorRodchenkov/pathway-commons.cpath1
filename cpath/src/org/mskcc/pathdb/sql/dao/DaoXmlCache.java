package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.util.ZipUtil;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.model.XmlCacheRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object to the xml_cache Table.
 *
 * @author Ethan Cerami.
 */
public class DaoXmlCache {

    /**
     * Maximum Number of Records Allowed in Cache.
     */
    public static final int MAX_CACHE_RECORDS = 500;

    private XDebug xdebug;

    /**
     * Constructor.
     *
     * @param xdebug XDebug Object.
     */
    public DaoXmlCache(XDebug xdebug) {
        this.xdebug = xdebug;
    }

    /**
     * Adds Specified Record to the xml_cache Table.
     *
     * @param hashKey     Unique HashKey.
     * @param url         URL String.
     * @param xmlAssembly XML Assembly Object.
     * @return true indicates success; false indicates failure.
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized boolean addRecord(String hashKey,
            String url, XmlAssembly xmlAssembly) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO xml_cache (`URL`, `DOC_MD5`,`NUM_HITS`, "
                    + "`DOC_BLOB`, `LAST_USED`) VALUES (?,?,?,?,?)");

            byte zippedData[] = ZipUtil.zip(xmlAssembly.getXmlString());
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());

            pstmt.setString(1, url);
            pstmt.setString(2, hashKey);
            pstmt.setInt(3, xmlAssembly.getNumHits());
            pstmt.setBytes(4, zippedData);
            pstmt.setTimestamp(5, timeStamp);
            int rows = pstmt.executeUpdate();
            conditionallyDeleteEldest ();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } catch (IOException e) {
            throw new DaoException("IOException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Record by specified Hash Key.
     *
     * @param hashKey Unique Hash Key.
     * @return XML Document
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized XmlAssembly getXmlAssemblyByKey(String hashKey)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM xml_cache WHERE DOC_MD5 = ?");
            pstmt.setString(1, hashKey);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int numHits = rs.getInt("NUM_HITS");
                Blob blob = rs.getBlob("DOC_BLOB");
                byte blobData[] = extractBlobData(blob);
                String xml = ZipUtil.unzip(blobData);
                XmlAssembly xmlAssembly =
                        XmlAssemblyFactory.createXmlAssembly
                        (xml, numHits, xdebug);
                updateLastUsedField (hashKey);
                return xmlAssembly;
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException(e);
        } catch (IOException e) {
            throw new DaoException(e);
        } catch (AssemblyException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes Record with the specified CACHE_ID.
     *
     * @param hashKey Unique Hash Key.
     * @return returns true if deletion was successful.
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized boolean deleteRecordByKey(String hashKey)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM xml_cache WHERE DOC_MD5 = ?");
            pstmt.setString(1, hashKey);
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

    /**
     * Deletes all Cached Records.
     *
     * @return returns true if deletion was successful.
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized boolean deleteAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("DELETE FROM xml_cache");
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

    /**
     * Updates XML Assembly Content for Cached Record.
     *
     * @param hashKey     Unique Hash Key.
     * @param xmlAssembly New XML Assembly Content.
     * @return true indicates success.
     * @throws DaoException Error Updating Data.
     */
    public synchronized boolean updateXmlAssemblyByKey(String hashKey,
            XmlAssembly xmlAssembly)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();

            pstmt = con.prepareStatement
                    ("UPDATE xml_cache SET `DOC_BLOB` = ?, `LAST_USED` = ?, "
                    + "`NUM_HITS` = ? WHERE `DOC_MD5` = ?");
            byte zippedData[] = ZipUtil.zip(xmlAssembly.getXmlString());
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());

            pstmt.setBytes(1, zippedData);
            pstmt.setTimestamp(2, timeStamp);
            pstmt.setInt(3, xmlAssembly.getNumHits());
            pstmt.setString(4, hashKey);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } catch (IOException e) {
            throw new DaoException("IOException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Updates the LastUsed Field for specified Record.
     *
     * @param hashKey     Unique Hash Key.
     * @return true indicates success.
     * @throws DaoException Error Updating Data.
     */
    public synchronized boolean updateLastUsedField(String hashKey)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();

            pstmt = con.prepareStatement
                    ("UPDATE xml_cache SET `LAST_USED` = ? "
                    + "WHERE `DOC_MD5` = ?");
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());

            pstmt.setTimestamp(1, timeStamp);
            pstmt.setString(2, hashKey);
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

    /**
     * Gets all Records in Cache (most recently used appear first).
     * @return ArrayList of XmlCacheRecord Objects.
     * @throws DaoException Error Connecting to Database.
     */
    public ArrayList getAllRecords() throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement ("SELECT * FROM xml_cache "
                    + "ORDER BY LAST_USED DESC");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                XmlCacheRecord record = new XmlCacheRecord();
                record.setCacheId(rs.getInt("CACHE_ID"));
                record.setUrl(rs.getString("URL"));
                record.setMd5(rs.getString("DOC_MD5"));
                record.setNumHits(rs.getInt("NUM_HITS"));
                record.setLastUsed(rs.getTimestamp("LAST_USED"));
                records.add(record);
            }
            return records;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Uses a Last Recently Used (LRU) Algorithm to Delete LRU Record
     * stored in database.  LRU algorithm kicks in only when number of
     * records exceeds MAX_CACHE_RECORDS.
     *
     * @throws DaoException Error Accessing Database.
     */
    public void conditionallyDeleteEldest() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement ("SELECT COUNT(*) FROM xml_cache");
            rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > MAX_CACHE_RECORDS) {
                deleteEldest();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Finds and Deletes the LRU Record.
     * @throws DaoException Error Accessing Database.
     */
    private void deleteEldest() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT DOC_MD5 FROM `xml_cache` ORDER BY "
                    + "LAST_USED ASC LIMIT 0 , 1");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String hashKey = rs.getString("DOC_MD5");
                deleteRecordByKey(hashKey);
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
     * Extracts Blob Data to Array of Bytes.
     */
    private byte[] extractBlobData(Blob blob) throws SQLException,
            IOException {
        InputStream in = blob.getBinaryStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        while (true) {
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            out.write(buffer, 0, bytesRead);
        }
        return out.toByteArray();
    }
}