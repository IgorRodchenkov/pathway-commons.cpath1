<%@ page import="org.mskcc.pathdb.action.BaseAction,
				 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
	// set title
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Browse Organism(s)";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
 %>

<jsp:include page="../global/header.jsp" flush="true" />
<div id="content">
<h1>Browse by Organism</h1>
<cbio:organismTable referer="BROWSE" />
</div>

<jsp:include page="../global/footer.jsp" flush="true" />