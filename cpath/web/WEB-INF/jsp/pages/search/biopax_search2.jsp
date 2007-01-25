<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.taglib.Pager,
                 org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.model.CPathRecord,
                 org.mskcc.pathdb.model.BioPaxEntityTypeMap,
                 org.mskcc.pathdb.model.GlobalFilterSettings,
                 org.mskcc.pathdb.util.html.HtmlUtil,
                 org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils,
                 org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary,
                 org.mskcc.pathdb.util.biopax.BioPaxRecordUtil,
                 org.mskcc.pathdb.model.CPathRecordType,
                 org.mskcc.pathdb.taglib.DbSnapshotInfo,
                 java.util.Set,
                 java.util.Map,
                 java.util.TreeMap"%>
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
    Map<String, Integer> hitByTypeMap =
        (Map<String, Integer>)request.getAttribute(BaseAction.ATTRIBUTE_HITS_BY_RECORD_TYPE_MAP);
	Map sortedHitByTypeMap = new TreeMap(hitByTypeMap);
    BioPaxEntityTypeMap typesMap = new BioPaxEntityTypeMap();
    typesMap.put(GlobalFilterSettings.ALL_ENTITY_TYPES_FILTER_VALUE,
    GlobalFilterSettings.ALL_ENTITY_TYPES_FILTER_VALUE);
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
<%
    if (totalNumHits.intValue() > 0) {
        if (hitByTypeMap != null && hitByTypeMap.size() > 0) {
            out.println("<h3>Narrow Results by Type:</h3>");
            out.println("<ul>");
            for (String type : (Set<String>)sortedHitByTypeMap.keySet()) {
			    // label
			    String plain = (type.equals(GlobalFilterSettings.ALL_ENTITY_TYPES_FILTER_VALUE)) ? "All Types" :
			        (String)typesMap.get(type);
			    String label = plain + " (" + hitByTypeMap.get(type) + ")";
			    // output an ahref or text string for each type 
			    if (type.equals(keyType)) {
                    out.println("<font size=\"2\"><li>" + label + "</li></font>");
			    }
			    else {
			        out.println("<li>" +
				                "<a href='webservice2.do?version=1.0&q=" + protocolRequest.getQuery() +
				                "&format=html&cmd=get_by_keyword&"
					            + GlobalFilterSettings.ENTITY_TYPES_FILTER_NAME + "=" + type +
				                "'>" + label +
				                "</a>" +
				                "</li>");
			    }
			}
	        out.println("</ul>");
        }
    }
%>
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
	    scoreboardWidth = (percentage == 1 && scoreboardWidth == 0) ? 1 : scoreboardWidth;
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
			out.println("<img src=\"jsp/images/relevance.gif\" width=\"" +
			            String.valueOf(scoreboardWidth) +
			            "\" height=\"" + SCOREBOARD_HEIGHT + "\" alt=\"" +
			            String.valueOf(percentage) +
			            "%\">" + spacerString);
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
