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
	String pathwayListRequestUrl = null;
	records = (ArrayList) request.getAttribute("RECORDS");
    ProtocolRequest pathwayListRequest = new ProtocolRequest();	
    pathwayListRequest.setCommand
    	(ProtocolConstants.COMMAND_GET_TOP_LEVEL_PATHWAY_LIST);
   	pathwayListRequest.setFormat(ProtocolConstants.FORMAT_BIO_PAX);
   	pathwayListRequestUrl = pathwayListRequest.getUri();

	// get right column content
	WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
	String homePageRightColumnContent = webUIBean.getHomePageRightColumnContent();
%>
<div id='axial' class='h3'>
<h3>Pathway Information</h3>
</div>

<table border='0' cellspacing='2' cellpadding='3' width='100%'>
<TR>
<TH>Pathway</TH>
<TH>Organism</TH>
</TR>
<%
    DaoOrganism daoOrganism = new DaoOrganism();
    HashMap organismMap = daoOrganism.getAllOrganismsMap();
    for (int i=0; i<records.size(); i++) {
        CPathRecord rec = (CPathRecord) records.get(i);
        if (i % 2 == 0) {
            out.println("<tr class='a'>");
        } else {
            out.println("<tr class='b'>");
        }
        String uri = "record.do?id=" + rec.getId();
        out.println("<TD><A HREF=\"" + uri + "\">"
                + rec.getName() + "</A>");
        out.println("</TD>");

        Organism organism = (Organism) organismMap.get(Integer.toString
                (rec.getNcbiTaxonomyId()));
        if (organism != null) {
            out.println("<TD>");
            if (organism.getSpeciesName() != null) {
                out.println (organism.getSpeciesName());
            }
            out.println("</TD>");
        }
        out.println("</TR>");
    }
%>
</TABLE>