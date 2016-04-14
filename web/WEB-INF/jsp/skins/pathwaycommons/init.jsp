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
		webUIBean.setDefaultUserMessage("This portal <b>has not been updated</b> since October " +
        "2011. Please use the new <a href=\"http://www.pathwaycommons.org/pc2/\" " +
        "target=\"_blank\">PC2 web services</a>. <hr style=\"margin-bottom: 5px; margin-top: 5px;\">" +
        "Send us your <a href='https://groups.google.com/forum/#!forum/pathway-commons-help/join'>feedback</a>." +
        "&nbsp;&nbsp;Sign up for Pathway Commons <a href='get_subscribe.do'>announcements</a>." +
        "&nbsp;&nbsp;<a href=\"http://groups.google.com/group/pathway-commons-announce/feed/rss_v2_0_msgs.xml\">" +
		"<img src=\"jsp/images/rss.gif\" alt=\"RSS Logo\"/></a> " +
		"<a href=\"http://groups.google.com/group/pathway-commons-announcements/feed/rss_v2_0_msgs.xml\">RSS Feed</a>");
		webUIBean.setBaseURL("www.pathwaycommons.org");
		webUIBean.setSmtpHost("cbio.mskcc.org");
		webUIBean.setFeedbackEmailTo("pathway-commons-help@googlegroups.org");
		webUIBean.setWebApiVersion(ProtocolConstantsVersion3.VERSION_3);
		webUIBean.addSupportedIdType(ExternalDatabaseConstants.UNIPROT);
		webUIBean.addSupportedIdType(ExternalDatabaseConstants.INTERNAL_DATABASE);
		webUIBean.addSupportedIdType(ExternalDatabaseConstants.ENTREZ_GENE);
        webUIBean.addSupportedIdType(ExternalDatabaseConstants.GENE_SYMBOL);
		webUIBean.setTagLine("Search and visualize public biological pathway information.  " +
				"Single point of access. [<a href='faq.do'>more...</a>]");
		webUIBean.setEnableMiniMaps(true);
        webUIBean.setImageMapServerURL("http://localhost:8080/nms/retrieve-neighborhood-map.do");
		webUIBean.setFilterInteractions("IN_SAME_COMPONENT,CO_CONTROL");
		webUIBean.setMaxMiniMapSize(100);
		webUIBean.setConverterThreshold(1000);
		webUIBean.setSnapshotDownloadBaseURL("http://www.pathwaycommons.org/archives/");
		CPathUIConfig.setWebUIBean(webUIBean);
    }
%>
