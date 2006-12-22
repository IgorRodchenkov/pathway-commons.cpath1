<%@ page import="org.mskcc.pathdb.action.BaseAction"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord"%>
<%@ page import="org.mskcc.pathdb.taglib.DbSnapshotInfo"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.sql.dao.DaoOrganism"%>
<%@ page import="java.util.List"%>
<%@ page import="org.mskcc.pathdb.model.Organism"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE, "Filter"); %>

<jsp:include page="../global/redesign/header.jsp" flush="true" />
<div>
<h1>Restrict my search results to the following data sources:</h1>

<form action="storeFilters.do">
    <table cellpadding="0" cellspacing="5">
    <%
    String referer = request.getHeader("Referer");
    if (referer != null && ! (referer.indexOf("filter.do") > 0)) {
        session.setAttribute("Referer", referer);
    }
    GlobalFilterSettings settings = (GlobalFilterSettings)
            session.getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
    if (settings == null) {
        settings = new GlobalFilterSettings();
        session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS, settings);
    }
    DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
    ArrayList list = dao.getAllDatabaseSnapshots();

    // process records
    if (list.size() == 0) {
        out.println("<tr>");
        out.println("<td>No Data Sources Available</td>");
        out.println("</td>");
    } else {
        for (int i = 0; i < list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            out.println("<tr><td>");
            out.println("<input type=\"checkbox\" name=\"SNAPSHOT_ID\" value=\""
                + snapshotRecord.getId() + "\"");
            if (settings.isSnapshotSelected(snapshotRecord.getId())) {
                out.print(" checked=\"checked\"");
            }
            out.println("/>");
            out.println("&nbsp;&nbsp;" +
                    DbSnapshotInfo.getDbSnapshotHtml(snapshotRecord.getId()));
            out.println("</td></tr>");
        }
    }
    %>
    </table>

<h1>Restrict my search results to the following organisms:</h1>
    <table cellpadding="0" cellspacing="5">
    <%
        DaoOrganism daoOrganism = new DaoOrganism();
        List organismList = daoOrganism.getAllOrganisms();
		ArrayList<StringBuffer> organismRadioButtonList = new ArrayList<StringBuffer>();
		boolean organismSelected = false;
        for (int i=0; i<organismList.size(); i++) {
			StringBuffer organismRadioButton = new StringBuffer();
            Organism organism = (Organism) organismList.get(i);
            organismRadioButton.append("<tr><td>\n");
            organismRadioButton.append("<input type=\"radio\" name=\"ORGANISM_TAXONOMY_ID\" value=\""
                + organism.getTaxonomyId() + "\"");
            if (settings.isOrganismSelected(organism.getTaxonomyId())) {
                organismRadioButton.append(" checked=\"checked\"");
				organismSelected = true;
            }
            organismRadioButton.append("/>\n");
            organismRadioButton.append("&nbsp;&nbsp;" + organism.getSpeciesName() + "\n");
            organismRadioButton.append("</td></tr>\n");
            organismRadioButtonList.add(organismRadioButton);
        }
		// create the "all organism" radio button
		String checked = (organismSelected) ? "" : " checked=\"checked\"";
		StringBuffer allOrganismsRadioButton = new StringBuffer();
		allOrganismsRadioButton.append("<tr><td>\n<input type=\"radio\" name=\"ORGANISM_TAXONOMY_ID\" value=\"" +
		    String.valueOf(GlobalFilterSettings.ALL_ORGANISMS_FILTER_VALUE) + "\"" +
		    checked + "/>\n&nbsp;&nbsp;All organisms\n</td></tr>\n");
		// prepend the "all organism" radio button to the organismRadioButtonList
        organismRadioButtonList.add(0, allOrganismsRadioButton);
		// lets output the entire organismRadioButtonList
		for (StringBuffer strBuffer : organismRadioButtonList) {
            out.println(strBuffer.toString());
        }
    %>
    </table>

<table cellpadding="0" cellspacing="5">
    <tr>
        <td>
            <input type="submit" value="Set Global Filters"/>
        </td>
    </tr>
</table>

</form>
</div>
<jsp:include page="../global/redesign/footer.jsp" flush="true" />
