package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.LogRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Admin/View the Log Messages.
 *
 * @author Ethan Cerami
 */
public class DaoLog {
    /**
     * Maximum Number of Log Messages Allowed at Any Time.
     */
    public static final int MAX_NUM_RECORDS = 100;

    /**
     * Gets all log messages.
     *
     * @return ArrayList of LogRecord objects.
     * @throws DaoException Error Accessing Database.
     */
    public ArrayList getLogRecords() throws DaoException {
        ArrayList records = getRecords();
        return records;
    }

    /**
     * Deletes all existing Log messages.
     *
     * @throws DaoException Error Accessing Database.
     */
    public void deleteAllLogRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("TRUNCATE TABLE log");
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Adds New Log Record to Database.
     *
     * @param logRecord LogRecord.
     * @throws DaoException Error Connecting to Database.
     */
    public synchronized void addRecord(LogRecord logRecord)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conditionallyPurgeRecords();
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO log (`TIMESTAMP`, `PRIORITY`, `MESSAGE`, "
                    + "`STACK_TRACE`, `WEB_URL`, `REMOTE_HOST`, `REMOTE_IP`)"
                    + " VALUES (?,?,?,?,?,?,?)");
            Timestamp timeStamp = new Timestamp(logRecord.getDate().getTime());
            pstmt.setTimestamp(1, timeStamp);
            pstmt.setString(2, logRecord.getPriority());
            pstmt.setString(3, logRecord.getMessage());
            pstmt.setString(4, logRecord.getStackTrace());
            pstmt.setString(5, logRecord.getWebUrl());
            pstmt.setString(6, logRecord.getRemoteHost());
            pstmt.setString(7, logRecord.getRemoteIp());
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Conditionally Purge Log Records.
     * If the number of records exceeds MAX_NUM_RECORDS, all records
     * are purged.  This is a simple mechanism of limiting the number
     * of log messages to a 'reasonable' level.
     *
     * @throws DaoException Error Accessing Database.
     */
    private void conditionallyPurgeRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM log");
            rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count >= MAX_NUM_RECORDS) {
                this.deleteAllLogRecords();
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
     * Gets all Log Records.
     */
    private ArrayList getRecords() throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM log order by TIMESTAMP desc");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Date date = rs.getTimestamp("TIMESTAMP");
                String priority = rs.getString("PRIORITY");
                String message = rs.getString("MESSAGE");
                String stackTrace = rs.getString("STACK_TRACE");
                String webUrl = rs.getString("WEB_URL");
                String remoteHost = rs.getString("REMOTE_HOST");
                String remoteIp = rs.getString("REMOTE_IP");

                LogRecord record = new LogRecord();
                record.setDate(date);
                record.setPriority(priority);
                record.setMessage(message);
                record.setStackTrace(stackTrace);
                record.setWebUrl(webUrl);
                record.setRemoteHost(remoteHost);
                record.setRemoteIp(remoteIp);
                records.add(record);
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
        return records;
    }
}