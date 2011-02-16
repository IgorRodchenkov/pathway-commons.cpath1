<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ taglib uri="/WEB-INF/taglib/struts-html.tld" prefix="html" %>
<%@ page errorPage = "JspError.jsp" %>

<%
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String title = webUIBean.getApplicationName() + "::Administration::Mini-Maps Configuration";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);
%>

<jsp:include page="../global/redesign/header.jsp" flush="true" />
<html:errors/>

<jsp:include page="../global/redesign/adminLeft.jsp" flush="true" />

<div class="splitcontentright">
<h1>cPath Administration</h1>

    <div class="h3">
        <h3>Configurable Mini-Map Elements</h3>
    </div>
	<html:form action="adminUpdateMiniMapsConfig.do" focus="logo">
		<table border="0" cellspacing="2" cellpadding="3" width="100%">
			<tr>
				<th align="LEFT">Enable Mini-Maps</th>
				<td>
					<html:checkbox property="enableMiniMaps"/>
				</td>
			</tr>
			<tr>
	        <th align="LEFT">Image Map Server URL</th>
				<td>
					<html:text property="imageMapServerURL" size="72" />
				</td>
			</tr>
			<tr>
			<th align="LEFT">Maximum size (nodes) of Mini-Maps</th>
				<td>
					<html:textarea property="maxMiniMapSize" cols="72"/>
				</td>
			</tr>
			<tr>
			<th align="LEFT">SIF Converter Threshold</th>
				<td>
					<html:textarea property="converterThreshold" cols="72"/>
				</td>
			</tr>
			<tr>
            <th align="LEFT">Interactions to Filter</th>
				<td>
					<html:textarea property="filterInteractions" rows="5" cols="72"/>
				</td>
			</tr>
            <tr>
     	   	<td><input type="submit" value="Update"></td>
			</tr>
		</table>
	</html:form>
</div>

<jsp:include page="../global/redesign/footer.jsp" flush="true" />
