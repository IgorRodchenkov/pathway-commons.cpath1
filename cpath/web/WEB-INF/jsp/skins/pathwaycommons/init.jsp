<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion2"%>
<%@ page import="org.mskcc.pathdb.util.ExternalDatabaseConstants"%>
<%
    CPathUIConfig.setShowDataSourceDetails(true);
    CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_BIOPAX);
    WebUIBean webUIBean = new WebUIBean();
    webUIBean.setApplicationName("Pathway Commons");
    webUIBean.setDisplayBrowseByOrganismTab(false);
    webUIBean.setDisplayBrowseByPathwayTab(false);
    webUIBean.setWantCytoscape(true);
    webUIBean.setDisplayWebServiceTab(true);
    webUIBean.setDisplayFilterTab(true);
    webUIBean.setDefaultUserMessage("Send us your <a href='get_feedback.do'>feedback</a>." +
		"&nbsp;&nbsp;Sign up for Pathway Commons <a href='get_subscribe.do'>announcements</a>." +
        "&nbsp;&nbsp;<a href=\"http://groups.google.com/group/pathway-commons-announce/feed/rss_v2_0_msgs.xml\"><img src=\"jsp/images/rss.gif\" alt=\"RSS Logo\"/></a> <a href=\"http://groups.google.com/group/pathway-commons-announce/feed/rss_v2_0_msgs.xml\">RSS Feed</a>");
    webUIBean.setBaseURL("pathwaycommons.org");
    webUIBean.setSmtpHost("cbio.mskcc.org");
    webUIBean.setFeedbackEmailTo("pc-info@pathwaycommons.org");
    webUIBean.setWebApiVersion(ProtocolConstantsVersion2.VERSION_2);
    webUIBean.addSupportedIdType(ExternalDatabaseConstants.UNIPROT);
    webUIBean.addSupportedIdType(ExternalDatabaseConstants.INTERNAL_DATABASE);
    webUIBean.addSupportedIdType(ExternalDatabaseConstants.ENTREZ_GENE);
    webUIBean.setTagLine("Search and visualize public biological pathway information.  Single point of access. [<a href='faq.do'>more...</a>]");
    CPathUIConfig.setWebUIBean(webUIBean);
%>
