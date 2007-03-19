<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%
    CPathUIConfig.setShowDataSourceDetails(true);

    // Set to CPathUIConfig.WEB_MODE_PSI_MI or
    // CPathUIConfig.WEB_MODE_BIO_PAX.
    CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_BIOPAX);
    WebUIBean webUIBean = new WebUIBean();
    webUIBean.setApplicationName("cPath");

    webUIBean.setDisplayBrowseByOrganismTab(false);
    webUIBean.setDisplayBrowseByPathwayTab(true);
    webUIBean.setDisplayCytoscapeTab(false);
    webUIBean.setDisplayWebServiceTab(true);
    webUIBean.setDisplayFilterTab(true);
    CPathUIConfig.setWebUIBean(webUIBean);
%>