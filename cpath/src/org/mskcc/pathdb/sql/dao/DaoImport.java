package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.Md5Util;
import org.mskcc.pathdb.util.ZipUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object to the Import Table.
 *
 * @author Ethan Cerami.
 */
public class DaoImport {

    private static final String IMPORT_ID = "IMPORT_ID";
    private static final String DESCRIPTION = "DESC";
    private static final String STATUS = "STATUS";
    private static final String CREATE_TIME = "CREATE_TIME";
    private static final String UPDATE_TIME = "UPDATE_TIME";
    private static final String DOC_BLOB = "DOC_BLOB";
    private static final String DOC_MD5 = "DOC_MD5";

    /**
     * Gets all Import Records.
     * Note:  this method does not retrieve the associated XML Blob.
     * @return ArrayList of ImportRecord Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getAllRecords() throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select `IMPORT_ID`, `DESC`, `DOC_MD5`, `STATUS`, "
                    + "`CREATE_TIME`, `UPDATE_TIME`"
                    + " from import order by IMPORT_ID");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ImportRecord record = extractRecord(rs, false);
                records.add(record);
            }
            return records;
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
     * Gets Individual Import Record.
     * @param importId ImportID of Record to Retrieve.
     * @return ImportRecord Object.
     * @throws DaoException Error Retrieving Data.
     */
    public ImportRecord getRecordById(long importId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from import where IMPORT_ID=? order "
                    + "by IMPORT_ID");
            pstmt.setLong(1, importId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractRecord(rs, true);
            } else {
                return null;
            }
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

    /**
     * Adds New Database Import Record.
     * @param description Import Record Description.
     * @param data String data.
     * @return indicate success (true) or failure (false).
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized long addRecord(String description, String data)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();

            String hash = Md5Util.createMd5Hash(data);
            byte zippedData[] = ZipUtil.zip(data);
            pstmt = con.prepareStatement
                    ("INSERT INTO import (`DESC`, `DOC_BLOB`,`DOC_MD5`,"
                    + " `STATUS`, `CREATE_TIME`)"
                    + " VALUES (?,?,?,?,?)");
            pstmt.setString(1, description);
            pstmt.setBytes(2, zippedData);
            pstmt.setString(3, hash);
            pstmt.setString(4, ImportRecord.STATUS_NEW);
            java.util.Date date = new java.util.Date();
            Timestamp timeStamp = new Timestamp(date.getTime());
            pstmt.setTimestamp(5, timeStamp);
            int rows = pstmt.executeUpdate();

            //  Get New ID
            pstmt = con.prepareStatement("SELECT MAX(IMPORT_ID) from import");
            rs = pstmt.executeQuery();
            rs.next();
            long importId = rs.getLong(1);
            return importId;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } catch (IOException e) {
            throw new DaoException("IOException:  " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new DaoException("NoSuchAlgorithmException:  "
                    + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes Import Record with the specified IMPORT_ID.
     * @param importID Import ID of record to delete.
     * @return returns true if deletion was successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(int importID) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM import WHERE IMPORT_ID = ?");
            pstmt.setInt(1, importID);
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
     * Updates Record Status.
     * @param importID Import ID.
     * @return Number of Rows Affected.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean markRecordAsTransferred(int importID) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("UPDATE import set STATUS=? WHERE IMPORT_ID=?");
            pstmt.setString(1, ImportRecord.STATUS_TRANSFERRED);
            pstmt.setInt(2, importID);
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
     * Extracts Database Record into Java class.
     */
    private ImportRecord extractRecord(ResultSet rs, boolean extractBlob)
            throws SQLException, IOException {
        ImportRecord record = new ImportRecord();
        record.setImportId(rs.getInt(IMPORT_ID));
        record.setDescription(rs.getString(DESCRIPTION));
        record.setStatus(rs.getString(STATUS));
        record.setCreateTime(rs.getTimestamp(CREATE_TIME));
        record.setUpdateTime(rs.getTimestamp(UPDATE_TIME));
        record.setMd5Hash(rs.getString(DOC_MD5));

        //  Unzip Blob
        if (extractBlob) {
            Blob blob = rs.getBlob(DOC_BLOB);
            byte blobData[] = extractBlobData(blob);
            String data = ZipUtil.unzip(blobData);
            record.setData(data);
        }
        return record;
    }
}