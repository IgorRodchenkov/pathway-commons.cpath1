package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.PagedResult;

public class Pager {
    private int HITS_PER_PAGE = 10;
    private PagedResult request;
    private int totalNumHits;
    private int startIndex;
    private int endIndex;

    public Pager(PagedResult request, int totalNumHits) {
        this.request = request;
        this.totalNumHits = totalNumHits;
        this.startIndex = request.getStartIndex();
        this.endIndex = Math.min(startIndex + HITS_PER_PAGE, totalNumHits);
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String getNextUrl() {
        int nextIndex = startIndex + HITS_PER_PAGE;
        if (nextIndex >= totalNumHits) {
            return null;
        } else {
            request.setStartIndex(nextIndex);
            return request.getUri();
        }
    }

    public String getPreviousUrl() {
        int prevIndex = startIndex - HITS_PER_PAGE;
        if (prevIndex < 0) {
            return null;
        } else {
            request.setStartIndex(prevIndex);
            return request.getUri();
        }
    }

    public String getFirstUrl() {
        if (startIndex != 0) {
            request.setStartIndex(0);
            return request.getUri();
        } else {
            return null;
        }
    }

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
            text.append(bar+link);
        }
        if (previousUrl != null) {
            String link = createLink("Previous " + HITS_PER_PAGE, previousUrl);
            text.append(bar+link);
        }
        if (nextUrl != null) {
            String link = createLink("Next " + HITS_PER_PAGE, nextUrl);
            text.append(bar+link);
        }
        return text.toString();
    }

    private String createLink(String linkName, String url) {
        String html = new String("<A HREF=\""
                + url + "\">" + linkName + "</A>");
        return html;
    }
}