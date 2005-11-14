<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.taglib.Pager,
                 org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecord,
                 org.mskcc.pathdb.util.html.HtmlUtil"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "../JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "cPath::Search Results"); %>
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
%>

<div id="content">
<% if (totalNumHits.intValue() ==0) { %>
    <h1>No Matching Records Found. Please try again.</h1>
<% } else {
    Pager pager = new Pager (protocolRequest, totalNumHits.intValue());
    out.println("<div id='search_bar'>");
    out.println(pager.getHeaderHtml());
    out.println("</div>");
%>
    <%
    DaoCPath dao = DaoCPath.getInstance();
    for (int i=0; i<cpathIds.length; i++) {
        CPathRecord record = dao.getRecordById(cpathIds[i]);
        String url = "record.do?id=" + record.getId();
        out.println("<div id='search_name'>" +
                "<A HREF=\"" + url + "\">" + record.getName() + "</A></div>");
        out.println("<div id='search_blob'>"
                + HtmlUtil.truncateLongWords(fragments[i], 40)
                +"</div>");
    }
    out.println("<div id='search_bar'>");
    out.println(pager.getHeaderHtml());
    out.println("</div>");
}
%>
</div>
<jsp:include page="../../global/footer.jsp" flush="true" />
