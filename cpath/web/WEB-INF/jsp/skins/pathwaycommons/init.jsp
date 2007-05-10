<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%

    CPathUIConfig.setShowDataSourceDetails(true);
    CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_BIOPAX);
    WebUIBean webUIBean = new WebUIBean();
    webUIBean.setApplicationName("Pathway Commons");
    webUIBean.setDisplayBrowseByOrganismTab(false);
    webUIBean.setDisplayBrowseByPathwayTab(false);
    webUIBean.setWantCytoscape(true);
    webUIBean.setDisplayWebServiceTab(false);
    webUIBean.setDisplayFilterTab(true);
    webUIBean.setDefaultUserMessage("Pathway Commons a project for the scientific community.  "
        + "We welcome your <a href='get_feedback.do'>feedback</a>.");
    webUIBean.setBaseURL("pathwaycommons.org");
    webUIBean.setSmtpHost("cbio.mskcc.org");
    webUIBean.setFeedbackEmailTo("pc-info@pathwaycommons.org");
    CPathUIConfig.setWebUIBean(webUIBean);
%>
