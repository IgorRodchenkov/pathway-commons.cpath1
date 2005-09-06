package org.mskcc.pathdb.util.cache;

import org.mskcc.pathdb.sql.util.TopLevelPathwayUtil;
import org.mskcc.pathdb.xdebug.XDebug;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Automatically Populates the Cache via a Quartz "cron" job.
 *
 * @author Ethan Cerami.
 */
public class AutoPopulateCache implements Job {

    /**
     * Executes the Job.
     *
     * @param jobExecutionContext JobExecutionContext Object.
     * @throws JobExecutionException Job Execution Error.
     */
    public void execute(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        try {
            System.err.println("Running Job:  AutoPopulateCache");
            XDebug xdebug = new XDebug();
            TopLevelPathwayUtil pathwayUtil = new TopLevelPathwayUtil(xdebug);
            pathwayUtil.getTopLevelPathwayList(false);
            pathwayUtil.getTopLevelPathwayList(9606, false);
            System.err.println("Job Done:  AutoPopulateCache");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
