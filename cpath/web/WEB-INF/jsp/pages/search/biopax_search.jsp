<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.taglib.Pager,
                 org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.model.CPathRecord,
                 org.mskcc.pathdb.util.html.HtmlUtil"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary"%>
<%@ page import="org.mskcc.pathdb.util.biopax.BioPaxRecordUtil"%>
<%@ page import="org.mskcc.pathdb.model.CPathRecordType"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "../JspError.jsp" %>
<%	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Search Results"); %>
<jsp:include page="../../global/redesign/header.jsp" flush="true" />
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    long cpathIds[] = (long[])
            request.getAttribute(BaseAction.ATTRIBUTE_CPATH_IDS);
    Integer totalNumHits = (Integer)
            request.getAttribute(BaseAction.ATTRIBUTE_TOTAL_NUM_HITS);
    String fragments[] = (String []) request.getAttribute
            (BaseAction.ATTRIBUTE_TEXT_FRAGMENTS);
    String organismFlag = request.getParameter(ProtocolRequest.ARG_ORGANISM);
%>

<% if (protocolRequest.getQuery() != null) { %>
<div>
<h1>Searched for:  <%= protocolRequest.getQuery() %></h1>
<% } %>
<% if (totalNumHits.intValue() ==0) { %>
    <p>No Matching Records Found. Please try again.</p>
<% } else {
    Pager pager = new Pager (protocolRequest, totalNumHits.intValue());
    out.println("<div class='search_buttons'>");
    out.println(pager.getHeaderHtml());
    out.println ("</div>");
%>
    <table cellpadding="2" cellspacing="0" width="100%">
    <%
    DaoCPath dao = DaoCPath.getInstance();
    for (int i=0; i<cpathIds.length; i++) {
        CPathRecord record = dao.getRecordById(cpathIds[i]);
        String url = "record2.do?id=" + record.getId();
        try {
            BioPaxRecordSummary summary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(summary);
            out.println("<tr valign=\"top\">");
            out.println("<th width=\"60%\">"
                    + "<a href=\"" + url + "\">" + header + "</a>");
            out.println("</th>");
            out.println("<th>");
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
            out.println("<tr><td colspan=\"2\"><div class='search_fragment'>"
                + HtmlUtil.truncateLongWords(fragments[i], 40)
                +"</div></td></tr>");
        }
    }
    out.println("</table>");
}
%>
<p/>
</div>
<jsp:include page="../../global/redesign/footer.jsp" flush="true" />
