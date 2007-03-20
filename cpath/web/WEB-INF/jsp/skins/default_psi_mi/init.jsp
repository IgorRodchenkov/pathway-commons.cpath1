<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%
    CPathUIConfig.setShowDataSourceDetails(true);

    // Set to CPathUIConfig.WEB_MODE_PSI_MI or
    // CPathUIConfig.WEB_MODE_BIO_PAX.
    CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_PSI_MI);
    WebUIBean webUIBean = new WebUIBean();
    webUIBean.setApplicationName("cPath");

    webUIBean.setDisplayBrowseByOrganismTab(true);
    webUIBean.setDisplayBrowseByPathwayTab(false);
    webUIBean.setDisplayCytoscapeTab(true);
    webUIBean.setDisplayWebServiceTab(true);
    webUIBean.setDisplayFilterTab(false);
    CPathUIConfig.setWebUIBean(webUIBean);
%>