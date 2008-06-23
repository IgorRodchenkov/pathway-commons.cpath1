<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page errorPage = "../JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/taglib/struts-html.tld" prefix="html" %>

<%
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Submit Feedback");
request.setAttribute(BaseAction.ATTRIBUTE_USER_MSG, "We welcome your comments...");

// we have to public captcha keys - one for pathway commons domain, another for dev (cbio.mskcc.org domain)
String serverName = request.getServerName();
String PUBLIC_RECAPTCHA_KEY = "";
if (serverName.equals("pathway.commons.org")) {
   PUBLIC_RECAPTCHA_KEY = "6Ld2PgIAAAAAAIF92OtuvCXDCRaVf2vFQyFgSuOC";
}
else if (serverName.equals("awabi.cbio.mskcc.org")) {
   PUBLIC_RECAPTCHA_KEY = "6LdtRAIAAAAAAHv-_R68sk7joQbnmzzkDhL4SzBJ";
}
else if (serverName.equals("toro.cbio.mskcc.org")) {
   PUBLIC_RECAPTCHA_KEY = "6LdzRAIAAAAAAHwtrXzvVK5HQ84lfmaOmnaQK0jW";
}
%>

<jsp:include page="../../global/redesign/header.jsp" flush="true" />

<div>
<h1>Send us your feedback:</h1>

<html:form action="/send_feedback">

<html:errors/>

<table>
    <tr>
        <td>Email address:</td>
        <td><html:text property="email" size="30" maxlength="100"/></td>
    </tr>
    <tr>
        <td>Subject:</td>
        <td><html:text property="subject" size="30" maxlength="100"/></td>
    </tr>
    <tr>
        <td valign="top">Message:</td>
        <td><html:textarea property="message" rows="10" cols="50"/></td>
    </tr>
	<tr><td><br></td></tr>
    <tr>
        <td></td>
        <td>
            <script>
			    var RecaptchaOptions = {
				    theme: 'custom',
				    lang: 'en',
				    custom_theme_widget: 'recaptcha_widget'
				};
            </script>
			<div id="recaptcha_widget" style="display:none"></div>
			<div id="recaptcha_image"></div>
		</td>
    </tr>
    <tr>
        <td>Challenge <a href="javascript:Recaptcha.showhelp()">(help)</a>:</td>
		<td>
			<input type="text" id="recaptcha_response_field" name="recaptcha_response_field" size="30" maxlength="100"/>
			<%
			   out.println("<script type=\"text/javascript\" src=\"http://api.recaptcha.net/challenge?k=" + PUBLIC_RECAPTCHA_KEY + "\"></script>");
			%>
			<noscript>
			    <%
			        out.println("<iframe src=\"http://api.recaptcha.net/noscript?k=" + PUBLIC_RECAPTCHA_KEY + "\" height=\"300\" width=\"500\" frameborder=\"0\"></iframe><br>");
			    %>
				<textarea property="recaptcha_challenge_field" size="30" maxlength="100"/></textarea>
				<input type="hidden" name="recaptcha_response_field" value="manual_challenge">
		    </noscript>
            <a href="javascript:Recaptcha.reload()">(reload images)</a>
	    </td>
    </tr>
	<tr><td><br></td></tr>
    <tr>
	    <td></td>
        <td><html:submit>Submit Feedback</html:submit></td>
    </tr>
</table>



</html:form>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
</div>
<jsp:include page="../../global/redesign/footer.jsp" flush="true" />
