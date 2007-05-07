<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page errorPage = "../JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/taglib/struts-html.tld" prefix="html" %>

<%
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Submit Feedback");
request.setAttribute(BaseAction.ATTRIBUTE_USER_MSG, "We welcome your comments...");
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