package org.mskcc.pathdb.logger;

import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Admin/View the Log Messages.
 *
 * @author Ethan Cerami
 */
public class AdminLogger {

    /**
     * Gets all log messages.
     * @return ArrayList of LogRecord objects.
     * @throws SQLException Indicates error connecting to database.
     * @throws ClassNotFoundException Indicates error locating jdbc driver.
     */
    public ArrayList getLogRecords() throws SQLException,
            ClassNotFoundException {
        ArrayList records = getRecords();
        return records;
    }

    /**
     * Deletes all existing Log messages.
     * @throws SQLException Indicates error connecting to database.
     * @throws ClassNotFoundException Indicates error locating jdbc driver.
     */
    public void deleteAllLogRecords() throws SQLException,
            ClassNotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement ("TRUNCATE TABLE log");
            pstmt.executeUpdate();
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets all Log Records.
     */
    private ArrayList getRecords() throws SQLException, ClassNotFoundException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM log order by date asc");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Date date = rs.getTimestamp("date");
                String logger = rs.getString("logger");
                String priority = rs.getString("priority");
                String message = rs.getString("message");
                String ip = rs.getString("ip");
                LogRecord record = new LogRecord(date, priority, logger,
                        message, ip);
                records.add(record);
            }
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
        return records;
    }
}