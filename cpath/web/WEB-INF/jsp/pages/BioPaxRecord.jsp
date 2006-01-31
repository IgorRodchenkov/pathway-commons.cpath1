<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.dao.DaoInternalLink,
                 java.util.Arrays,
                 java.util.ArrayList,
                 java.util.HashSet,
				 java.util.Collections,
				 org.mskcc.pathdb.schemas.biopax.BioPaxConstants,
				 org.mskcc.pathdb.schemas.biopax.MemberMolecules,
				 org.mskcc.pathdb.schemas.biopax.MemberPathways,
				 org.mskcc.pathdb.schemas.biopax.RecordLinkSorter,
                 java.util.List,
                 org.mskcc.pathdb.model.*,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
				 org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary,
				 org.mskcc.pathdb.schemas.biopax.summary.InteractionParser"%>
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
	CPathRecord record = null;
    Object possibleRecord = request.getAttribute("RECORD");
	if (possibleRecord instanceof CPathRecord){
		record = (CPathRecord)possibleRecord;
	}

	// create our biopaxConstants "helper" class
	BioPaxConstants biopaxConstants = new BioPaxConstants();

	// set request title attribute
	if (record != null){
	    String title = webUIBean.getApplicationName() + "::" + record.getName();
	    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
	}
%>

<jsp:include page="../global/header.jsp" flush="true" />

<% if (record != null) { %>
<%
	// biopax record summary
%>
	<cbio:biopaxRecordSummaryTable record="<%=record%>"/>
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
<% } // record != null %>
<jsp:include page="../global/footer.jsp" flush="true" />