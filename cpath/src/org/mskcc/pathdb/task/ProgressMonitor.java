package org.mskcc.pathdb.task;

/**
 * Monitors Progress of Long Term Tasks.
 *
 * @author Ethan Cerami.
 */
public class ProgressMonitor {
    private int maxValue;
    private int curValue;
    private String currentMessage;

    /**
     * Gets Percentage Complete.
     * @return double value.
     */
    public double getPercentComplete() {
        if (curValue == 0) {
            return 0.0;
        } else {
            return (curValue / (double) maxValue);
        }
    }

    /**
     * Gets Max Value.
     * @return max value.
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Sets Max Value.
     * @param maxValue Max Value.
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        this.curValue = 0;
    }

    /**
     * Gets Current Value.
     * @return Current Value.
     */
    public int getCurValue() {
        return curValue;
    }

    /**
     * Incremenets the Current Value.
     */
    public void incrementCurValue() {
        curValue++;
    }

    /**
     * Sets the Current Value.
     * @param curValue Current Value.
     */
    public void setCurValue(int curValue) {
        this.curValue = curValue;
    }

    /**
     * Gets the Current Task Message.
     * @return Currest Task Message.
     */
    public String getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Sets the Current Task Message.
     * @param currentMessage Current Task Message.
     */
    public void setCurrentMessage(String currentMessage) {
        this.currentMessage = currentMessage;
    }
}
