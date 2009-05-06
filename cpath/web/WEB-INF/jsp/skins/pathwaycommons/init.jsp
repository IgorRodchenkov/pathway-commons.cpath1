<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstantsVersion3"%>
<%@ page import="org.mskcc.pathdb.util.ExternalDatabaseConstants"%>
<%
    CPathUIConfig.setShowDataSourceDetails(true);
    CPathUIConfig.setWebMode(CPathUIConfig.WEB_MODE_BIOPAX);
	if (CPathUIConfig.getWebUIBean() == null) {
	    WebUIBean webUIBean = new WebUIBean();
    	webUIBean.setApplicationName("Pathway Commons");
		webUIBean.setDisplayBrowseByOrganismTab(false);
		webUIBean.setDisplayBrowseByPathwayTab(false);
		webUIBean.setWantCytoscape(true);
		webUIBean.setDisplayWebServiceTab(true);
		webUIBean.setDisplayFilterTab(true);
		webUIBean.setDefaultUserMessage("Send us your <a href='get_feedback.do'>feedback</a>." +
		"&nbsp;&nbsp;Sign up for Pathway Commons <a href='get_subscribe.do'>announcements</a>." +
        "&nbsp;&nbsp;<a href=\"http://groups.google.com/group/pathway-commons-announce/feed/rss_v2_0_msgs.xml\"><img src=\"jsp/images/rss.gif\" alt=\"RSS Logo\"/></a> <a href=\"http://groups.google.com/group/pathway-commons-announcements/feed/rss_v2_0_msgs.xml\">RSS Feed</a>");
		webUIBean.setBaseURL("pathwaycommons.org");
		webUIBean.setSmtpHost("cbio.mskcc.org");
		webUIBean.setFeedbackEmailTo("pc-info@pathwaycommons.org");
		webUIBean.setWebApiVersion(ProtocolConstantsVersion3.VERSION_3);
		webUIBean.addSupportedIdType(ExternalDatabaseConstants.UNIPROT);
		webUIBean.addSupportedIdType(ExternalDatabaseConstants.INTERNAL_DATABASE);
		webUIBean.addSupportedIdType(ExternalDatabaseConstants.ENTREZ_GENE);
		webUIBean.setTagLine("Search and visualize public biological pathway information.  Single point of access. [<a href='faq.do'>more...</a>]");
		webUIBean.setEnableMiniMaps(true);
		//webUIBean.setImageMapServerURL("http://www.pathwaycommons.org/nms/retrieve-neighborhood-map.do");
		webUIBean.setImageMapServerURL("http://toro.cbio.mskcc.org:8080/nms/retrieve-neighborhood-map.do");
		webUIBean.setFilterInteractions("IN_SAME_COMPONENT,CO_CONTROL_DEPENDENT_SIMILAR,CO_CONTROL_DEPENDENT_ANTI,CO_CONTROL_INDEPENDENT_SIMILAR,CO_CONTROL_INDEPENDENT_ANTI");
		webUIBean.setMaxMiniMapSize(200);
		webUIBean.setSnapshotDownloadBaseURL("http://awabi.cbio.mskcc.org/pcdl/");
		CPathUIConfig.setWebUIBean(webUIBean);
    }
%>
