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
            append("<TD COLSPAN=2><span class='small'>"
                    + "No Active Tasks" + "</span></TD>");
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
                    img = "icon_error_sml.gif";
                } else {
                    status = pMonitor.getCurrentMessage();
                    img = "icon_success_sml.gif";
                }
            }
            if (img != null) {
                status = "<img src='jsp/images/" + img + "'/>&nbsp;" + status;
            }
            outputDataField("<small>" + status + "</small>");
            if (!isAlive) {
                outputDataField("<small>"
                        + "<IMG SRC='jsp/images/icon_waste_sml.gif'/>"
                        + "&nbsp;<A HREF='adminRemoveTask.do?index="
                        + i + "'>Clear Task</A>"
                        + "</small>");
            } else {
                outputDataField("");
            }
            endRow();
        }
    }
}