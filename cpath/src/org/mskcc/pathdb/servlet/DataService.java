package org.mskcc.pathdb.servlet;

import org.mskcc.pathdb.controller.DataServiceController;
import org.mskcc.pathdb.logger.ConfigLogger;
import org.mskcc.pathdb.logger.AdminLogger;
import org.mskcc.pathdb.util.PropertyManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.sql.SQLException;

/**
 * Data Service Servlet.
 *
 * @author Ethan Cerami
 */
public final class DataService extends HttpServlet {
    /**
     * Logger.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Responds to a GET request for the content produced by
     * this servlet.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are producing
     *
     * @exception java.io.IOException if an input/output error occurs
     * @exception javax.servlet.ServletException if a servlet error occurs
     */
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        logger.info("Data Servlet Invoked.  Getting live data");
        response.setHeader("Cache-control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        DataServiceController controller = new DataServiceController
                (request, response, this.getServletContext());
        controller.execute();
        PrintWriter writer = response.getWriter();
        writer.flush();
        writer.close();
    }

    /**
     * Shutdown the Servlet.
     */
    public void destroy() {
        super.destroy();
        logger.info("Data Servlet Servlet is shutting down");
    }

    /**
     * Responds to a POST request for the content produced by
     * this servlet.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are producing
     *
     * @exception java.io.IOException if an input/output error occurs
     * @exception javax.servlet.ServletException if a servlet error occurs
     */
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
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
        String logConfigFile = config.getInitParameter("log_config_file");
        ServletContext ctx = this.getServletContext();
        String realLogPath = ctx.getRealPath(logConfigFile);
        System.err.println("web.xml param:  log_config_file --> " + logConfigFile);
        System.err.println("Real Path for log config file:  " + realLogPath);
        System.err.println("web.xml param:  db_host --> " + dbHost);
        System.err.println("web.xml param:  db_user --> " + dbUser);
        System.err.println("web.xml param:  db_password --> " + dbPassword);
        manager.setDbHost(dbHost);
        manager.setDbUser(dbUser);
        manager.setDbPassword(dbPassword);
        manager.setLogConfigFile(realLogPath);
        System.err.println("Starting up Log4J Logging System");
        ConfigLogger.configureLogger();
        verifyDbConnection();
        System.err.println("Data Service Initialization Complete --> OK");
    }

    /**
     * Verifies Database Connection.  In the event of an error, log
     * messages are written out to catalina.out.
     */
    private void verifyDbConnection() {
        System.err.println("Veriyfing Database Connection...");
        AdminLogger adminLogger = new AdminLogger();
        try {
            adminLogger.getLogRecords();
            System.err.println("Database Connection -->  OK");
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("****  Error Connecting to Database");
                System.err.println("SQLException:  " + e.toString());
                System.err.println("Message:  " + e.getMessage());
                System.err.println("Error Code:  " + e.getErrorCode());
                System.err.println("Localized Message:  "
                        + e.getLocalizedMessage());
                System.err.println("SQL State:  " + e.getSQLState());
                e = e.getNextException();
            }
        } catch (ClassNotFoundException e) {
            System.err.println(e.toString());
        }
    }
}