package org.mskcc.pathdb.util;

/**
 * This class is used to provide some simple profiling.
 *
 * @author Scooter Morris.
 */
public class Profile {
	long startTime;
	long totalTime;

    /**
     * Constructor.
     */
    public Profile() {
		this.startTime = 0;
		this.totalTime = 0;
	}

    /**
     * Starts timer.
     */
    public void start() {
		this.startTime = System.currentTimeMillis();
	}

    /**
     * Sets checkpoint.
     * @return time elapsed.
     */
    public long checkpoint() {
		long runTime = System.currentTimeMillis()-this.startTime;
		this.totalTime += runTime;
		return runTime;
	}

    /**
     * Stops timer.
     * @param message Message to display.
     */
    public void done(String message) {
		// Get our runtime
		checkpoint();

		System.out.println (message +totalTime + " ms");
		this.totalTime = 0;
	}

    /**
     * Gets total time elapsed.
     * @return time elapsed.
     */
    public long getTotalTime() {
		return this.totalTime;
	}
}