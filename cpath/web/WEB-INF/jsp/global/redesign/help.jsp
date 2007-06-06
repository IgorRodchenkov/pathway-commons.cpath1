<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.protocol.*"%>

<%
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
if (webUIBean.getWebApiVersion().equals(ProtocolConstantsVersion2.VERSION_2)) {
%>
<jsp:include page="web_api_help_2_0.jsp" flush="true"/>
<% } else { %>
<jsp:include page="web_api_help_1_0.jsp" flush="true"/>
<% } %>