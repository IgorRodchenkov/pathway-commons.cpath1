<%@ page import="java.io.PrintWriter"%>
<%@ page isErrorPage = "true" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%= exception %>

<PRE>
<% exception.printStackTrace(new PrintWriter(out)); %>
</PRE>

<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
