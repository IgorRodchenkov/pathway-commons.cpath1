package org.mskcc.pathdb.servlet;

import org.mskcc.pathdb.controller.DataServiceController;
import org.mskcc.pathdb.logger.ConfigLogger;
import org.mskcc.pathdb.util.PropertyManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

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
        logger.info("Data Service Servlet Starting up");
        PropertyManager manager = PropertyManager.getInstance();
        ServletConfig config = this.getServletConfig();
        String dbHost = config.getInitParameter("db_host");
        String dbUser = config.getInitParameter("db_user");
        String dbPassword = config.getInitParameter("db_password");
        String logConfigFile = config.getInitParameter("log_config_file");
        logger.info("Found dbHost:  " + dbHost);
        logger.info("Found dbUser:  " + dbUser);
        logger.info("Found Log Configuration File:  " + logConfigFile);
        manager.setDbHost(dbHost);
        manager.setDbUser(dbUser);
        manager.setDbPassword(dbPassword);
        manager.setLogConfigFile(logConfigFile);
        File file = new File(logConfigFile);
        logger.info("Absolute Path for Log File:  " + file.getAbsolutePath());
        ConfigLogger.configureLogger();
    }
}