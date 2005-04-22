/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoLog;
import org.mskcc.pathdb.util.CPathConstants;

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

        manager.setProperty(PropertyManager.DB_USER, dbUser);
        manager.setProperty(PropertyManager.DB_PASSWORD,
                dbPassword);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_USER, adminUser);
        manager.setProperty(BaseAction.PROPERTY_ADMIN_PASSWORD, adminPassword);
        manager.setProperty(CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION,
                psiSchemaUrl);
        manager.setProperty(BaseAction.PROPERTY_WEB_MODE, webMode);
        verifyDbConnection();

        //  Set Location of TextIndexer based on servlet real path.
        //  Hide within the WEB-INF subdirectory, so that browsers
        //  cannot view textIndex contents directly.
        ServletContext context = getServletContext();
        String dir = context.getRealPath("WEB-INF/"
                + LuceneConfig.INDEX_DIR_PREFIX);
        manager.setProperty(LuceneConfig.PROPERTY_LUCENE_DIR, dir);
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
            DaoCPath dao = new DaoCPath();
            System.err.println("Attempting to retrieve Entity Records...");
            int num = dao.getNumEntities(CPathRecordType.PHYSICAL_ENTITY);
            System.err.println("Database Connection -->  [OK]");
        } catch (DaoException e) {
            System.err.println("****  Fatal Error.  Could not connect to "
                    + "database");
            System.err.println("DaoException:  " + e.toString());
        }
    }
}