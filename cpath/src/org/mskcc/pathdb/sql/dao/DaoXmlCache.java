package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.ZipUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

/**
 * Data Access Object to the xml_cache Table.
 *
 * @author Ethan Cerami.
 */
public class DaoXmlCache {

    /**
     * Adds Specified Record to the xml_cache Table.
     * @param hashKey Unique HashKey.
     * @param xml XML Document.
     * @return true indicates success; false indicates failure.
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized boolean addRecord(String hashKey, String xml)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO xml_cache (`DOC_MD5`,`DOC_BLOB`,"
                    + " `CREATE_TIME`) VALUES (?,?,?)");

            byte zippedData[] = ZipUtil.zip(xml);
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());

            pstmt.setString(1, hashKey);
            pstmt.setBytes(2, zippedData);
            pstmt.setTimestamp(3, timeStamp);
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
     * Gets Record by specified Hash Key.
     * @param hashKey Unique Hash Key.
     * @return XML Document
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized String getXmlByKey(String hashKey) throws DaoException {
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
                Blob blob = rs.getBlob("DOC_BLOB");
                byte blobData[] = extractBlobData(blob);
                return ZipUtil.unzip(blobData);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } catch (IOException e) {
            throw new DaoException("IOException:  "
                    + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes Record with the specified CACHE_ID.
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
     * Updates XML Content for Cached Record.
     * @param hashKey Unique Hash Key.
     * @param newXml New XML Content.
     * @return true indicates success.
     * @throws DaoException Error Updating Data.
     */
    public synchronized boolean updateXmlByKey(String hashKey, String newXml)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();

            pstmt = con.prepareStatement
                    ("UPDATE xml_cache SET `DOC_BLOB` = ?, `CREATE_TIME` = ? "
                    + "WHERE `DOC_MD5` = ?");
            byte zippedData[] = ZipUtil.zip(newXml);
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());

            pstmt.setBytes(1, zippedData);
            pstmt.setTimestamp(2, timeStamp);
            pstmt.setString(3, hashKey);
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