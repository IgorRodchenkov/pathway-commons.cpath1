package org.mskcc.pathdb.util;

import org.mskcc.pathdb.model.LogRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * Utility Class for Writing Log Messages to Database.
 *
 * @author Ethan Cerami.
 */
public class LogUtil {

    /**
     * Logs an Exception Message.
     * This method first tries to log the message to the database.
     * If this fails (perhaps the database is down), the method will
     * log to catalina.out.
     *
     * @param throwable The Exception to Log
     * @param url       Web URL requested by user
     * @param host      Remote Host of User.
     * @param ip        Remote IP Address of User.
     */
    public static void logException(Throwable throwable, String url,
            String host, String ip) {
        StringWriter writer = new StringWriter();
        PrintWriter pWriter = new PrintWriter(writer);
        throwable.printStackTrace(pWriter);

        LogRecord record = new LogRecord();
        record.setDate(new Date());
        record.setPriority(LogRecord.PRIORITY_ERROR);
        record.setMessage(throwable.getMessage());
        record.setStackTrace(writer.toString());
        record.setWebUrl(url);
        record.setRemoteHost(host);
        record.setRemoteIp(ip);

        DaoLog dao = new DaoLog();
        try {
            dao.addRecord(record);
        } catch (DaoException e) {
            //  If we can't log to Database, log to catalina.out
            System.err.println("An Error Has Occured, and the Database "
                    + "is down!");
            System.err.println("Error occurred while processing web request: "
                    + url);
            System.err.println("Got the Error Message:  "
                    + throwable.getMessage());
            System.err.println("Stack Trace Follows:\n" + writer.toString());
        }
    }
}