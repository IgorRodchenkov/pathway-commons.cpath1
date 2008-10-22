<link rel="stylesheet" href="jsp/css/andreas08.css" type="text/css" media="screen,projection" />
<link rel="stylesheet" href="jsp/javascript/highslide/highslide-with-html.css" type="text/css" />

<%-- Explicitly Set Character Encoding
Helps prevent against Cross-site scripting attacks:
See http://www.cert.org/tech_tips/malicious_code_mitigation.html.
--%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>

<!-- Yahoo UI -->
<%
    //  As an optimization, do not include the .js files on the home page,
    //  as we don't need them there.
    String uri = (String) request.getAttribute("servlet_name");
    if (uri != null  && !uri.endsWith("home.do")) { %>

    <script type="text/javascript">
    var context_path = '<%= request.getContextPath() %>';
    </script>

    <script type="text/javascript" src="jsp/all_scripts.jsp"></script>
<% } %>

