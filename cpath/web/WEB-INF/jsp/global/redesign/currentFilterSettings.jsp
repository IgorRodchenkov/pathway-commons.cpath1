<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoOrganism"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Collections"%>
<%@ page import="org.mskcc.pathdb.model.Organism"%>
<%
    GlobalFilterSettings settings = (GlobalFilterSettings)
            session.getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);

    if (settings == null) {
        settings = new GlobalFilterSettings();
        session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS, settings);
    }
    DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
    Set<Long> snapshotIdSet = settings.getSnapshotIdSet();
%>
<h3>Filters</h3>
<h0>Data Source:</h0>
<%
    out.println("<ul>");
    if (snapshotIdSet.size() == dao.getAllDatabaseSnapshots().size()) {
        out.println("<li>All Data Sources</li>");
    }
    else {
		ArrayList<String> dsNames = new ArrayList<String>();
        for (Long snapshotId : snapshotIdSet) {
            ExternalDatabaseSnapshotRecord snapshotRecord = dao.getDatabaseSnapshot(snapshotId);
			dsNames.add(snapshotRecord.getExternalDatabase().getName());
		}
		Collections.sort(dsNames);
        for (String dsName : dsNames) {
            out.println("<li>" + dsName + "</li>");
        }
	}
    out.println("</ul>");
%>
<h0>Organism:</h0>
<%
    out.println("<ul>");
    DaoOrganism daoOrganism = new DaoOrganism();
    ArrayList<String> organismNames = new ArrayList<String>();
    for (Integer ncbiTaxonomyId : (Set<Integer>)settings.getOrganismTaxonomyIdSet()) {
        if (ncbiTaxonomyId == GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE) {
            organismNames.add("All Organisms");
        } else {
			organismNames.add(daoOrganism.getOrganismByTaxonomyId(ncbiTaxonomyId).getSpeciesName());
		}
	}
    Collections.sort(organismNames);
    for(String organismName : organismNames) {
        out.println("<li>" + organismName + "</li>");
    }
    out.println("</ul>");
    out.println("<p>[<a href='filter.do'>Update Filter Settings</a>]</p>");
%>
