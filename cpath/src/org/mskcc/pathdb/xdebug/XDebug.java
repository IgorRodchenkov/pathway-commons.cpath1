package org.mskcc.pathdb.xdebug;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import java.util.Date;
import java.util.Vector;

/**
 * Encapsulates Real-time debugging information.
 * XDebug provides a simple facility for logging debug messages,
 * and recording debug parameters. Results of XDebug are conditionally
 * displayed at the bottom of the JSP page.
 * @author Ethan Cerami
 */
public class XDebug {
    private Vector messages;
    private Vector parameters;
    private Date startTime;
    private Date stopTime;
    private long timeElapsed;

    /**
     * Constructor.
     */
    public XDebug() {
        messages = new Vector();
        parameters = new Vector();
        startTime = null;
        stopTime = null;
        timeElapsed = -1;
    }

    /**
     *  Logs a new message with the specified color code.
     *  @param caller object that is making the log request
     *  @param msg message to log
     *  @param color color of message, e.g. "RED, "GREEN"
     */
    public void logMsg(Object caller, String msg, String color) {
        Class callerClass = caller.getClass();
        XDebugMessage message = new XDebugMessage
                (callerClass.getName(), msg, color);
        messages.addElement(message);
        Category cat = Category.getInstance(callerClass.getName());
        cat.log(callerClass.getName(), Priority.INFO, msg, null);
    }

    /**
     *  Logs a new message.
     *  @param caller object that is making the log request
     *  @param msg message to log
     */
    public void logMsg(Object caller, String msg) {
        logMsg(caller, msg, "black");
    }

    /**
     *  Adds a new String Parameter.
     *  @param type parameter type code, e.g. COOKIE_TYPE, ENVIRONMENT_TYPE
     *  @param name parameter name
     *  @param value parameter String value
     */
    public void addParameter(int type, String name, String value) {
        XDebugParameter param = new XDebugParameter(type, name, value);
        parameters.addElement(param);
    }

    /**
     *  Adds a new integer Parameter.
     *  @param type    parameter type code, e.g. COOKIE_TYPE, ENVIRONMENT_TYPE
     *  @param name    parameter name
     *  @param value   parameter integer value
     */
    public void addParameter(int type, String name, int value) {
        XDebugParameter param = new XDebugParameter(type, name, value);
        parameters.addElement(param);
    }

    /**
     *  Adds a new boolean Parameter.
     *  @param type    parameter type code, e.g. COOKIE_TYPE, ENVIRONMENT_TYPE
     *  @param name    parameter name
     *  @param value   parameter boolean value
     */
    public void addParameter(int type, String name, boolean value) {
        XDebugParameter param = new XDebugParameter(type, name, value);
        parameters.addElement(param);
    }

    /**
     *  Gets all Debug Messages.
     *  @return Vector of XDebugMessage objects
     */
    public Vector getDebugMessages() {
        return messages;
    }

    /**
     *  Gets all Parameters.
     *  @return Vector of XDebugParameter objects
     */
    public Vector getParameters() {
        return parameters;
    }

    /**
     * Starts the internal timer
     */
    public void startTimer() {
        this.startTime = new Date();
    }

    /**
     * Stops the internal timer
     */
    public void stopTimer() {
        this.stopTime = new Date();
        if (startTime != null) {
            this.timeElapsed = stopTime.getTime() - startTime.getTime();
        }
    }

    /**
     * Gets the total time elapsed (in milliseconds.)
     * @return totalTimeElapsed (ms)
     */
    public long getTimeElapsed() {
        return timeElapsed;
    }
}