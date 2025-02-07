<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.protocol.*"%>

<%
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
// as of 12/1/08, version 2 and version 3 are identical with the exception that 
// binary sif out for version < 3.0 gets translated to use old binary interaction tags
if (webUIBean.getWebApiVersion().equals(ProtocolConstantsVersion2.VERSION_2) ||
    webUIBean.getWebApiVersion().equals(ProtocolConstantsVersion3.VERSION_3)) {
%>
<jsp:include page="web_api_help_2_0.jsp" flush="true"/>
<% } else { %>
<jsp:include page="web_api_help_1_0.jsp" flush="true"/>
<% } %>
