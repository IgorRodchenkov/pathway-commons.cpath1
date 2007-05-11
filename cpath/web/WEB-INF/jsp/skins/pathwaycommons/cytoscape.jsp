<h1>View Pathways and Network Neighborhood Maps within Cytoscape</h1>
<h3>Introduction</h3>
<p>
The Pathway Commons plugin enables users to visualize pathways and
network neighborhood maps in <a href="http://www.cytoscape.org">Cytoscape</a>,
an open source bioinformatics software platform for visualizing molecular interaction networks.
In this context, pathways include biochemical reactions, complex assembly, transport
and catalysis events, and physical interactions involving proteins, DNA, RNA, small
molecules and complexes.
</p>
<!--
In addition network neighborhood maps are networks which include those proteins and
interactions that involve the protein or small molecule referred to on the protein
or search result page in which the network neighborhood map link resides.
-->
<p>
To do this, simply click on any of the following link(s) found throughout Pathway Commons (requires <a href="http://java.sun.com/docs/books/tutorial/information/javawebstart.html">Java Web Start</a>):
</p>
<ul>
<li>'View this pathway in Cytoscape'<font size="-1"> [<a href="jsp/images/plugin/pc_pathway.png">example</a>]</font></li>
<!--<li>'View network neighborhood map in Cytoscape'</li>-->
</ul>

<h3>1. Using the Pathway Commons Plugin</h3>
<ul>
	<li>
	    Load a pathway by clicking on the "View this pathway in Cytoscape" link found on
        both the Search Result page and Pathway page.  Below is a sample screenshot of the
        Pathway page which includes the "View this pathway in Cytoscape" link on
        the left-hand panel:
        <p>&nbsp;</p>
        <p>
	    <a href="jsp/images/plugin/pc_pathway.png"><img alt="Sample Screenshot of the Pathway page" src="jsp/images/plugin/pc_pathway_thumb.png" border="0"></a>
	    <br>
	    <font size="-1">[<a href="jsp/images/plugin/pc_pathway.png">Click to enlarge</a>]</font>
	    </p>
	</li>
    <!--  Commented out until we have network neighborhoods
        <li>
	    Load a network neighborhood map by clicking on the "View network neighborhood map in Cytoscape" link found on both the Search Result page and Physical Entity page.
	    Below is a sample screenshot of the Search Results page which includes the "View network neighborhood map in Cytoscape" link under each search result:
	    <p>
	    <a href="../../jsp/images/plugin/pc_search_results.png"><img alt="Sample Screenshot of the Search Results page" src="../../jsp/images/plugin/pc_search_results_thumb.png" border="0"></a>
	    <br>
	    <font size="-1">[<a href="../../jsp/images/plugin/pc_search_results.png">Click to enlarge</a>]
	    </p>
	    </li>
		</li>
    -->
    <li>
    The first time you click on a Cytoscape link, a
    <a href="http://java.sun.com/products/javawebstart/">Java Web Start</a> version of Cytoscape will
    be launched.  Loading the Web Start version of Cytoscape will take a few moments.  Once launched,
    Cytoscape will automatically download and visualize your chosen pathway.
    </li>

    <li>Once Cytoscape is running, you can load additional data from Pathway Commons
        by clicking on the Cytoscape links in your web browser.
        If one or more networks are already loaded in Cytoscape, you will be given the
        option to merge your new network with an existing network or create a new network.
	    Below is a sample screenshot of the merge dialog window displayed within Cytoscape:
        <p>&nbsp;</p>
        <p>
	    <a href="jsp/images/plugin/pc_plugin_merge.png"><img alt="Sample Screenshot of the Pathway Commons Plugin Merge Dialog Window" src="jsp/images/plugin/pc_plugin_merge_thumb.png" border="0"></a>
	    <br>
	    <font size="-1">[<a href="jsp/images/plugin/pc_plugin_merge.png">Click to enlarge</a>]</font>
	    </p>
	</li>
</ul>

<h3>2. Understanding and Using Cytoscape</h3>

<p>
For further details on using Cytoscape, please refer to the <a href="http://cytoscape.org/">Cytoscape</a>
web site.  The Cytoscape user manual is available in HTML and PDF formats.
</p>

