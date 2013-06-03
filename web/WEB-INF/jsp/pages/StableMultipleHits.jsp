<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.model.CPathRecord" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoOrganism" %>
<%@ page import="org.mskcc.pathdb.model.Organism" %>
<%@ page import="org.mskcc.pathdb.action.ShowBioPaxRecord2" %>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%
    ArrayList<CPathRecord> recordList = (ArrayList<CPathRecord>)
            request.getAttribute("MULTIPLE_HITS");
    String db = request.getParameter(ShowBioPaxRecord2.DB_PARAMETER);
    String id = request.getParameter(ShowBioPaxRecord2.ID_PARAMETER);
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Disambiguation Page");
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<div>

<% if (recordList.size() > 0) { %>
    <h2>Disambiguation Page</h2>
    <P>Your request matches multiple records:
    <table>
    <% for (CPathRecord record : recordList) {
        int taxId = record.getNcbiTaxonomyId();
        DaoOrganism daoOrganism = new DaoOrganism();
        Organism organism = daoOrganism.getOrganismByTaxonomyId(taxId);
        out.println("<tr>");
        out.println("<td>"
                + "<a href='record2.do?id=" + record.getId()
                + "'>"
                + record.getName() + "</a></td>");
        out.println("<td> - " + organism.getSpeciesName() + "</td>");
        out.println("</tr>");
    }
    %>
    </table>
<% } else { %>
    <h1>Ooops..</h1>
    <h2>No stable page available for <%= db %>:<%= id %>.</h2>  
<% } %>
<P>&nbsp;</p>
<P>&nbsp;</p>
<P>&nbsp;</p>
<P>&nbsp;</p>
</div>

<jsp:include page="../global/redesign/footer.jsp" flush="true" />
