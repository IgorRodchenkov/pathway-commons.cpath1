<%@ page import="java.util.HashMap,
                 java.util.ArrayList,
				 org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.sql.dao.DaoOrganism,
                 org.mskcc.pathdb.model.CPathRecord,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	String title =
		(CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX) ?
	    "cPath:: Browse by Pathway" : "cPath::Browse By Organism";
    	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);

	// get pathway list records
	ArrayList records = null;
	String pathwayListRequestUrl = null;
	if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX){
	    records = (ArrayList) request.getAttribute("RECORDS");
    	ProtocolRequest pathwayListRequest = new ProtocolRequest();	
    	pathwayListRequest.setCommand
            (ProtocolConstants.COMMAND_GET_TOP_LEVEL_PATHWAY_LIST);
    	pathwayListRequest.setFormat(ProtocolConstants.FORMAT_BIO_PAX);
    	pathwayListRequestUrl = pathwayListRequest.getUri();
    }

	// get right column content
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String homePageRightColumnContent = webUIBean.getHomePageRightColumnContent();
%>

<jsp:include page="../global/header.jsp" flush="true" />

<% // render the following content if we are in biopax mode %>
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX){ %>
<table border='0' cellspacing='0' cellpadding='0' width='100%'>
    <tr>
        <td width="50%">
            <div id="apphead">
            <h2>Browse By Pathway</h2>
            </div>
        </td>
        <td width="50%" align="right">
            <IMG SRC="jsp/images/xml_doc.gif">&nbsp;
            <A HREF="<%= pathwayListRequestUrl %>">Get Pathway List in BioPAX Format</A>
        </td>
    </tr>
</table>
<% // render the following content if we are in psi mi mode %>
<% } else { %>
<div id="apphead">
    <h2>Browse By Organism</h2>
</div>
<% } %>

<TABLE WIDTH=100%>
<TR>
<TD>
<% // render the following content if we are in biopax mode %>
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX){ %>
<div id='axial' class='h3'>
<h3>Pathway Information</h3>
</div>

<table border='0' cellspacing='2' cellpadding='3' width='100%'>
<TR>
<TH>Pathway</TH>
<TH>Organism</TH>
</TR>
<%
    DaoOrganism daoOrganism = new DaoOrganism();
    HashMap organismMap = daoOrganism.getAllOrganismsMap();
    for (int i=0; i<records.size(); i++) {
        CPathRecord rec = (CPathRecord) records.get(i);
        if (i % 2 == 0) {
            out.println("<tr class='a'>");
        } else {
            out.println("<tr class='b'>");
        }
        String uri = "record.do?id=" + rec.getId();
        out.println("<TD><A HREF=\"" + uri + "\">"
                + rec.getName() + "</A>");
        out.println("</TD>");

        Organism organism = (Organism) organismMap.get(Integer.toString
                (rec.getNcbiTaxonomyId()));
        if (organism != null) {
            out.println("<TD>");
            if (organism.getSpeciesName() != null) {
                out.println (organism.getSpeciesName());
            }
            out.println("</TD>");
        }
        out.println("</TR>");
    }
%>
</TABLE>
<% // render the following in psi mi mode %>
<% } else { %>
<cbio:organismTable />
<% } %>
</TD>

<TD WIDTH=15>
    &nbsp;
</TD>

<% out.println(homePageRightColumnContent); %>
</TR>

</TABLE>

<jsp:include page="../global/footer.jsp" flush="true" />