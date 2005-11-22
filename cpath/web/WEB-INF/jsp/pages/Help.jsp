<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
 <%-- Displays Query / Web Service Help Page --%>
<%@ page errorPage = "JspError.jsp" %>

<%
	// set title
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Web Service API";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>
<jsp:include page="../global/header.jsp" flush="true" />
<jsp:include page="../global/help.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />