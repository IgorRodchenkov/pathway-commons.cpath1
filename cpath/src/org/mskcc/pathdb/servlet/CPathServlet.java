package org.mskcc.pathdb.servlet;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.sql.dao.DaoLog;
import org.mskcc.pathdb.logger.ConfigLogger;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;

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
     * Logger.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Shutdown the Servlet.
     */
    public void destroy() {
        super.destroy();
        logger.info("cPath is shutting down");
    }

    /**
     * Initializes Servlet with parameters in web.xml file.
     * @throws ServletException Servlet Initialization Error.
     */
    public void init() throws ServletException {
        super.init();
        System.err.println("Starting up CBio Data Service...");
        System.err.println("Reading in init parameters from web.xml");
        PropertyManager manager = PropertyManager.getInstance();
        ServletConfig config = this.getServletConfig();
        String dbHost = config.getInitParameter("db_host");
        String dbUser = config.getInitParameter("db_user");
        String dbPassword = config.getInitParameter("db_password");
        String adminUser = config.getInitParameter("admin_user");
        String adminPassword = config.getInitParameter("admin_password");
        String logConfigFile = config.getInitParameter("log_config_file");
        ServletContext ctx = this.getServletContext();
        String realLogPath = ctx.getRealPath(logConfigFile);
        System.err.println("web.xml param:  log_config_file --> "
                + logConfigFile);
        System.err.println("Real Path for log config file:  " + realLogPath);
        System.err.println("web.xml param:  db_host --> " + dbHost);
        System.err.println("web.xml param:  db_user --> " + dbUser);
        System.err.println("web.xml param:  db_password --> " + dbPassword);
        System.err.println("web.xml param:  admin_user --> " + adminUser);
        System.err.println("web.xml param:  admin_password --> "
                + adminPassword);

        manager.setProperty(PropertyManager.DB_USER, dbUser);
        manager.setProperty(PropertyManager.DB_PASSWORD,
                dbPassword);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_USER, adminUser);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_PASSWORD, adminPassword);
        manager.setProperty(PropertyManager.LOG_CONFIG_FILE,
                realLogPath);
        System.err.println("Starting up Log4J Logging System");
        ConfigLogger.configureLogger();
        verifyDbConnection();
        System.err.println("Registering CPath Data Services");
        try {
            RegisterCPathServices.registerServices();
        } catch (DataServiceException e) {
            throw new ServletException(e.toString());
        }
        System.err.println("Data Service Initialization Complete --> OK");
    }

    /**
     * Verifies Database Connection.  In the event of an error, log
     * messages are written out to catalina.out.
     */
    private void verifyDbConnection() {
        System.err.println("Veriyfing Database Connection...");
        DaoLog adminLogger = new DaoLog();
        try {
            adminLogger.getLogRecords();
            DaoCPath dao = new DaoCPath();
            int num = dao.getNumEntities(CPathRecordType.PHYSICAL_ENTITY);
            System.err.println("Number of Physical Entities:  " + num);
            System.err.println("Database Connection -->  OK");
        } catch (DaoException e) {
            System.err.println("****  Error Connecting to Database:");
            System.err.println("DaoException:  " + e.toString());
        }
    }
}