<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.sql.dao.DaoXmlCache"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<%
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Administration::View XML Cache";
	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>
<jsp:include page="../global/header.jsp" flush="true" />

cPath provides a cache of the <%= DaoXmlCache.DEFAULT_MAX_CACHE_RECORDS %> most
recent web requests.  Contents of the XML Cache are displayed below (most
recently requested queries appear first.)

<cbio:cacheTable />

<jsp:include page="../global/footer.jsp" flush="true" />
