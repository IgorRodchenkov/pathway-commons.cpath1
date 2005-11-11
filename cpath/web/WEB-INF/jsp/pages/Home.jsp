<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	String title =
		(CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX) ?
	    "cPath:: Browse by Pathway" : "cPath::Browse By Organism";
    	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);

	// get right column content
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String tagLine = webUIBean.getHomePageTagLine();
	String homePageRightColumnContent = webUIBean.getHomePageRightColumnContent();
%>

<jsp:include page="../global/header.jsp" flush="true" />

<div id="apphead">
<h2><% out.print(tagLine);%></h2>
</div>

<table>
<tr>
<td width=100%>
<% // render the following content if we are in biopax mode %>
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX){ %>
<jsp:include page="./PathwayListTable.jsp" flush="true" />
<% // render the following in psi mi mode %>
<% } else { %>
<cbio:organismTable />
<% } %>
</td>
<td>
<% out.println(homePageRightColumnContent); %>
</td>
</tr>
</table>
<table width=100%>
<tr>
<td>
<jsp:include page="../global/footer.jsp" flush="true" />
</td>
</tr>
</table>
