// $Id: NeighborhoodMapRetriever.java,v 1.13 2008-12-10 05:30:48 grossben Exp $
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
package org.mskcc.pathdb.action.web_api;

// imports
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.action.BaseAction;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.form.WebUIBean;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.schemas.binary_interaction.util.BinaryInteractionUtil;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssembly;
import org.mskcc.pathdb.schemas.binary_interaction.assembly.BinaryInteractionAssemblyFactory;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolConstantsVersion2;
import org.mskcc.pathdb.protocol.ProtocolConstantsVersion3;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.sql.util.NeighborsUtil;
import org.mskcc.pathdb.servlet.CPathUIConfig;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;

import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 * Generates Neighborhood Map.
 *
 * @author Benjamin Gross
 */
public class NeighborhoodMapRetriever {

	// some statics
	private static int SVG_WIDTH_SMALL = 150;
	private static int SVG_HEIGHT_SMALL = 150;
	private static int SVG_WIDTH_LARGE = 585;
	private static int SVG_HEIGHT_LARGE = 540;
    private static Logger log = Logger.getLogger(NeighborhoodMapRetriever.class);

	// member vars
	private int WIDTH;
	private int HEIGHT;
	private boolean COOK_INPUT_ID;
	private boolean WANT_FRAMESET;
	private boolean WANT_THUMBNAIL;
	private boolean CONTENT_TYPE_SET;
	private boolean FULLY_CONNECTED;
	private long PHYSICAL_ENTITY_RECORD_ID;
	private ProtocolRequest PROTOCOL_REQUEST;
	private NeighborsUtil NEIGHBORS_UTIL;
	private ArrayList<String> UNWANTED_INTERACTIONS;
	private String UNWANTED_INTERACTIONS_STRING;
	private String UNWANTED_SMALL_MOLECULES_STRING;

