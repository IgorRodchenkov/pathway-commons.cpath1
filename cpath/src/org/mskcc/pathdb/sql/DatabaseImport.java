package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.util.Md5Util;
import org.mskcc.pathdb.util.ZipUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Provides access to the CPath Import Table.
 *
 * @author Ethan Cerami.
 */
public class DatabaseImport {
    /**
     * New Record Status.
     */
    public static final String STATUS_NEW = "NEW";

    /**
     * Transferred Record Status.
     */
    public static final String STATUS_TRANSFERRED = "TRANSFERRED";

    private static final String IMPORT_ID = "IMPORT_ID";
    private static final String STATUS = "STATUS";
    private static final String CREATE_TIME = "CREATE_TIME";
    private static final String UPDATE_TIME = "UPDATE_TIME";
    private static final String DOC_BLOB = "DOC_BLOB";
    private static final String DOC_MD5 = "DOC_MD5";

    /**
     * Gets all Import Records.
     * @return ArrayList of ImportRecord Objects.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     * @throws SQLException Error Connecting to Database.
     * @throws IOException Error Performing I/O.
     */
    public ArrayList getAllImportRecords()
            throws ClassNotFoundException, SQLException, IOException {
        ArrayList records = new ArrayList();
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("select * from import order by IMPORT_ID");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            ImportRecord record = extractRecord(rs);
            records.add(record);
        }
        return records;
    }

    /**
     * Gets Individual Import Record.
     * @param importId ImportID of Record to Retrieve.
     * @return ImportRecord Object.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     * @throws SQLException Error Connecting to Database.
     * @throws IOException Error Performing I/O.
     */
    public ImportRecord getImportRecordById(int importId)
            throws ClassNotFoundException, SQLException, IOException {
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("select * from import  where IMPORT_ID=? order by IMPORT_ID");
        pstmt.setInt(1, importId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return extractRecord(rs);
        } else {
            return null;
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
     * @param data String data.
     * @return indicate success (true) or failure (false).
     * @throws NoSuchAlgorithmException Could not locate MD5 Hash Algorithm.
     * @throws SQLException Could not insert new data.
     * @throws ClassNotFoundException Could not locate SQL Driver.
     * @throws IOException Input Output Exception.
     */
    public boolean addImportRecord(String data) throws NoSuchAlgorithmException,
            SQLException, ClassNotFoundException, IOException {
        boolean successFlag = false;
        String hash = Md5Util.createMd5Hash(data);
        byte zippedData[] = ZipUtil.zip(data);
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("INSERT INTO import (DOC_BLOB,DOC_MD5, STATUS,"
                + "CREATE_TIME)"
                + " VALUES (?,?,?,?)");
        pstmt.setBytes(1, zippedData);
        pstmt.setString(2, hash);
        pstmt.setString(3, STATUS_NEW);
        java.util.Date date = new java.util.Date();
        Timestamp timeStamp = new Timestamp(date.getTime());
        pstmt.setTimestamp(4, timeStamp);
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            successFlag = true;
        }
        return successFlag;
    }

    /**
     * Deletes Import Record with the specified IMPORT_ID.
     * @param importID Import ID of record to delete.
     * @return returns true if deletion was successful.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public boolean deleteImportRecord(int importID)
            throws SQLException, ClassNotFoundException {
        boolean successFlag = false;
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("DELETE FROM IMPORT WHERE IMPORT_ID = ?");
        pstmt.setInt(1, importID);
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            successFlag = true;
        }
        return successFlag;
    }

    /**
     * Updates Record Status.
     * @param importID Import ID.
     * @return Number of Rows Affected.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public boolean markRecordAsTransferred(int importID) throws SQLException,
            ClassNotFoundException {
        Connection con = getConnection();
        PreparedStatement pstmt = con.prepareStatement
                ("UPDATE import set STATUS=? WHERE IMPORT_ID=?");
        pstmt.setString(1, STATUS_TRANSFERRED);
        pstmt.setInt(2, importID);
        int rows = pstmt.executeUpdate();
        return (rows > 0) ? true : false;
    }

    /**
     * Extracts Database Record into Java class.
     */
    private ImportRecord extractRecord(ResultSet rs) throws SQLException,
            IOException {
        ImportRecord record = new ImportRecord();
        record.setImportId(rs.getInt(IMPORT_ID));
        record.setStatus(rs.getString(STATUS));
        record.setCreateTime(rs.getTimestamp(CREATE_TIME));
        record.setUpdateTime(rs.getTimestamp(UPDATE_TIME));
        record.setMd5Hash(rs.getString(DOC_MD5));

        //  Unzip Blob
        Blob blob = rs.getBlob(DOC_BLOB);
        byte blobData[] = extractBlobData(blob);
        String data = ZipUtil.unzip(blobData);
        record.setData(data);
        return record;
    }

    /**
     * Gets Connection to the CPath Database.
     * @return Live Connection to Database.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     */
    private static Connection getConnection()
            throws SQLException, ClassNotFoundException {
        PropertyManager manager = PropertyManager.getInstance();
        String userName = manager.getProperty
                (PropertyManager.DB_USER);
        String password = manager.getProperty
                (PropertyManager.DB_PASSWORD);
        String url =
                new String("jdbc:mysql://" + "localhost/cpath"
                + "?user=" + userName + "&password=" + password);
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url);
        return con;
    }
}