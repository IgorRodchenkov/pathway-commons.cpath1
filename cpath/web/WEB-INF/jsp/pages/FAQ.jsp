<%@ page import="org.mskcc.pathdb.protocol.ProtocolException,
                 org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.action.BaseAction,
                 org.mskcc.pathdb.lucene.PsiInteractionToIndex,
                 org.mskcc.pathdb.lucene.LuceneReader,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.lucene.LuceneConfig"%>
<%@ page errorPage = "JspError.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cbio-taglib.tld" prefix="cbio" %>

<%
request.setAttribute("advancedSearch", "true");
%>
<% request.setAttribute(BaseAction.ATTRIBUTE_TITLE,
        "cPath::FAQ"); %>

<jsp:include page="../global/header.jsp" flush="true" />

<div id="apphead">
    <h2>cPath FAQ</h2>
</div>

<P>General Questions:
<div>
    <a href="#what_is_cpath">What is cPath?</a>
</div>

<div>
    <a href="#data">What kind of data is currently available via cPath?</a>
</div>

<div>
    <a href="#plans">What are the near term / long term goals for cPath?</a>
</div>

<div>
    <a href="#contact">Who do I contact for additional information
    regarding cPath?</a>
</div>

<P>Using cPath:

<div>
    <a href="#search_interactions">How do I search for interactions?</a>
</div>

<div>
    <a href="#organism">How do I restrict my search to a specific organism?</a>
</div>

<div>
    <a href="#fields">How do I restrict my search to a specific field, such as
    Pub Med ID or Experiment Type?</a>
</div>

<div>
    <a href="#construct">How do I construct an advanced search query?</a>
</div>

<div>
    <a href="#cytoscape">What is Cytoscape?  How can I visualize cPath data
    in Cytoscape?</a>
</div>

<div>
    <a href="#webservices">How can I programmatically access cPath?  How do I use
    the Web Services API?</a>
</div>

<div class="h3">
    <h3>General Questions</h3>
</div>

<div class="h4" id="what_is_cpath">
    <h4>What is cPath?</h4>
</div>

<P>
cPath is a web-based database of protein-protein interactions.  Using cPath,
a researcher can search for specific protein-protein interaction records,
inspect matching records, and export them to a third-party database or
visualization application.
</P>
<P>
Currently, cPath only provides support for protein-protein interaction
records, but we plan to add support for metabolic pathways and signal
transduction pathways in the near future.  The long term goal of cPath is
to provide a public repository of cancer specific pathways, and make it
freely available to the scientific community.
<P/>
cPath is currently being developed by the Sander group at the
<A HREF="http://cbio.mskcc.org">Computational Biology Center</A>
of <A HREF="http://www.mskcc.org">Memorial Sloan-Kettering Cancer Center</A>.
<P/>

<div class="h4" id="data">
    <h4>What kind of data is currently available via cPath?</h4>
</div>

<P>
cPath aggregates protein-protein interaction records
from a number of public repositories, e.g.
<A HREF="http://www.blueprint.org/bind/bind.php">BIND</A>,
<A HREF="http://mint.bio.uniroma2.it/mint/index.php">MINT</A>
and <A HREF="http://www.ebi.ac.uk/intact/index.html">Intact</A>.
For a complete list of the imported data sets, view the
<A HREF="dbStats.do">Database Stats</A> page.

<div class="h4" id="plans">
    <h4>What are the near term / long term goals for cPath?</h4>
</div>

<P>
In the near-term, we plan to add support for metabolic pathways, and signal
transduction pathways.  We are also exploring options for manual curation
of cancer specific pathways.

<div class="h4" id="contact">
    <h4>Who do I contact for additional information
    regarding cPath?</h4>
    For scientific questions regarding cPath, please contact
    <A HREF="http://www.cbio.mskcc.org/people/info/gary_bader.html">Gary Bader</A>.
    For technical / programming questions regarding cPath, please contact
    <A HREF="http://www.cbio.mskcc.org/people/info/ethan_cerami.html">Ethan Cerami</A>.
</div>

<div class="h3">
    <h3>Using cPath</h3>
</div>

<div class="h4" id="search_interactions">
    <h4>How do I search for interactions?</h4>
</div>
    In the main cPath search box (always available in the left column), you
    can specify basic or advanced search queries.  Based on your search
    criteria, you will receive back a list of matching interactions and
    protein interactors:
    <UL>
    <LI>The body of the search results page will contain
    the top matching interactions, sorted by relevance.
    <LI>The left column of the search results page will contain the
    top matching protein interactors.
    </UL>
    <P>
    You can use the next/previous links to scroll through all matching
    interactions, or click on one of the matching proteins.
    <P>If you click on a protein, you will see detailed information
    regarding the protein, including links to external databases, and a
    complete list of all known interactions for the specific protein.

