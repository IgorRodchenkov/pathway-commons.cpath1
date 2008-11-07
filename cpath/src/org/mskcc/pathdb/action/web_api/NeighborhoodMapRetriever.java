// $Id: NeighborhoodMapRetriever.java,v 1.5 2008-11-07 16:33:48 grossben Exp $
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
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolConstantsVersion2;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.sql.util.NeighborsUtil;

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
	private static int SVG_WIDTH_SMALL = 185;
	private static int SVG_HEIGHT_SMALL = 185;
	private static int SVG_WIDTH_LARGE = 585;
	private static int SVG_HEIGHT_LARGE = 540;
    private static Logger log = Logger.getLogger(NeighborhoodMapRetriever.class);
	private static String NMS;
	static {
		org.mskcc.dataservices.util.PropertyManager pManager = org.mskcc.dataservices.util.PropertyManager.getInstance();
		NMS = pManager.getProperty(org.mskcc.pathdb.action.BaseAction.PROPERTY_ADMIN_NEIGHBORHOOD_MAP_SERVER_URL);
	}

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

		if (NMS == null || NMS.length() == 0) {
			throw new IllegalArgumentException("Neighborhood Map Server URL has not been properly set in build.properties file.");
		}

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

			// short circuit if necessary
			if (neighborIDs.length == 0) {
				writeMapToResponse(new ImageIcon(NeighborhoodMapRetriever.class.getResource("resources/no-neighbors-found.png")), response);
				return null;
			}

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
	}

	/**
	 * Given a cpath record id, returns list of neighbors.
	 *
	 * @return long[]
	 * @throws DaoException
	 */
	private long[] getNeighborIDs() throws DaoException {

		// get neighbors - easy!
		long neighborRecordIDs[] = NEIGHBORS_UTIL.getNeighbors(PHYSICAL_ENTITY_RECORD_ID, FULLY_CONNECTED);

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

		HttpClient client = new HttpClient();
		NameValuePair nvps[] = new NameValuePair[4];
		nvps[0] = new NameValuePair("data", biopaxAssembly.getXmlString());
		nvps[1] = new NameValuePair("width", Integer.toString(WIDTH));
		nvps[2] = new NameValuePair("height", Integer.toString(HEIGHT));
		nvps[3] = new NameValuePair("version", "0.1");
		PostMethod method = new PostMethod(NMS);
		method.addParameters(nvps);

		// check for http errors
		int statusCode = client.executeMethod(method);
		if (statusCode != 200) {
			throw new Exception("Error fetching neighborhood map image: " + HttpStatus.getStatusText(statusCode));
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
        stream.println("<frame src=\"webservice.do?version=2.0&cmd=get_neighbors&q=" + Long.toString(PHYSICAL_ENTITY_RECORD_ID)  + "&output=image_map\">");
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
