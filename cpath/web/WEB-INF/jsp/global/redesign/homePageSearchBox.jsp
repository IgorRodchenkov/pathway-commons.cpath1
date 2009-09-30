<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest"%>
<%@ page import="org.mskcc.pathdb.model.GlobalFilterSettings"%>
<%@ page import="org.mskcc.pathdb.form.WebUIBean"%>
<%@ page import="org.mskcc.pathdb.servlet.CPathUIConfig"%>
<%@ page import="org.mskcc.pathdb.protocol.ProtocolConstants"%>
<%@ page import="java.io.IOException" %>
<%@ page import="org.mskcc.pathdb.taglib.SearchTabs" %>
<%@ page import="java.util.Date" %>
<%
WebUIBean webUIBean = CPathUIConfig.getWebUIBean();
GlobalFilterSettings filterSettings = (GlobalFilterSettings)
        session.getAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS);
if (filterSettings == null) {
    filterSettings = new GlobalFilterSettings();
    session.setAttribute(GlobalFilterSettings.GLOBAL_FILTER_SETTINGS, filterSettings);
}
%>

<script>
  var tab_content = ["pathway_tab","protein_tab"];
  var tabs = ["pathway_tab_tab", "protein_tab_tab"];

  //  Highlight the specified search tab
  function showSearchTab(tab){
    // first make sure all the tabs are hidden
    for(i=0; i < tab_content.length; i++){
      var obj = document.getElementById(tab_content[i]);
      obj.style.display = "none";
      var obj_tab = document.getElementById(tabs[i]);
      obj_tab.className = 'search_tab_inactive';
    }

    // show the tab we're interested in
    var obj = document.getElementById(tab);
    obj.style.display = "block";
    var obj_tab = document.getElementById(tab + "_tab");
    obj_tab.className = 'search_tab_active';

    //  Update the hidden record type parameter
    var record_type = document.getElementById("<%= ProtocolRequest.ARG_RECORD_TYPE %>");
    if (tab == "pathway_tab") {
        record_type.value = "<%= GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS %>"
    } else {
        record_type.value = "<%= GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PHYSICAL_ENTITIES %>"
    }

    //  Set Focus to the Text Box
    var textBox = document.getElementById("<%= ProtocolRequest.ARG_QUERY %>")
    textBox.focus();
      
    return true;
  }

  //  Submit search with specified search query parameter
  function submitSearch (q) {
    var qParam = document.getElementById("<%= ProtocolRequest.ARG_QUERY %>");
    qParam.value = q;
    searchForm = document.getElementById("searchbox");
    searchForm.submit();
    return false;
  }
</script>
<form name='searchbox' id='searchbox' action='webservice.do' method='get'>
<fieldset>
<legend>
<span class="search_tabs">
    <a href="#" id="pathway_tab_tab" class="search_tab_active" onclick="showSearchTab('pathway_tab')">Find <%= SearchTabs.PATHWAYS_TAB_TITLE %></a>
    <a href="#" id="protein_tab_tab" class="search_tab_inactive" onclick="showSearchTab('protein_tab')">Find <%= SearchTabs.PHYSICAL_ENTITIES_TAB_TITLE %></a>
</span>
</legend>
<div class="input_search_box">
<%  outputSearchForm(out, webUIBean, GlobalFilterSettings.NARROW_BY_RECORD_TYPES_PATHWAYS); %>
</div>
<div id="pathway_tab" style="display:block;">
<P>For example, if you enter: <a href="#" onClick="submitSearch('BRCA1')">BRCA1</a>, you will <span class="search_highlight">get back the list of pathways</span> containing the
keyword "BRCA1", and a list pathways that contain the BRCA1 gene.
</div>
<div id="protein_tab" style="display:none;">
<P>For example, if you enter: <a href="#" onClick="submitSearch('BRCA1')">BRCA1</a>, you will <span class="search_highlight">get back the list of proteins, genes, or small molecules</span>
that contain the keyword "BRCA1".
</div>    
</p>
</fieldset>
</form>

<%!
    private void outputSearchForm(JspWriter out, WebUIBean webUIBean, String recordType) throws IOException {
        out.println ("<input type='hidden' name='" + ProtocolRequest.ARG_VERSION + "' value='" + webUIBean.getWebApiVersion() +"'>");
        out.println ("<input type='hidden' name='" + GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_NAME
                + "' value='" + GlobalFilterSettings.NARROW_BY_DATA_SOURCES_FILTER_VALUE_GLOBAL + "'>");
        out.println ("<input type='hidden' id='" + ProtocolRequest.ARG_RECORD_TYPE
                + "' name='" + ProtocolRequest.ARG_RECORD_TYPE + "' value='"
                + recordType + "'>");
        out.println ("<input class='text_box' type='text' id='" + ProtocolRequest.ARG_QUERY
                + "' name='" + ProtocolRequest.ARG_QUERY + "' size='30'>");
        out.println ("<input class='button' type='submit' value='Search'/>");
        out.println ("<input type='hidden' name='"+ ProtocolRequest.ARG_FORMAT + "' value='"
                + ProtocolConstants.FORMAT_HTML + "'/>");
        out.println ("<input type='hidden' name='" + ProtocolRequest.ARG_COMMAND
            + "' size='25' value='" + ProtocolConstants.COMMAND_GET_BY_KEYWORD + "'/>");
    }
%>
<p>Current filter settings:  <%= filterSettings.getFilterSummary() %>.&nbsp;<a href='filter.do'>Set filters.</a>
</p>
   