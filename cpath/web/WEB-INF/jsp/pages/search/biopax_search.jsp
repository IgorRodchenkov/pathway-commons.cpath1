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

<%
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Search Results";
	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>
<jsp:include page="../../global/header.jsp" flush="true" />
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
<h2>Searched for:  <%= protocolRequest.getQuery() %></h2>
<% } %>
<% if (totalNumHits.intValue() ==0) { %>
    <h2>No Matching Records Found. Please try again.</h2>
<% } else {
    Pager pager = new Pager (protocolRequest, totalNumHits.intValue());
    out.println("<div class='search_bar'>");
    out.println(pager.getHeaderHtml());
    out.println ("</div>");
    out.println ("<div>");
%>
    <table cellpadding=2 cellspacing=0 width=100%>
    <%
    DaoCPath dao = DaoCPath.getInstance();
    for (int i=0; i<cpathIds.length; i++) {
        CPathRecord record = dao.getRecordById(cpathIds[i]);
        String url = "record.do?id=" + record.getId();
        try {
            BioPaxRecordSummary summary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(summary);
            if (record.getType().equals(CPathRecordType.PATHWAY)) {
                out.println("<tr valign=center bgcolor='#DDDDDD'>");
            } else {
                out.println("<tr valign=center bgcolor='#E1EBF5'>");
            }
            out.println("<td>"
                    + "<A HREF=\"" + url + "\">" + header + "</A>");
            if (record.getSnapshotId () > -1) {
                out.println("<div class=small>&gt; ");
                out.println(DbSnapshotInfo.getDbSnapshotHtml (record.getSnapshotId ()));
                out.println("</div>");
            }
            out.println("</td>");
            out.println("<td valign=center align=right><nobr>");
            out.println("<span class='mini_buttons'>");
            if (record.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
                out.println("<span class='mini_button_1'><A "
                 + "title='View all pathways that contain this physical entity' HREF='"
                 + url + "#pathway_list'>P</A></span>");
                out.println("<span class='mini_button_2'><A "
                 + "title='View all interactions that contain this physical entity' HREF='"
                 + url + "&show_flags=00100#interaction_list'>I</A></span>");
                out.println("<span class='mini_button_3'><A "
                 + "title='View all complexes that contain this physical entity' HREF='"
                 + url + "&show_flags=01000#complex_list'>C</A></span>");
            } else {
                out.println("<span class='mini_button_4'><A "
                 + "title='View all molecules that participate in this pathway' HREF='"
                 + url + "&show_flags=00001#pe_list'>M</A></span>");
                out.println("<span class='mini_button_2'><A "
                 + "title='View all interactions that participate in this pathway' HREF='"
                 + url + "&show_flags=00100#interaction_list'>I</A></span>");
            }
            out.println("</span>");
            out.println("</nobr></td></tr>");
        } catch (IllegalArgumentException e) {
            out.println("<div class='search_name'>" +
                    "<A HREF=\"" + url + "\">" + record.getName() + "</A></div>");
        }
        if (organismFlag == null) {
            out.println("<tr><td colspan=2><div class='search_blob'>"
                + HtmlUtil.truncateLongWords(fragments[i], 40)
                +"</div></td></tr>");
        }
    }
    out.println("</table>");
    out.println("<div class='search_bar'>");
    out.println(pager.getHeaderHtml());
    out.println("</div>");
}
%>
<jsp:include page="../../global/footer.jsp" flush="true" />
