<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%

    CPathUIConfig.setShowDataSourceDetails(true);
    CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_BIOPAX);
    WebUIBean webUIBean = new WebUIBean();
    webUIBean.setApplicationName("cPath");
    webUIBean.setDisplayBrowseByOrganismTab(true);
    webUIBean.setDisplayBrowseByPathwayTab(true);
    webUIBean.setDisplayCytoscapeTab(true);
    webUIBean.setDisplayWebServiceTab(true);
    CPathUIConfig.setWebUIBean(webUIBean);
%>