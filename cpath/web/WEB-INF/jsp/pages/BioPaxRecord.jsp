<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.dao.DaoInternalLink,
                 org.mskcc.pathdb.sql.dao.DaoExternalLink,
                 java.util.Arrays,
                 java.util.ArrayList,
                 java.util.HashSet,
				 java.util.Collections,
                 org.jdom.input.SAXBuilder,
                 org.jdom.Element,
                 org.jdom.Document,
				 org.mskcc.pathdb.schemas.biopax.BioPaxConstants,
				 org.mskcc.pathdb.schemas.biopax.MemberMolecules,
				 org.mskcc.pathdb.schemas.biopax.MemberPathways,
				 org.mskcc.pathdb.schemas.biopax.RecordLinkSorter,
                 java.io.StringReader,
                 org.jdom.xpath.XPath,
                 java.util.List,
                 org.mskcc.pathdb.model.*,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.action.admin.AdminWebLogging"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.InteractionParser"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	// used to indicate if basic information
	// section header has been rendered
	boolean basicInformationHeaderRendered = false;

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
	Element e;
	XPath xpath;
	StringReader reader = new StringReader (record.getXmlContent());
    SAXBuilder builder = new SAXBuilder();
	Document bioPaxDoc = builder.build(reader);
    Element root = bioPaxDoc.getRootElement();
%>

<jsp:include page="../global/header.jsp" flush="true" />

<DIV ID="apphead">
<%
	// get name (short name else name, else shortest synonym)
	xpath = XPath.newInstance("/*/bp:SHORT-NAME");
   	xpath.addNamespace("bp", root.getNamespaceURI());
	e = (Element) xpath.selectSingleNode(root);
	String name = null;
	if (e != null && e.getTextNormalize().length() > 0) {
		name = e.getTextNormalize();
	}
	// no short name, try to use name
	if (name == null) {
		xpath = XPath.newInstance("/*/bp:NAME");
	   	xpath.addNamespace("bp", root.getNamespaceURI());
		e = (Element) xpath.selectSingleNode(root);
		if (e != null && e.getTextNormalize().length() > 0){
			name = e.getTextNormalize();
		}
	}
	// synonym
	xpath = XPath.newInstance("/*/bp:SYNONYMS");
    xpath.addNamespace("bp", root.getNamespaceURI());
	List list = xpath.selectNodes(root);
    List synonymList = null;
	String synonym = "";
	int synonymIndex = -1;
    if (list != null && list.size() > 0) {
		int minLength = 0;
		synonymList = list;
        for (int i=0; i<list.size(); i++) {
        	e = (Element) list.get(i);
			if (i == 0){
				minLength = e.getTextNormalize().length();
				synonym = e.getTextNormalize();
				synonymIndex = i;
			}
			else if (e.getTextNormalize().length() < minLength){
				minLength = e.getTextNormalize().length();
				synonym = e.getTextNormalize();
				synonymIndex = i;
			}
        }
    }
	// no short name, use synonym (may be empty)
	if (name == null) {
		name = synonym;
		// we are using synonym as name, remove synonym from list
		if (synonymIndex >= 0){
			synonymList.remove(synonymIndex);
		}
	}
	
	// organism
	xpath = XPath.newInstance("/*/bp:ORGANISM/*/bp:NAME");
   	xpath.addNamespace("bp", root.getNamespaceURI());
	e = (Element) xpath.selectSingleNode(root);
	String organism = null;
	if (e != null && e.getTextNormalize().length() > 0){
		organism = e.getTextNormalize();
	}

	// type
	String type = null;
	if (record != null){
		type = record.getSpecificType();
	}
	if (type != null){
		name = (name + " (" + entityTypeMap.get(type) + ")");
	}

	// our heading 
	String headerString = (organism != null) ?
		("<H2>" + name + " from " + organism + "</H2>") :
		("<H2>" + name + "</H2>");
	out.println(headerString);
%>
</DIV>

