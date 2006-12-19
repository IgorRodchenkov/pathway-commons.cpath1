<%@ page import="org.mskcc.pathdb.action.BaseAction,
				 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Browse by Organism"); %>

<jsp:include page="../global/header.jsp" flush="true" />
<div id="content">
<h1>Browse by Organism</h1>
<cbio:organismTable referer="BROWSE" />
<p>
<div class="text">
* Indicates number of pathways, molecules and complexes.
</div>
</div>

<jsp:include page="../global/footer.jsp" flush="true" />