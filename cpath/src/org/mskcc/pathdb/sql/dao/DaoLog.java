package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.logger.LogRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Admin/View the Log Messages.
 *
 * @author Ethan Cerami
 */
public class DaoLog {

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
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
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
                    ("SELECT * FROM log order by date asc");
            rs = pstmt.executeQuery();
            int counter = 0;
            while (rs.next() && counter < 25) {
                Date date = rs.getTimestamp("date");
                String logger = rs.getString("logger");
                String priority = rs.getString("priority");
                String message = rs.getString("message");
                String ip = rs.getString("ip");
                LogRecord record = new LogRecord(date, priority, logger,
                        message, ip);
                records.add(record);
                counter++;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
        return records;
    }
}