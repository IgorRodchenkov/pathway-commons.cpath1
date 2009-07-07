package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.form.WebUIBean;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.lucene.LuceneQuery;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.log4j.Logger;
import org.mskcc.pathdb.sql.query.QueryException;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Custom JSP Tag for displaying a list of data sources
 *
 * @author Benjamin Gross
 */
public class DataSourceListTable extends HtmlTable {
	
	private static int NUM_ICONS_ROW = 3;

	private boolean renderForHomepage;
    private Logger log = Logger.getLogger(DataSourceListTable.class);

	/**
	 * Set renderForHomepage boolean
	 *
	 * @param renderForHomepage Boolean
	 */
	public void setRenderForHomepage(Boolean renderForHomepage) {
		this.renderForHomepage = renderForHomepage;
	}

	/**
	 * Get renderForHomepage boolean
	 *
	 * @return renderForHomepage Boolean
	 */
	public Boolean getRenderForHomepage() {
		return renderForHomepage;
	}

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    protected void subDoStartTag() throws Exception {
        outputRecords();
    }

    /**
     * Output the Pathway List.
     */
    private void outputRecords() throws DaoException, QueryException, IOException,
										AssemblyException, ParseException {

		DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllNetworkDatabaseSnapshots();

        // process records
        if (list.size() == 0) {
            startTable();
            startRow();
            append("<TD COLSPAN=2>No Data Sources Available.</TD>");
            endRow();
            endTable();
        }
		else {
			WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
			ProtocolRequest protocolRequest = getProtocolRequest(webUIBean);
			GlobalFilterSettings filterSettings = new GlobalFilterSettings();
			if (renderForHomepage) {
				renderForHomepage(filterSettings, list);
			}
			else {
				renderStatsPage(filterSettings, protocolRequest, webUIBean, list);
			}
        }
    }

	/**
	 * Routine to render data source list table on home page.
	 *
	 * @param filterSettings GlobalFilterSettings
	 * @param list ArrayList
	 * @throws QueryException
	 * @throws DaoException
	 * @throws AssemblyException
	 * @throws IOException
	 * @throws ParseException
	 */
	private void renderForHomepage(GlobalFilterSettings filterSettings, ArrayList list)
		throws QueryException, DaoException, AssemblyException, IOException, ParseException {

		ArrayList<String> typeList = new ArrayList();
		typeList.add(GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS);
		filterSettings.setEntityTypeSelected(typeList);
		int rowIconCounter = -1;
		boolean tableCreated = false;
		for (int i = 0; i < list.size(); i++) {
			ExternalDatabaseSnapshotRecord snapshotRecord =
				(ExternalDatabaseSnapshotRecord) list.get(i);
			ExternalDatabaseRecord dbRecord = snapshotRecord.getExternalDatabase();
			//  Show all snapshots, except PROTEIN_UNIFICATION databases.
			if (dbRecord != null) {
				if (!tableCreated) {
					append("<table class='datasource_homepage_table'>");
					tableCreated = true;
					append ("<tr valign=top>");
				}
				if (dbRecord.getIconFileExtension() != null) {
					append("<td class='datasource_logo_table_cell'");
					append("<a href=" + "'" + dbRecord.getHomePageUrl() + "'>" +
						   "<img class='data_source_logo' src='jsp/images/database/" +
						   "db_" + dbRecord.getId() + "." + dbRecord.getIconFileExtension() + "'" +
						   "title='" + DbSnapshotInfo.getDbSnapshotHtml(snapshotRecord.getId(), false) + "'" +
						   "/>" + "</a>");
					append ("</td>");
				}
				if (++rowIconCounter == NUM_ICONS_ROW-1) {
					endRow();
					rowIconCounter = -1;
					append ("<tr valign=top>");
				}
			}
		}
		if (tableCreated) {
			endRow();
			endTable();
		}
	}

	/**
	 * Routine to render data source list table on stats page.
	 *
	 */
	private void renderStatsPage(GlobalFilterSettings filterSettings, ProtocolRequest protocolRequest,
                                 WebUIBean webUIBean, ArrayList list)
		throws QueryException, DaoException, AssemblyException, IOException, ParseException {

		// set proper type list for lucene query
		ArrayList<String> typeList = new ArrayList();
		typeList.add(GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS);
		filterSettings.setEntityTypeSelected(typeList);
		for (int i = 0; i < list.size(); i++) {
			ExternalDatabaseSnapshotRecord snapshotRecord =
				(ExternalDatabaseSnapshotRecord) list.get(i);
			ExternalDatabaseRecord dbRecord = snapshotRecord.getExternalDatabase();
			//  Show all snapshots, except PROTEIN_UNIFICATION databases.
			if (dbRecord != null) {
				append("<table border='0' cellspacing='2' cellpadding='3' "
					   + "width='100%' class='datasource_table'>");
				append ("<tr class='c' valign=top>");
				append("<td class='datasource_logo_table_cell' valign=top");
				if (dbRecord.getIconFileExtension() != null) {
					append("<img class='data_source_logo' src='jsp/images/database/"
						   + "db_" + dbRecord.getId() + "." + dbRecord.getIconFileExtension()
						   + "'/>");
				}
				append ("</td>");
				append ("<td class='datasource_link_table_cell' valign=center>"
						+ DbSnapshotInfo.getDbSnapshotHtml(snapshotRecord.getId()));
				// set proper data source for lucene query
				ArrayList<Long> dataSourceList = new ArrayList();
				dataSourceList.add(Long.valueOf(snapshotRecord.getId()));
				filterSettings.setSnapshotsSelected(dataSourceList);
				protocolRequest.setQuery("q=data_source:" + dbRecord.getMasterTerm());
				// do lucene query
				LuceneQuery search = new LuceneQuery(protocolRequest, filterSettings, new XDebug());
				search.executeSearch();
				// create the html
				append ("<br><a href=\"webservice.do?version=");
				append (webUIBean.getWebApiVersion());
				append ("&format=html&cmd=get_by_keyword");
				append ("&" + ProtocolRequest.ARG_RECORD_TYPE + "=" + (search.getLuceneResults().getNumHits() > 0
                        ? GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS
                        : GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PHYSICAL_ENTITIES));
				append ("&" + GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME
                        + "=" + GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL);
				append ("&q=data_source%3A" + dbRecord.getMasterTerm() + "\">");
				append ("Browse</a>");
				append ("</td>");
				endRow();
				endTable();
			}
		}
	}


    /**
	 * Creates a ProtocolRequest Object.
	 */
	private ProtocolRequest getProtocolRequest(WebUIBean webUIBean) {
		HashMap<String,String> parameters = new HashMap<String,String>();
		parameters.put(ProtocolRequest.ARG_VERSION, webUIBean.getWebApiVersion());
		parameters.put(ProtocolRequest.ARG_FORMAT, ProtocolConstants.FORMAT_HTML);
		parameters.put(ProtocolRequest.ARG_COMMAND, ProtocolConstants.COMMAND_GET_BY_KEYWORD);
		parameters.put(ProtocolRequest.ARG_RECORD_TYPE, GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS);
		return new ProtocolRequest(parameters);
	}
}
