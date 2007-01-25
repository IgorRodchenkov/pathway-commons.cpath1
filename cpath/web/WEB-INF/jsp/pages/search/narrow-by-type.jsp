<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.model.BioPaxEntityTypeMap"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.TreeMap"%>
<%
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    Map<String, Integer> hitByTypeMap =
        (Map<String, Integer>)request.getAttribute(BaseAction.ATTRIBUTE_HITS_BY_RECORD_TYPE_MAP);
	Map sortedHitByTypeMap = new TreeMap(hitByTypeMap);
    BioPaxEntityTypeMap typesMap = new BioPaxEntityTypeMap();
    typesMap.put(GlobalFilterSettings.ALL_ENTITY_TYPES_FILTER_VALUE,
    GlobalFilterSettings.ALL_ENTITY_TYPES_FILTER_VALUE);
    String keyType = (String)request.getParameter(GlobalFilterSettings.ENTITY_TYPES_FILTER_NAME);
%>
<%
    out.println("<h3>Narrow Results by Type:</h3>");
    out.println("<ul>");
    for (String type : (Set<String>)sortedHitByTypeMap.keySet()) {
	    // label
		String plain = (type.equals(GlobalFilterSettings.ALL_ENTITY_TYPES_FILTER_VALUE)) ? "All Types" : (String)typesMap.get(type);
	    String label = plain + " (" + hitByTypeMap.get(type) + ")";
	    // output an ahref or text string for each type 
	    if (type.equals(keyType)) {
		   out.println("<font size=\"2\"><li>" + label + "</li></font>");
	    }
	    else {
	        out.println("<li>" +
			            "<a href='webservice2.do?version=1.0&q=" + protocolRequest.getQuery() +
			            "&format=html&cmd=get_by_keyword&" +
			            GlobalFilterSettings.ENTITY_TYPES_FILTER_NAME + "=" + type +
				        "'>" + label +
				        "</a>" +
				        "</li>");
			}
        }
	out.println("</ul>");
%>
