<h1>Visualizing Pathways and Networks in Cytoscape</h1>
<p>
You can visualize pathways and network neighborhoods that you find in Pathway
Commons using the open source <a href="http://www.cytoscape.org">Cytoscape</a> software.
In this context, pathways include biochemical reactions, complex assembly,
transport and catalysis events, and physical interactions involving proteins, DNA, RNA,
small molecules and complexes.
</p>
<p>
Step-by-step instructions are provided below.
</p>
<h2>Getting Started</h2>
<table>
<tr>

<td valign=top>
<img alt="Sample Screenshot Cytoscape Link" src="jsp/images/plugin/pc_pathway_thumb.png" border="0" align="right">
To load a pathway or network neighborhood, click on any of the "View in Cytoscape"
links found throughout Pathway Commons.
<p>&nbsp;</p>
These Cytoscape links are found on search results pages, pathway pages and physical entity pages.
<p>&nbsp;</p>
A sample screenshot of the mTOR Pathway page with a Cytoscape link is shown at right.
</td>

<tr>
<td valign=top>
<p>&nbsp;</p>    
<img alt="Sample Screenshot of mTOR Pathway, as visualized within Cytoscape"
src="jsp/images/plugin/mtor_cyto.png" border="0" align="right">

The first time you click on a Cytoscape link, a
<a href="http://java.sun.com/products/javawebstart/">Java Web Start</a> version
of Cytoscape will be launched, which takes a few moments.
<p>&nbsp;</p>
Once launched, your chosen pathway or network neighborhood will be displayed within Cytoscape.
<p>&nbsp;</p>
<p>A screenshot of the mTOR pathway, as visualized within Cytoscape, is shown at right.</p>
</td>
<td valign=top>
</td>
</tr>
</table>

<h2>Loading additional data from Pathway Commons</h2>
Once Cytoscape is running, you have two options for loading additional data from Pathway Commons:

<ul>
    <li>Click on additional "View in Cytoscape" links in the Pathway Commons web site; or</li>
    <li>Search Pathway Commons directly from within Cytoscape.  See the next section for details.
    </li>
</ul>

<h2>Accessing Pathway Commons Data from within Cytoscape</h2>

To search Pathway Commons directly from within Cytoscape:

<ul>
    <li>Select: File &rarr; Import &rarr; Network from web services...
    <li>From the pull-down menu, select the Pathway Commons Web Service Client.</li>
</ul>
<p><img src="jsp/images/plugin/file_import.png" alt="Importing networks via the Cytoscape web services framework"></p>
<p>Then, follow the three steps outlined below:</p>

<p><img src="jsp/images/plugin/3_steps.png" alt="Three-step process for searching Pathway Commons"></p>
<ul>
<li>Step 1: Enter your search term and organism filter; for example: BRCA1 [All Organisms].</li>
<li>Step 2: Select the protein or small molecule of interest. Full details regarding each molecule is shown in the bottom left panel.</li>
<li>Step 3: Download a specific pathway or interaction network.</li>
</ul>
In Step 3, you can simply double-click on a pathway of interest, or click on the "Interaction Networks" tab.
This tab enables you to filter interactions by data source and/or interaction type.
For example, you can choose to restrict your network to direct physical interactions from HPRD and MINT only:

<p><img src="jsp/images/plugin/intxn_filter.png" alt="Interaction Filters"></p>

<h2>Retrieval Options</h2>

You can configure access options from the Options tab. There are two retrieval options:
<br>
<ul>
<li><b>Simplified Binary Model:</b> Retrieve a simplified binary network, as inferred from the original
BioPAX representation. In this representation, nodes within a network refer to physical entities
only, and edges refer to inferred interactions.</li>
<li><b>Full Model:</b> Retrieve the full model, as stored in the original BioPAX representation.
In this representation, nodes within a network can refer to physical entities and interactions.</li>
</ul>
By default, the simplified binary model is selected.  Note, however, that all pathways and networks
loaded via the Cytoscape links on the Pathway Commons web site will automatically be retrieved and
displayed in the full BioPAX model.

<h2>Expanding a Network</h2>

If you download a network in the Simplified Binary Mode, you can choose to expand the network
by retrieving neighbors of existing nodes within your network.  To do so:
<ul>
<li>Right click on a node of interest.</li>
<li>From the pull-down menu, select:  Use Web Services &rarr; Pathway Commons Web Service Client &rarr; Get Neighbors.</li>
</ul>
<p><img src="jsp/images/plugin/get_neighbors.png" alt="How to expand a network"></p>

<h2>Understanding and Using Cytoscape</h2>

<p>
You can click on molecules and their interactions in the Cytoscape view to get
  more details about them. Molecules can be moved around by clicking and dragging
  on them. You can zoom in and out using buttons on the toolbar and pan around
  the network view using the small overview window in the bottom left hand panel
  of Cytoscape. For further details, please refer to the <a href="http://cytoscape.org/">Cytoscape</a>
web site and user manual (available in HTML and PDF formats).
</p>

<h2>Full BioPAX Mode:  The Meaning of the Symbols in a Network View</h2>
By default, nodes and edges in the full BioPAX mode are displayed as follows:
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
			 No Abbreviation  Provided.
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
			 No Abbreviation Provided.
		</td>
	</tr>
</tbody></table>
<h2>Bug Reporting</h2>
If you encounter a bug with this plugin, please use our
<a href="get_feedback.do">feedback form</a>,
or log a bug directly to the
<a href="http://www.cbio.mskcc.org/cytoscape/bugs/">Cytoscape Bug Tracker</a>.

<h2>Contacts</h2>
<a href="http://cbio.mskcc.org/people/info/ethan_cerami.html">Ethan Cerami</a><br/>
<a href="http://cbio.mskcc.org/people/info/benjamin_gross.html">Benjamin Gross</a><br/>
Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>,
<a href="http://www.mskcc.org/">Memorial Sloan-Kettering Cancer Center</a>, New York City
<p>
	For any questions or feedback concerning this plugin, please use our
    <a href="get_feedback.do">feedback form</a>.
</p>
<h2>Release Notes / Current Limitations</h2>
<p>
	Version: 2.0
</p>
<ul>
    <li>
        Date: April 1, 2008
    </li>
    <li>
        Features:
        <ul>
            <li>Now supports full BioPAX mode and simplified binary model.</li>
            <li>Now supports direct query of Pathway Commons from within Cytoscape.</li>
            <li>Additional merge features for merging two or more networks.</li>
        </ul>
    </li>
</ul>
<p>
	Version: 0.2
</p>
<ul>
    <li>
        Date: June 25, 2007
    </li>
    <li>
        Features:
        <ul>
            <li>
                Enables the automatic download and visualization of network neighborhoods
                directly from PathwayCommons.org.
            </li>
        </ul>
    </li>
</ul>
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
<h2>License / Credits</h2>
This software is made available under the LGPL (Lesser General Public License).
</p>
<p>This product includes software developed by the
<a href="http://www.apache.org">Apache Software Foundation</a>
</p>
