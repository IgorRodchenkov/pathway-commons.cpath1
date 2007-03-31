<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%
String dataSourceName = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME;
String dataSourceValue = GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL;
String entityValue =  "pathway";
String entityName = GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME;
%>
<form name="searchbox" action="webservice.do" method="get">
<input type="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0"/>
<input type="hidden" name="<%= dataSourceName %>" value="<%= dataSourceValue %>"/>
<input type="hidden" name="<%= entityName %>" value="<%= entityValue %>"/>
<input type="text" name="<%= ProtocolRequest.ARG_QUERY %>" size="15"/>
<input type="submit" value="Search"/>
<input type="hidden" name="<%= ProtocolRequest.ARG_FORMAT %>" value="<%= ProtocolConstants.FORMAT_HTML %>"/>
<input type="hidden" name="<%= ProtocolRequest.ARG_COMMAND %>"
    size="25" value='<%= ProtocolConstants.COMMAND_GET_BY_KEYWORD %>'/>
</form>