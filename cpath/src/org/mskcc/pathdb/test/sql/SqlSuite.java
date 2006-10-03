// $Id: SqlSuite.java,v 1.31 2006-10-03 19:51:45 cerami Exp $
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
package org.mskcc.pathdb.test.sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.pathdb.test.sql.assembly.AssemblySuite;
import org.mskcc.pathdb.test.sql.references.ReferencesSuite;

/**
 * Suite of all SQL Unit Tests.
 *
 * @author Ethan Cerami
 */
public class SqlSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     *
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestDaoImport.class);
        suite.addTestSuite(TestDaoExternalDb.class);
        suite.addTestSuite(TestDaoExternalDbCv.class);
        suite.addTestSuite(TestDaoExternalLink.class);
        suite.addTestSuite(TestDaoCPath.class);
        suite.addTestSuite(TestDaoInternalLink.class);
        suite.addTestSuite(TestUpdatePsiInteractor.class);
        suite.addTestSuite(TestDaoXmlCache.class);
        suite.addTestSuite(TestDaoOrganism.class);
        suite.addTestSuite(TestDaoLog.class);
        suite.addTestSuite(TestDaoBackgroundReference.class);
        suite.addTestSuite(TestDaoIdGenerator.class);
        suite.addTestSuite(TestQueryFileReader.class);
        suite.addTestSuite(TestDaoSourceTracker.class);
        suite.addTestSuite(TestDaoExternalDbSnapshot.class);
        suite.addTestSuite(TestDaoInternalFamily.class);
        suite.addTestSuite(TestSnapshotReader.class);
        suite.addTest(AssemblySuite.suite());
        suite.addTest(ReferencesSuite.suite());
        suite.setName("SQL Database Tests");
        return suite;
    }
}
