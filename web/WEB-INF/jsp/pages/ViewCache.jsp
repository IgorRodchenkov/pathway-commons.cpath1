<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.sql.dao.DaoXmlCache"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Administration::View XML Cache"); %>
<jsp:include page="../global/redesign/header.jsp" flush="true" />
<jsp:include page="../global/redesign/adminLeft.jsp" flush="true" />
<div class="splitcontentright">
cPath provides a cache of the <%= DaoXmlCache.DEFAULT_MAX_CACHE_RECORDS %> most
recent web requests.  Contents of the XML Cache are displayed below (most
recently requested queries appear first.)

<cbio:cacheTable />
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
