package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.task.GlobalTaskList;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.task.Task;

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
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        append("<table valign=top width=100% cellpadding=2 border=0 "
                + "cellspacing=2 BGCOLOR=#666699>");
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
            append("<TR><TD COLSPAN=2><span class='small'>"
                    + "No Active Tasks" + "</span></TD></TR>");
        }
        for (int i = 0; i < taskList.size(); i++) {
            append("<TR>");
            Task task = (Task) taskList.get(i);
            String name = task.getTaskName();
            ProgressMonitor pMonitor = task.getProgressMonitor();
            String currentMessage = pMonitor.getCurrentMessage();
            double percentComplete = pMonitor.getPercentComplete();
            outputDataField("<span class='small'>" + name + "</span>");
            NumberFormat format = DecimalFormat.getPercentInstance();

            boolean isAlive = task.isAlive();
            String status = null;
            if (isAlive) {
                String perc = format.format(percentComplete);
                status = currentMessage + " [" + perc + "]";
            } else {
                if (task.errorOccurred()) {
                    status = task.getErrorMessage();
                } else {
                    status = pMonitor.getCurrentMessage();
                }
            }
            outputDataField("<span class='small'>" + status + "</span>");
            if (!isAlive) {
                outputDataField("<span class='small'>"
                        + "<A HREF='adminHome.do?action=remove&index=" + i
                        + "'>Clear Task</A>"
                        + "</span>");
            }
            append("</TR>");
        }
    }
}