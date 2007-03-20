<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<% String footerFile = "../" + CPathUIConfig.getPath("footer.jsp"); %>

</div>
<div id="footer">
<p/>
<p><jsp:include page="<%=footerFile%>" flush="true"/></p>
</div>
<jsp:include page="xdebug.jsp" flush="true" />

</div>
</body>
</html>
