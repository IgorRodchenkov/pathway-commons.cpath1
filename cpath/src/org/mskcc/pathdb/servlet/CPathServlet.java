package org.mskcc.pathdb.servlet;

import org.apache.struts.action.ActionServlet;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;

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
    }

    /**
     * Initializes Servlet with parameters in web.xml file.
     *
     * @throws ServletException Servlet Initialization Error.
     */
    public void init() throws ServletException {
        super.init();
        System.err.println("Starting up cPath...");
        System.err.println("Reading in init parameters from web.xml");
        PropertyManager manager = PropertyManager.getInstance();
        ServletConfig config = this.getServletConfig();
        String dbHost = config.getInitParameter("db_host");
        String dbUser = config.getInitParameter("db_user");
        String dbPassword = config.getInitParameter("db_password");
        String adminUser = config.getInitParameter("admin_user");
        String adminPassword = config.getInitParameter("admin_password");
        System.err.println("web.xml param:  db_host --> " + dbHost + " [OK]");
        System.err.println("web.xml param:  db_user --> " + dbUser + " [OK]");
        System.err.println("web.xml param:  db_password --> " + dbPassword
                + " [OK]");
        System.err.println("web.xml param:  admin_user --> " + adminUser
                + " [OK]");
        System.err.println("web.xml param:  admin_password --> "
                + adminPassword + " [OK]");

        manager.setProperty(PropertyManager.DB_USER, dbUser);
        manager.setProperty(PropertyManager.DB_PASSWORD,
                dbPassword);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_USER, adminUser);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_PASSWORD, adminPassword);
        verifyDbConnection();

        //  Set Location of TextIndexer based on servlet real path.
        //  Hide within the WEB-INF subdirectory, so that browsers
        //  cannot view textIndex contents directly.
        ServletContext context = getServletContext();
        String dir = context.getRealPath("WEB-INF/"
                + LuceneIndexer.INDEX_DIR_PREFIX);
        manager.setProperty(LuceneIndexer.PROPERTY_LUCENE_DIR, dir);
    }

    /**
     * Verifies Database Connection.  In the event of an error, log
     * messages are written out to catalina.out.
     */
    private void verifyDbConnection() {
        System.err.println("Verifying Database Connection...");
        DaoLog adminLogger = new DaoLog();
        try {
            adminLogger.getLogRecords();
            DaoCPath dao = new DaoCPath();
            int num = dao.getNumEntities(CPathRecordType.PHYSICAL_ENTITY);
            System.err.println("Database Connection -->  [OK]");
        } catch (DaoException e) {
            System.err.println("****  Fatal Error.  Could not connect to "
                    + "database");
            System.err.println("DaoException:  " + e.toString());
        }
    }
}