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
package org.mskcc.pathdb.task;


/**
 * Wrapper for Background Threads.
 *
 * @author Ethan Cerami
 */
public abstract class Task extends Thread {
    protected boolean verbose;
    private String taskName;
    private Exception exception;
    private int verboseCounter = 0;
    protected ProgressMonitor pMonitor;

    /**
     * Constructor.
     *
     * @param taskName Task Name.
     */
    public Task(String taskName) {
        GlobalTaskList taskList = GlobalTaskList.getInstance();
        taskList.addTask(this);
        this.taskName = taskName;
    }

    /**
     * Sets Verbose Flag.
     *
     * @param verbose Verbose Flag.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Gets the Task Name.
     *
     * @return Task Name.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Conditionally Output Message to System.out.
     *
     * @param msg User Message.
     */
    public void outputMsg(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }

    /**
     * Detects if an error has occurred.
     *
     * @return true or false.
     */
    public boolean errorOccurred() {
        return (exception != null) ? true : false;
    }

    /**
     * Gets the Error Message.
     *
     * @return Error Message.
     */
    public String getErrorMessage() {
        return exception.getMessage();
    }

    /**
     * Sets the Exception Object.
     *
     * @param e Exception Object.
     */
    public void setException(Exception e) {
        this.exception = e;
    }

    /**
     * Gets the Progress Monitor.
     *
     * @return Progress Monitor object.
     */
    public abstract ProgressMonitor getProgressMonitor();
}