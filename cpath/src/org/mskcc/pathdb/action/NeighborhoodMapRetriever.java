// $Id: NeighborhoodMapRetriever.java,v 1.2 2008-07-22 18:29:35 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2008 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
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
package org.mskcc.pathdb.action;

// imports
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.schemas.binary_interaction.util.BinaryInteractionUtil;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssembly;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssemblyFactory;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.GraphReader;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.task.TaskMonitor;

import org.mskcc.biopax_plugin.util.biopax.BioPaxUtil;
import org.mskcc.biopax_plugin.util.cytoscape.LayoutUtil;
import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.cytoscape.coreplugin.cpath2.cytoscape.BinarySifVisualStyleUtil;

import ding.view.DGraphView;
import ding.view.DingCanvas;
import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Attribute;
import org.jdom.Namespace;

import java.io.File;
import java.io.StringReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Generates Neighborhood Map.
 *
 * @author Benjamin Gross
 */
public class NeighborhoodMapRetriever extends BaseAction {

	// some statics
	private static int SVG_WIDTH_SMALL = 200;
	private static int SVG_HEIGHT_SMALL = 200;
	private static int SVG_WIDTH_LARGE = 640;
	private static int SVG_HEIGHT_LARGE = 480;
	private static java.awt.Color BACKGROUND_COLOR = new java.awt.Color(204,204,255);
    private static Logger log = Logger.getLogger(NeighborhoodMapRetriever.class);

	// member vars
	private boolean contentTypeSet;

    /**
     * Generates neighborhood map image
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping, ActionForm form,
									HttpServletRequest request, HttpServletResponse response,
									XDebug xdebug) throws Exception {

		// set some member args
		this.contentTypeSet = false;

		// check args
		String id = request.getParameter("id");
		if (id == null) {
			return mapping.findForward(BaseAction.FORWARD_FAILURE);
		}
		Boolean wantThumbnail = new Boolean(request.getParameter("want_thumbnail"));

		log.info("************************ NeighborhoodMapRetriever.subExecute(), id: " + id + ", want_thumbnail: " + wantThumbnail);

		try {
			// get neighbor ids
			long[] neighborIDs = getNeighborIDs(id);

			log.info("************************ NeighborhoodMapRetriever.subExecute(), id count: " + neighborIDs.length);

			// get biopax assembly
			XmlAssembly biopaxAssembly = XmlAssemblyFactory.createXmlAssembly(neighborIDs, XmlRecordType.BIO_PAX, 1,
																			  XmlAssemblyFactory.XML_FULL, true, new XDebug());

			log.info("************************ NeighborhoodMapRetriever.subExecute(), biopax assembly: " + biopaxAssembly);
			
			// get binary sif tmpFile
			File sifFile = getSIFFile(biopaxAssembly);

			// get CyNetwork
			CyNetwork cyNetwork = getCyNetwork(sifFile, biopaxAssembly);

			// post process the network (layout, apply style, etc)
			CyNetworkView cyNetworkView = postProcessCyNetwork(cyNetwork, wantThumbnail);

			// write out png 
			writePNGToResponse(response, cyNetworkView);
		}
		catch (Exception e) {
			if (!contentTypeSet) {
				return mapping.findForward(BaseAction.FORWARD_FAILURE);
			}
			else {
				e.printStackTrace();
			}
		}

		// outta here
		return null;
    }

	/**
	 * Given a cpath record id, returns list of neighbors.
	 *
	 * @param id String
	 * @return long[]
	 * @throws DaoException
	 */
	private long[] getNeighborIDs(String id) throws DaoException {

		log.info("************************ NeighborhoodMapRetriever.getNeighborIDs, id: " + id);

		// grab starting id
        long cpathId = Long.parseLong(id);

		// set to return
		Set<Long> neighborIDs = new HashSet<Long>();
		neighborIDs.add(cpathId);

		// get all parents/sources of this record
        DaoInternalLink daoInternalLink = new DaoInternalLink();
        ArrayList<InternalLinkRecord> internalLinkRecords = daoInternalLink.getSources(cpathId);
        for (InternalLinkRecord linkRecord : internalLinkRecords) {
            long sourceID = linkRecord.getSourceId();
            neighborIDs.add(sourceID);
        }

		// outta here
		int lc = -1;
		long[] toReturn = new long[neighborIDs.size()];
		for (Long neighborID : neighborIDs) {
			toReturn[++lc] = neighborID;
		}

		return toReturn;
	}

	/**
	 * Given a set of cpath ids, returns a binary sif file.
	 *
	 * @param biopaxAssembly XmlAssembly
	 * @return File
	 * @throws IOException
	 */
	private File getSIFFile(XmlAssembly biopaxAssembly) throws AssemblyException, IOException {

		log.info("************************ NeighborhoodMapRetriever.getSIFFile, biopaxAssembly: " + biopaxAssembly);

		// get binary interaction assembly from biopax
		BinaryInteractionAssembly sifAssembly =
			BinaryInteractionAssemblyFactory.createAssembly(BinaryInteractionAssemblyFactory.AssemblyType.SIF,
															BinaryInteractionUtil.getRuleTypes(),
															biopaxAssembly.getXmlString());

		// create tmp file
		String tmpDir = System.getProperty("java.io.tmpdir");
		File tmpFile = File.createTempFile("temp", ".sif", new File(tmpDir));
		tmpFile.deleteOnExit();

		// get data to write into temp file
		FileWriter writer = new FileWriter(tmpFile);
		log.info("************************ NeighborhoodMapRetriever.getSIFFile: sif assembly: " + sifAssembly);
		writer.write(sifAssembly.getBinaryInteractionString());
		writer.close();

		// outta here
		return tmpFile;
	}

