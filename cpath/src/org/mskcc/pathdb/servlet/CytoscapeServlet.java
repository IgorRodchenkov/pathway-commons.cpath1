// $Id: CytoscapeServlet.java,v 1.1 2007-03-23 20:04:29 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.servlet;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyMain;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.layout.LayoutAlgorithm;
import cytoscape.layout.algorithms.GridNodeLayout;

import ding.view.DGraphView;
import ding.view.DingCanvas;

import org.apache.log4j.Logger;
import org.freehep.graphicsio.svg.SVGGraphics2D;

import javax.servlet.ServletException;
//import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

/**
 * CPath Servlet.
 *
 * @author Benjamin Gross
 */
public final class CytoscapeServlet extends HttpServlet {
//public final class CytoscapeServlet extends HttpServlet implements SingleThreadModel {

	/*
	 * the width of our graphic
	 */
	private static int SVG_WIDTH = 400;

	/*
	 * the height of our graphic
	 */
	private static int SVG_HEIGHT = 400;

	/*
	 * the background color of the graphic
	 */
	private static Color BACKGROUND_COLOR = new Color(204,204,255);

	/*
	 * ref to the logger
	 */
    private Logger log = Logger.getLogger(CPathServlet.class);

    /**
     * Initializes Servlet with parameters in web.xml file.
     *
     * @throws ServletException Servlet Initialization Error.
     */
    public void init() throws ServletException {
        super.init();
    }

    /**
     * Shutdown the Servlet.
     */
    public void destroy() {
        super.destroy();
		Cytoscape.exit(0);
    }

    /**
     * Handles Client Request.
     *
     * @param req Http Servlet Request.
     * @param res Http Servlet Response.
     * @throws ServletException All Servlet Errors.
     * @throws IOException      All Input/Output Errors.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

		testCytoscape(res.getOutputStream());
    }

	private void testCytoscape(OutputStream out) {

		// create the network
        CyNetwork net = defineNetwork();
		CyNetworkView view = Cytoscape.getNetworkView(net.getIdentifier());

		// setup the style
		defineStyle(view);

		// layout
		LayoutAlgorithm layout = new GridNodeLayout();
		layout.doLayout(view, null);
		view.fitContent();

		// gen the svg
		writeSVG(out, view);
	}

	private void defineStyle(CyNetworkView view) {

		// set canvas attributes
		DGraphView dView = (DGraphView)view;
		DingCanvas innerCanvas = (DingCanvas)dView.getComponent();
		innerCanvas.setOpaque(true);
		innerCanvas.setBackground(BACKGROUND_COLOR);
		innerCanvas.setBounds(0, 0, SVG_WIDTH, SVG_HEIGHT);

		// choose visual style
		VisualMappingManager manager = new VisualMappingManager(view);
		VisualStyle chosen = manager.getCalculatorCatalog().getVisualStyle("default");
		manager.setVisualStyle(chosen);
		manager.applyNodeAppearances();
		manager.applyEdgeAppearances();
	}

    private CyNetwork defineNetwork() {

		int nodeIndices[] = new int[2];
		int edgeIndices[] = new int[1];

		// create nodes
        CyNode n1 = Cytoscape.getCyNode("n1", true);
		nodeIndices[0] = n1.getRootGraphIndex();
		CyNode n2 = Cytoscape.getCyNode("n2", true);
		nodeIndices[1] = n2.getRootGraphIndex();

		// create edges
        CyEdge edge = Cytoscape.getCyEdge("n1", "edge 1", "n2", "interaction type");
		edgeIndices[0] = edge.getRootGraphIndex();

		// create the network
        CyNetwork net = Cytoscape.getRootGraph().createNetwork(nodeIndices, edgeIndices);
		Cytoscape.addNetwork(net, "net 1", null, true);

		// outta here
		return net;
    }

	private void writeSVG(OutputStream out, CyNetworkView view) {

		DGraphView dView = (DGraphView)view;
		DingCanvas innerCanvas = (DingCanvas)dView.getComponent();

		// needed to prevent java.lang.unsatisfiedLinkError: initGVIDS
		GraphicsEnvironment.getLocalGraphicsEnvironment();

		try {
			Dimension dim = new Dimension(SVG_WIDTH, SVG_HEIGHT);
			SVGGraphics2D svgGraphics = new SVGGraphics2D(out, dim);
			svgGraphics.startExport(); 
			innerCanvas.print(svgGraphics);
			svgGraphics.endExport();
		}
		catch (Exception e) {
			log.error("Error generating SVG data: ", e);
		}
	}
}
