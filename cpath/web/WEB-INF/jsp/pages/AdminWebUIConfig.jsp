<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ taglib uri="/WEB-INF/taglib/struts-html.tld" prefix="html" %>
<%@ page errorPage = "JspError.jsp" %>

<%
    String title = "cPath::AdministrationWebUIConfig";
    request.setAttribute(BaseAction.ATTRIBUTE_TITLE, title);

	// web mode
	String webMode =
		(CPathUIConfig.getWebMode() == CPathUIConfig.WEB_MODE_BIOPAX) ?
		CPathUIConfig.BIOPAX : CPathUIConfig.PSI_MI;
%>

<jsp:include page="../global/header.jsp" flush="true" />
<html:errors/>

<div id="apphead">
    <h2>cPath Administration</h2>
</div>
<table border="0" cellspacing="2" cellpadding="3">
	<tr>
	<div><h3>Web Mode</h3></div>
	<td><%=webMode%></td>
	</tr>
</table>

    <div class="h3">
        <h3>Configurable UI Elements</h3>
    </div>
	<html:form action="adminUpdateWebUI.do" focus="logo">
		<table border="0" cellspacing="2" cellpadding="3" width="100%">
			<tr>
				<th align="LEFT">Home Page Header</th>
				<td>
					<html:textarea property="homePageHeader" rows="25" cols="80"/>
				</td>
			</tr>
			<tr>
				<th align="LEFT">Home Page Tag Line</th>
				<td>
					<html:textarea property="homePageTagLine" rows="5" cols="80"/>
				</td>
			</tr>
			<tr>
				<th align="LEFT">Home Page Right Column Content</th>
				<td>
					<html:textarea property="homePageRightColumnContent" rows="25" cols="80"/>
				</td>
			</tr>
			<tr>
				<th align="LEFT">Display Browse by Pathway Tab</th>
				<td>
					<html:checkbox property="displayBrowseByPathwayTab"/>
				</td>
			</tr>
			<tr>
				<th align="LEFT">Display Browse by Organism Tab</th>
				<td>
					<html:checkbox property="displayBrowseByOrganismTab"/>
				</td>
			</tr>
			<tr>
				<th align="LEFT">FAQ Page Content</th>
				<td>
					<html:textarea property="FAQPageContent" rows="25" cols="80"/>
				</td>
			</tr>
			<tr>
				<th align="LEFT">About Page Content</th>
				<td>
					<html:textarea property="aboutPageContent" rows="25" cols="80"/>
				</td>
			</tr>
            <tr>
     	   	<td><input type="submit" value="Update"></td>
			</tr>
			<tr>
				<th align="LEFT">Maintenance Tag Line</th>
				<td>
					<html:textarea property="maintenanceTagLine" rows="5" cols="80"/>
				</td>
			</tr>
		</table>
	</html:form>

<jsp:include page="../global/footer.jsp" flush="true" />
