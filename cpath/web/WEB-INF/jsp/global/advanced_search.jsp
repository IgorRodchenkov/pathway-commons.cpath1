<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants"%>
<div id="apphead">
    <h2>Advanced Search</h2>
</div>

<div class="h3">
    <h3>Search Criteria</h3>
</div>
<FORM NAME="search" ACTION="webservice.do" METHOD="GET">
<TABLE>
    <INPUT TYPE="hidden" name="<%= ProtocolRequest.ARG_VERSION %>" value="1.0">
	<tr>
    <th>Step 1:  Specify Search Criteria</th>
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
	<th>

    <div CLASS="show" ID="option1Text">
	    Step 2:  Enter Interactor ID
    </div>
    <div CLASS="hide" ID="option2Text">
        Step 2:   Enter Interactor Name
    </div>
    <div CLASS="hide" ID="option3Text">
        Step 2:  Select Organism
    </div>
    <div CLASS="hide" ID="option4Text">
        Step 2:  Enter Keyword
    </div>
    <div CLASS="hide" ID="option5Text">
        Step 2:  Enter Database Name
    </div>

    <div CLASS="hide" ID="option6Text">
        Step 2:  Enter PubMedID
    </div>
    </th>
    <td>

    <div CLASS="hide" SIZE=25 ID="searchOrganismBox">
		<select ID="selectOrganism" name="q">
			<option VALUE="9606">Homo Sapiens
			<option VALUE="562">Escherichia coli
            <option value="10090">Mus musculus
			<option VALUE="4932">Saccharomyces cerevisiae
		</select>
	</div>

	<div CLASS="show" ID="searchTextBox">
		<input NAME="<%= ProtocolRequest.ARG_QUERY %>" SIZE=25 TYPE="TEXT">
	</div>
	</td>
    </tr>

	<tr>
    <th>
	Step 3:  Limit Number of Results
    </th>
    <td>
	<select name="maxHits">
            <option VALUE="10">10
			<option VALUE="25">25
			<option VALUE="50">50
            <option VALUE="100">100
            <option VALUE="500">500
            <option VALUE="unbounded">unbounded
    </select>
	</td>
    </tr>

	<tr>
    <th>
	Step 4:  Specify Results Format
    </th>
    <td>
	<select name="<%= ProtocolRequest.ARG_FORMAT %>">

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