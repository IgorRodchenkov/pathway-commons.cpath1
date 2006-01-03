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
	Element e = null;
	XPath xpath = null;
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
	if (e != null) {
		name = e.getTextNormalize();
	}
	// no short name, try to use name
	if (name == null) {
		xpath = XPath.newInstance("/*/bp:NAME");
	   	xpath.addNamespace("bp", root.getNamespaceURI());
		e = (Element) xpath.selectSingleNode(root);
		if (e != null){
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
	if (e != null){
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
    if (e != null) {
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
%>
<%
		// get physical interaction information
		physicalInteraction = interactionParser.getPhysicalInteractionInformation();
		if (physicalInteraction != null){
%>
			<cbio:pathwayInteractionTable physicalinteraction="<%=physicalInteraction%>"/>
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
%>
			<DIV CLASS ='h3'>
			<H3>Contains the Following Interactions</H3>
			</DIV>
			<TABLE>
<%
			for (int lc = 0; lc < internalLinks.size(); lc++) {
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
		<h3>Member of the Following Pathways</h3>
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