	/**
	 * Given a sif file, returns a CyNetwork.
	 *
	 * @param sifFile File
	 * @param biopaxAssembly XmlAssembly
	 * @return CyNetwork
	 * @throws IOException
	 * @throws JDOMException
	 */
	private CyNetwork getCyNetwork(File sifFile, XmlAssembly biopaxAssembly) throws IOException, JDOMException {

		log.info("************************ NeighborhoodMapRetriever.getCyNetwork, biopaxAssembly: " + biopaxAssembly);

		// create cytoscape network
		GraphReader reader = Cytoscape.getImportHandler().getReader(sifFile.getAbsolutePath());
		CyNetwork cyNetwork = Cytoscape.createNetwork(reader, false, null);

		// init the attributes
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        MapNodeAttributes.initAttributes(nodeAttributes);

        // specify that this is a BINARY_NETWORK
        Cytoscape.getNetworkAttributes().setAttribute(cyNetwork.getIdentifier(),
													  BinarySifVisualStyleUtil.BINARY_NETWORK, Boolean.TRUE);

		// setup node attributes
		StringReader strReader = new StringReader(biopaxAssembly.getXmlString());
		BioPaxUtil bpUtil = new BioPaxUtil(strReader, new NullTaskMonitor());
		ArrayList<Element> peList = bpUtil.getPhysicalEntityList();
		Namespace ns = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		for (Element element : peList) {
			String id = element.getAttributeValue("ID", ns);
			if (id != null) {
				id = id.replaceAll("CPATH-", "");
				MapNodeAttributes.mapNodeAttribute(element, id, nodeAttributes, bpUtil);
			}
		}

		// outta here
		return cyNetwork;
	}

	/**
	 * Given a binary sif file, creates a CyNetwork.
	 *
	 * @param cyNetwork CyNetwork
	 * @param wantThumbnail boolean
	 * @returns CyNetworkView
	 */
	private CyNetworkView postProcessCyNetwork(CyNetwork cyNetwork, boolean wantThumbnail) {

		log.info("************************ NeighborhoodMapRetriever.postProcessCyNetwork(), cyNetwork: " + cyNetwork + ", wantThumbnail: " + wantThumbnail);

		//  create view - use local create view option, so that we don't mess up the visual style.
		LayoutUtil layoutAlgorithm = new LayoutUtil();
		final DingNetworkView dView = new DingNetworkView(cyNetwork, "");
		dView.setGraphLOD(new CyGraphLOD());

		// set canvas attributes
		DingCanvas innerCanvas = (DingCanvas)dView.getComponent();
		innerCanvas.setOpaque(true);
		innerCanvas.setBackground(BACKGROUND_COLOR);
		innerCanvas.setBounds(0, 0,
							  (wantThumbnail) ? SVG_WIDTH_SMALL : SVG_WIDTH_LARGE,
							  (wantThumbnail) ? SVG_HEIGHT_SMALL : SVG_HEIGHT_LARGE);

		// setup visual style
		VisualStyle visualStyle = BinarySifVisualStyleUtil.getVisualStyle();
		VisualMappingManager VMM = Cytoscape.getVisualMappingManager();
		dView.setVisualStyle(visualStyle.getName());
		VMM.setVisualStyle(visualStyle);
		VMM.setNetworkView(dView);

		// layout
		layoutAlgorithm.doLayout(dView);
		dView.redrawGraph(false, true);
		dView.fitContent();

		// outta here
		return dView;
	}

	/**
	 * Given a CyNetwork view, generates a png file.
	 * 
	 * @param response HttpServletResponse
	 * @param cyNetworkView CyNetworkView
	 * @throws IOException
	 */
	private void writePNGToResponse(HttpServletResponse response, CyNetworkView cyNetworkView) throws IOException {

		log.info("************************ NeighborhoodMapRetriever.writePNGToResponse, cyNetworkView: " + cyNetworkView);

		double scale = 1.0;

		// needed to prevent java.lang.unsatisfiedLinkError: initGVIDS
		GraphicsEnvironment.getLocalGraphicsEnvironment();

		DGraphView dView = (DGraphView)cyNetworkView;
		DingCanvas innerCanvas = (DingCanvas)dView.getComponent();
		innerCanvas.setOpaque(true);
			
		int width  = (int) (innerCanvas.getWidth() * scale);
		int height = (int) (innerCanvas.getHeight() * scale);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.scale(scale, scale);
		innerCanvas.print(g);
		g.dispose();

		response.setContentType("image/png");
		contentTypeSet = true;
		ImageIO.write(image, "png", response.getOutputStream());
	}
}

class NullTaskMonitor implements TaskMonitor {

    public void setPercentCompleted(int i) throws IllegalArgumentException {
    }

    public void setEstimatedTimeRemaining(long l) throws IllegalThreadStateException {
    }

    public void setException(Throwable throwable, String string)
            throws IllegalThreadStateException {
    }

    public void setException(Throwable throwable, String string, String string1)
            throws IllegalThreadStateException {
    }

    public void setStatus(String string) throws IllegalThreadStateException, NullPointerException {
    }
}
