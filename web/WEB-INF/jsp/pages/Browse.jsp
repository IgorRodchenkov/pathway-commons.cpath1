<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Browse by Organism"); %>

<jsp:include page="../global/redesign/header.jsp" flush="true" />
<div>
<h1>Browse by Organism</h1>
<cbio:organismTable referer="BROWSE" />
<div>
<p>* Indicates number of pathways, molecules and complexes.</p>
</div>
</div>

<jsp:include page="../global/redesign/footer.jsp" flush="true" />