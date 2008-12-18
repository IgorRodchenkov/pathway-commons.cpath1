<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion2"%>
<%
    CPathUIConfig.setShowDataSourceDetails(true);

    // Set to CPathUIConfig.WEB_MODE_PSI_MI or
    // CPathUIConfig.WEB_MODE_BIO_PAX.
    CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_BIOPAX);
	if (CPathUIConfig.getWebUIBean() == null) {
	   WebUIBean webUIBean = new WebUIBean();
	   webUIBean.setApplicationName("cPath");

	   webUIBean.setDisplayBrowseByOrganismTab(false);
	   webUIBean.setDisplayBrowseByPathwayTab(true);
	   webUIBean.setWantCytoscape(true);
	   webUIBean.setDisplayWebServiceTab(true);
	   webUIBean.setDisplayFilterTab(true);
	   webUIBean.setWebApiVersion(ProtocolConstantsVersion2.VERSION_2);    
	   CPathUIConfig.setWebUIBean(webUIBean);
    }
%>