<% if (record != null) { %>

<%	
	// xml abbrev content link - log/debug mode only
	String xdebugFlag = (String)session.getAttribute(AdminWebLogging.WEB_LOGGING);
	if (queryString.indexOf("debug=1") != -1 || xdebugFlag != null){
		String xmlAbbrevUrl = "record.do?format=xml_abbrev&id=" + record.getId();
		out.println("<A HREF=\"" + xmlAbbrevUrl + "\">XML Content (Abbrev)</A>");
		out.println("<BR>");
	}
%>
<%
	// synonym
    if (synonymList != null && synonymList.size() > 0) {
		// render basic information header if necessary
		if (!basicInformationHeaderRendered){
			out.println("<TABLE>");
			basicInformationHeaderRendered = true;
		}
    	out.println("<TR>");
        out.println("<TD>Synonyms:</TD>");
        out.println("<TD COLSPAN=3>");
        for (int i=0; i<synonymList.size(); i++) {
        	e = (Element) synonymList.get(i);
            out.println(e.getTextNormalize());
        }
        out.println("</TD>");
        out.println("</TR>");
    }
%>
<%
	// data source
    xpath = XPath.newInstance("/*/bp:DATA-SOURCE/*/bp:NAME");
    xpath.addNamespace("bp", root.getNamespaceURI());
    e = (Element) xpath.selectSingleNode(root);
    if (e != null && e.getTextNormalize().length() > 0) {
		// render basic information header if necessary
		if (!basicInformationHeaderRendered){
			out.println("<TABLE>");
			basicInformationHeaderRendered = true;
		}
        out.println("<TR>");
        out.println("<TD>Data Source:</TD>");
        out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
        out.println("</TR>");
    }
%>
<%
	// availability
	xpath = XPath.newInstance("/*/bp:AVAILABILITY");
    xpath.addNamespace("bp", root.getNamespaceURI());
    e = (Element) xpath.selectSingleNode(root);
    if (e != null) {
		// render basic information header if necessary
		if (!basicInformationHeaderRendered){
			out.println("<TABLE>");
			basicInformationHeaderRendered = true;
		}
        out.println("<TR>");
        out.println("<TD>Availability:</TD>");
        out.println("<TD COLSPAN=3>" + e.getTextNormalize() + "</TD>");
        out.println("</TR>");
    }
%>
<%
	// xrefs
    DaoExternalLink externalLinker = DaoExternalLink.getInstance();
    ArrayList externalLinks =
            externalLinker.getRecordsByCPathId(record.getId());
    if (externalLinks.size() > 0) {
		// render basic information header if necessary
		if (!basicInformationHeaderRendered){
			out.println("<TABLE>");
			basicInformationHeaderRendered = true;
		}
		out.println("<TR>");
		out.println("<TD>External Links:</TD>");
        for (int lc = 1; lc <= externalLinks.size(); lc++) {
			out.println("<TD>");
            ExternalLinkRecord link = (ExternalLinkRecord) externalLinks.get(lc-1);
            ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
            String dbId = link.getLinkedToId();
            String linkStr = dbRecord.getName() + ":" + dbId;
            String uri = link.getWebLink();
            if (uri != null && uri.length() > 0) {
                out.println("<A HREF=\""+ uri + "\">" + linkStr + "</A>");
            } else {
                out.println(linkStr);
            }
			out.println("</TD>");
			//if ((lc != 0) && (lc % 3) == 0){
			if (lc % 3 == 0){
				out.println("</TR>");
				out.println("<TR>");
				// for nice spacing
	 			out.println("<TD></TD>");
			}
        }
	   	out.println("</TR>");
	}
%>
<%
	// comment
	xpath = XPath.newInstance("/*/bp:COMMENT");
    xpath.addNamespace("bp", root.getNamespaceURI());
    e = (Element) xpath.selectSingleNode(root);
    if (e != null) {
		// render basic information header if necessary
		if (!basicInformationHeaderRendered){
			out.println("<TABLE>");
			basicInformationHeaderRendered = true;
		}
        out.println("<TR>");
        out.println("<TD>Comment:</TD>");
        String text = e.getTextNormalize();
        text = text.replaceAll("<BR>", "<P>");
        out.println("<TD COLSPAN=3>" + text + "</TD>");
        out.println("</TR>");
    }
%>
<%
	// close basic information table if necessary
	if (basicInformationHeaderRendered){
		out.println("</TABLE>");
	}
%>
<%
	// pathway interactions
	if (biopaxConstants.isPhysicalInteraction(record.getSpecificType())){
%>
		<DIV CLASS ='h3'>
		<H3>Summary</H3>
		</DIV>
		<TABLE>
<%
		// init an interaction parser
		InteractionParser interactionParser = new InteractionParser(record.getId());
		InteractionSummary interactionSummary = interactionParser.getInteractionSummary();

		// display interaction summary
		if (interactionSummary != null){
%>
			<cbio:pathwayInteractionTable interactionSummary="<%=interactionSummary%>"/>
<%
		}
%>
		</TABLE>
<%
	}
%>

<%
	// if pathway or complex, show member molecules&complex
	if (biopaxConstants.isPathway(record.getSpecificType()) ||
		record.getSpecificType().equals(BioPaxConstants.COMPLEX)) {
%>
		<DIV class ='h3'>
		<H3>Contains the Following Molecules</H3>
		</DIV>
<%
		HashSet moleculeSet = MemberMolecules.getMemberMolecules(record);	
		String[] molecules = (String[])moleculeSet.toArray(new String[0]);
		List moleculesList = Arrays.asList(molecules);
		Collections.sort(moleculesList, new RecordLinkSorter());
		int cnt = moleculesList.size();
		if (cnt > 0){
			out.println("<TABLE>");
			out.println("<TR>");
			out.println("<TD>");
		}
		for (int lc = 0; lc < cnt; lc++){
			out.println(moleculesList.get(lc));
		}
		if (cnt > 0){
			out.println("</TD>");
			out.println("</TR>");
			out.println("</TABLE>");
		}
	}
%>

<%
	// if physical entity, show pathway(s) it belongs to
	if (biopaxConstants.isPathway(record.getSpecificType())){
		// child nodes
		DaoInternalLink daoInternalLinks = new DaoInternalLink();
		ArrayList internalLinks = daoInternalLinks.getTargetsWithLookUp(record.getId());
		// interate through results
		if (internalLinks.size() > 0){
			boolean showAll = (queryString.indexOf("show=ALL") != -1);
			int cnt = (showAll) ? internalLinks.size() : (internalLinks.size() > 10) ? 10 : internalLinks.size();
			String heading = (showAll) ? "Contains the Following Interactions" :
				(internalLinks.size() > 10) ? "Includes the Following Ten Interactions" : "Contains the Following Interactions";

			// heading
			out.println("<DIV CLASS ='h3'>");
			out.println("<H3>");
			out.println("<TABLE><TR>");
			out.println("<TD>" + heading + "</TD>");

			// limited pagination support if necessary
			if (internalLinks.size() > 10){
				// generate link to change number of interactions to display
				if (showAll){
					String uri = "record.do?id=" + record.getId();
					out.println("<TD><A HREF=\"" + uri + "\">[display 10 interactions]</A></TD>");
				}
				else{
					String uri = "record.do?id=" + record.getId() + "&show=ALL";
					out.println("<TD><A HREF=\"" + uri + "\">[display all interactions]</A></TD>");
				}
			}
			out.println("</TR></TABLE>");
			out.println("</H3>");
			out.println("</DIV>");

			// start child node output
			out.println("<TABLE>");
			for (int lc = 0; lc < cnt; lc++) {
				CPathRecord childRecord = (CPathRecord)internalLinks.get(lc);
				// render interaction information
%>
				<cbio:pathwayChildNodeTable recid="<%=childRecord.getId()%>"/>
<%
			}
%>
		</TABLE>
<%
		}
	}
%>
<%
	// if physical entity, show pathway(s) it belongs to
	if (biopaxConstants.isPhysicalEntity(record.getSpecificType())){
%>
		<DIV CLASS ='h3'>
		<H3>Member of the Following Pathways</H3>
		</DIV>
<%
		HashSet pathwaySet = MemberPathways.getMemberPathways(record);
		String[] pathways = (String[])pathwaySet.toArray(new String[0]);
		List pathwayList = Arrays.asList(pathways);
		Collections.sort(pathwayList, new RecordLinkSorter());
		int cnt = pathwayList.size();
		if (cnt > 0){
			out.println("<TABLE>");
			out.println("<TR>");
			out.println("<TD>");
		}
		for (int lc = 0; lc < cnt; lc++){
			out.println(pathwayList.get(lc));
		}
		if (cnt > 0){
			out.println("</TD>");
			out.println("</TR>");
			out.println("</TABLE>");
		}
	}
%>
<% } // record not equal to null %>
<jsp:include page="../global/footer.jsp" flush="true" />