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
package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.dao.DaoIdGenerator;

/**
 * Tests the DaoID Generator Class.
 *
 * @author Ethan Cerami
 */
public class TestDaoIdGenerator extends TestCase {

    /**
     * Tests the ID Generator.
     *
     * @throws Exception All Exceptions.
     */
    public void testIdGenerator() throws Exception {
        DaoIdGenerator dao = new DaoIdGenerator();

        //  Reset the ID Generator, so that we start at 0.
        dao.resetIdGenerator();

        //  Get Two IDs
        int id1 = parseId(dao.getNextId());
        int id2 = parseId(dao.getNextId());

        //  Verify that the IDs are sequential, and start at 0.
        assertEquals(0, id1);
        assertEquals(1, id2);
    }

    /**
     * Parses an ID String with a cPath Prefix.
     */
    private int parseId(String idStr) {
        return Integer.parseInt
                (idStr.substring
                (DaoIdGenerator.CPATH_LOCAL_ID_PREFIX.length()));
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the MySQL IdGenerator Data Access Object (DAO)";
    }
}
