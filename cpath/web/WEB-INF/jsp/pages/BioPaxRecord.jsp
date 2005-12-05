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
                 org.mskcc.pathdb.model.*,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	// ui bean
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();

	// get the cpath record
    CPathRecord record = (CPathRecord) request.getAttribute("RECORD");

	// our biopax entity type 2 plain english hashmap
    BioPaxEntityTypeMap entityTypeMap = new BioPaxEntityTypeMap();

	// set request title attribute
    String title = webUIBean.getApplicationName() + "::" + record.getName();
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);

	// setup for biopax queries
	Element e = null;
	XPath xpath = null;
	StringReader reader = new StringReader (record.getXmlContent());
    SAXBuilder builder = new SAXBuilder();
	Document bioPaxDoc = builder.build(reader);
    Element root = bioPaxDoc.getRootElement();
%>

<jsp:include page="../global/header.jsp" flush="true" />

<% if (record != null) { %>
<div id="apphead">
<h2><%= record.getName() %></h2>
</div>
<% } %>

<% if (record != null) { %>
<div class ='h3'>
	<h3>Common</h3>
</div>
<TABLE WIDTH=100%>
<%
	// short name
	xpath = XPath.newInstance("/*/bp:SHORT-NAME");
   	xpath.addNamespace("bp", root.getNamespaceURI());
	e = (Element) xpath.selectSingleNode(root);
	String shortName = null;
	if (e != null) {
		shortName = e.getTextNormalize();
		if (!shortName.equals(record.getName())){
			out.println("<TR>");
			out.println("<TD>Short Name:</TD>");
			out.println("<TD COLSPAN=3>" + shortName + "</TD>");
			out.println("</TR>");
		}
	}

	// name
	xpath = XPath.newInstance("/*/bp:NAME");
   	xpath.addNamespace("bp", root.getNamespaceURI());
	e = (Element) xpath.selectSingleNode(root);
	if (e != null){
		String name = e.getTextNormalize();
		if ((shortName == null || !shortName.equals(name)) &&
			!name.equals(record.getName())){
				out.println("<TR>");
				out.println("<TD>Name:</TD>");
				out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
				out.println("</TR>");
		}
	}
%>
<TR>
<TD>Specific Type:</TD>
<TD COLSPAN=3><%= entityTypeMap.get(record.getSpecificType()) %></TD>
</TR>
<%
	// organism
	xpath = XPath.newInstance("/*/bp:ORGANISM/*/bp:NAME");
   	xpath.addNamespace("bp", root.getNamespaceURI());
	e = (Element) xpath.selectSingleNode(root);
	if (e != null){
		out.println("<TR>");
		out.println("<TD>Organism:</TD>");
		out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
		out.println("</TR>");
	}

	// synonym
	xpath = XPath.newInstance("/*/bp:SYNONYMS");
    xpath.addNamespace("bp", root.getNamespaceURI());
    List list = xpath.selectNodes(root);
    if (list != null && list.size() > 0) {
    	out.println("<TR>");
        out.println("<TD>Synonyms:</TD>");
        out.println("<TD COLSPAN=3>");
        for (int i=0; i<list.size(); i++) {
        	e = (Element) list.get(i);
            out.println(e.getTextNormalize());
        }
        out.println("</TD>");
        out.println("</TR>");
    }

	// comment
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
%>
<TR>
<TD>External Links:</TD>
<%
	// xrefs
    DaoExternalLink externalLinker = DaoExternalLink.getInstance();
    ArrayList externalLinks =
            externalLinker.getRecordsByCPathId(record.getId());
    if (externalLinks.size() > 0) {
        for (int i = 0; i < externalLinks.size(); i++) {
			out.println("<TD COLSPAN=3>");
            ExternalLinkRecord link = (ExternalLinkRecord) externalLinks.get(i);
            ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
            String dbId = link.getLinkedToId();
            out.println(dbRecord.getName() + ": " + dbId);
            String uri = link.getWebLink();
            if (uri != null && uri.length() > 0) {
                out.println("[<A HREF=\""+ uri + "\">Web Link</A>]");
            } else {
                out.println("[No Web Link Available]");
            }
			out.println("</TD></TR>");
        }
    	out.println("<TR>");
	}
	else{
        out.println("None");
    }
%>
</TD>
</TR>
<%
	// data source
    xpath = XPath.newInstance("/*/bp:DATA-SOURCE/*/bp:NAME");
    xpath.addNamespace("bp", root.getNamespaceURI());
    e = (Element) xpath.selectSingleNode(root);
    if (e != null) {
        out.println("<TR>");
        out.println("<TD>Data Source:</TD>");
        out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
        out.println("</TR>");
    }

	// availability
	xpath = XPath.newInstance("/*/bp:AVAILABILITY");
    xpath.addNamespace("bp", root.getNamespaceURI());
    e = (Element) xpath.selectSingleNode(root);
    if (e != null) {
        out.println("<TR>");
        out.println("<TD>Availability:</TD>");
        out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
        out.println("</TR>");
    }
%>
</TABLE>

<% // physical interactions %>
<cbio:pathwayInteractionTable/>

<% } // record not equal to null %>
<jsp:include page="../global/footer.jsp" flush="true" />