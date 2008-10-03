<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page errorPage = "../JspError.jsp" %>

<%
request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Subscribe");
request.setAttribute(BaseAction.ATTRIBUTE_USER_MSG, "Help us improve Pathway Commons.  Send us your <a href='get_feedback.do'>feedback</a>.");
%>

<jsp:include page="../../global/redesign/header.jsp" flush="true" />

<div>
<h1>Subscribe to pathway-commons-announce mailing list:</h1>

<table>
   <tr>
		<td><form action="http://groups-beta.google.com/group/pathway-commons-announcements/boxsubscribe">
		Email address:&nbsp;&nbsp;<input type=text name=email size="30" maxlength="100"/>
		<input type=submit name="sub" value="Subscribe"></form></td>
   </tr>
</table>
<p>
    <ul>
        <li><a href="http://groups-beta.google.com/group/pathway-commons-announcements">Mailing List Archive</a></li>
        <li><a href="http://groups.google.com/group/pathway-commons-announcements/feeds"><img src="jsp/images/rss.gif" alt="RSS Logo"/></a> <a href="http://groups.google.com/group/pathway-commons-announcements/feeds">Mailing List RSS / Atom Feeds</a></li>
    </ul>
</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
</div>
<jsp:include page="../../global/redesign/footer.jsp" flush="true" />
