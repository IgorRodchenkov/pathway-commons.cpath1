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