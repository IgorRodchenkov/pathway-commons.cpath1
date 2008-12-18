<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion1"%>
<%
    CPathUIConfig.setShowDataSourceDetails(true);

    // Set to CPathUIConfig.WEB_MODE_PSI_MI or
    // CPathUIConfig.WEB_MODE_BIO_PAX.
    CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_PSI_MI);
	if (CPathUIConfig.getWebUIBean() == null) {
	    WebUIBean webUIBean = new WebUIBean();
    	webUIBean.setApplicationName("cPath");

		webUIBean.setDisplayBrowseByOrganismTab(true);
		webUIBean.setDisplayBrowseByPathwayTab(false);
		webUIBean.setWantCytoscape(true);
		webUIBean.setDisplayWebServiceTab(true);
		webUIBean.setDisplayFilterTab(false);
		webUIBean.setWebApiVersion(ProtocolConstantsVersion1.VERSION_1);    
		CPathUIConfig.setWebUIBean(webUIBean);
    }
%>
