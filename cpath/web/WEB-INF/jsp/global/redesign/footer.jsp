<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<% String footerFile = "../" + CPathUIConfig.getPath("footer.jsp"); %>

<%
int webMode = CPathUIConfig.getWebMode();

if (webMode == CPathUIConfig.WEB_MODE_PSI_MI) { %>
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