<div class="h4" id="organism">
    <h4>How do I restrict my search to a specific organism?</h4>
</div>
    The main cPath search box contains a "Filter by Organism" option.
    You can choose to search "All Organisms", or pick one of the organisms
    from the pull-down menu.

<div class="h4" id="fields">
    <h4>How do I restrict my search to a specific field, such as
    Pub Med ID or Experiment Type?</h4>
</div>
    <p>It is possible to filter your search results by specific fields.
    For example, you can retrieve all interaction records discovered
    via a specific experimental technique or retrieve all interaction
    records associated with a specific Pub Med ID.
    <p>To filter by a specific field, you have two options:
    <UL>
    <LI>From the main search box, click the link, entitled
    "Show Field Specific Filter...".  Then, select a field filter,
    enter your search query, and click the Search button.
    <P>For example, to find all
    interaction records associated with Pub Med ID:  11821039, click
    "Show Field Specific Filter...", select "Pub Med ID" from the pulldown
    menu, enter the text: "11821039", and click the Search button.
    <LI>You can also perform field specific queries via an advanced search query.
    See FAQ below, "How do I construct an advanced search query?".
    </UL>

<div class="h4" id="construct">
    <h4>How do I construct an advanced search query?</h4>
</div>
    <p>Here are a few advanced search queries to get you started:

<%
    String searchTerm = null;
    ProtocolRequest pRequest = new ProtocolRequest();
    pRequest.setCommand(ProtocolConstants.COMMAND_GET_BY_KEYWORD);
    pRequest.setFormat(ProtocolConstants.FORMAT_HTML);