	/**
	 * Gets neighborhood map count.
	 *
     * @param xdebug   XDebug Object.
     * @param protocolRequest Protocol Request Object.
	 * @return Integer
	 */
	public Integer getNeighborhoodMapSize(XDebug xdebug, ProtocolRequest protocolRequest) {

		HashSet<String> filteredBinaryInteractionParticipants = new HashSet<String>();
		try {
			// get list of neighbor ids
			setMemberVars(xdebug, protocolRequest);
			long[] neighborIDs = getNeighborIDs();

			log.info("NeighborhoodMapRetriever.getNeighborMapSize(), before sif conversion: " + Long.toString(neighborIDs.length));
			if (neighborIDs.length == 0) return 0;

			// create sif assembly
			XmlAssembly biopaxAssembly = XmlAssemblyFactory.createXmlAssembly(neighborIDs, XmlRecordType.BIO_PAX, 1,
																			  XmlAssemblyFactory.XML_FULL, true, new XDebug());
			BinaryInteractionAssembly sifAssembly =
				BinaryInteractionAssemblyFactory.createAssembly(BinaryInteractionAssemblyFactory.AssemblyType.SIF,
																BinaryInteractionUtil.getRuleTypes(),
																biopaxAssembly.getXmlString());

			// filter out unwanted interactions
			String[] binaryInteractionStringArray = sifAssembly.getBinaryInteractionString().split("\n");
			for (String binaryInteractionString : binaryInteractionStringArray) {
				if (binaryInteractionString != null) {
					// sif format:  ID\tINTERACTION_TYPE\tID
					String[] components = binaryInteractionString.split("\t");
					if (components.length == 3) {
						// populate filteredBinaryInteractionParticpants (neighbors in map)
						if (!UNWANTED_INTERACTIONS.contains(components[1])) {
							filteredBinaryInteractionParticipants.add(components[0]);
							filteredBinaryInteractionParticipants.add(components[2]);
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.info("NeighborhoodMapRetriever.getNeighborMapSize(), Exception caught: " + e.getMessage() + ", PHYSICAL_ENTITY_RECORD_ID: " + Long.toString(PHYSICAL_ENTITY_RECORD_ID));
			return 0;
		}

		log.info("NeighborhoodMapRetriever.getNeighborMapSize(), after sif conversion: " + filteredBinaryInteractionParticipants.size());

		// outta here
		return filteredBinaryInteractionParticipants.size();
	}

    /**
     * Generates neighborhood map image
     *
     * @param xdebug   XDebug Object.
     * @param protocolRequest Protocol Request Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param mapping  Struts ActionMapping Object.
     * @return Struts Action Forward Object.
	 * @throws DaoException, NumberFormatException, IllegalArgumentException
     */
    public ActionForward processRequest(XDebug xdebug, ProtocolRequest protocolRequest,
										HttpServletRequest request, HttpServletResponse response,
										ActionMapping mapping) throws DaoException, NumberFormatException, IllegalArgumentException {

		// set some member args
		setMemberVars(xdebug, protocolRequest);

		log.info("NeighborhoodMapRetriever.subExecute(), id: " + PHYSICAL_ENTITY_RECORD_ID + ", want_thumbnail: " + WANT_THUMBNAIL + ", want_map_legend_frameset: " + WANT_FRAMESET);

		try {

			// short circuit if frameset wanted
			if (WANT_FRAMESET) {
				writeFramesetToResponse(response);
				return null;
			}

			// get neighbor ids
			long[] neighborIDs = getNeighborIDs();
			log.info("NeighborhoodMapRetriever.subExecute(), numNodes: " + Long.toString(neighborIDs.length));

			// short circuit if necessary - now handled in BioPAXRecord2.jsp
			//if (neighborIDs.length == 0) {
			//	String imageFile = (WANT_THUMBNAIL) ? "resources/no-neighbors-found-thumbnail.png" : "resources/no-neighbors-found.png";
			//	writeMapToResponse(new ImageIcon(NeighborhoodMapRetriever.class.getResource(imageFile)), response);
			//	return null;
			//}
			//else if (neighborIDs.length > CPathUIConfig.getWebUIBean().getMaxMiniMapSize()) {
			//	String imageFile = (WANT_THUMBNAIL) ? "resources/too-many-neighbors-found-thumbnail.png" : "resources/too-many-neighbors-found.png";
			//	writeMapToResponse(new ImageIcon(NeighborhoodMapRetriever.class.getResource(imageFile)), response);
			//	return null;
			//}

			// get biopax assembly
			XmlAssembly biopaxAssembly = XmlAssemblyFactory.createXmlAssembly(neighborIDs, XmlRecordType.BIO_PAX, 1,
																			  XmlAssemblyFactory.XML_FULL, true, new XDebug());

			// write out png
			ImageIcon imageIcon = getNeighborhoodMapImage(biopaxAssembly);
			if (imageIcon != null) writeMapToResponse(imageIcon, response);
		}
		catch (Exception e) {
			if (!CONTENT_TYPE_SET) {
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
	 * Set member vars
	 *
     * @param xdebug   XDebug Object.
	 * @param protocolRequest ProtocolRequest
	 * @throws NumberFormatException
	 * @throws DaoException
	 * @throws IllegalArgumentException
	 */
	private void setMemberVars(XDebug xdebug, ProtocolRequest protocolRequest) throws NumberFormatException, DaoException {

		CONTENT_TYPE_SET = false;
		PROTOCOL_REQUEST = protocolRequest;
        NEIGHBORS_UTIL = new NeighborsUtil(xdebug);

		// cook input id ?
		String inputIDType = PROTOCOL_REQUEST.getInputIDType();
		COOK_INPUT_ID = (inputIDType != null &&
						 !inputIDType.equals(ExternalDatabaseConstants.INTERNAL_DATABASE));
		PHYSICAL_ENTITY_RECORD_ID = NEIGHBORS_UTIL.getPhysicalEntityRecordID(PROTOCOL_REQUEST, COOK_INPUT_ID);

		// fully connected ?
		String fullyConnectedStr = protocolRequest.getFullyConnected();
		FULLY_CONNECTED = (fullyConnectedStr != null && fullyConnectedStr.equalsIgnoreCase("yes"));

		// output
		String output = PROTOCOL_REQUEST.getOutput();
		WANT_THUMBNAIL = (output != null && output.equals(ProtocolConstantsVersion2.FORMAT_IMAGE_MAP_THUMBNAIL));
		WANT_FRAMESET = (output != null && output.equals(ProtocolConstantsVersion2.FORMAT_IMAGE_MAP_FRAMESET));

		// if WANT_FRAMESET, these will be ignored
		WIDTH = (WANT_THUMBNAIL) ? SVG_WIDTH_SMALL : SVG_WIDTH_LARGE;
		HEIGHT = (WANT_THUMBNAIL) ? SVG_HEIGHT_SMALL : SVG_HEIGHT_LARGE;

		UNWANTED_INTERACTIONS_STRING = "";
		UNWANTED_INTERACTIONS = new ArrayList<String>();
		WebUIBean bean = (CPathUIConfig.getWebUIBean() != null) ? CPathUIConfig.getWebUIBean() : new WebUIBean();
		String[] filterInteractions = bean.getFilterInteractions().split(",");
		if (filterInteractions.length > 0) {
			for (String filterInteraction : filterInteractions) {
				UNWANTED_INTERACTIONS.add(filterInteraction.trim());
				UNWANTED_INTERACTIONS_STRING = UNWANTED_INTERACTIONS_STRING + filterInteraction.trim() + " ";
			}
		}
		UNWANTED_SMALL_MOLECULES_STRING = "";
		//final String[] UNWANTED_SMALL_MOLECULES = { "ATP", "ADP", "GTP", "GDP", "NADP", "NADP+",
		//											"NADPH", "NAD", "NAD+", "NADH", "FAD", "FADH2", "H2O" };
		//try {
		//	org.mskcc.pathdb.sql.dao.DaoCPath daoCPath = org.mskcc.pathdb.sql.dao.DaoCPath.getInstance();
		//	for (String smallMolecule : UNWANTED_SMALL_MOLECULES) {
		//		org.mskcc.pathdb.model.CPathRecord cpathRecord = daoCPath.getRecordByName(smallMolecule);
		//		if (cpathRecord != null && cpathRecord.getSpecificType().equalsIgnoreCase(org.mskcc.pathdb.schemas.biopax.BioPaxConstants.SMALL_MOLECULE)) {
		//			UNWANTED_SMALL_MOLECULES_BUFFER.append(Long.toString(cpathRecord.getId()) + " ");
		//			log.info("Mapping unwanted small molecule " + smallMolecule + " to cpath id: " + Long.toString(cpathRecord.getId()));
		//		}
		//	}
		//}
		//catch (DaoException e) {
		//	log.info("NeighborhoodMapRetriever (static code execution)");
		//	e.printStackTrace();
		//}
	}

	/**
	 * Given a cpath record id, returns list of neighbors.
	 *
	 * @return long[]
	 * @throws DaoException
	 */
	private long[] getNeighborIDs() throws DaoException {

		log.info("NeighborhoodMapRetriever.getNeighborIDs(), record id: " + PHYSICAL_ENTITY_RECORD_ID);
		log.info("NeighborhoodMapRetriever.getNeighborIDs(), fully connected: " + FULLY_CONNECTED);

		// get neighbors - easy!
		long neighborRecordIDs[] = NEIGHBORS_UTIL.getNeighbors(PHYSICAL_ENTITY_RECORD_ID, FULLY_CONNECTED);
		if (neighborRecordIDs.length == 0) return neighborRecordIDs;

		// filter by data sources & return
		return NEIGHBORS_UTIL.filterByDataSource(PROTOCOL_REQUEST, neighborRecordIDs);
	}

	/**
	 * Gets url to neighborhood map server.
	 *
	 * @param biopaxAssembly XmlAssemby
	 * @return ImageIcon
	 * @throws Exception
	 */
	private ImageIcon getNeighborhoodMapImage(XmlAssembly biopaxAssembly) throws Exception {

		log.info("NeighborhoodMapRetriever.getNeighborhoodMapImage()");
		
		HttpClient client = new HttpClient();
		NameValuePair nvps[] = new NameValuePair[6];
		nvps[0] = new NameValuePair("data", biopaxAssembly.getXmlString());
		nvps[1] = new NameValuePair("width", Integer.toString(WIDTH));
		nvps[2] = new NameValuePair("height", Integer.toString(HEIGHT));
		nvps[3] = new NameValuePair("unwanted_interactions", UNWANTED_INTERACTIONS_STRING.trim());
		nvps[4] = new NameValuePair("unwanted_small_molecules", UNWANTED_SMALL_MOLECULES_STRING.trim());
		nvps[5] = new NameValuePair("version", "1.0");
		PostMethod method = new PostMethod(CPathUIConfig.getWebUIBean().getImageMapServerURL());
		method.addParameters(nvps);

		// execute method
		log.info("NeighborhoodMapRetriever.getNeighborhoodMapImage(), executing method....");
		int statusCode = client.executeMethod(method);
		log.info("NeighborhoodMapRetriever.getNeighborhoodMapImage(), neighborhood map image fetched, response code: " + Integer.toString(statusCode) + ",  reading response...");
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("Error fetching neighborhood map image: " + method.getStatusLine());
		}

		// get content
		InputStream instream = method.getResponseBodyAsStream();
		ByteArrayOutputStream outstream =  new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len;
		int totalBytes = 0;
		while ((len = instream.read(buffer)) > 0) {
			outstream.write(buffer, 0, len);
		}
		instream.close();

		// outta here
		byte[] responseBody = outstream.toByteArray(); 
		return (responseBody != null) ? new ImageIcon(responseBody) : null;		
	}

	/**
	 * Writes html code that constructs frameset for map/legend.
	 *
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void writeFramesetToResponse(HttpServletResponse response) throws IOException {

		log.info("NeighborhoodMapRetriever.writeFramesetToResponse");

		// set content type
		response.setContentType("text/html");
		CONTENT_TYPE_SET = true;
		ServletOutputStream stream = response.getOutputStream();
		
		// write out html to define frameset
		stream.println("<html>");
		stream.println("<head>");
		stream.println("<title>Neighborhood Map</title>");
		stream.println("</head>");
        stream.println("<frameset cols=\"60%,40%\"");
        stream.println("<frame src=\"webservice.do?" + ProtocolRequest.ARG_VERSION + "=" + ProtocolConstantsVersion3.VERSION_3 +
					   "&" + ProtocolRequest.ARG_COMMAND + "=" + ProtocolConstantsVersion2.COMMAND_GET_NEIGHBORS +
					   "&" + ProtocolRequest.ARG_QUERY + "=" + Long.toString(PHYSICAL_ENTITY_RECORD_ID)  +
					   "&" + ProtocolRequest.ARG_DATA_SOURCE + "=" + PROTOCOL_REQUEST.getDataSource() + 
					   "&" + ProtocolRequest.ARG_OUTPUT + "=" + ProtocolConstantsVersion2.FORMAT_IMAGE_MAP + "\">");
        stream.println("<frame src=\"sif_legend.do\">");
        stream.println("</frameset>");
		stream.println("</html>");

		// outta here
		stream.flush();
		stream.close();
	}

	/**
	 * Writes no neighbors found image to response.
	 *
	 * @param icon imageIcon
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void writeMapToResponse(ImageIcon icon, HttpServletResponse response) throws IOException {

		log.info("NeighborhoodMapRetriever.writeMapToResponse");

		// create buffered image
		final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2d = image.createGraphics();
		g2d.drawImage(icon.getImage(), 0, 0, WIDTH, HEIGHT, null);
		g2d.dispose();

		// write out the image bytes
		response.setContentType("image/png");
		CONTENT_TYPE_SET = true;
		ImageIO.write(image, "png", response.getOutputStream());
	}
}
