package org.mskcc.pathdb.servlet;

import org.mskcc.pathdb.controller.DataServiceController;
import org.mskcc.pathdb.util.PropertyManager;
import org.mskcc.pathdb.util.ConfigLogger;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

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

        logger.info("Data Servlet Invokved.  Getting live data");
        response.setHeader ("Cache-control", "no-cache");
        response.setHeader ("Pragma", "no-cache");
        DataServiceController controller = new DataServiceController
                (request, response, this.getServletContext());
        controller.execute();
        PrintWriter writer = response.getWriter();
        writer.flush();
        writer.close();
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
        doGet (request, response);
    }

    public void init() throws ServletException {
        super.init();
        PropertyManager manager = PropertyManager.getInstance();
        ServletConfig config = this.getServletConfig();
        String gridHost = config.getInitParameter("grid_host");
        String gridUser = config.getInitParameter("grid_user");
        String gridPassword = config.getInitParameter("grid_password");
        String logFile = config.getInitParameter("log_file");
        manager.setGridHost(gridHost);
        manager.setGridUser(gridUser);
        manager.setGridPassword(gridPassword);
        manager.setLogFile(logFile);
        ConfigLogger.configureLogger();
    }
}