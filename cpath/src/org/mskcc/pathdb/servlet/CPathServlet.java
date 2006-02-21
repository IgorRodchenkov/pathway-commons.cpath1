// $Id: CPathServlet.java,v 1.26 2006-02-21 22:51:10 grossb Exp $
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

import org.apache.struts.action.ActionServlet;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.sql.dao.DaoWebUI;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.util.cache.AutoPopulateCache;
import org.mskcc.pathdb.util.cache.EhCache;
import org.mskcc.pathdb.form.WebUIBean;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * CPath Servlet.
 *
 * @author Ethan Cerami
 */
public final class CPathServlet extends ActionServlet {

    /**
     * Shutdown the Servlet.
     */
    public void destroy() {
        super.destroy();
        try {
            SchedulerFactory schedFact = new StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.shutdown();
        } catch (SchedulerException e) {
            System.err.println("Error Stopping Quartz Scheduler:  "
                    + e.getMessage());
        }
    }

    /**
     * Initializes Servlet with parameters in web.xml file.
     *
     * @throws ServletException Servlet Initialization Error.
     */
    public void init() throws ServletException {
        super.init();
        System.err.println("Starting up cPath...");
        System.err.println("Using cPath Version:  " + CPathConstants.VERSION);
        System.err.println("Reading in init parameters from web.xml");
        PropertyManager manager = PropertyManager.getInstance();
        ServletConfig config = this.getServletConfig();
        String dbHost = config.getInitParameter("db_host");
        String dbUser = config.getInitParameter("db_user");
        String dbPassword = config.getInitParameter("db_password");
        String adminUser = config.getInitParameter("admin_user");
        String adminPassword = config.getInitParameter("admin_password");
        String webMode = config.getInitParameter(BaseAction.PROPERTY_WEB_MODE);
        String adminModeActive = config.getInitParameter(BaseAction.PROPERTY_ADMIN_MODE_ACTIVE);
        String psiSchemaUrl = config.getInitParameter
                (CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION);
        System.err.println("web.xml param:  db_host --> " + dbHost + " [OK]");
        System.err.println("web.xml param:  db_user --> " + dbUser + " [OK]");
        System.err.println("web.xml param:  db_password --> " + dbPassword
                + " [OK]");
        System.err.println("web.xml param:  admin_user --> " + adminUser
                + " [OK]");
        System.err.println("web.xml param:  admin_password --> "
                + adminPassword + " [OK]");
        System.err.println("web.xml param:  psi_schema_location --> "
                + psiSchemaUrl + " [OK]");
        System.err.println("web.xml param:  "
                + BaseAction.PROPERTY_WEB_MODE + "--> "
                + webMode + " [OK]");
        System.err.println("web.xml param:  "
                + BaseAction.PROPERTY_ADMIN_MODE_ACTIVE + "--> "
                + adminModeActive + " [OK]");

        manager.setProperty(PropertyManager.DB_USER, dbUser);
        manager.setProperty(PropertyManager.DB_PASSWORD,
                dbPassword);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_USER, adminUser);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_PASSWORD, adminPassword);
        manager.setProperty(CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION,
                psiSchemaUrl);
        manager.setProperty(BaseAction.PROPERTY_WEB_MODE, webMode);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_MODE_ACTIVE, adminModeActive);

        storeWebMode(webMode);
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

		// populate the CPathUIConfig
		populateWebUIBean();
    }

    /**
     * Stores the Web Mode in CPathUIConfig.
     * @param webMode web mode String.
     */
    private void storeWebMode(String webMode) {
        int mode;
        if (webMode.equals (CPathUIConfig.PSI_MI)) {
            mode = CPathUIConfig.WEB_MODE_PSI_MI;
        } else if (webMode.equals(CPathUIConfig.BIOPAX)){
            mode = CPathUIConfig.WEB_MODE_BIOPAX;
        } else {
            System.err.println("Web mode not recognized:  " + webMode
                + ".  Defaulting to:  " + CPathUIConfig.BIOPAX);
            mode = CPathUIConfig.WEB_MODE_BIOPAX;
        }
        CPathUIConfig.setWebMode(mode);
    }

    /**
     * Stores the Admin Mode in CPathUIConfig.
     * @param adminModeActive web mode String.
     */
    private void storeAdminModeActive(String adminModeActive) {
        int activeMode;
        if (adminModeActive.equals (String.valueOf(CPathUIConfig.ADMIN_MODE_DEACTIVE))){
            activeMode = CPathUIConfig.ADMIN_MODE_DEACTIVE;
        } else if (adminModeActive.equals(String.valueOf(CPathUIConfig.ADMIN_MODE_ACTIVE))){
            activeMode = CPathUIConfig.ADMIN_MODE_ACTIVE;
        } else {
            System.err.println("Admin mode not recognized, deactivating Admin Mode");
            activeMode = CPathUIConfig.ADMIN_MODE_DEACTIVE;
        }
        CPathUIConfig.setAdminModeActive(activeMode);
    }

    /**
     * Initializes the Quartz Scheduler.
     */
    private void initQuartzScheduler() {
        try {
            Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
            JobDetail jobDetail = new JobDetail("autoPopulateCache",
                    Scheduler.DEFAULT_GROUP, AutoPopulateCache.class);

            //  Currently set to run every 60 minutes
            SimpleTrigger trigger = new SimpleTrigger("cPathTrigger",
                    Scheduler.DEFAULT_GROUP, SimpleTrigger.REPEAT_INDEFINITELY,
                    60L * 60L * 1000L);
            sched.scheduleJob(jobDetail, trigger);
            sched.start();
            System.err.println("Starting Quartz Scheduler:  [OK]");
        } catch (SchedulerException e) {
            System.err.println("Error Starting Quartz Scheduler:  "
                    + e.getMessage());
        }
    }
 
    /**
     * Initializes the Global Cache.
     */
    private void initGlobalCache() {
        try {
            EhCache.initCache();
            System.err.println("Initializing Cache:  [OK]");
        } catch (Exception e) {
            System.err.println("Error Initializing/Prepopulating Cache:  "
                    + e.getMessage());
        }
    }

    /**
     * Verifies Database Connection.  In the event of an error, log
     * messages are written out to catalina.out.
     */
    private void verifyDbConnection() {
        System.err.println("Verifying Database Connection...");
        DaoLog adminLogger = new DaoLog();
        try {
            System.err.println("Attempting to retrieve Log Records...");
            adminLogger.getLogRecords();
            DaoOrganism dao = new DaoOrganism();
            System.err.println("Attempting to retrieve Entity Records...");
            // getNumEntities take a few mins on a large database!
            // int num = dao.getNumEntities(CPathRecordType.PHYSICAL_ENTITY);
            dao.countAllOrganisms();
            System.err.println("Database Connection -->  [OK]");
        } catch (DaoException e) {
            System.err.println("****  Fatal Error.  Could not connect to "
                    + "database");
            System.err.println("DaoException:  " + e.toString());
        }
    }

	/**
	 * Populates WebUIBean within CPathUIConfig.
	 */
	private void populateWebUIBean() {

		// bean we retrieve
		WebUIBean record = null;

		System.err.print("Attempting to populate WebUIBean...");

		// create dao object
		try{
			DaoWebUI dbWebUI = new DaoWebUI();
			record = dbWebUI.getRecord();
		}
		catch (DaoException e) {
            System.err.println("****  Fatal Error.  Could not connect to "
                    + "database");
            System.err.println("DaoException:  " + e.toString());
        }

		// set the bean
		if (record != null){
			CPathUIConfig.setWebUIBean(record);
		}

		// outta here
		System.err.println("SUCCESS!");
	}
}
