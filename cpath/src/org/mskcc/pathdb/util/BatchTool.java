package org.mskcc.pathdb.util;

import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;

/**
 * Abstract Base Class for all Command Line Batch Tools.
 *
 * @author Ethan Cerami
 */
public abstract class BatchTool {
    private boolean runningFromCommandLine;
    private XDebug xdebug;

    /**
     * Constructor.
     * @param runningFromCommandLine Indicates that we are running from console.
     * @param xdebug XDebug Object.
     */
    public BatchTool(boolean runningFromCommandLine, XDebug xdebug) {
        this.runningFromCommandLine = runningFromCommandLine;
        this.xdebug = xdebug;
    }

    /**
     * Conditionally output messages to the console and  xdebug
     * @param message User message.
     */
    protected void outputMsg(String message) throws IOException {
        if (runningFromCommandLine) {
            System.out.println(message);
        }
        if (xdebug != null) {
            xdebug.logMsg(this, message);
        }
    }
}
