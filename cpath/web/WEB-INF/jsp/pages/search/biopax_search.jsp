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
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
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

<div id="content">
<% if (protocolRequest.getQuery() != null) { %>
<h1>Searched for:  <%= protocolRequest.getQuery() %></h1>
<% } %>
<% if (totalNumHits.intValue() ==0) { %>
    <h1>No Matching Records Found. Please try again.</h1>
<% } else {
    Pager pager = new Pager (protocolRequest, totalNumHits.intValue());
    out.println ("<div CLASS ='h3'><h3>");
    out.println(pager.getHeaderHtml());
    out.println ("</h3>");
    out.println ("<div>");
%>
    <%
    DaoCPath dao = DaoCPath.getInstance();
    for (int i=0; i<cpathIds.length; i++) {
        CPathRecord record = dao.getRecordById(cpathIds[i]);
        String url = "record.do?id=" + record.getId();
        try {
            BioPaxRecordSummary summary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(summary);
            out.println("<div class='search_name'>" +
                    "<A HREF=\"" + url + "\">" + header + "</A></div>");
        } catch (IllegalArgumentException e) {
            out.println("<div class='search_name'>" +
                    "<A HREF=\"" + url + "\">" + record.getName() + "</A></div>");
        }
        if (organismFlag == null) {
            out.println("<div class='search_blob'>"
                + HtmlUtil.truncateLongWords(fragments[i], 40)
                +"</div>");
        }
    }
    out.println("<div class='search_bar'>");
    out.println(pager.getHeaderHtml());
    out.println("</div>");
}
%>
</div>
<jsp:include page="../../global/footer.jsp" flush="true" />
