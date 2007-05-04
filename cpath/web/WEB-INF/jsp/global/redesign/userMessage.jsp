<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<!-- Get User Message -->
<% String userMessage = (String) request.getAttribute("userMsg");
   if (userMessage == null) {
       userMessage = (String) request.getParameter("userMsg");
       if (userMessage == null) {
           WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
           userMessage = webUIBean.getDefaultUserMessage();
       }
   }
%>

<% if (userMessage != null) { %>
    <div class="user_message">
    &gt; <%= userMessage %>
    </div>
<% } %>