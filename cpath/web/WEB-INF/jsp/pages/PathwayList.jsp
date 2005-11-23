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
<%
	// set title
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Browse Pathway(s)";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>

<jsp:include page="../global/header.jsp" flush="true" />

<div id="content">
<h1>Browse Pathways</h1>
<cbio:pathwayListTable/>
</div>
<jsp:include page="../global/footer.jsp" flush="true" />