<%@ page import="java.util.HashMap,
                 java.util.ArrayList,
                 org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.model.CPathRecord,
				 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.sql.dao.DaoOrganism,
                 org.mskcc.pathdb.model.Organism,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%
	// get pathway list records
	ArrayList records = null;
	records = (ArrayList) request.getAttribute("RECORDS");
%>

<ul>
<%
	if (records.size() > 0){
	    for (int i=0; i<records.size(); i++) {
    	    CPathRecord rec = (CPathRecord) records.get(i);
        	String uri = "record.do?id=" + rec.getId();
	        out.println("<li><a href=\"" + uri + "\">"
    	            + rec.getName() + "</a>");
        	out.println("</li>");
	    }
	}
	else{
		out.println("No Pathway Records Exist");
	}
%>
</ul>