<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.action.BaseAction" %>
<% String footerFile = "../" + CPathUIConfig.getPath("footer.jsp"); %>

<%
int webMode = CPathUIConfig.getWebMode();
String isAdminPage = (String) request.getAttribute(BaseAction.PAGE_IS_ADMIN);

if (webMode == CPathUIConfig.WEB_MODE_PSI_MI && isAdminPage == null) { %>
    </div>
<% } %>

</div>
<div id="footer">
<p/>
<p><jsp:include page="<%=footerFile%>" flush="true"/></p>
</div>
<jsp:include page="xdebug.jsp" flush="true" />

</div>
</body>
</html>
