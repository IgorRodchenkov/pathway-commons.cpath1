<!-- Get User Message -->
<% String userMessage = (String) request.getAttribute("userMsg"); %>

<% if (userMessage != null) { %>
    <div>
    <img src="jsp/images/icon_infosml.gif"/>
    <%= userMessage %>
    </div>
    <hr>
<% } %>