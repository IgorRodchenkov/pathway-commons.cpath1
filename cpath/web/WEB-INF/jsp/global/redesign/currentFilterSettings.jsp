<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoOrganism"%>
<%@ page import="java.util.Map"%>
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
<h3>Current Filter Settings:</h3>
<%
    out.println("<ul>");
    if (snapshotIdSet.size() == dao.getAllDatabaseSnapshots().size()) {
        out.println("<li>All Data Sources</li>");
    }
    else {
        for (Long snapshotId : snapshotIdSet) {
            ExternalDatabaseSnapshotRecord snapshotRecord = dao.getDatabaseSnapshot(snapshotId);
            out.println("<li>" + snapshotRecord.getExternalDatabase().getName() + "</li>");
        }
	}
    for (Integer ncbiTaxonomyId : (Set<Integer>)settings.getOrganismTaxonomyIdSet()) {
        if (ncbiTaxonomyId == GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE) {
            out.println("<li>All Organisms</li>");
        } else {
            DaoOrganism daoOrganism = new DaoOrganism();
            Organism organism = daoOrganism.getOrganismByTaxonomyId(ncbiTaxonomyId);
            out.println("<li>" + organism.getSpeciesName() + "</li>");
        }
    }
    out.println("</ul>");
    out.println("<p>[<a href='filter.do'>Update Filter Settings</a>]</p>");
%>
