package org.mskcc.pathdb.test.taglib;


import junit.framework.TestCase;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.taglib.Pager;

/**
 * Tests the Pager Class.
 *
 * @author Ethan Cerami
 */
public class TestPager extends TestCase {

    /**
     * Tests the Pager Class.
     * @throws Exception All Exceptions.
     */
    public void testAccess1() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery("ABC123");
        request.setStartIndex(0);
        Pager pager = new Pager(request, 250);
        long start = pager.getStartIndex();
        long end = pager.getEndIndex();
        String html = pager.getHeaderHtml();
        String firstUrl = pager.getFirstUrl();
        String nextUrl = pager.getNextUrl();
        String prevUrl = pager.getPreviousUrl();
        assertEquals(0, start);
        assertEquals(20, end);
        assertEquals("webservice?version=1.0&cmd=&db=&uid=ABC123&"
                + "format=&startIndex=20", nextUrl);
        assertEquals(null, firstUrl);
        assertEquals(null, prevUrl);
    }

    /**
     * Tests the Pager Class.
     * @throws Exception All Exceptions.
     */
    public void testAccess2() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery("ABC123");
        request.setStartIndex(20);
        Pager pager = new Pager(request, 250);
        long start = pager.getStartIndex();
        long end = pager.getEndIndex();
        String html = pager.getHeaderHtml();
        String firstUrl = pager.getFirstUrl();
        String nextUrl = pager.getNextUrl();
        String prevUrl = pager.getPreviousUrl();
        assertEquals(20, start);
        assertEquals(40, end);
        assertEquals("webservice?version=1.0&cmd=&db=&uid=ABC123&"
                + "format=&startIndex=0", firstUrl);
        assertEquals("webservice?version=1.0&cmd=&db=&uid=ABC123&"
                + "format=&startIndex=40", nextUrl);
        assertEquals("webservice?version=1.0&cmd=&db=&uid=ABC123&"
                + "format=&startIndex=0", prevUrl);
    }

    /**
     * Tests the Pager Class.
     * @throws Exception All Exceptions.
     */
    public void testAccess3() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
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
        assertEquals("webservice?version=1.0&cmd=&db=&uid=ABC123&"
                + "format=&startIndex=0", firstUrl);
        assertEquals(null, nextUrl);
        assertEquals("webservice?version=1.0&cmd=&db=&uid=ABC123&"
                + "format=&startIndex=220", prevUrl);
    }

    /**
     * Verify that a negative startIndex becomes 0.
     * @throws Exception All Exceptions.
     */
    public void testAccess4() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery("ABC123");
        request.setStartIndex(-11);
        Pager pager = new Pager(request, 255);
        assertEquals(0, pager.getStartIndex());
        assertEquals(20, pager.getEndIndex());
    }

    /**
     * Verify that a startIndex > Total Num Hits shows last page of results.
     * @throws Exception All Exceptions.
     */
    public void testAccess5() throws Exception {
        ProtocolRequest request = new ProtocolRequest();
        request.setQuery("ABC123");
        request.setStartIndex(2800);
        Pager pager = new Pager(request, 255);
        assertEquals(240, pager.getStartIndex());
        assertEquals(255, pager.getEndIndex());
    }

}