// $Id: Pager.java,v 1.18 2007-12-28 14:44:37 cerami Exp $
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
package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.PagedResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Utility Class for creating next/previous pages.
 *
 * @author Ethan Cerami.
 */
public class Pager {
    private PagedResult request;
    private int totalNumHits;
    private int startIndex;
    private int endIndex;
    private int hitsPerPage;

    /**
     * Constructor.
     *
     * @param request      PagedResult Request Object.
     * @param totalNumHits Total Number of Hits.
     */
    public Pager(PagedResult request, int totalNumHits) {
        this.request = request;
        this.hitsPerPage = request.getMaxHitsInt();
        this.totalNumHits = totalNumHits;
        this.startIndex = Math.max(0, request.getStartIndex());
        if (startIndex >= totalNumHits) {
            this.startIndex = getRevisedStartIndex(totalNumHits);
        }
        this.endIndex = Math.min(startIndex + hitsPerPage, totalNumHits);
    }

    /**
     * Retrieves the last whole page of results.
     */
    private int getRevisedStartIndex(int totalNumHits) {
        int i = 0;
        while (i < totalNumHits) {
            i += hitsPerPage;
        }
        return Math.max(0, i - hitsPerPage);
    }

    /**
     * Gets the Start Index Value.
     *
     * @return start indexAllInteractions value.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Gets the End Index Value.
     *
     * @return end indexAllInteractions value.
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Gets URL for Next Page of Results.
     * Returns null if there is no next page.
     *
     * @return url string or null value.
     */
    public String getNextUrl() {
        int nextIndex = startIndex + hitsPerPage;
        if (nextIndex >= totalNumHits) {
            return null;
        } else {
            request.setStartIndex(nextIndex);
            return request.getUri();
        }
    }

    /**
     * Gets URL for Previous Page of Results.
     * Returns null if there is no previous page.
     *
     * @return url string or null value.
     */
    public String getPreviousUrl() {
        int prevIndex = startIndex - hitsPerPage;
        if (prevIndex < 0) {
            return null;
        } else {
            request.setStartIndex(prevIndex);
            return request.getUri();
        }
    }

    /**
     * Gets URL for First Page of Results.
     * Returns null if this is the first page already.
     *
     * @return url string or null value.
     */
    public String getFirstUrl() {
        if (startIndex != 0) {
            request.setStartIndex(0);
            return request.getUri();
        } else {
            return null;
        }
    }

    /**
     * Gets HTML Header with Next/Previous Links.
     *
     * @return HTML Text.
     */
    public String getHeaderHtml() {
        NumberFormat formatter = new DecimalFormat("#,###,###");
        String bar = "&nbsp;|&nbsp;";
        StringBuffer text = new StringBuffer();
        text.append((startIndex + 1) + " - " + endIndex);
        text.append(" of " + formatter.format(totalNumHits));
        text.append("&nbsp;");
        String firstUrl = this.getFirstUrl();
        String previousUrl = this.getPreviousUrl();
        String nextUrl = this.getNextUrl();
        if (nextUrl != null) {
            String link = createLink("Next " + hitsPerPage, nextUrl);
            text.append(bar + link);
        }
        if (previousUrl != null) {
            String link = createLink("Previous " + hitsPerPage, previousUrl);
            text.append(bar + link);
        }
        if (firstUrl != null) {
            String link = createLink("First Page", firstUrl);
            text.append(bar + link);
        }
        return text.toString();
    }

    /**
     * Gets HTML Header with Next/Previous Links.
     *
     * @return HTML Text.
     */
    public String getHeaderHtmlForSearchPage(String color, String entityTypeParameter) {
        NumberFormat formatter = new DecimalFormat("#,###,###");
        String bar = "&nbsp;|&nbsp;";
        StringBuffer text = new StringBuffer();
		text.append("Showing Results ");
        text.append((startIndex + 1) + " - " + endIndex);
        text.append(" of " + formatter.format(totalNumHits));
        text.append("&nbsp;");
        String firstUrl = this.getFirstUrl();
        String previousUrl = this.getPreviousUrl();
        String nextUrl = this.getNextUrl();
        if (nextUrl != null) {
			nextUrl += ("&" + entityTypeParameter);
            String link = createLink("<font color=\"" + color + "\">Next " + hitsPerPage + "</font>", nextUrl);
            text.append(bar + link);
        }
        if (previousUrl != null) {
			previousUrl += ("&" + entityTypeParameter);
            String link = createLink("<font color=\"" + color + "\">Previous " + hitsPerPage + "</font>", previousUrl);
            text.append(bar + link);
        }
        if (firstUrl != null) {
			firstUrl += ("&" + entityTypeParameter);
            String link = createLink("<font color=\"" + color + "\">First Page</font>", firstUrl);
            text.append(bar + link);
        }
        return text.toString();
    }

    private String createLink(String linkName, String url) {
        String html = new String("<a href=\""
                + url + "\">" + linkName + "</a>");
        return html;
    }
}
