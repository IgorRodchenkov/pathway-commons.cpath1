// $Id: CPathServlet.java,v 1.39 2008-04-09 18:32:41 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.servlet;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.cache.AutoPopulateCache;
import org.mskcc.pathdb.util.cache.EhCache;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Date;

/**
 * CPath Servlet.
 *
 * @author Ethan Cerami
 */
public final class CPathServlet extends ActionServlet {
    private Logger log = Logger.getLogger(CPathServlet.class);

    /**
     * Shutdown the Servlet.
     */
    public void destroy() {
        super.destroy();
        try {
            log.info("Shutting Down cPath...");
            log.info("Shutting Down Quartz Scheduler...");
            SchedulerFactory schedFact = new StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.shutdown();
            log.info("Shutting Down Cache Manager...");
            EhCache.shutDownCache();
        } catch (SchedulerException e) {
            log.error("Error Stopping Quartz Scheduler:  " + e.getMessage());
        }
    }

    /**
     * Initializes Servlet with parameters in web.xml file.
     *
     * @throws ServletException Servlet Initialization Error.
     */
    public void init() throws ServletException {
        super.init();
        log.info("Starting up cPath...");
        log.info("Using cPath Version:  " + CPathConstants.VERSION);
        log.info("Reading in init parameters from web.xml");
        PropertyManager manager = PropertyManager.getInstance();
        ServletConfig config = this.getServletConfig();
        String dbHost = config.getInitParameter("db_host");
        String dbUser = config.getInitParameter("db_user");
        String dbPassword = config.getInitParameter("db_password");
        String adminUser = config.getInitParameter("admin_user");
        String adminPassword = config.getInitParameter("admin_password");
        String webSkin = config.getInitParameter("web_skin");
        String adminModeActive = config.getInitParameter(BaseAction.PROPERTY_ADMIN_MODE_ACTIVE);
        String psiSchemaUrl = config.getInitParameter
                (CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION);
        log.info("web.xml param:  db_host --> " + dbHost + " [OK]");
        log.info("web.xml param:  db_user --> " + dbUser + " [OK]");
        log.info("web.xml param:  db_password --> " + dbPassword
                + " [OK]");
        log.info("web.xml param:  admin_user --> " + adminUser
                + " [OK]");
        log.info("web.xml param:  admin_password --> "
                + adminPassword + " [OK]");
        log.info("web.xml param:  psi_schema_location --> "
                + psiSchemaUrl + " [OK]");
        log.info("web.xml param:  web_skin --> "
                + webSkin + " [OK]");
        log.info("web.xml param:  "
                + BaseAction.PROPERTY_ADMIN_MODE_ACTIVE + "--> "
                + adminModeActive + " [OK]");

        manager.setProperty(PropertyManager.DB_USER, dbUser);
        manager.setProperty(PropertyManager.DB_PASSWORD,
                dbPassword);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_USER, adminUser);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_PASSWORD, adminPassword);
        manager.setProperty(CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION,
                psiSchemaUrl);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_MODE_ACTIVE, adminModeActive);

        CPathUIConfig.setWebSkin(webSkin);
        if (webSkin.equals("default_psi_mi")) {
            CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_PSI_MI);
        }
        
        storeAdminModeActive(adminModeActive);
        String dbName = config.getInitParameter("db_name");
        manager.setProperty(PropertyManager.DB_LOCATION, dbHost);
        manager.setProperty(CPathConstants.PROPERTY_MYSQL_DATABASE, dbName);
        verifyDbConnection();

        //  Set Location of TextIndexer based on servlet real path.
        //  Hide within the WEB-INF subdirectory, so that browsers
        //  cannot view textIndex contents directly.
        ServletContext context = getServletContext();
        String dir = context.getRealPath("WEB-INF/"
                + LuceneConfig.INDEX_DIR_PREFIX);
        manager.setProperty(LuceneConfig.PROPERTY_LUCENE_DIR, dir);

        //  Init the Global Cache
        initGlobalCache();

        //  Start Quartz Scheduler
        initQuartzScheduler();
    }

    /**
     * Stores the Admin Mode in CPathUIConfig.
     *
     * @param adminModeActive web mode String.
     */
    private void storeAdminModeActive(String adminModeActive) {
        int activeMode;
        if (adminModeActive.equals(String.valueOf(CPathUIConfig.INACTIVE))) {
            activeMode = CPathUIConfig.INACTIVE;
        } else if (adminModeActive.equals(String.valueOf(CPathUIConfig.ACTIVE))) {
            activeMode = CPathUIConfig.ACTIVE;
        } else {
            log.error("Admin mode not recognized, deactivating Admin Mode");
            activeMode = CPathUIConfig.INACTIVE;
        }
        CPathUIConfig.setAdminModeActive(activeMode);
    }

    /**
     * Initializes the Quartz Scheduler.
     */
    private void initQuartzScheduler() {
        try {
            Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
            Date date = new Date();
            long time = date.getTime();
            Date scheduledDate = new Date (time + 5000);
            JobDetail jobDetail = new JobDetail("autoPopulateCache",
                    Scheduler.DEFAULT_GROUP, AutoPopulateCache.class);

            SimpleTrigger trigger = new SimpleTrigger("cPathTrigger",
                    Scheduler.DEFAULT_GROUP, scheduledDate);
            sched.start();
            sched.scheduleJob(jobDetail, trigger);
            log.info ("Starting Quartz Scheduler:  [OK]");
            log.info ("Auto Populate Cache will start in 5 seconds...");
        } catch (SchedulerException e) {
            log.error("Error Starting Quartz Scheduler:  ", e);
        }
    }

    /**
     * Initializes the Global Cache.
     */
    private void initGlobalCache() {
        try {
            EhCache.initCache();
            log.info("Initializing Cache:  [OK]");
        } catch (Exception e) {
            log.error("Error Initializing/Prepopulating Cache:  ", e);
        }
    }

    /**
     * Verifies Database Connection.  In the event of an error, log
     * messages are written out to catalina.out.
     */
    private void verifyDbConnection() {
        log.info("Verifying Database Connection...");
        DaoLog adminLogger = new DaoLog();
        try {
            log.info("Attempting to retrieve Log Records...");
            adminLogger.getLogRecords();
            DaoOrganism dao = new DaoOrganism();
            log.info("Attempting to retrieve Entity Records...");
            dao.countAllOrganisms();
            log.info("Database Connection -->  [OK]");
        } catch (DaoException e) {
            log.fatal("****  Fatal Error.  Could not connect to "
                    + "database", e);
        }
    }
}
