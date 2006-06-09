// $Id: TestBioPaxShowFlag.java,v 1.2 2006-06-09 19:22:04 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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
package org.mskcc.pathdb.test.taglib;

import junit.framework.TestCase;
import org.mskcc.pathdb.taglib.BioPaxShowFlag;

/**
 * Tests the BioPaxShowFlag Object.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxShowFlag extends TestCase {
    private String testName;

    /**
     * Tests the core functionality of the BioPaxShowFlag Object.
     */
    public void testBioPaxShowFlag() {
        testName = "Test the core functionality of the BioPaxShowFlag Object";
        BioPaxShowFlag showFlag = new BioPaxShowFlag();
        String urlParam = showFlag.getUrlParameter();
        assertEquals("show_flags=000", urlParam);
        int value = showFlag.getFlag(BioPaxShowFlag.SHOW_ALL_MOLECULES);
        assertEquals(0, value);

        showFlag.setFlag(BioPaxShowFlag.SHOW_ALL_MOLECULES, 1);
        value = showFlag.getFlag(BioPaxShowFlag.SHOW_ALL_MOLECULES);
        assertEquals(1, value);
        urlParam = showFlag.getUrlParameter();
        assertEquals("show_flags=100", urlParam);

        showFlag.setFlag(BioPaxShowFlag.SHOW_ALL_CHILDREN, 1);
        value = showFlag.getFlag(BioPaxShowFlag.SHOW_ALL_CHILDREN);
        assertEquals(1, value);
        urlParam = showFlag.getUrlParameter();
        assertEquals("show_flags=110", urlParam);

        showFlag = new BioPaxShowFlag("100");
        value = showFlag.getFlag(BioPaxShowFlag.SHOW_ALL_MOLECULES);
        assertEquals(1, value);
    }

    /**
     * Tests the creation of HTML Headers
     */
    public void testHtmlHeader() {
        testName = "Test the creation of HTML Headers";
        BioPaxShowFlag showFlag = new BioPaxShowFlag("100");
        String html = BioPaxShowFlag.createHtmlHeader(10, 100, 1234,
                "Contains the following molecules", showFlag, BioPaxShowFlag.SHOW_ALL_MOLECULES);
        assertTrue(html.indexOf("show_flags=000\">[display 1-10]") > -1);

        showFlag = new BioPaxShowFlag("000");
        html = BioPaxShowFlag.createHtmlHeader(10, 100, 1234,
                "Contains the following molecules", showFlag, BioPaxShowFlag.SHOW_ALL_MOLECULES);
        assertTrue(html.indexOf("show_flags=100\">[display all]") > -1);
    }

    /**
     * Gets the Current Test Name.
     *
     * @return test name.
     */
    public String getName() {
        return "Test the BioPax Show/Hide Pagination Feature:  " + testName;
    }
}