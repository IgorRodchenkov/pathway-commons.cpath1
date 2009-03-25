<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ taglib uri="/WEB-INF/taglib/struts-html.tld" prefix="html" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Administration::Snapshot Download Configuration";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />
<html:errors/>

<jsp:include page="../global/redesign/adminLeft.jsp" flush="true" />

<div class="splitcontentright">
<h1>cPath Administration</h1>

    <div class="h3">
        <h3>Configurable Snapshot Download Elements</h3>
    </div>
	<html:form action="adminUpdateSnapshotDownloadConfig.do" focus="logo">
		<table border="0" cellspacing="2" cellpadding="3" width="100%">
			<tr>
	        <th align="LEFT">Snapshot Download Base URL</th>
				<td>
					<html:text property="snapshotDownloadBaseURL" size="72" />
				</td>
			</tr>
            <tr>
     	   	<td><input type="submit" value="Update"></td>
			</tr>
		</table>
	</html:form>
</div>

<jsp:include page="../global/redesign/footer.jsp" flush="true" />
