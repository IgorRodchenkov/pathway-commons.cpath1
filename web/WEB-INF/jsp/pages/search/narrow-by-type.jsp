<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.model.BioPaxEntityTypeMap"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.TreeMap"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.mskcc.pathdb.schemas.biopax.BioPaxConstants" %>
<%@ page import="org.mskcc.pathdb.taglib.SearchTabs" %>
<%
    WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
    ProtocolRequest protocolRequest = (ProtocolRequest) request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    Map<String, Integer> hitByTypeMap =
            (Map<String, Integer>) request.getAttribute(BaseAction.ATTRIBUTE_HITS_BY_RECORD_TYPE_MAP);
    Map sortedHitByTypeMap = new TreeMap(hitByTypeMap);
    String recordType = protocolRequest.getRecordType();
    String keyDataSource = request.getParameter(GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME);
%>
<span class="results_tabs">
<%
    for (String type : (Set<String>)sortedHitByTypeMap.keySet()) {
	    // label
        String typePlainEnglish = SearchTabs.PATHWAYS_TAB_TITLE;
        if (type.equals("PHYSICAL_ENTITY")) {
            typePlainEnglish = SearchTabs.PHYSICAL_ENTITIES_TAB_TITLE;
        }
        String label = typePlainEnglish + " (" + hitByTypeMap.get(type) + ")";
	    if (type.equals(recordType)) {
		   out.print("<span class='results_tab_active'>" + label + "</span>");
	    }
	    else {
		    if (hitByTypeMap.get(type) > 0) {
                out.print("<span class='results_tab_inactive'>");
                out.println("<a href='webservice.do?version=" +
                            webUIBean.getWebApiVersion() +
                            "&q=" + protocolRequest.getQuery() +
			                "&format=html&cmd=get_by_keyword&" +
			                ProtocolRequest.ARG_RECORD_TYPE + "=" + type + "&" +
						    GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME + "=" + keyDataSource +
				            "'>" + label +
				            "</a></span>");
            }
	    }
    }
%>
</span>