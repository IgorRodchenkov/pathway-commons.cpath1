<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	// title
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Home");

	// referer string used in tabs
	request.setAttribute(BaseAction.REFERER, BaseAction.FORWARD_HOME);
%>

<jsp:include page="../global/header.jsp" flush="true" />
<%
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
String tagLineFile = CPathUIConfig.getPath("tagLine.jsp");
String homePageRightFile = CPathUIConfig.getPath("homePageRight.jsp");
%>

<table>
<tr VALIGN=TOP>
<td width="60%">
<div class="tagline">
<jsp:include page="<%=tagLineFile%>" flush="true"/>
</div>

<% if (CPathUIConfig.getShowDataSourceDetails() == true) { %>

    <div class="large_search_box">
    <h1>Search <%= webUIBean.getApplicationName() %>:</h1>
    <P>
    <form name="searchbox" action="webservice.do" method="get">
    <input type="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
    <input type="text" name="<%= ProtocolRequest.ARG_QUERY %>" size="15"/>
    <input type="submit" value="Search"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_FORMAT %>" value="<%= ProtocolConstants.FORMAT_HTML %>"/>
    <input type="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
        size="25" value='<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>'/>
    </form>
    <P>To get started, enter a gene name or identifier in the text box above.
    <P>To restrict your search to specific data sources or specific organisms, update your
    <a href="filter.do">global filter settings</A>.</p>
    </div>
<% } %>

<% // render the following content if we are in biopax mode %>
<% if (CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX){
    if (CPathUIConfig.getShowDataSourceDetails() == true) { %>
        <div class="home_page_box">
        <P/>
        <B><%= webUIBean.getApplicationName() %> currently contains the following data sources:</B>
        <cbio:dataSourceListTable/>
        </div>
    <% } else { %>
        <div id="home_page_box">
        <b>Browse Pathways:</b>
        <cbio:pathwayListTable/>
        </div>
    <% } %>
<% // render the following in psi mi mode %>
<% } else { %>
    <cbio:organismTable referer="HOME" />
<% } %>
</td>
<td VALIGN=TOP>
    <div class="home_page_box">
    <jsp:include page="../global/dbStatsMini.jsp" flush="true" />
    </div>
    <jsp:include page="<%=homePageRightFile %>" flush="true"/>
</td>
</tr>
</table>

<jsp:include page="../global/footer.jsp" flush="true" />