<h3>3. Understanding Visual Rendering Clues</h3>
By default, nodes and edges are rendered as follows:
<p>
</p>
<table cellpadding="3" cellspacing="3" width=100%>
	<tbody><tr bgcolor="#dddddd">
		<td width=30%>
			Entity
		</td>
		<td>
			Visual Shape
		</td>
	</tr>
	<tr>
		<td>
			Proteins, small molecules, complexes and other physical entities
            (as defined in the <a href="http://biopax.org">BioPAX pathway format</a>).
		</td>
		<td>
			<img alt="Ellipse Shape" src="jsp/images/plugin/ellipse.jpg">
		</td>
	</tr>
	<tr>
		<td>
			Conversions, biochemical reactions, modulations and other interaction entities
            (as defined in the <a href="http://biopax.org">BioPAX pathway format</a>).
		</td>
		<td>
			<img alt="Square Shape" src="jsp/images/plugin/square.jpg">
		</td>
	</tr>
	<tr bgcolor="#dddddd">
		<td>
			Interaction Type
		</td>
		<td>
			Arrow Shape
		</td>
	</tr>
	<tr>
		<td>
			Inhibition
		</td>
		<td>
			<img alt="Black inhibition arrow type" src="jsp/images/plugin/BLACK_T.jpg">
		</td>
	</tr>
	<tr>
		<td>
			Containment, e.g. a complex can contain one or more proteins.
		</td>
		<td>
			<img alt="Black circle arrow type" src="jsp/images/plugin/BLACK_CIRCLE.jpg">
		</td>
	</tr>
	<tr>
		<td>
			All other interaction types, e.g. activation, left, right, etc.
		</td>
		<td>
			<img alt="Black arrow type" src="jsp/images/plugin/BLACK_DELTA.jpg">
		</td>
	</tr>
	<tr bgcolor="#DDDDDD">
		<td VALIGN=TOP COLSPAN="2">
			 Labels for Physical Entities
		</td>
	</tr>
	<tr>
		<td VALIGN=TOP colspan=2>
			All physical entities will be labeled as follows:
            <p>[NAME] [-CHEMICAL_MODIFICATION_ABBR] [(CELLULAR_LOCATION_ABBR)]</p>
            <p>where [NAME] is determined by the following order of precedence: NAME, SHORT NAME,
            or Shortest Synonym, and [CHEMICAL_MODIFICATION_ABBR] and [CELLULAR_LOCATION_ABBR]
            are one of the abbreviations described below.</p>
        </td>
	</tr>
	<tr bgcolor="#DDDDDD">
		<td VALIGN=TOP COLSPAN="2">
			 Examples:
		</td>
	</tr>
	<tr>
		<td VALIGN=TOP>MITF:</td>
		<td VALIGN=TOP>MITF</td>
	</tr>
	<tr>
		<td VALIGN=TOP>MITF Phosphorylated:</td>
		<td VALIGN=TOP>MITF-P</td>
	</tr>
	<tr>
		<td VALIGN=TOP>MITF Phosphorylated, in the cytoplasm:</td>
		<td VALIGN=TOP>MITF-P (CP)</td>
	</tr>
	<tr bgcolor="#DDDDDD">
		<td VALIGN=TOP COLSPAN="2">Abbreviations for Chemical Modifications</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>acetylation site</td>
		<td VALIGN=TOP>A</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>glycosylation site</td>
		<td VALIGN=TOP>G</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>phosphorylation site</td>
		<td VALIGN=TOP>
			 P
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     proteolytic cleavage site
		</td>
		<td VALIGN=TOP>
			 PCS
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     sumoylation site
		</td>
		<td VALIGN=TOP>
			 S
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     ubiquitination site
		</td>
		<td VALIGN=TOP>
			 U
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     [All others]
		</td>
		<td VALIGN=TOP>
			 No Abbreviation will be provided.
		</td>
	</tr>
	<tr bgcolor="#DDDDDD">
		<td VALIGN=TOP COLSPAN="2">
			 Abbreviations for Cellular Locations
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     cellular component unknown
		</td>
		<td VALIGN=TOP>
			 No Abbreviation will be provided.
	</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     centrosome
		</td>
		<td VALIGN=TOP>
			 CE
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     cytoplasm
		</td>
		<td VALIGN=TOP>
			 CY
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     endoplasmic reticulum
		</td>
		<td VALIGN=TOP>
			 ER
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     endosome
		</td>
		<td VALIGN=TOP>
			 EN
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     extracellular
		</td>
		<td VALIGN=TOP>
			 EM
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     golgi apparatus
		</td>
		<td VALIGN=TOP>
			 GA
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     mitochondrion
		</td>
		<td VALIGN=TOP>
			 MI
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     nucleus
		</td>
		<td VALIGN=TOP>
			 NU
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     plasma membrane
		</td>
		<td VALIGN=TOP>
			 PM
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     ribosome
		</td>
		<td VALIGN=TOP>
			 RI
		</td>
	</tr>
	<tr>
	    <td VALIGN=TOP>
		     [All others]
		</td>
		<td VALIGN=TOP>
			 No Abbreviation will be provided.
		</td>
	</tr>
</tbody></table>
<h3>4. Bug Reporting</h3>
If you encounter a bug with this plugin, please use our
<a href="get_feedback.do">feedback form</a>,
or log a bug directly to the
<a href="http://www.cbio.mskcc.org/cytoscape/bugs/">Cytoscape Bug Tracker</a>.

<h3>5. Contacts</h3>
<a href="http://cbio.mskcc.org/people/info/ethan_cerami.html">Ethan Cerami</a><br/>
<a href="http://cbio.mskcc.org/people/info/benjamin_gross.html">Benjamin Gross</a><br/>
Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>,
<a href="http://www.mskcc.org/">Memorial Sloan-Kettering Cancer Center</a>, New York City
<p>
	For any questions or feedback concerning this plugin, please use our
    <a href="get_feedback.do">feedback form</a>.
</p>
<h3>6. Release Notes / Current Limitations</h3>
<p>
	Version: 0.1
</p>
<ul>
	<li>
		Date: May 15, 2007
	</li>
	<li>
		Features:
		<ul>
			<li>
				Enables the automatic download and visualization of pathways
                directly from PathwayCommons.org.
            </li>
		</ul>
	</li>
</ul>
<h1>License / Credits</h1>
This software is made available under the LGPL (Lesser General Public License).
</p>
<p>This product includes software developed by the
<a href="http://www.apache.org">Apache Software Foundation</a>
</p>
