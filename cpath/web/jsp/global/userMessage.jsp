<!-- Get User Message -->
<% String userMessage = (String) request.getAttribute("userMsg"); %>

<% if (userMessage != null) { %>
<B><FONT SIZE=+1>&gt;&gt;</FONT>&nbsp;Message:  <%= userMessage %></B>
<% }  else { %>
    &nbsp;
<% } %>

