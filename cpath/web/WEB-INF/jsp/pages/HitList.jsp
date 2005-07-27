<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 java.util.ArrayList,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.assembly.XmlAssembly,
                 org.mskcc.pathdb.taglib.Pager,
                 org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.model.CPathRecord,
                 org.mskcc.pathdb.util.xml.XmlUtil,
                 org.mskcc.pathdb.util.html.HtmlUtil"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "cPath::Search Results"); %>
<jsp:include page="../global/header.jsp" flush="true" />
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

<div id='axial' class='h3'>
<h3>Search Results</h3>
</div>

<table border='0' cellspacing='0' cellpadding='3' width='100%'>

<% if (totalNumHits.intValue() ==0) { %>
    <tr class='a'>
    <td colspan=4>No Matching Patwhays/Interactions Found.
        Please try again.</td>
    </tr>
<% } else {
    Pager pager = new Pager (protocolRequest, totalNumHits.intValue());
    int startIndex = pager.getStartIndex();
    out.println("<tr><td colspan=2>");
    out.println(pager.getHeaderHtml());
    out.println("</td>");
    protocolRequest.setFormat(ProtocolConstants.FORMAT_PSI_MI);
    protocolRequest.setStartIndex(startIndex);
    String psiUrl = protocolRequest.getUri();
    out.println("<td width=25% align=\"right\">");
    out.println("<IMG SRC=\"jsp/images/xml_doc.gif\">&nbsp;");
    out.println("<A HREF=\"" + psiUrl + "\">PSI-MI Format</A>");
    protocolRequest.setFormat(ProtocolConstants.FORMAT_BIO_PAX);
    String bioPaxUrl = protocolRequest.getUri();
    out.println("<BR><IMG SRC=\"jsp/images/xml_doc.gif\">&nbsp;");
    out.println("<A HREF=\"" + bioPaxUrl + "\">BioPAX Format</A>");
    out.println("</td>");
    out.println("</tr>");
%>
    <TR>
    <TD></TD>
    </TR>
<%
    DaoCPath dao = DaoCPath.getInstance();
    for (int i=0; i<cpathIds.length; i++) {
        CPathRecord record = dao.getRecordById(cpathIds[i]);
        int index = startIndex + i + 1;
        out.println("<tr>");
        out.println("<th width='25%'>");
        out.println(index);
        out.println(". cPath Record");
        out.println("</th>");
        out.println("<th colspan=2>");
        String url = "bb_web.do?id=" + record.getId();
        out.println("[<A HREF=\"" + url + "\">View Details</A>]");
        out.println("</th>");
        out.println("</tr>");
        out.println("<tr>");
    %>

<% if (record != null) { %>
<TR>
    <TD>cPath ID:</TD>
    <TD><%= record.getId() %></TD>
</TR>
<TR>
    <TD>Name:</TD>
    <TD><%= record.getName() %></TD>
</TR>
<TR>
    <TD>Description:</TD>
    <TD><%= record.getDescription() %></TD>
</TR>
<TR>
    <TD>Type:</TD>
    <TD><%= record.getType() %></TD>
</TR>
<TR>
    <TD>Specific Type:</TD>
    <TD><%= record.getSpecificType() %></TD>
</TR>
<TR>
    <TD>XML Type:</TD>
    <TD><%= record.getXmlType().toString() %></TD>
</TR>
<%
    if (protocolRequest.getQuery() != null
            && protocolRequest.getQuery().length() > 0) {
%>
<TR>
    <TD>Hit Fragment:</TD>
    <TD><SMALL>... <%= HtmlUtil.truncateLongWords(fragments[i], 40) %>...</SMALL></TD>
</TR>
<% } %>
<TR>
    <TD>&nbsp;</TD>
</TR>
    <% } %>
    <% } %>
    <% } %>
</TABLE>
<jsp:include page="../global/footer.jsp" flush="true" />
