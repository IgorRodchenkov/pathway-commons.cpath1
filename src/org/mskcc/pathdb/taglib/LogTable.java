// $Id: LogTable.java,v 1.13 2006-12-22 21:03:48 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.LogRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;
import org.mskcc.pathdb.util.html.HtmlUtil;

import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying cPath Logs.
 *
 * @author Ethan Cerami
 */
public class LogTable extends HtmlTable {

    /**
     * Start Tag Processing.
     *
     * @throws DaoException Database Error.
     */
    protected void subDoStartTag() throws DaoException {
        createHeader("cPath Error Log Records");
        this.startTable();
        String headers[] = {"Messages"};
        createTableHeaders(headers);
        outputResults();
        endTable();
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputResults() throws DaoException {
        DaoLog adminLogger = new DaoLog();
        ArrayList logRecords = adminLogger.getLogRecords();
        if (logRecords.size() == 0) {
            append("<tr>");
            append("<td colspan='4'>No Log Records in Database.</td>");
            append("</tr>");
        }
        for (int i = 0; i < logRecords.size(); i++) {
            startRow(i - 1);
            LogRecord record = (LogRecord) logRecords.get(i);
            append("<td>");
            append("<b>Error Logged at:  " + record.getDate()
                    + "</b>");
            append("<p>Web URL:  <a href='" + record.getWebUrl()
                    + "'>" + record.getWebUrl() + "</a>");
            append("<p>Remote Host:  " + record.getRemoteHost() + "</p>");
            append("<p>Remote IP:  " + record.getRemoteIp() + "</p>");
            append("<p>Error Message:  " + record.getMessage() +"</p>");
            endRow();
        }
    }
}
