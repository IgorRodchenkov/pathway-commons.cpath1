<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.taglib.Pager,
                 org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecord,
                 org.mskcc.pathdb.model.GlobalFilterSettings,
                 org.mskcc.pathdb.util.html.HtmlUtil,
                 org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils,
                 org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary,
                 org.mskcc.pathdb.util.biopax.BioPaxRecordUtil,
                 org.mskcc.pathdb.taglib.DbSnapshotInfo,
                 java.util.Set,
                 java.util.Map"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "../JspError.jsp" %>
<%	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Search Results"); %>
<jsp:include page="../../global/redesign/header.jsp" flush="true" />
<%
    final int MAX_SCOREBOARD_WIDTH = 62;
    final int SCOREBOARD_HEIGHT = 6;

    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    long cpathIds[] = (long[])
            request.getAttribute(BaseAction.ATTRIBUTE_CPATH_IDS);
    Integer totalNumHits = (Integer)
            request.getAttribute(BaseAction.ATTRIBUTE_TOTAL_NUM_HITS);
    String fragments[] = (String []) request.getAttribute
            (BaseAction.ATTRIBUTE_TEXT_FRAGMENTS);
    Set<String> dataSources = (Set<String>) request.getAttribute
            (BaseAction.ATTRIBUTE_DATA_SOURCES);
    Map<Long,Float> scores = (Map<Long,Float>) request.getAttribute
            (BaseAction.ATTRIBUTE_SCORES);
    String organismFlag = request.getParameter(ProtocolRequest.ARG_ORGANISM);
    String keyType = (String)request.getParameter(GlobalFilterSettings.ENTITY_TYPES_FILTER_NAME);
%>
<%
if (protocolRequest.getQuery() != null) {
%>
<h1>Searched for:  <%= protocolRequest.getQuery() %></h1>
<%
}
%>
<%
if (totalNumHits.intValue() == 0) {
%>
    <p>No Matching Records Found. Please try again.</p>
<%
}
else {
    out.println("<p>");
    out.println("Pathway Commons completed your search for \"<i>" +
                protocolRequest.getQuery() + "</i>\" " +
                "and found <b>" + totalNumHits.intValue() + "</b> " +
                "relevant records in:<br>");
    out.println("<ul>");
	for (String dataSource : dataSources) {
	    out.println("<li>" + dataSource + "</li>");
	}
	out.println("</ul>");
	out.println("</p>");
%>
	<div class="splitcontentleft">
    <jsp:include page="./narrow-by-type.jsp" flush="true" />
    <jsp:include page="../../global/currentFilterSettings.jsp" flush="true" />
	</div>
	<div class="splitcontentright">
<%
	Pager pager = new Pager (protocolRequest, totalNumHits.intValue());
	out.println("<div class='search_buttons'>");
	out.println("<h3>" + pager.getHeaderHtmlForSearchPage("white", GlobalFilterSettings.ENTITY_TYPES_FILTER_NAME + "=" + keyType) + "</h3>");
	out.println ("</div>");
%>
    <table cellpadding="2" cellspacing="0" border="0" width="100%">
<%
    DaoCPath dao = DaoCPath.getInstance();
    for (int i=0; i< cpathIds.length; i++) {
        CPathRecord record = dao.getRecordById(cpathIds[i]);
        String url = "record2.do?id=" + record.getId();
	    // used to render score board graphic
	    Float score = (Float)scores.get(cpathIds[i]);
	    int scoreboardWidth = (int)(score * MAX_SCOREBOARD_WIDTH);
	    int percentage = (int)(score * 100);
	    // some massaging to handle scores very close to zero, but not zero
	    scoreboardWidth = (percentage == 1 && scoreboardWidth == 0) ? 1 : scoreboardWidth;
	    scoreboardWidth = (score > 0.0 && scoreboardWidth == 0) ? 1 :  scoreboardWidth;
	    String spacerString = (scoreboardWidth < MAX_SCOREBOARD_WIDTH) ?
		    ("<img src=\"jsp/images/spacer.gif\" width=\"" + String.valueOf(MAX_SCOREBOARD_WIDTH-scoreboardWidth) + "\" height=\"" + SCOREBOARD_HEIGHT + "\" alt=\"" + String.valueOf(percentage) + "%\">") : "";
        try {
            BioPaxRecordSummary summary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(summary);
            out.println("<tr valign=\"top\">");
			// score bar
			out.println("<th align=left width=\"" + MAX_SCOREBOARD_WIDTH + "\">");
			out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" bgcolor=\"#ffffff\">");
			out.println("<tr><td valign=\"top\" align=\"left\">");
			out.println("<div class='scoreboard'>");
			out.println("<img src=\"jsp/images/relevance.gif\" width=\"" +
			            String.valueOf(scoreboardWidth) +
			            "\" height=\"" + SCOREBOARD_HEIGHT + "\" alt=\"" +
			            String.valueOf(percentage) +
			            "%\">" + spacerString);
			out.println("</div>");
		    out.println("</table>");
			out.println("</th>");
			// record header
            out.println("<th align=left width=\"60%\">");
			out.println("<a href=\"" + url + "\">" + header + "</a>");
            out.println("</th>");
			// datasource snapshot info
            out.println("<th align=left>");
            if (record.getSnapshotId () > -1) {
                out.println("<div><small>&gt; ");
                out.println(DbSnapshotInfo.getDbSnapshotHtml (record.getSnapshotId ()));
                out.println("</small></div>");
            }
            out.println("</th>");
            out.println("</tr>");
        } catch (IllegalArgumentException e) {
            out.println("<div>" +
                    "<a href=\"" + url + "\">" + record.getName() + "</a></div>");
        }
        if (organismFlag == null) {
            out.println("<tr><td colspan=\"3\"><div class='search_fragment'>"
                + HtmlUtil.truncateLongWords(fragments[i], 40)
                +"</div></td></tr>");
        }
    }
    out.println("</table>");
	out.println("</div>");
}
%>
<p/>
<jsp:include page="../../global/redesign/footer.jsp" flush="true" />
