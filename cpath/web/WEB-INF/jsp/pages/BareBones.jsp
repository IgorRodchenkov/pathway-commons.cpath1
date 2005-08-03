<%@ page import="org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.dao.DaoInternalLink,
                 java.util.ArrayList,
                 org.mskcc.pathdb.sql.dao.DaoExternalLink,
                 org.jdom.input.SAXBuilder,
                 org.jdom.Element,
                 org.jdom.Document,
                 java.io.StringReader,
                 org.jdom.xpath.XPath,
                 java.util.List,
                 org.mskcc.pathdb.model.*"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
    String title = "cPath:: Bare Bones Web Site";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
    CPathRecord record = (CPathRecord) request.getAttribute("RECORD");
    ArrayList records = (ArrayList) request.getAttribute("RECORDS");
%>

<jsp:include page="../global/header.jsp" flush="true" />

<div id='axial' class='h3'>
<h3>cPath Record Details</h3>
</div>

<TABLE WIDTH=100%>
<% if (record != null) { %>
<TR>
    <TD>Name:</TD>
    <TD><%= record.getName() %></TD>
    <TD>Type:</TD>
    <TD><%= record.getType() %></TD>
</TR>
<TR>
    <TD>Description:</TD>
    <TD><%= record.getDescription() %></TD>
    <TD>Specific Type:</TD>
    <TD><%= record.getSpecificType() %></TD>
</TR>
<TR>
    <TD>Taxonomy ID:</TD>
    <TD>
        <%
        int taxId= record.getNcbiTaxonomyId();
        if (taxId == CPathRecord.TAXONOMY_NOT_SPECIFIED) {
            out.println(CPathRecord.NA_STRING);
        } else {
            out.println(taxId);
        }
        %>
    </TD>
    <TD>XML Type:</TD>
    <TD><%= record.getXmlType() %></TD>
</TR>
<TR>
</TR>
<TR>
    <TD>XML Content:</TD>
    <TD>
    <%
        String xmlAbbrevUrl = "bb_web.do?format=xml_abbrev&id="
                +record.getId();
        String xmlFullUrl = "bb_web.do?format=xml_full&id="
                +record.getId();
        out.println("<A HREF=\""+ xmlAbbrevUrl + "\">XML Abbrev</A>");
        out.println("&nbsp;&nbsp;");
    out.println("<A HREF=\""+ xmlFullUrl + "\">XML Full</A>");
    %>
    </TD>
</TR>
<% if (record.getXmlType().equals(XmlRecordType.BIO_PAX)) {
        StringReader reader = new StringReader (record.getXmlContent());
        SAXBuilder builder = new SAXBuilder();
        Document bioPaxDoc = builder.build(reader);

        //  Get Root Element
        Element root = bioPaxDoc.getRootElement();

        XPath xpath = XPath.newInstance("/*/bp:AVAILABILITY");
        xpath.addNamespace("bp", root.getNamespaceURI());
        Element e = (Element) xpath.selectSingleNode(root);
        if (e != null) {
            out.println("<TR>");
            out.println("<TD>Availability:</TD>");
            out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
            out.println("</TR>");
        }

        xpath = XPath.newInstance("/*/bp:COMMENT");
        xpath.addNamespace("bp", root.getNamespaceURI());
        e = (Element) xpath.selectSingleNode(root);
        if (e != null) {
            out.println("<TR>");
            out.println("<TD>Comment:</TD>");
            String text = e.getTextNormalize();
            text = text.replaceAll("<BR>", "<P>");
            out.println("<TD COLSPAN=3>" + text + "</TD>");
            out.println("</TR>");
        }

        xpath = XPath.newInstance("/*/bp:DATA-SOURCE/*/bp:NAME");
        xpath.addNamespace("bp", root.getNamespaceURI());
        e = (Element) xpath.selectSingleNode(root);
        if (e != null) {
            out.println("<TR>");
            out.println("<TD>Data Source:</TD>");
            out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
            out.println("</TR>");
        }

        xpath = XPath.newInstance("/*/bp:SYNONYMS");
        xpath.addNamespace("bp", root.getNamespaceURI());
        List list = xpath.selectNodes(root);
        if (list != null && list.size() > 0) {
            out.println("<TR>");
            out.println("<TD>Synonyms:</TD>");
            out.println("<TD COLSPAN=3><UL>");
            for (int i=0; i<list.size(); i++) {
                e = (Element) list.get(i);
                out.println("<LI>" + e.getTextNormalize());
            }
            out.println("</UL></TD>");
            out.println("</TR>");
        }
}
%>
<TR>
    <TD>Internal Links:</TD>
    <TD>
    <%
    DaoInternalLink internalLinker = new DaoInternalLink();
    ArrayList recordList = internalLinker.getTargetsWithLookUp(record.getId());
    if (recordList.size() > 0) {
        out.println("<UL>");
        for (int i=0; i<recordList.size(); i++) {
            CPathRecord link = (CPathRecord) recordList.get(i);
            String url = "bb_web.do?id="+link.getId();
            if (link.getName().equals("N/A")) {
                out.println("<LI><A HREF=\""+url+ "\">"
                    + link.getType()
                    + ":: " + link.getSpecificType()
                    + "</A>");
            } else {
                out.println("<LI><A HREF=\""+url+ "\">"
                    + link.getName() + "</A>");
            }
        }
        out.println("</UL>");
    } else {
        out.println("None");
    }

    %></TD>
</TR>
<TR>
    <TD>External Links:</TD>
    <TD>
    <%
    DaoExternalLink externalLinker = DaoExternalLink.getInstance();
    ArrayList externalLinks =
            externalLinker.getRecordsByCPathId(record.getId());
    if (externalLinks.size() > 0) {
        out.println("<UL>");
        for (int i = 0; i < externalLinks.size(); i++) {
            ExternalLinkRecord link = (ExternalLinkRecord) externalLinks.get(i);
            ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
            String dbId = link.getLinkedToId();
            out.println("<LI>" + dbRecord.getName() + ": " + dbId);
            String uri = link.getWebLink();
            if (uri != null && uri.length() > 0) {
                out.println(" [<A HREF=\""+ uri + "\">Web Link</A>]");
            } else {
                out.println(" [No Web Link Available]");
            }
        }
        out.println("</UL>");
    }  else {
        out.println("None");
    }
    %>
<% }%>
</TABLE>

<jsp:include page="../global/footer.jsp" flush="true" />