// $Id: AutoPopulateCache.java,v 1.3 2006-02-21 22:58:35 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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
