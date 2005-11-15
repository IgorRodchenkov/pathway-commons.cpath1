<%@ page import="org.mskcc.pathdb.action.admin.AdminWebLogging,
                 org.mskcc.pathdb.action.HomeAction,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	String title = "cPath:: Home";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);

	// get right column content
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String tagLine = webUIBean.getHomePageTagLine();
	String homePageRightColumnContent = webUIBean.getHomePageRightColumnContent();

	// referer string used in tabs
	request.setAttribute(BaseAction.REFERER, BaseAction.FORWARD_HOME);
%>

<jsp:include page="../global/header.jsp" flush="true" />


<TABLE>
<TR>
<TD WIDTH=60%>
<div id="tagline">
    <% out.print(tagLine);%>
</div>

<% // render the following content if we are in biopax mode %>
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX){ %>
    <div id="home_page_box">
    Browse Pathways:
    <jsp:include page="./PathwayListTable.jsp" flush="true" />
    </div>
<% // render the following in psi mi mode %>
<% } else { %>
    <cbio:organismTable referer="HOME" />
<% } %>
</TD>
<TD>
<% out.println(homePageRightColumnContent); %>
</TD>
</TR>
</TABLE>

<jsp:include page="../global/footer.jsp" flush="true" />
