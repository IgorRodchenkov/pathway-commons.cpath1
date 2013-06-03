// $Id: TestTabSpaceTokenizer.java,v 1.6 2006-02-22 22:47:51 grossb Exp $
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
package org.mskcc.pathdb.test.sql.references;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.references.IndexedToken;
import org.mskcc.pathdb.sql.references.TabSpaceTokenizer;

/**
 * Tests the TabSpaceTokenizer Object.
 *
 * @author Ethan Cerami.
 */
public class TestTabSpaceTokenizer extends TestCase {

    /**
     * Tests the TabSpaceTokenizer.
     */
    public void testTokenizer1() {
        String line = "A B \tC D\tE F\t\tG\tH I";
        TabSpaceTokenizer tokenizer = new TabSpaceTokenizer(line);
        int index = 0;
        while (tokenizer.hasMoreElements()) {
            IndexedToken token = (IndexedToken) tokenizer.nextElement();
            if (index == 0) {
                assertEquals("A", token.getToken());
                assertEquals(0, token.getColumnNumber());
            } else if (index == 1) {
                assertEquals("B", token.getToken());
                assertEquals(0, token.getColumnNumber());
            } else if (index == 2) {
                assertEquals("C", token.getToken());
                assertEquals(1, token.getColumnNumber());
            } else if (index == 3) {
                assertEquals("D", token.getToken());
                assertEquals(1, token.getColumnNumber());
            } else if (index == 4) {
                assertEquals("E", token.getToken());
                assertEquals(2, token.getColumnNumber());
            } else if (index == 5) {
                assertEquals("F", token.getToken());
                assertEquals(2, token.getColumnNumber());
            } else if (index == 6) {
                assertEquals("G", token.getToken());
                assertEquals(4, token.getColumnNumber());
            } else if (index == 7) {
                assertEquals("H", token.getToken());
                assertEquals(5, token.getColumnNumber());
            } else if (index == 8) {
                assertEquals("I", token.getToken());
                assertEquals(5, token.getColumnNumber());
            }
            index++;
        }
    }

    /**
     * Tests the TabSpaceTokenizer.
     */
    public void testTokenizer2() {
        //  Try an input line with no tabs;  should work.
        String line = "A B C";
        TabSpaceTokenizer tokenizer = new TabSpaceTokenizer(line);
        int index = 0;
        while (tokenizer.hasMoreElements()) {
            IndexedToken token = (IndexedToken) tokenizer.nextElement();
            if (index == 0) {
                assertEquals("A", token.getToken());
                assertEquals(0, token.getColumnNumber());
            } else if (index == 1) {
                assertEquals("B", token.getToken());
                assertEquals(0, token.getColumnNumber());
            } else if (index == 2) {
                assertEquals("C", token.getToken());
                assertEquals(0, token.getColumnNumber());
            }
            index++;
        }
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the TabSpace Tokenizer, used to parse background "
                + "reference/id files";
    }
}