%>
    <P>
    <TABLE WIDTH=100% CELLPADDING=2>
    <TR>
        <TH>Search Terms</TH>
        <TH>Description</TH>
    </TR>
    <TR>
        <%
        searchTerm = "dna AND repair";
        pRequest.setQuery(searchTerm);
        %>
       <TD><A HREF="<%= pRequest.getUri() %>">
        <%= searchTerm %></A></TD>
        <TD>Uses a logical AND operator.</TD>
    </TR>

    <TR>
        <%
        searchTerm = "\"dna repair\"";
        pRequest.setQuery(searchTerm);
        %>
       <TD><A HREF="<%= pRequest.getUri() %>">
        <%= searchTerm %></A></TD>
        <TD>Finds an exact match for the specified string.</TD>
    </TR>

    <TR>
        <%
        searchTerm = "helica*";
        pRequest.setQuery(searchTerm);
        %>
       <TD><A HREF="<%= pRequest.getUri() %>">
        <%= searchTerm %></A></TD>
        <TD>Uses a wild card option to find all terms beginning with the
        word "helica".</TD>
    </TR>

    <TR>
        <%
        searchTerm = "experiment_type:\"Two hybrid\"";
        pRequest.setQuery(searchTerm);
        %>
       <TD><A HREF="<%= pRequest.getUri() %>">
        <%= searchTerm %></A></TD>
        <TD>Finds all interaction records discovered via "Two hybrid".</TD>
    </TR>
    </TABLE>

    <p><strong>Terms</strong>

    <p>A query is broken up into terms and operators. There are two types of
    terms: Single Terms and Phrases. A Single Term is a single word such as
    "dna" or "repair". A Phrase is a group of words surrounded by double
    quotes such as "dna repair".  Multiple terms can be combined together with
    Boolean operators to form a more complex query (see below).

    <p><strong>Boolean operators</strong>
    <p>Boolean operators allow terms to be combined logically:
    AND, OR, and NOT. Boolean operators must be ALL CAPS. If two
    terms are entered with no Boolean operator, the OR operator is used by
    default.
    <UL>
    <LI>The OR operator is used between two terms to search for text that
    contains either of the terms. This is equivalent to a union using sets.
    The symbol || can be used in place of the word OR.

    <LI>The AND operator is used to find text that contains both terms
    anywhere in the text. This is equivalent to an intersection using sets.
    The symbol && can be used in place of the word AND.

    <LI>The "+", or required, operator requires that the term after the
    "+" symbol exist somewhere in the text. To search for results that must
    contain "dna" and may contain "repair" use the query: +dna repair.

    <LI>The NOT operator excludes results that contain the term after NOT.
    This is equivalent to a difference using sets. The symbol ! can be used
    in place of the word NOT. To search for text that contain "dna repair"
    but not "helix-destabilizing" use the query:
    "dna repair" NOT "helix-destabilizing". Note: The NOT operator cannot be
    used with just one term. For example, the following search will return
    no results: NOT "dna repair".

    <LI>The "-", or prohibit, operator excludes results that contain the
    term after the "-" symbol. To search for text that contain
    "dna repair" but not "helix-destabilizing" use the query:
    "dna repair" -"helix-destabilizing".
    </UL>

    <p><strong>Grouping</strong>

    <p>Grouping is supported using parentheses to group clauses to form sub
    queries. This can be very useful if you want to control the boolean logic
    for a query. To search for either "dna" or "rna" and "repair" use
    the query: (dna OR rna) AND repair. This eliminates any confusion
    and makes sure you that repair must exist and either term dna or
    rna may exist.

    <p><strong>Wild Card Options</strong>
    <UL>
    <LI>To perform a single character wildcard search use the "?" symbol.
    <p>The single character wildcard search looks for terms that match that
    with the single character replaced. For example, to search for
    "text" or "test" you can use the search: te?t.

    <LI>To perform a multiple character wildcard search use the "*" symbol.
    <p>Multiple character wildcard searches looks for 0 or more characters.
    For example, to search for test, tests or tester, you can use the search:
    test*.  You can also use the wildcard searches in the middle of a term,
    e.g. te*t.
    </UL>

    <p>Note: You cannot use a * or ? symbol as the first character of a search.

    <p><strong>Field Specific Options</strong>
    <p>It is possible to restrict your search terms within a query to the
    following fields:
    <UL>
    <LI><%= PsiInteractionToIndex.FIELD_INTERACTOR %>:
    interactor name or external reference.
    <LI><%= PsiInteractionToIndex.FIELD_ORGANISM %>:
    organism name or NCBI taxonomy identifier.
    <LI><%= PsiInteractionToIndex.FIELD_PMID %>:
    Pub Med Identifier
    <LI><%= PsiInteractionToIndex.FIELD_EXPERIMENT_TYPE %>:
    Experiment type, e.g. experiment_type:"Two hybrid"
    <LI><%= PsiInteractionToIndex.FIELD_DATABASE %>:  database source
    <LI><%= LuceneConfig.FIELD_INTERACTOR_ID%>:  interactor cPath ID.
    <LI><%= LuceneConfig.FIELD_INTERACTION_ID%>:  interaction cPath ID.
    </UL>

    If no field is specified, the default is to search all information
    related to a collection.  You can search any field by typing the field
    name followed by a colon ":" and then the term you are looking for.

    <p>For example:
    <%
        searchTerm = "organism:\"Homo Sapiens\"";
        pRequest.setQuery(searchTerm);
    %>
    <UL>
    <LI><A HREF="<%= pRequest.getUri() %>">
    <%= searchTerm %></A> will retrieve all interaction records
    for Homo sapiens.
    <%
        searchTerm = "experiment_type:\"Two hybrid\"";
        pRequest.setQuery(searchTerm);
    %>

    <LI><A HREF="<%= pRequest.getUri() %>">
    <%= searchTerm %></A> will retreive all interaction records
    discovered via "Two hybrid".
    </UL>
    <%
        searchTerm = "organism:Homo sapiens";
        pRequest.setQuery(searchTerm);
    %>
    <p>Note: The field is only valid for the term that it directly precedes,
    so the query: <A HREF="<%= pRequest.getUri() %>"><%= searchTerm %></A>
    will only find "Homo" in the organism field;  it will find "sapiens" in
    the default field.   To search the organism field for Homo Sapiens enter:
    <%
        searchTerm = "organism:\"Homo sapiens\"";
        pRequest.setQuery(searchTerm);
    %>
    <A HREF="<%= pRequest.getUri() %>"><%= searchTerm %></A>
    <p><B>Attribution</B>:  The advanced search instructions given above are
    based on the Lucene documentation at:
    <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">
    http://jakarta.apache.org/lucene/docs/queryparsersyntax.html</a>

<div class="h4" id="cytoscape">
    <h4>What is Cytoscape?  How can I visualize cPath data
    in Cytoscape?</h4>
</div>
    Cytoscape is a bioinformatics software platform for visualizing molecular
    interaction networks and integrating these interactions with gene expression
    profiles and other state data.  Cytoscape is open source software,
    and available for download from the <A HREF="http://www.cytoscape.org">
    cytoscape.org</A> web site.  Cytoscape is written in Java, and therefore
    runs on Windows, Mac OS X, and Linux.
    <P>
    Cytoscape includes a built-in PlugIn framework for adding new features and
    functionality.  The <A HREF="cytoscape.do">cPath PlugIn</A>
    enables Cytoscape users to directly query, retrieve and visualize
    interactions retrieved from the cPath database.
<div class="h4" id="webservices">
    <h4>How can I programmatically access cPath?  How do I use
    the Web Services API?</h4>
</div>
<div>
cPath provides a complete Web Services API for programmatically accessing
cPath data.  Complete details are available at the <A HREF="webservice.do?cmd=help">
Web Services API Help Page</A>.
</div>

<jsp:include page="../global/footer.jsp" flush="true" />