<!-- Get User Message -->
<% String userMessage = (String) request.getAttribute("userMsg");
   if (userMessage == null) {
       userMessage = (String) request.getParameter("userMsg");
   }
%>

<% if (userMessage != null) { %>
    <div class="user_message">
    &gt; <%= userMessage %>
    </div>
<% } %>