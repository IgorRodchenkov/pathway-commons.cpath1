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
    typesMap.put(GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_VALUE_ALL,
    GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_VALUE_ALL);
    String keyType = (String)request.getParameter(GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME);
    String keyDataSource = (String)request.getParameter(GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME);
%>
<%
    out.println("<h3>Narrow Results by Type:</h3>");
    out.println("<ul>");
    for (String type : (Set<String>)sortedHitByTypeMap.keySet()) {
	    // label
		String plain = (type.equals(GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_VALUE_ALL)) ? "All Types" : (String)typesMap.get(type);
	    String label = plain + " (" + hitByTypeMap.get(type) + ")";
	    if (type.equals(keyType)) {
		   out.println("<li>" + label + "<img src=\"jsp/images/spacer.gif\" width=\"10\"><img src=\"jsp/images/arrow.gif\"></li>");
	    }
	    else {
		    if (hitByTypeMap.get(type) > 0) {
	            out.println("<li>" +
			                "<a href='webservice2.do?version=1.0&q=" + protocolRequest.getQuery() +
			                "&format=html&cmd=get_by_keyword&" +
			                GlobalFilterSettings.NARROW_BY_ENTITY_TYPES_FILTER_NAME + "=" + type + "&" +
						    GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME + "=" + keyDataSource +
				            "'>" + label +
				            "</a>" +
				            "</li>");
            }
            else {
			    out.println("<li>" + label + "</li>");
            }
	    }
    }
	out.println("</ul>");
%>
