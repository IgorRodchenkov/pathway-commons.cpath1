package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.util.Md5Util;
import org.mskcc.pathdb.util.ZipUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Provides access to the CPath Import Table.
 *
 * @author Ethan Cerami.
 */
public class DatabaseImport {
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
            ImportRecord record = new ImportRecord();
            record.setImportId(rs.getInt(IMPORT_ID));
            record.setStatus(rs.getString(STATUS));
            record.setCreateTime(rs.getDate(CREATE_TIME));
            record.setUpdateTime(rs.getDate(UPDATE_TIME));
            record.setMd5Hash(rs.getString(DOC_MD5));

            //  Unzip Blob
            Blob blob = rs.getBlob(DOC_BLOB);
            //  TODO:  FIX cast to int problem
            byte bytes[] = blob.getBytes(1, (int) blob.length());
            String data = ZipUtil.unzip(bytes);
            record.setData(data);

            records.add(record);
        }
        return records;
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
        pstmt.setString(3, "NEW");
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
     * @throws ClassNotFoundException Error locating correct SQL drier.
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
     * Gets Connection to the CPath Database.
     * @return Live Connection to Database.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     */
    private static Connection getConnection()
            throws SQLException, ClassNotFoundException {
        PropertyManager manager = PropertyManager.getInstance();
        // TODO:  Fix Properties
        String userName = manager.getProperty
                (PropertyManager.PROPERTY_GRID_DB_USER);
        String password = manager.getProperty
                (PropertyManager.PROPERTY_GRID_DB_PASSWORD);
        String url =
                new String("jdbc:mysql://" + "localhost/cpath"
                + "?user=" + userName + "&password=" + password);
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url);
        return con;
    }
}