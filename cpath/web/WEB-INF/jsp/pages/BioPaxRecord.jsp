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
<%@ page import="org.mskcc.pathdb.taglib.BioPaxParentChildTable"%>
<%@ page import="org.mskcc.pathdb.schemas.biopax.summary.EntitySummary"%>
<%@ page import="java.util.Iterator"%>
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

    GlobalFilterSettings filterSettings = (GlobalFilterSettings)
            session.getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
    if (filterSettings == null) {
        filterSettings = new GlobalFilterSettings();
        session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS, filterSettings);
    }
%>

<jsp:include page="../global/header.jsp" flush="true" />


<% if (record != null) { %>
	<cbio:biopaxRecordSummaryTable record="<%=record%>"/>
<%
	// xml abbrev content link - log/debug mode only
	if (debugMode){
        out.println("<DIV CLASS='debug'>");
        String xmlAbbrevUrl = "record.do?format=xml_abbrev&id=" + record.getId();
		out.println("<FONT COLOR=RED>&gt;&gt;</FONT>");
        out.println("<A HREF=\"" + xmlAbbrevUrl + "\">XML Content (Abbrev)</A>");
        out.println("<BR>");
        DaoInternalLink internalLinker = new DaoInternalLink();
        out.println("<UL>");
        ArrayList children = internalLinker.getTargetsWithLookUp(record.getId());
        for (int i=0; i<children.size(); i++) {
            CPathRecord child = (CPathRecord) children.get(i);
            out.println("<LI><A HREF=\"record.do?id=" + child.getId()
                + "\">" + child.getType().toString() + ":"
                + child.getSpecificType() + ": "
                + child.getName() + "</A></LI>");
        }
        out.println("</UL>");
        out.println("</DIV>");
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
        if (biopaxConstants.isPathway(record.getSpecificType())) {
            moleculeSet = MemberMolecules.getMoleculesInPathway(record);
        } else {
            moleculeSet = MemberMolecules.getMoleculesInComplex(record);
        }
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
        SummaryListUtil util = new SummaryListUtil (record.getId(),
                SummaryListUtil.MODE_GET_CHILDREN, new GlobalFilterSettings());
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
	// if physical entity or interaction, show pathway(s) it belongs to
	if (record.getType().equals(CPathRecordType.PHYSICAL_ENTITY)
            || record.getType().equals(CPathRecordType.INTERACTION)) {
%>
		<DIV CLASS ='h3'>
		<H3><A NAME="pathway_list">Member of the Following Pathways</A></H3>
		</DIV>
		<TABLE WIDTH=100%>
<%
        HashSet pathwaySet;
        pathwaySet = MemberPathways.getMemberPathways(record, filterSettings);
		if (pathwaySet != null && pathwaySet.size() > 0){
%>
			<cbio:pathwayMembershipTable pathwaySet="<%=pathwaySet%>"/>
<%
		} else {
            out.println("<TR><TD>");
            out.println("No records found for your selected data sources.  ");
            out.println("You may wish to update your ");
            out.println("<A HREF='filter.do'>global filter settings</A>.");
            out.println("</TD></TR>");
        }
%>
		</TABLE>
<%
        SummaryListUtil util = new SummaryListUtil
                (record.getId(), SummaryListUtil.MODE_GET_PARENTS, filterSettings);
        ArrayList allList = util.getSummaryList();

        //  Separate interactions from complexes
        ArrayList interactionList = new ArrayList();
        ArrayList complexList = new ArrayList();
        for (int i=0; i<allList.size(); i++) {
            EntitySummary entitySummary = (EntitySummary) allList.get(i);
            if (entitySummary.getSpecificType().equals(BioPaxConstants.COMPLEX)) {
                complexList.add(entitySummary);
            } else {
                interactionList.add(entitySummary);
            }
        }
%>
    <!-- Show all Interaction Parents of this entity -->
    <cbio:bioPaxParentChildTable
            entitySummaryList="<%= interactionList %>"
            request="<%= request %>"
            cpathId="<%= record.getId()%>"
            mode="<%= BioPaxParentChildTable.MODE_SHOW_PARENT_INTERACTIONS %>"/>

    <!-- Show all Complex Parents of this entity -->
    <cbio:bioPaxParentChildTable
            entitySummaryList="<%= complexList %>"
            request="<%= request %>"
            cpathId="<%= record.getId()%>"
            mode="<%= BioPaxParentChildTable.MODE_SHOW_PARENT_COMPLEXES %>"/>

<%
    }
%>


<% } // record != null %>
</p><jsp:include page="../global/footer.jsp" flush="true" />
