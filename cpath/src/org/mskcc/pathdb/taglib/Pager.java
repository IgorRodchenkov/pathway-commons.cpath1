package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.PagedResult;

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
     * @param request PagedResult Request Object.
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
        return i - hitsPerPage;
    }

    /**
     * Gets the Start Index Value.
     * @return start indexAllInteractions value.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Gets the End Index Value.
     * @return end indexAllInteractions value.
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Gets URL for Next Page of Results.
     * Returns null if there is no next page.
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
     * @return HTML Text.
     */
    public String getHeaderHtml() {
        String bar = "&nbsp;|&nbsp;";
        StringBuffer text = new StringBuffer();
        text.append((startIndex + 1) + " - " + endIndex);
        text.append(" of " + totalNumHits);
        text.append("&nbsp;");
        String firstUrl = this.getFirstUrl();
        String previousUrl = this.getPreviousUrl();
        String nextUrl = this.getNextUrl();
        if (firstUrl != null) {
            String link = createLink("First Page", firstUrl);
            text.append(bar + link);
        }
        if (previousUrl != null) {
            String link = createLink("Previous " + hitsPerPage, previousUrl);
            text.append(bar + link);
        }
        if (nextUrl != null) {
            String link = createLink("Next " + hitsPerPage, nextUrl);
            text.append(bar + link);
        }
        return text.toString();
    }

    private String createLink(String linkName, String url) {
        String html = new String("<A HREF=\""
                + url + "\">" + linkName + "</A>");
        return html;
    }
}