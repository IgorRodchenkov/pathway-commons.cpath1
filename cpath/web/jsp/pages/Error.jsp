<%@ page isErrorPage = "true" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<jsp:include page="../global/getInteractionsBox.jsp" flush="true" />
<cbio:errorMessage throwable="<%= exception %>"/>
<jsp:include page="../global/xdebug.jsp" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />
