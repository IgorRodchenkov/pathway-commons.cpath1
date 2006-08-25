<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.BaseAction,
                 java.util.ArrayList,
                 java.util.HashSet,
				 org.mskcc.pathdb.schemas.biopax.BioPaxConstants,
				 org.mskcc.pathdb.schemas.biopax.MemberMolecules,
				 org.mskcc.pathdb.schemas.biopax.MemberPathways,
                 org.mskcc.pathdb.model.*,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
				 org.mskcc.pathdb.schemas.biopax.summary.InteractionSummary,
				 org.mskcc.pathdb.schemas.biopax.summary.EntitySummaryParser"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.SummaryListUtil"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoInternalLink"%>
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

	// debug mode boolean
	String xdebugFlag = (String)session.getAttribute(AdminWebLogging.WEB_LOGGING);
	boolean debugMode = (queryString.indexOf("debug=1") != -1 || xdebugFlag != null);
%>

<jsp:include page="../global/header.jsp" flush="true" />


<% if (record != null) { %>
	<cbio:biopaxRecordSummaryTable record="<%=record%>"/>
<%
	// xml abbrev content link - log/debug mode only
	if (debugMode){
        out.println("<TABLE WIDTH='100%'>");
        out.println("<TR BGCOLOR=#DDDDDD><TD COLSPAN=2>Debug</TD></TR>");
        out.println("<TR><TD>");
        String xmlAbbrevUrl = "record.do?format=xml_abbrev&id=" + record.getId();
		out.println("<FONT COLOR=RED>&gt;&gt;</FONT>");
        out.println("<A HREF=\"" + xmlAbbrevUrl + "\">XML Content (Abbrev)</A>");
        out.println("<BR>");
        out.println("</TD></TR>");

        DaoInternalLink internalLinker = new DaoInternalLink();
        out.println("<TR><TD><UL>");
        ArrayList children = internalLinker.getTargetsWithLookUp(record.getId());
        for (int i=0; i<children.size(); i++) {
            CPathRecord child = (CPathRecord) children.get(i);
            out.println("<LI><A HREF=\"record.do?id=" + child.getId()
                + "\">" + child.getType().toString() + ":"
                + child.getSpecificType() + ": "
                + child.getName() + "</LI>");
        }
        out.println("</UL></TD></TR>");
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
		EntitySummaryParser entityParser = new EntitySummaryParser(record.getId());
		long interactionSummaryTime = 0;
		InteractionSummary interactionSummary = (InteractionSummary) entityParser.getEntitySummary();
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
		HashSet moleculeSet;
        MemberMolecules.reset();
        moleculeSet = MemberMolecules.getMemberMolecules(record, null);
		if (moleculeSet != null && moleculeSet.size() > 0){
%>
			<cbio:pathwayMoleculesTable moleculeSet="<%=moleculeSet%>"
                    request="<%= request %>"
                    cpathId="<%= record.getId() %>"/>
<%
		}
	}
%>

<%
	// if pathway, show 1st level child nodes
	if (biopaxConstants.isPathway(record.getSpecificType())){
		// child nodes
        SummaryListUtil util = new SummaryListUtil (record.getId(), SummaryListUtil.MODE_GET_CHILDREN);
        ArrayList summaryList = util.getSummaryList();
%>
    <cbio:bioPaxParentChildTable
            entitySummaryList="<%= summaryList %>"
            request="<%= request %>"
            cpathId="<%= record.getId()%>"
            mode="<%= SummaryListUtil.MODE_GET_CHILDREN %>"/>
<%
    }
%>
<%
	// if physical entity, show pathway(s) it belongs to
	if (biopaxConstants.isPhysicalEntity(record.getSpecificType())){
%>
		<DIV CLASS ='h3'>
		<H3>Member of the Following Pathways</H3>
		</DIV>
		<TABLE>
<%
        HashSet pathwaySet;
        pathwaySet = MemberPathways.getMemberPathways(record, null);
		if (pathwaySet != null && pathwaySet.size() > 0){
%>
			<cbio:pathwayMembershipTable pathwaySet="<%=pathwaySet%>"/>
<%
		}
%>
		</TABLE>
<%
        SummaryListUtil util = new SummaryListUtil (record.getId(), SummaryListUtil.MODE_GET_PARENTS);
        ArrayList summaryList = util.getSummaryList();
%>
    <cbio:bioPaxParentChildTable
            entitySummaryList="<%= summaryList %>"
            request="<%= request %>"
            cpathId="<%= record.getId()%>"
            mode="<%= SummaryListUtil.MODE_GET_PARENTS %>"/>
<%
    }
%>


<% } // record != null %>
</p><jsp:include page="../global/footer.jsp" flush="true" />
