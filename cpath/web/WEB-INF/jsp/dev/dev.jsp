<%@ page import="org.mskcc.pathdb.action.BaseAction"%>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE,
        "cPath Developer Docs"); %>
<jsp:include page="../global/header.jsp" flush="true" />

<%
    String pageParam = request.getParameter("page");

    if (pageParam.indexOf(".") >= 0
            || pageParam.indexOf("/") >= 0) {
        pageParam = null;
    } else {
        pageParam = pageParam + ".jsp";
    }
%>
<jsp:include page="<%= pageParam %>" flush="true" />
<jsp:include page="../global/footer.jsp" flush="true" />