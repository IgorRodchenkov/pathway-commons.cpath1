<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.dao.DaoXmlCache"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE,
        "cPath::Admin::View Logs"); %>
<jsp:include page="../global/header.jsp" flush="true" />

<cbio:logTable />

<jsp:include page="../global/footer.jsp" flush="true" />
