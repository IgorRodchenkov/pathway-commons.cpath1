<%@ page import="org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.lucene.PsiInteractionToIndex,
                 org.mskcc.pathdb.lucene.LuceneReader,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.lucene.LuceneConfig"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%
request.setAttribute("advancedSearch", "true");
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "About");
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />

<%
String aboutFile = CPathUIConfig.getPath("about.jsp");
%>
<div>
<jsp:include page="<%=aboutFile%>" flush="true"/>
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
