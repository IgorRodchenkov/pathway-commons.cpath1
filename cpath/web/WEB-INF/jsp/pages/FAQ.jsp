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

// get tag line
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
String faqContent = webUIBean.getFAQPageContent();

// title
String title = webUIBean.getApplicationName() + "::FAQ";
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>

<jsp:include page="../global/header.jsp" flush="true" />
<div id="content">
<%= faqContent %>
</div>
<jsp:include page="../global/footer.jsp" flush="true" />