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
package org.mskcc.pathdb.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.test.lucene.IndexerSuite;
import org.mskcc.pathdb.test.protocol.ProtocolSuite;
import org.mskcc.pathdb.test.sql.SqlSuite;
import org.mskcc.pathdb.test.taglib.TagLibSuite;
import org.mskcc.pathdb.test.util.UtilSuite;
import org.mskcc.pathdb.test.web.WebSuite;
import org.mskcc.pathdb.test.xmlrpc.XmlRpcSuite;

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
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(ProtocolSuite.suite());
        suite.addTest(UtilSuite.suite());
        suite.addTest(IndexerSuite.suite());
        suite.addTest(SqlSuite.suite());
        suite.addTest(TagLibSuite.suite());
        suite.addTest(WebSuite.suite());
        suite.addTest(XmlRpcSuite.suite());
        suite.setName("PathDB Tests");
        return suite;
    }

    /**
     * Run the all tests method.
     *
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        PropertyManager manager = PropertyManager.getInstance();
        manager.setProperty(PropertyManager.CPATH_READ_LOCATION,
                "http://localhost:8080/ds/dataservice");
        if (args.length > 0 && args[0] != null && args[0].equals("-ui")) {
            String newargs[] = {"org.mskcc.pathdb.test.AllTest",
                                "-noloading"};
            junit.swingui.TestRunner.main(newargs);
        } else {
            junit.textui.TestRunner.run(suite());
        }
    }

}