<%@ page import="org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.BaseAction,
				 org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.sql.dao.DaoInternalLink,
                 org.mskcc.pathdb.sql.dao.DaoExternalLink,
                 java.util.Arrays,
                 java.util.ArrayList,
                 java.util.HashSet,
				 java.util.Collections,
                 org.jdom.input.SAXBuilder,
                 org.jdom.Element,
                 org.jdom.Document,
				 org.jdom.Attribute,
				 org.mskcc.pathdb.model.PhysicalInteraction,
				 org.mskcc.pathdb.schemas.biopax.RdfUtil,
				 org.mskcc.pathdb.schemas.biopax.RdfConstants,
				 org.mskcc.pathdb.schemas.biopax.BioPaxConstants,
				 org.mskcc.pathdb.schemas.biopax.InteractionParser,
				 org.mskcc.pathdb.schemas.biopax.MemberMolecules,
				 org.mskcc.pathdb.schemas.biopax.MemberPathways,
				 org.mskcc.pathdb.schemas.biopax.RecordLinkSorter,
                 java.io.StringReader,
                 org.jdom.xpath.XPath,
                 java.util.List,
                 org.mskcc.pathdb.model.*,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.action.admin.AdminWebLogging"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	// query string
	String queryString = request.getQueryString();
	if (queryString == null){
		queryString = "";
	}

	// ui bean
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();

	// get the cpath record
    CPathRecord record = (CPathRecord) request.getAttribute("RECORD");

	// our biopax entity type 2 plain english hashmap
    BioPaxEntityTypeMap entityTypeMap = new BioPaxEntityTypeMap();

	// create our biopaxConstants "helper" class
	BioPaxConstants biopaxConstants = new BioPaxConstants();

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

<div id="apphead">
<%
	// short name
	xpath = XPath.newInstance("/*/bp:SHORT-NAME");
   	xpath.addNamespace("bp", root.getNamespaceURI());
	e = (Element) xpath.selectSingleNode(root);
	String shortName = null;
	if (e != null) {
		shortName = e.getTextNormalize();
		out.println("<h2>" + shortName + "</h2>");
	}
	String name = null;
	if (shortName == null){
		xpath = XPath.newInstance("/*/bp:NAME");
	   	xpath.addNamespace("bp", root.getNamespaceURI());
		e = (Element) xpath.selectSingleNode(root);
		if (e != null){
			name = e.getTextNormalize();
			out.println("<h2>" + name + "</h2>");	
		}
	}
%>
</div>

<% if (record != null) { %>

<%	
	// xml abbrev content link - log/debug mode only
	if (queryString.indexOf("debug=1") != -1){
		String xmlAbbrevUrl = "record.do?format=xml_abbrev&id=" + record.getId();
		out.println("<a href=\"" + xmlAbbrevUrl + "\">XML Content (Abbrev)</a>");
		out.println("<br>");
	}
%>

<div class ='h3'>
	<h3>Basic Information</h3>
</div>
<TABLE WIDTH=100%>
<%
	// name
	if (name == null){
		xpath = XPath.newInstance("/*/bp:NAME");
	   	xpath.addNamespace("bp", root.getNamespaceURI());
		e = (Element) xpath.selectSingleNode(root);
		if (e != null){
			name = e.getTextNormalize();
			if (!shortName.equals(name)){
				out.println("<TR>");
				out.println("<TD>Name:</TD>");
				out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
				out.println("</TR>");
			}
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
			// for nice spacing
			if (i > 0){
				out.println("<TD></TD>");
			}
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
        out.println("<TD>None</TD></TR>");
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

<%
	// pathway interactions
	if (biopaxConstants.isPhysicalInteraction(record.getSpecificType())){
%>
		<div class ='h3'>
		<h3>Summary</h3>
		</div>
<%
		// init an interaction parser
		InteractionParser interactionParser = new InteractionParser(record.getId());
		PhysicalInteraction physicalInteraction = null;

		// get conversion information
		physicalInteraction = interactionParser.getConversionInformation();
		if (physicalInteraction != null){
%>
			<cbio:pathwayInteractionTable physicalinteraction="<%=physicalInteraction%>"/>
<%
		}
%>
<%
		// get controller information
		physicalInteraction = interactionParser.getControllerInformation();
		if (physicalInteraction != null){
%>
			<cbio:pathwayInteractionTable physicalinteraction="<%=physicalInteraction%>"/>
<%
		}
	}
%>
<%
	// child nodes
	DaoInternalLink daoInternalLinks = new DaoInternalLink();
	ArrayList internalLinks = daoInternalLinks.getTargetsWithLookUp(record.getId());
	// interate through results
	if (internalLinks.size() > 0){
%>
		<div class ='h3'>
		<h3>Contains the following Interactions and/or Physical Entities</h3>
		</div>
<%
		for (int lc = 0; lc < internalLinks.size(); lc++) {
			CPathRecord childRecord = (CPathRecord)internalLinks.get(lc);
			// render interaction information
%>
			<cbio:pathwayChildNodeTable recid="<%=childRecord.getId()%>"/>
<%
		}
%>
<%
	}
%>
<%
	// if physical entity, show pathway(s) it belongs to
	if (biopaxConstants.isPhysicalEntity(record.getSpecificType())){
%>
		<div class ='h3'>
		<h3>Member of the following Pathways</h3>
		</div>
<%
		HashSet pathwaySet = MemberPathways.getMemberPathways(record);
		String[] pathways = (String[])pathwaySet.toArray(new String[0]);
		List pathwayList = Arrays.asList(pathways);
		Collections.sort(pathwayList, new RecordLinkSorter());
		int cnt = pathwayList.size();
		for (int lc = 0; lc < cnt; lc++){
			out.println(pathwayList.get(lc));
		}
	}
%>

<%
	// if pathway, show member molecules
	if (biopaxConstants.isPathway(record.getSpecificType())){
%>
		<div class ='h3'>
		<h3>Contains the following Molecules</h3>
		</div>
<%
		HashSet moleculeSet = MemberMolecules.getMemberMolecules(record);	
		String[] molecules = (String[])moleculeSet.toArray(new String[0]);
		List moleculesList = Arrays.asList(molecules);
		Collections.sort(moleculesList, new RecordLinkSorter());
		int cnt = moleculesList.size();
		for (int lc = 0; lc < cnt; lc++){
			out.println(moleculesList.get(lc));
		}
	}
%>

<% } // record not equal to null %>
<jsp:include page="../global/footer.jsp" flush="true" />