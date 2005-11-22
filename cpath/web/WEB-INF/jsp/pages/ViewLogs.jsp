<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.sql.dao.DaoXmlCache"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Administration::View Logs";
	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>
<jsp:include page="../global/header.jsp" flush="true" />

<cbio:logTable />

<jsp:include page="../global/footer.jsp" flush="true" />
