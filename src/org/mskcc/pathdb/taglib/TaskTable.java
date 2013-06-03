// $Id: TaskTable.java,v 1.13 2006-12-22 18:30:53 cerami Exp $
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

import org.mskcc.pathdb.task.GlobalTaskList;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.task.Task;
import org.mskcc.pathdb.util.html.HtmlUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying All Active Tasks.
 *
 * @author Ethan Cerami
 */
public class TaskTable extends HtmlTable {

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        createHeader("Active Tasks");
        startTable();
        outputRecords();
        endTable();
    }

    /**
     * Output Active Task List.
     */
    private void outputRecords() {
        GlobalTaskList globalTaskList = GlobalTaskList.getInstance();
        ArrayList taskList = globalTaskList.getTaskList();
        if (taskList.size() == 0) {
            startRow(0);
            append("<td colspan=\"2\">"
                    + "No Active Tasks" + "</td>");
            endRow();
        }
        for (int i = 0; i < taskList.size(); i++) {
            String img = null;
            startRow(i);
            Task task = (Task) taskList.get(i);
            String name = task.getTaskName();
            ProgressMonitor pMonitor = task.getProgressMonitor();
            String currentMessage = pMonitor.getCurrentMessage();
            double percentComplete = pMonitor.getPercentComplete();
            outputDataField(name);
            NumberFormat format = DecimalFormat.getPercentInstance();

            boolean isAlive = task.isAlive();
            String status = null;
            String log = null;
            String stackTrace = "";
            if (isAlive) {
                String perc = format.format(percentComplete);
                status = currentMessage + " [" + perc + "]";
            } else {
                if (task.errorOccurred()) {
                    status = task.getErrorMessage();
                    if (status == null || status.equals("null")) {
                        status = "An Error has occurred.";
                    }
                    img = "icon_error_sml.gif";
                    Throwable t = task.getThrowable();
                    StringWriter writer = new StringWriter();
                    PrintWriter pWriter = new PrintWriter(writer);
                    t.printStackTrace(pWriter);
                    stackTrace = "<p>Stack:Trace:<P> "
                            + "<pre>"
                            + HtmlUtil.convertToHtml(writer.toString())
                            + "</pre>";
                } else {
                    status = pMonitor.getCurrentMessage();
                    img = "icon_success_sml.gif";
                }
                log = pMonitor.getLog();
                if (log != null && log.length() > 0) {
                    log = new String("<p>Log Messages:<pre>"
                            + HtmlUtil.convertToHtml(log)
                            + "</pre>");
                } else {
                    log = "";
                }
            }
            if (img != null) {
                status = "<img src='jsp/images/" + img + "'/>&nbsp;" + status
                        + log + stackTrace;
            }
            outputDataField(status);
            if (!isAlive) {
                outputDataField("<small>"
                        + "<img src='jsp/images/icon_waste_sml.gif'/>"
                        + "&nbsp;<a href='adminRemoveTask.do?index="
                        + i + "'>Clear Task</a>"
                        + "</small>");
            } else {
                outputDataField("");
            }
            endRow();
        }
    }
}
