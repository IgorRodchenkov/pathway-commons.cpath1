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
    //Set<String> dataSourceSet = (Set<String>) request.getAttribute
    //        (BaseAction.ATTRIBUTE_DATA_SOURCE_SET);
    Map<Long,Set<String>> recordDataSources = (Map<Long,Set<String>>) request.getAttribute
            (BaseAction.ATTRIBUTE_DATA_SOURCES);
    Map<Long,Float> scores = (Map<Long,Float>) request.getAttribute
            (BaseAction.ATTRIBUTE_SCORES);
    String organismFlag = request.getParameter(ProtocolRequest.ARG_ORGANISM);
    String keyType = (String)request.getParameter(GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME);
    String keyDataSource = (String)request.getParameter(GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME);
%>
<script type="text/javascript">
    //  Toggles details on single row
    function toggleDetails (id) {
        YAHOO.log ("Toggling row with cPathID:  " + id);
        var elements = new Array();
        elements[0] = document.getElementById(id + "_datasources");
        var current = YAHOO.util.Dom.getStyle(elements[0], 'display');
        YAHOO.log ("Current Display Style is set to:  " + current);
        var toggleImage = document.getElementById (id + "_image");
        if (current == false || current == "none" || current == null) {
            YAHOO.util.Dom.setStyle(elements, 'display', 'inline');
            if (toggleImage != null) {
                toggleImage.innerHTML = "<img src='jsp/images/close.gif'>";
            }
        } else {
            YAHOO.util.Dom.setStyle(elements, 'display', 'none');
            if (toggleImage != null) {
                toggleImage.innerHTML = "<img src='jsp/images/open.gif'>";
            }
        }
    }
</script>
<%!
private String getInspectorButtonHtml (long cPathId) {
	return "<div class='toggle_details'><a "
        + "title='Toggle Record Details' onClick=\"toggleDetails('cpath_" + cPathId
        + "')\"><div id='cpath_" + cPathId + "_image' class='toggleImage'>"
        + "<img src='jsp/images/open.gif'/></div></a>";
}
private String getDetailsHtml (long cPathId, String label, String html) {
    return ("<div id='cpath_" + cPathId + "_" + label +"' class='details'>"
        + html + "</div>");
}
private String getDataSourceHtml(long cPathId, Map<Long,Set<String>> recordDataSources) {
		StringBuffer html = new StringBuffer();
		html.append("<p><b>Data Sources:</b></p>\n\r");
		html.append("<ul>\n\r");
		// loop here
		for (String dataSource : (Set<String>)recordDataSources.get(cPathId)) {
     		html.append("<li>");
		    html.append(dataSource);
		    html.append("</li>\n\r");
		}
		html.append("</ul>\n\r");
		return html.toString();
}
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
                "relevant records:<br>");
    //out.println("<ul>");
	//for (String dataSource : dataSourceSet) {
	//    out.println("<li>" + dataSource + "</li>");
	//}
	//out.println("</ul>");
	//out.println("</p>");
%>
	<div class="splitcontentleft">
    <jsp:include page="./narrow-by-type.jsp" flush="true" />
    <jsp:include page="./narrow-by-datasource.jsp" flush="true" />
    <jsp:include page="../../global/currentFilterSettings.jsp" flush="true" />
	</div>
	<div class="splitcontentright">
<%
	Pager pager = new Pager (protocolRequest, totalNumHits.intValue());
	out.println("<div class='search_buttons'>");
	out.println("<h3>" + pager.getHeaderHtmlForSearchPage("white", GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME + "=" + keyType + "&" + GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME + "=" + keyDataSource) + "</h3>");
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
		    ("<td><img src=\"jsp/images/spacer.gif\" width=\"" + String.valueOf(MAX_SCOREBOARD_WIDTH-scoreboardWidth) + "\" height=\"" + SCOREBOARD_HEIGHT + "\" alt=\"" + String.valueOf(percentage) + "%\"></td>") : "";
        try {
            BioPaxRecordSummary summary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(summary);
            out.println("<tr valign=\"top\">");
			// score bar
			//out.println("<th align=left>" + String.valueOf(score) + "</th>");
			out.println("<th align=left width=\"" + MAX_SCOREBOARD_WIDTH + "\">");
			out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" bgcolor=\"#ffffff\">");
			out.println("<div id='scoreboard'><tr>");
			out.println("<tr><td><img src=\"jsp/images/relevance.gif\" width=\"" +
			            String.valueOf(scoreboardWidth) +
			            "\" height=\"" + SCOREBOARD_HEIGHT + "\" alt=\"" +
			            String.valueOf(percentage) +
			            "%\">" + "</td>" + spacerString);
			out.println("</tr></div>");
		    out.println("</table>");
			out.println("</th>");
			// record header
            out.println("<th align=left width=\"60%\">");
			out.println("<a href=\"" + url + "\">" + header + "</a>");
            out.println("</th>");
			// inspection button
			out.println("<th align=right>");
			out.println(getInspectorButtonHtml(record.getId()));
			out.println("</th>");
			out.println("</tr>");
			// data sources
			//if (record.getSnapshotId () > -1) {
			    out.println("<tr><td colspan=3>");
			    out.println(getDetailsHtml(record.getId(),
			                "datasources",
                            getDataSourceHtml(record.getId(), recordDataSources)));
			                //DbSnapshotInfo.getDbSnapshotHtml(record.getSnapshotId())));
			    out.println("</td></tr>");
		    //}
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
