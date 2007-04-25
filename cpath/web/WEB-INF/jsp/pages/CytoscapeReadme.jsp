<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>

<%
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Cytoscape Plugin Readme");
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<%
String cytoscapeFile = CPathUIConfig.getPath("cytoscape_readme.jsp");
%>
<div>
<jsp:include page="<%=cytoscapeFile%>" flush="true"/>
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
