/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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