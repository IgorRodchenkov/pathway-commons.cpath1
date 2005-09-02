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
package org.mskcc.pathdb.test.taglib;


import junit.framework.TestCase;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.taglib.Pager;

/**
 * Tests the Pager Class.
 *
 * @author Ethan Cerami
 */
public class TestPager extends TestCase {

    /**
     * Tests the Pager Class.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess1() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setMaxHits("20");
        request.setQuery("ABC123");
        request.setStartIndex(0);
        Pager pager = new Pager(request, 250);
        long start = pager.getStartIndex();
        long end = pager.getEndIndex();
        String firstUrl = pager.getFirstUrl();
        String nextUrl = pager.getNextUrl();
        String prevUrl = pager.getPreviousUrl();
        assertEquals(0, start);
        assertEquals(20, end);
        assertEquals("webservice.do?version=1.0&q=ABC123"
                + "&startIndex=20&maxHits=20", nextUrl);
        assertEquals(null, firstUrl);
        assertEquals(null, prevUrl);
    }

    /**
     * Tests the Pager Class.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess2() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setMaxHits("20");
        request.setQuery("ABC123");
        request.setStartIndex(20);
        Pager pager = new Pager(request, 250);
        long start = pager.getStartIndex();
        long end = pager.getEndIndex();
        String firstUrl = pager.getFirstUrl();
        String nextUrl = pager.getNextUrl();
        String prevUrl = pager.getPreviousUrl();
        assertEquals(20, start);
        assertEquals(40, end);
        assertEquals("webservice.do?version=1.0&q=ABC123"
                + "&maxHits=20", firstUrl);
        assertEquals("webservice.do?version=1.0&q=ABC123"
                + "&startIndex=40&maxHits=20", nextUrl);
        assertEquals("webservice.do?version=1.0&q=ABC123"
                + "&maxHits=20", prevUrl);
    }

    /**
     * Tests the Pager Class.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess3() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setMaxHits("20");
        request.setQuery("ABC123");
        request.setStartIndex(240);
        Pager pager = new Pager(request, 255);
        long start = pager.getStartIndex();
        long end = pager.getEndIndex();
        String html = pager.getHeaderHtml();
        String firstUrl = pager.getFirstUrl();
        String nextUrl = pager.getNextUrl();
        String prevUrl = pager.getPreviousUrl();
        assertEquals(240, start);
        assertEquals(255, end);
        assertEquals("webservice.do?version=1.0&q=ABC123"
                + "&maxHits=20", firstUrl);
        assertEquals(null, nextUrl);
        assertEquals("webservice.do?version=1.0&q=ABC123"
                + "&startIndex=220&maxHits=20", prevUrl);
    }

    /**
     * Verify that a negative startIndex becomes 0.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess4() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setMaxHits("20");
        request.setQuery("ABC123");
        request.setStartIndex(-11);
        Pager pager = new Pager(request, 255);
        assertEquals(0, pager.getStartIndex());
        assertEquals(20, pager.getEndIndex());
    }

    /**
     * Verify that a startIndex > Total Num Hits shows last page of results.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess5() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setMaxHits("20");
        request.setQuery("ABC123");
        request.setStartIndex(2800);
        Pager pager = new Pager(request, 255);
        assertEquals(240, pager.getStartIndex());
        assertEquals(255, pager.getEndIndex());
    }


    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the Pager Object, used to generate Next/Previous "
                + "web links";
    }
}