<%@ page import="org.mskcc.pathdb.protocol.ProtocolRequest,
                 org.mskcc.pathdb.protocol.ProtocolConstants,
                 org.mskcc.pathdb.lucene.PsiInteractionToIndex,
                 org.mskcc.pathdb.lucene.LuceneIndexer"%>
<div id="apphead">
    <h2>Search FAQ</h2>
</div>

<div>
    <a href="#construct">How do I Construct a search query?</a>
</div>
<div>
    <a href="#fields">What Fields can I search?</a>
</div>

<div class="h3" id="construct">
    <h3>How do I construct a search query?</h3>
</div>

    <p><strong>Terms</strong>

    <p>A query is broken up into terms and operators. There are two types of
    terms: Single Terms and Phrases. A Single Term is a single word such as
    "dna" or "repair". A Phrase is a group of words surrounded by double
    quotes such as "dna repair".  Multiple terms can be combined together with
    Boolean operators to form a more complex query (see below).

    <p><strong>Boolean operators</strong>
    <p>Boolean operators allow terms to be combined through logic operators:
    AND, "+", OR, NOT and "-". Boolean operators must be ALL CAPS. If two
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
    the query: (dna OR RNA) AND repair. This eliminates any confusion
    and makes sure you that repair must exist and either term dna or
    rna may exist.

   <p><strong>Wildcard Searches</strong>
    <p>To perform a single character wildcard search use the "?" symbol.
    <p>To perform a multiple character wildcard search use the "*" symbol.
    <p>The single character wildcard search looks for terms that match that
    with the single character replaced. For example, to search for
    "text" or "test" you can use the search:
    <p>te?t

    <p>Multiple character wildcard searches looks for 0 or more characters.
    For example, to search for test, tests or tester, you can use the search:
    <p>test*
    <p>You can also use the wildcard searches in the middle of a term.
    <p>te*t
    <p>Note: You cannot use a * or ? symbol as the first character of a search.
    <p>The instructions given above are based on the lucene documentation at:
    <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">
    http://jakarta.apache.org/lucene/docs/queryparsersyntax.html</a>

<div class="h3" id="fields">
    <h3>What Fields can I Search?</h3>
</div>

    <p><strong id="fields">Fields</strong>
    <p>It is possible to restrict your search to the following fields:
    <UL>
    <LI><%= PsiInteractionToIndex.FIELD_INTERACTOR %>:
    interactor name or external reference.
    <LI><%= PsiInteractionToIndex.FIELD_ORGANISM %>:
    organism name or NCBI taxonomy identifier.
    <LI><%= PsiInteractionToIndex.FIELD_PMID %>:
    Pub Med Identifier
    <LI><%= PsiInteractionToIndex.FIELD_INTERACTION_TYPE %>:
    Interaction type, e.g. interaction_type:"Two hybrid"
    <LI><%= PsiInteractionToIndex.FIELD_DATABASE %>:  database source
    <LI><%= LuceneIndexer.FIELD_INTERACTOR_ID%>:  interactor cPath ID.
    <LI><%= LuceneIndexer.FIELD_INTERACTION_ID%>:  interaction cPath ID.
    </UL>

    If no field is specified, the default is to search all information
    related to a collection.  You can search any field by typing the field
    name followed by a colon ":" and then the term you are looking for.

    <p>For example:
    <p>organism:"Homo sapiens" will retrieve the all interaction records
    for Homo sapiens.
    <p>interaction_type:"Two hybrid" will retreive all interaction records
    discovered via "Two hybrid".

    <p>Note: The field is only valid for the term that it directly precedes,
    so the query
    <p>organism:Homo sapiens

    <p>Will only find "Homo" in the organism field. It will find "sapiens" in
    the default field.

    <p>To search the organism field for Homo Sapiens enter:
    <p>organism:"Homo sapiens"