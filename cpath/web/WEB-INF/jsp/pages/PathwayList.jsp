<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.model.CPathRecord,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 java.util.ArrayList,
                 org.mskcc.pathdb.sql.dao.DaoOrganism,
                 java.util.HashMap,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "JspError.jsp" %>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Browse Pathway(s)"); %>

<jsp:include page="../global/header.jsp" flush="true" />

<div id="content">
<h1>Browse Pathways:</h1>
</div>
<cbio:pathwayListTable/>
<jsp:include page="../global/footer.jsp" flush="true" />