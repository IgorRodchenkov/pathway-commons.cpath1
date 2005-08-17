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
package org.mskcc.pathdb.test.schemas.externalDb;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.schemas.externalDb.ExternalDbLinkTester;
import org.mskcc.pathdb.schemas.externalDb.ExternalDbXmlUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the ExternalDbLinkTester class.
 *
 * @author Ethan Cerami
 */
public class TestExternalDbLinkTester extends TestCase {

    /**
     * Tests the Link Tester.
     *
     * @throws Exception All Exceptions.
     */
    public void testExternalDbLinkTester() throws Exception {
        File file = new File("testData/externalDb/external_db.xml");
        ExternalDbXmlUtil util = new ExternalDbXmlUtil(file);
        ArrayList dbList = util.getExternalDbList();
        ExternalDatabaseRecord dbRecord =
                (ExternalDatabaseRecord) dbList.get(0);
        int statusCode = ExternalDbLinkTester.checkSampleLink(dbRecord);
        assertEquals(200, statusCode);
    }

    /**
     * Gets Test Description.
     * @return Description.
     */
    public String getName() {
        return "Test live HTTP connection to a sample external database";
    }

}
