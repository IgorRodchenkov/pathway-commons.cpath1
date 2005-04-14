<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.util.security.XssFilter"%>
 <%
    StringBuffer url = null;
    String base = (String) request.getAttribute
            (BaseAction.ATTRIBUTE_SERVLET_NAME);
    if (base != null) {
        url = XssFilter.getUrlFiltered(base, request.getParameterMap());
        url.append(BaseAction.ATTRIBUTE_STYLE + "=" +
            BaseAction.ATTRIBUTE_STYLE_PRINT);
    }
%>
<% if (url != null) { %>
<BR><IMG SRC="jsp/images/icon_doc_sml.gif">&nbsp;
<A target="_new" HREF="<%= url.toString() %>">Printer Friendly Page</A>
<% } %>
