<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
 <%
    StringBuffer uri = new StringBuffer (
            (String) request.getAttribute
            (BaseAction.ATTRIBUTE_SERVLET_NAME));
    uri = new StringBuffer(uri.substring(1));
    uri.append("?");
    String queryString = request.getQueryString();
    if (queryString != null) {
        uri.append(queryString);
        uri.append("&");
    }
    uri.append(BaseAction.ATTRIBUTE_STYLE + "=" +
            BaseAction.ATTRIBUTE_STYLE_PRINT);
%>
<BR><IMG SRC="jsp/images/icon_doc_sml.gif">&nbsp;
<A target="_new" HREF="<%= uri.toString() %>">Printer Friendly Page</A>
