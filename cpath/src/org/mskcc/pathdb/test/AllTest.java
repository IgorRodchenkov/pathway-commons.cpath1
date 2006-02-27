// $Id: AllTest.java,v 1.39 2006-02-27 22:46:57 grossb Exp $
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
package org.mskcc.pathdb.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.test.lucene.IndexerSuite;
import org.mskcc.pathdb.test.model.ModelSuite;
import org.mskcc.pathdb.test.protocol.ProtocolSuite;
import org.mskcc.pathdb.test.schemas.SchemaSuite;
import org.mskcc.pathdb.test.sql.SqlSuite;
import org.mskcc.pathdb.test.taglib.TagLibSuite;
import org.mskcc.pathdb.test.task.TaskSuite;
import org.mskcc.pathdb.test.util.UtilSuite;
import org.mskcc.pathdb.test.web.WebSuite;
import org.mskcc.pathdb.test.xmlrpc.XmlRpcSuite;
import org.mskcc.pathdb.util.CPathConstants;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/**
 * Runs all Unit Tests.
 *
 * @author Ethan Cerami
 */
public class AllTest extends TestCase {

    /**
     * The suite method kicks off all of the tests.
     *
     * @return junit.framework.Test
	 * @throws IOException Throwable
     */
    public static Test suite() throws IOException {
        PropertyManager manager = PropertyManager.getInstance();

        //  Load build.properties
        String cpathHome = System.getProperty("CPATH_HOME");
        String separator = System.getProperty("file.separator");
        Properties buildProps = new Properties();
        File file = new File (cpathHome + separator + "build.properties");
        try {
            buildProps.load(new FileInputStream (file));
        } catch (FileNotFoundException e) {
            System.err.println("Error.  Cannot find file:  " + file.getAbsolutePath());
            throw e;
        }

        String dbUser = buildProps.getProperty("db.user");
        String dbPwd = buildProps.getProperty("db.password");
        String dbName = buildProps.getProperty("db.name");
        String dbHost = buildProps.getProperty("db.host");

        manager.setProperty(PropertyManager.DB_USER, dbUser);
        manager.setProperty(PropertyManager.DB_PASSWORD, dbPwd);
        manager.setProperty(CPathConstants.PROPERTY_MYSQL_DATABASE, dbName);
        manager.setProperty(PropertyManager.DB_LOCATION, dbHost);

        TestSuite suite = new TestSuite();
        suite.addTest(ProtocolSuite.suite());
        suite.addTest(ModelSuite.suite());
        suite.addTest(UtilSuite.suite());
        suite.addTest(IndexerSuite.suite());
        suite.addTest(SqlSuite.suite());
        suite.addTest(TaskSuite.suite());
        suite.addTest(TagLibSuite.suite());
        suite.addTest(WebSuite.suite());
        suite.addTest(XmlRpcSuite.suite());
        suite.addTest(SchemaSuite.suite());
        suite.setName("cPath Tests");
        return suite;
    }

    /**
     * Run the all tests method.
     *
     * @param args java.lang.String[]
     * @throws Exception All Errors.
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0] != null && args[0].equals("-ui")) {
            String newargs[] = {"org.mskcc.pathdb.test.AllTest",
                                "-noloading"};
            junit.swingui.TestRunner.main(newargs);
        } else {
            junit.textui.TestRunner.run(suite());
        }
    }
}
