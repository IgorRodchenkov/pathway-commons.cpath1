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

<div id='apphead'>
<%
    String q = protocolRequest.getQuery();
    if (q != null && q.trim().length() > 0) {
        out.println("<h2>Search Results:  " + q + "</h2>");
    }
%>
</div>

<table border='0' cellspacing='0' cellpadding='3' width='100%'>
<% if (totalNumHits.intValue() ==0) { %>
    <tr class='a'>
    <td colspan=4>No Matching Records Found. Please try again.</td>
    </tr>
<% } else {
    Pager pager = new Pager (protocolRequest, totalNumHits.intValue());
    out.println("<tr><td colspan=2>");
    out.println("<div class=\"functnbar\">");
    out.println(pager.getHeaderHtml());
    out.println("</div></td>");
    out.println("</tr>");
%>
    <TR>
    <TD></TD>
    </TR>
<%
    DaoCPath dao = DaoCPath.getInstance();
    for (int i=0; i<cpathIds.length; i++) {
        CPathRecord record = dao.getRecordById(cpathIds[i]);
        out.println("<tr>");
        out.println("<td colspan=2>");
        String url = "record.do?id=" + record.getId();
        out.println("<A HREF=\"" + url + "\">" + record.getName() + "</A>");
        out.println("</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>" + HtmlUtil.truncateLongWords(fragments[i], 40));
        out.println("<BR>&nbsp;</td>");
        out.println("</tr>");
    }
    out.println("<tr><td colspan=2>");
    out.println("<div class=\"functnbar2\">");
    out.println(pager.getHeaderHtml());
    out.println("</div>");
    out.println("</td>");
    out.println("</tr>");
}
%>
</TABLE>
<jsp:include page="../../global/footer.jsp" flush="true" />
