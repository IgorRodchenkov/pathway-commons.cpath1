<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
%>

<table>
<tr valign="top">
<td width="60%">
<div>
<b>
Lorem ipsum dolor sit amet, consectetuer adipiscing elit.
<a href="about.do">more...</a>
</b>
</div>
<div class="large_search_box">
<h1>Search <%= webUIBean.getApplicationName() %>:</h1>
<form name="searchbox" action="webservice.do" method="get">
<input type="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
<input type="text" name="<%= ProtocolRequest.ARG_QUERY %>" size="15"/>
<input type="submit" value="Search"/>
<input type="hidden" name="<%= ProtocolRequest.ARG_FORMAT %>" value="<%= ProtocolConstants.FORMAT_HTML %>"/>
<input type="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
    size="25" value='<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>'/>
</form>
<p>To get started, enter a gene name or identifier in the text box above.</p>
<p>To restrict your search to specific data sources or specific organisms, update your
<a href="filter.do">global filter settings</a>.</p>
</div>

<div class="home_page_box">
<p>
<%= webUIBean.getApplicationName() %> currently contains the following data sources:</p>
<cbio:dataSourceListTable/>
</div>
</td>
<td valign="top">
    <div class="home_page_box">
    <jsp:include page="../../global/dbStatsMini.jsp" flush="true" />
    </div>
    <p>
    Lorem ipsum dolor sit amet, consectetuer adipiscing elit.
    Proin ultrices, odio eu elementum placerat, risus tellus iaculis enim, nec auctor mauris lacus eget leo.
    </p>
    <p>Curabitur massa diam, euismod vel, faucibus ut, varius suscipit, libero. Phasellus quis orci.
    Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nulla id justo.
    Fusce risus urna, adipiscing ut, posuere in, euismod quis, orci.
    </p>
</td>
</tr>
</table>