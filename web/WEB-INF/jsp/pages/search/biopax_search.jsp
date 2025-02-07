<%@ page import="org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstantsVersion1,
                 org.mskcc.pathdb.protocol.ProtocolConstantsVersion2,
                 org.mskcc.pathdb.form.WebUIBean,
                 org.mskcc.pathdb.servlet.CPathUIConfig,
                 org.mskcc.pathdb.taglib.Pager,
                 org.mskcc.pathdb.sql.dao.DaoCPath,
                 org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot,
                 org.mskcc.pathdb.lucene.LuceneResults,
                 org.mskcc.pathdb.util.html.HtmlUtil,
                 org.mskcc.pathdb.schemas.biopax.BioPaxConstants,
                 org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils,
                 org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary,
                 org.mskcc.pathdb.util.biopax.BioPaxRecordUtil,
                 org.mskcc.pathdb.taglib.ReactomeCommentUtil,
                 java.net.URLEncoder"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="java.util.*" %>
<%@ page import="org.mskcc.pathdb.model.*" %>
<%@ page import="org.mskcc.pathdb.xdebug.XDebugUtil" %>
<%@ page import="org.apache.lucene.search.Explanation" %>
<%@ page import="org.mskcc.pathdb.util.ExternalDatabaseConstants" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>
<%@ page errorPage = "../JspError.jsp" %>
<%	request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Search Results"); %>
<jsp:include page="../../global/redesign/header.jsp" flush="true" />
<%
    final String CYTOSCAPE_HTTP_SERVER = "127.0.0.1:27182";
    final int MAX_SCOREBOARD_WIDTH = 62;
    final int SCOREBOARD_HEIGHT = 6;

    WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
    LuceneResults luceneResults = (LuceneResults) request.getAttribute
            (BaseAction.ATTRIBUTE_LUCENE_RESULTS);
    ProtocolRequest protocolRequest = (ProtocolRequest)
            request.getAttribute(BaseAction.ATTRIBUTE_PROTOCOL_REQUEST);
    long cpathIds[] = luceneResults.getCpathIds();
    long geneSymbolHitIds[] = (long[]) request.getAttribute(BaseAction.ATTRIBUTE_GENE_SYMBOL_HIT_LIST);
    int totalNumHits = luceneResults.getNumHits();
    List<List<String>> fragments = luceneResults.getFragments();
    Map<Long, Set<String>> recordDataSources = luceneResults.getDataSourceMap();
    Map<Long, Float> scores = luceneResults.getScores();
    ArrayList<Integer> numDescendentsList = luceneResults.getNumDescendentsList();
    ArrayList<Integer> numParentsList = luceneResults.getNumParentsList();
    ArrayList<Integer> numParentPathwaysList = luceneResults.getNumParentPathwaysList();
    ArrayList<Integer> numParentInteractionsList = luceneResults.getNumParentInteractionsList();
    Map<Long, Explanation> explanationMap = luceneResults.getExplanationMap();
    String organismFlag = request.getParameter(ProtocolRequest.ARG_ORGANISM);
    String recordType = protocolRequest.getRecordType();
    String keyDataSource = request.getParameter
            (GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME);
    // server name
    String serverName = request.getServerName();
    serverName = (serverName.indexOf("pathwaycommons") != -1) ? serverName : serverName + ":8080";
    // cytoscape link
    String urlForCytoscapeLink = (String) request.getAttribute("request_url");
    //String urlForCytoscapeLink = ((StringBuffer)request.getRequestURL()).toString();
    urlForCytoscapeLink = urlForCytoscapeLink.substring(7); // remove "http://" from string
    // data source parameter string  to network neighborhood map
    String encodedDataSourceParameter = "";
    GlobalFilterSettings filterSettings =
            (GlobalFilterSettings) request.getSession().getAttribute
                    (GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
    DaoExternalDbSnapshot daoSnapShot = new DaoExternalDbSnapshot();
    if (filterSettings != null) {
        for (Long snapshotID : (Set<Long>) filterSettings.getSnapshotIdSet()) {
            ExternalDatabaseSnapshotRecord record = daoSnapShot.getDatabaseSnapshot(snapshotID);
            encodedDataSourceParameter += record.getExternalDatabase().getMasterTerm() + ",";
        }
        // snip off last ' '
        encodedDataSourceParameter = encodedDataSourceParameter.replaceAll(",$", "");
        // encode
        encodedDataSourceParameter = URLEncoder.encode(encodedDataSourceParameter, "UTF-8");
    }
    boolean debugMode = XDebugUtil.xdebugIsEnabled(request);
%>
<script type="text/javascript">
    //  Toggles details on single row
    function toggleDetails (id) {
        YAHOO.log ("Toggling row with cPathID:  " + id);
        var elements = new Array();
        elements[0] = document.getElementById(id + "_details");
        var current = YAHOO.util.Dom.getStyle(elements[0], 'display');
        YAHOO.log ("Current Display Style is set to:  " + current);
        var toggleImage = document.getElementById (id + "_image");
        if (current == false || current == "none" || current == null) {
            YAHOO.util.Dom.setStyle(elements, 'display', 'inline');
            if (toggleImage != null) {
                toggleImage.innerHTML = "<img src='jsp/images/close.gif'>";
            }
        } else {
            YAHOO.util.Dom.setStyle(elements, 'display', 'none');
            if (toggleImage != null) {
                toggleImage.innerHTML = "<img src='jsp/images/open.gif'>";
            }
        }
    }
</script>
<%!
    private String getInspectorButtonHtml(long cPathId) {
        return "<div class='toggle_details'><a "
                + "title='Toggle Record Details' onClick=\"toggleDetails('cpath_" + cPathId
                + "')\"><div id='cpath_" + cPathId + "_image' class='toggleImage'>"
                + "<img src='jsp/images/open.gif'/></div></a>";
    }

    private String getDetailsHtml(long cPathId, String label, String html) {
        return ("<div id='cpath_" + cPathId + "_" + label + "' class='details'>"
                + html + "</div>");
    }

    private String getDataSourceHtml(long cPathId, Map<Long, Set<String>> recordDataSources) {
        StringBuffer html = new StringBuffer();
        Set<String> dataSourceSet = recordDataSources.get(cPathId);
        if (dataSourceSet.size() > 0) {
            html.append("<p><b>Data Source(s):</b>&nbsp;&nbsp;");
            // loop here
            int counter = 0;
            for (String dataSource : recordDataSources.get(cPathId)) {
                html.append(dataSource);
                if (counter < recordDataSources.get(cPathId).size() -1) {
                    html.append (", ");
                }
                counter++;
            }
            html.append("</p>\n\r");
        }
        return html.toString();
    }

    /**
	 * Method used to print datasource on first line of search result (next to Pathway: or Protein:)
	 */
    private String getDataSourceHtml2(long cPathId, Map<Long, Set<String>> recordDataSources) {
        StringBuffer html = new StringBuffer();
        Set<String> dataSourceSet = recordDataSources.get(cPathId);
        if (dataSourceSet.size() > 0) {
            html.append("from ");
            // loop here
            int counter = 0;
            for (String dataSource : recordDataSources.get(cPathId)) {
                html.append(dataSource);
                if (counter < recordDataSources.get(cPathId).size() -1) {
                    html.append (", ");
                }
                counter++;
            }
        }
        return html.toString();
    }

    private String getRecordSummaryHtml(CPathRecord record,
            BioPaxRecordSummary summary,
            ProtocolRequest request) {
        // only show pathway summary info
        //if (record.getType() != CPathRecordType.PATHWAY) return "";
        StringBuffer html = new StringBuffer();
        if (summary.getComments() != null) {
            String comments[] = summary.getComments();
            if (comments.length > 0) {
                String comment = ReactomeCommentUtil.massageComment(comments[0]);
                String paragraphs[] = comment.split("<p>");
                if (paragraphs.length > 0) {
                    html.append("<p><b>Summary:</b>&nbsp;&nbsp;");
                    for (String term : request.getQuery().split(" ")) {
                        paragraphs[0] = paragraphs[0].replaceAll(term,
                                LuceneResults.START_TAG +
                                        term +
                                        LuceneResults.END_TAG);
                    }
                    html.append(paragraphs[0] + "</p>\n\r");
                }
            }
        }
        return html.toString();
    }

    private String getFragmentsHtml(List<String> fragments, String summaryLabel,
            BioPaxRecordSummary summary, int maxLength) {

        // check args
        if (fragments == null || fragments.size() == 0) return "";

        Character period = new Character('.');
        StringBuffer html = new StringBuffer();
        boolean ulAppended = false;
        for (String fragment : fragments) {
            // create copy of fragment with html stripped out for comparision purposes
            String fragmentCopy = new String(fragment);
            fragmentCopy = fragmentCopy.replaceAll("<b>", "");
            fragmentCopy = fragmentCopy.replaceAll("</b>", "");
            boolean appendPrefix = (Character.isLowerCase(fragmentCopy.charAt(0)) ||
                    !Character.isLetter(fragmentCopy.charAt(0)));
            boolean appendSuffix = period.compareTo(fragment.charAt(fragment.length() - 1)) != 0;
            // if the fragment equals the summaryLabel (header) skip
            if (fragmentCopy.indexOf(summaryLabel) != -1
                    && fragmentCopy.length() <= summaryLabel.length())
                continue;
            if (fragment.indexOf(LuceneResults.MEMBER_OF) != -1) {
                String recordType = summary.getType();
                HashMap entityTypeMap = BioPaxEntityTypeMap.getCompleteMap();

                if (recordType != null) {
                    recordType = (String) entityTypeMap.get(recordType);
                }
                if (recordType == null) {
                    recordType = "Record";
                }
                fragment = recordType + " " + fragment + ".";
                appendPrefix = appendSuffix = false;
            }
            // write out the html, add "..." in front and back of fragment as needed
            if (!ulAppended) {
                html.append("<ul>");
                ulAppended = true;
            }
            html.append("<li>");
            if (appendPrefix) html.append("... ");
            fragment = HtmlUtil.truncateLongWords(fragment, maxLength);
            // to prevent Dr. being replade with Dr. ...
            fragment = fragment.replaceAll("(?i)(dr)\\.", "$1\\*");
            fragment = fragment.replaceAll("\\.", ". ...");
            // to prevent Dr. being replade with Dr. ...
            fragment = fragment.replaceAll("(?i)(dr)\\*", "$1\\.");
            if (fragment.matches("^.*\\. \\.\\.\\.$")) {
                fragment = fragment.replaceAll("\\. \\.\\.\\.$", ".");
            }
            html.append(fragment);
            if (appendSuffix) html.append(" ...");
            html.append("</li>\n\r");
        }
        if (ulAppended) {
            html.append("</ul>");
        }
        return html.toString();
    }
%>
<%
if (protocolRequest.getQuery() != null) {
%>
<h1>Searched for:  <%= protocolRequest.getQuery() %> [<%= filterSettings.getFilterSummary()%>]</h1>
<%
}
%>
<%
if (totalNumHits == 0) {
%>
	<div class="splitcontentleft">
    <jsp:include page="../../global/redesign/currentFilterSettings.jsp" flush="true" />
	</div>
	<div class="splitcontentright">
<%
	Set<Integer> organismIdSet = filterSettings.getOrganismTaxonomyIdSet();
	if (daoSnapShot.getAllNetworkDatabaseSnapshots().size() !=
            filterSettings.getSnapshotIdSet().size() ||
		!organismIdSet.contains(GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE)) {
		out.println("<div class=\"user_message\"><b>No Matching Records Found. " +
                "Try updating your filter settings or try a different search term.</b></div></p>");
	}
	else {
		out.println("<div class=\"user_message\"><b>No Matching Records Found.  Please try " +
                "a different search term.</b></div>");
	}
%>
    </div>
<%
}
else {
    out.println("<p>");
    String recordStr = "records";
    if (totalNumHits == 1) {
        recordStr = "record";
    }
    out.println("Your search has found <b>" + totalNumHits + "</b> " +
                "relevant " + recordStr + ":<br>");
%>
	<div class="splitcontentleft">

    <div class="box">
    <h3>Narrow Results:</h3>
    <jsp:include page="./narrow-by-datasource.jsp" flush="true" />
    </div>
	</div>
    <div class="splitcontentright">
    <form action="#">
    <fieldset>
    <legend>
    <jsp:include page="./narrow-by-type.jsp" flush="true" />
    </legend>

    <%
        if (geneSymbolHitIds != null && geneSymbolHitIds.length > 0) {
            out.println ("<div class='exact_matches'>Exact Matches:  " );
            DaoCPath dao = DaoCPath.getInstance();
            for (int i=0; i<geneSymbolHitIds.length; i++) {
                CPathRecord record = dao.getRecordById(geneSymbolHitIds[i]);
                BioPaxRecordSummary summary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
                List<ExternalLinkRecord> linkList = summary.getExternalLinks();
                String geneSymbol = protocolRequest.getQuery();
                if (linkList != null) {
                    for (ExternalLinkRecord externalLink:  linkList) {
                        if (externalLink.getExternalDatabase().getMasterTerm().equals
                        (ExternalDatabaseConstants.GENE_SYMBOL)) {
                            geneSymbol = externalLink.getLinkedToId();
                        }
                    }
                    out.println("<a href='record2.do?id=" + record.getId()
                        + "'>" + geneSymbol + "</a> [" + summary.getOrganism() + "]&nbsp;");
                }
            }
            out.println ("</div>");
        }
    %>

<%
	Pager pager = new Pager (protocolRequest, totalNumHits);
	out.println("<div class='search_buttons'>");
	out.println("<span class='srp'>" + pager.getHeaderHtmlForSearchPage("white",
	ProtocolRequest.ARG_RECORD_TYPE + "=" + recordType + "&" +
	GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME + "=" + keyDataSource) + "</span>");
	out.println ("</div>");
%>
    <table cellpadding="2" cellspacing="0" border="0" width="100%">
<%
    DaoCPath dao = DaoCPath.getInstance();
    for (int i=0; i< cpathIds.length; i++) {
        CPathRecord record = dao.getRecordById(cpathIds[i]);
        Integer numDescendents = numDescendentsList.get(i);
        Integer numParents = numParentsList.get(i);
		Integer numParentPathways = numParentPathwaysList.get(i);
		Integer numParentInteractions = numParentInteractionsList.get(i);

        boolean showCytoscape = false;

		//  Determine if we should show / hide Cytoscape links
		if (record.getType() == CPathRecordType.PHYSICAL_ENTITY) {
		    if (numParents > 0) {
		        showCytoscape = true;
            }
        } else {
		    if (numDescendents > 0) {
		        showCytoscape = true;
            }
        }

        String url = "record2.do?id=" + record.getId();
	    // used to render score board graphic
	    Float score = (Float)scores.get(cpathIds[i]);
	    int scoreboardWidth = (int)(score * MAX_SCOREBOARD_WIDTH);
	    int percentage = (int)(score * 100);
	    // some massaging to handle scores very close to zero, but not zero
	    scoreboardWidth = (percentage == 1 && scoreboardWidth == 0) ? 1 : scoreboardWidth;
	    scoreboardWidth = (score > 0.0 && scoreboardWidth == 0) ? 1 :  scoreboardWidth;
	    String spacerString = (scoreboardWidth < MAX_SCOREBOARD_WIDTH) ?
		    ("<td><img src=\"jsp/images/spacer.gif\" width=\"" + String.valueOf
		    (MAX_SCOREBOARD_WIDTH-scoreboardWidth) + "\" height=\""
		    + SCOREBOARD_HEIGHT + "\" alt=\"" + String.valueOf(percentage) + "%\"></td>") : "";
		String summaryLabel = "";
		String header = "";
		BioPaxRecordSummary summary = null;
        try {
            summary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
			summaryLabel = summary.getLabel();
            header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(summary);
			// if protein, add organism information
			if (record.getType() == CPathRecordType.PHYSICAL_ENTITY) {
			    String organism = summary.getOrganism();
			    if (organism != null && organism.length() > 0) {
				    header += (" [" + organism + "]");
                }
            }
            out.println("<tr valign=\"top\">");
			// score bar
			//out.println("<th align=left>" + String.valueOf(score) + "</th>");
			out.println("<th align=left width=\"" + MAX_SCOREBOARD_WIDTH + "\">");
			out.println("<div class='scoreboard'>");
			out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" bgcolor=\"#ffffff\">");
			out.println("<tr>");
			out.println("<tr valign=top><td><img src=\"jsp/images/relevance.gif\" width=\"" +
			            String.valueOf(scoreboardWidth) +
			            "\" height=\"" + SCOREBOARD_HEIGHT + "\" alt=\"" +
			            String.valueOf(percentage) +
			            "%\">" + "</td>" + spacerString);
			out.println("</tr>");
		    out.println("</table>");
		    out.println("</div>");
			out.println("</th>");
			// record header
            out.println("<th align=left width=\"90%\">");
			out.println("<a href=\"" + url + "\">" + header + "</a>");

			// create data source text
            String dataSourceHtml = getDataSourceHtml2(record.getId(), recordDataSources);

			//  Show pathway or protein (interaction & pathway) size
			if (record.getType() == CPathRecordType.PATHWAY) {
				String text = (numDescendents == 1) ? "1 molecule" : numDescendents + " molecules";
				text = (numDescendents == 0) ? "" : text;
				if (numDescendents > 0) {
				    out.println ("&nbsp;<span class='small_no_bold'>" + dataSourceHtml + "&nbsp;&nbsp;[" + text +"]</span>");
                }

				//  Output first sentence in description
				String comments[] = summary.getComments();
				if (comments != null && comments.length > 0) {
				    String comment = ReactomeCommentUtil.massageComment(comments[0]);
				    String sentences[] = comment.split("\\.");
				    out.print ("<div class='first_sentence'>" + sentences[0].trim() +".");
				    //  Don't end with the word dr.
			        if (sentences[0].toLowerCase().endsWith("dr")) {
			            if (sentences.length > 1) {
			                out.print (" " + sentences[1].trim() + ".");	     
                        }
                    }
				    out.println ("</span>");
                }

            }
			else if (record.getSpecificType().equalsIgnoreCase(BioPaxConstants.PROTEIN)) {
				String textPathways = (numParentPathways == 1) ? "1 pathway" : numParentPathways + " pathways";
				textPathways = (numParentPathways == 0) ? "" : textPathways;
				String textInteractions = (numParentInteractions == 1) ? "1 interaction" : numParentInteractions + " interactions";
				textInteractions = (numParentInteractions == 0) ? "" : textInteractions;
				if (textPathways.length() > 0 || textInteractions.length() > 0) {
					String delimiter = (textPathways.length() > 0 && textInteractions.length() > 0) ? ", " : "";
					out.println ("&nbsp;<span class='small_no_bold'>" + dataSourceHtml  + "&nbsp;&nbsp;[" + textPathways + delimiter + textInteractions +"]</span>");
				}

				//  Show full protein name
				String name = summary.getName();
				if (name != null && summary.getLabel() != null && (!name.equals(summary.getLabel()))) {
			        out.println ("<div class='first_sentence'>" + name + "</div>");
                }
			}
            else {
                    out.println("&nbsp;<span class='small_no_bold'>" + dataSourceHtml + "</span>");
            }
            out.println("</th>");
			// inspection button
			out.println("<th align=right>");

            String recordSummaryHtml = getRecordSummaryHtml(record, summary, protocolRequest);
            dataSourceHtml = getDataSourceHtml (record.getId(), recordDataSources);
			//  Since all results have Cytoscape links, we assume all have details
			boolean hasDetails = true;
			if (hasDetails) {
    			out.println(getInspectorButtonHtml(record.getId()));
            }
			out.println("</th>");
			out.println("</tr>");
			// details
			out.println("<tr><td colspan=\"3\">");
			out.println("<div id='cpath_" + record.getId() + "_details' class='details'>");
			out.println(recordSummaryHtml);
			//out.println(dataSourceHtml);

			//  Cytoscape Links
			boolean pathwayType = record.getType() == CPathRecordType.PATHWAY;
			if (webUIBean.getWantCytoscape() && showCytoscape) {
				// add link to cytoscape
				if (pathwayType) {
					out.println("<p><a href=\"http://" + CYTOSCAPE_HTTP_SERVER + "/" +
								urlForCytoscapeLink +
								"?" + ProtocolRequest.ARG_VERSION + "=" + ProtocolConstantsVersion2.VERSION_2 +
								"&" + ProtocolRequest.ARG_COMMAND + "=" + ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID +
								"&" + ProtocolRequest.ARG_OUTPUT + "=" + ProtocolConstantsVersion1.FORMAT_BIO_PAX +
								"&" + ProtocolRequest.ARG_QUERY + "=" + String.valueOf(cpathIds[i])  +
								"&" + ProtocolRequest.ARG_DATA_SOURCE + "=" + encodedDataSourceParameter +
								"\"" + " id=\"" + String.valueOf(cpathIds[i]) +"\"" +
								" onclick=\"appRequest(this.href, this.id, " + "'"
								+ ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID + "', "
								+ "'empty_title', '" + encodedDataSourceParameter + "'); return false;\"" +
								">View pathway in Cytoscape</a>");
				}
				else {
					String encodedNeighborhoodTitle = URLEncoder.encode("Neighborhood: " + summaryLabel, "UTF-8");
					out.println("<p><a href=\"http://" + CYTOSCAPE_HTTP_SERVER + "/" +
								urlForCytoscapeLink +
								"?" + ProtocolRequest.ARG_VERSION + "=" + ProtocolConstantsVersion2.VERSION_2 +
								"&" + ProtocolRequest.ARG_COMMAND + "=" + ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS +
								"&" + ProtocolRequest.ARG_OUTPUT + "=" + ProtocolConstantsVersion1.FORMAT_BIO_PAX +
								"&" + ProtocolRequest.ARG_QUERY + "=" + String.valueOf(cpathIds[i]) +
								"&" + ProtocolRequest.ARG_DATA_SOURCE + "=" + encodedDataSourceParameter +
								"&" + ProtocolRequest.ARG_NEIGHBORHOOD_TITLE + "=" + encodedNeighborhoodTitle + "\"" +
								" id=\"" + String.valueOf(cpathIds[i]) +"\"" +
								" onclick=\"appRequest(this.href, this.id, " + "'"
								+ ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS + "', '"
								+ encodedNeighborhoodTitle + "', '" + encodedDataSourceParameter + "'); return false;\"" +
								">View network neighborhood in Cytoscape</a>");
				}
				out.println("<a href=\"cytoscape.do\">(help)</a></p>");
			}
			out.println("</div>");
			out.println("</td></tr>");
        } catch (IllegalArgumentException e) {
            out.println("<div>" +
                    "<a href=\"" + url + "\">" + record.getName() + "</a></div>");
        }
        if (organismFlag == null) {
			// fragments
            out.println("<tr><td colspan=\"3\">");
			out.println("<div class='search_fragment'>");
			String htmlFragments = getFragmentsHtml(fragments.get(i), summaryLabel, summary, 40);
			if (htmlFragments.length() > 0) {
				out.println(htmlFragments);
			}
			out.println("</div>");
			if (debugMode) {
			    out.println ("<div class='lucene_explain'>");
			    Explanation explanation = explanationMap.get(cpathIds[i]);
			    out.println ("<B>Lucene Score Explanation:</B>  " + explanation.toHtml());
			    out.println ("</div>");
            }
			out.println("</td></tr>");
        }
    }
    out.println("</table>");
    out.println("</fieldset>");
    out.println("</form>");
	out.println("</div>");
}
%>

<%!
    public boolean detailsExist(String summary, String dataSourceSummary) {
        if (summary != null && summary.trim().length() > 0) {
            return true;
        } else if (dataSourceSummary != null && dataSourceSummary.trim().length() > 0) {
            return true;
        }
        return false;
    }
%>
<p/>
<jsp:include page="../../global/redesign/footer.jsp" flush="true" />
