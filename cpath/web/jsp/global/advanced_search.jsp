<%@ page import="org.mskcc.pathdb.controller.ProtocolRequest,
                 org.mskcc.pathdb.controller.ProtocolConstants"%>
<%
    String format = request.getParameter(ProtocolRequest.ARG_FORMAT);
%>
<FORM NAME="search" ACTION="webservice" METHOD="GET">
<TABLE WIDTH="100%" CELLPADDING=5 CELLSPACING=5 BORDER=0>
    <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0">
    <tr>
    <td colspan=2><B><FONT SIZE=+3>Advanced Search</FONT></B>
    <P>The Advanced Search page enables you to retrieve cPath
    interactions by multiple criteria.
    </td>
    </tr>

    <tr>

	<tr>
    <td>
	<FONT SIZE=+1>Step 1:  Specify Search Criteria</FONT>
    </td>
    <td>
	<select ID="searchCriteria" name="<%= ProtocolRequest.ARG_COMMAND %>"
        onChange="updateAdvancedSearchBox()">
			<option VALUE="get_by_interactor_id" SELECTED >Search by Interactor ID
			<option VALUE="get_by_interactor_name">Search by Interactor Name
			<option VALUE="get_by_interactor_tax_id">Search by Organism
			<option VALUE="get_by_interactor_keyword">Search by Interactor Keyword
			<option VALUE="get_by_interaction_db">Search by Interaction Database
			<option VALUE="get_by_interaction_pmid">Search by Interaction Pub Med ID
		</select>
	</td>
    </tr>
    <tr>
	<td ALIGN=LEFT>

    <SPAN CLASS="show" ID="option1Text">
	    <FONT SIZE=+1>Step 2:  Enter Interactor ID</FONT>
    </SPAN>
    <SPAN CLASS="hide" ID="option2Text">
        <FONT SIZE=+1>Step 2:   Enter Interactor Name</FONT>
    </SPAN>
    <SPAN CLASS="hide" ID="option3Text">
        <FONT SIZE=+1>Step 2:  Select Organism</FONT>
    </SPAN>
    <SPAN CLASS="hide" ID="option4Text">
        <FONT SIZE=+1>Step 2:  Enter Keyword</FONT>
    </SPAN>
    <SPAN CLASS="hide" ID="option5Text">
        <FONT SIZE=+1>Step 2:  Enter Database Name</FONT>
    </SPAN>
    <SPAN CLASS="hide" ID="option6Text">
        <FONT SIZE=+1>Step 2:  Enter PubMedID</FONT>
    </SPAN>
    </td>
    <td>

    <SPAN CLASS="hide" SIZE=25 ID="searchOrganismBox">
		<select ID="selectOrganism" name="q">
			<option VALUE="9609">Homo Sapiens
			<option VALUE="562">Escherichia coli
            <option value="10090">Mus musculus
			<option VALUE="4932">Saccharomyces cerevisiae
		</select>
	</SPAN>

	<SPAN CLASS="show" ID="searchTextBox">
		<input NAME="q" SIZE=25 TYPE="TEXT">
	</SPAN>
	</td>
    </tr>

	<tr>
    <td>
	<FONT SIZE=+1>Step 1:  Specify Results Format</FONT>
    </td>
    <td>
	<select name="format">

			<option VALUE="html">HTML
			<option VALUE="psi">PSI-MI XML Format
    </select>
	</td>
    </tr>

    <tr>
    <td></td>
	<td>
	<input TYPE="SUBMIT" VALUE="Retrieve Results">
	</td>
	</tr>
</form>
</table>