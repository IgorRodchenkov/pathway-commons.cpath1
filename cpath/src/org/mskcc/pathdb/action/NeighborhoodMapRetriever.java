// $Id: NeighborhoodMapRetriever.java,v 1.7 2008-09-08 18:43:29 grossben Exp $
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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.commons.httpclient.NameValuePair;

import java.net.URL;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 * Generates Neighborhood Map.
 *
 * @author Benjamin Gross
 */
public class NeighborhoodMapRetriever extends BaseAction {

	// some statics
	private static int SVG_WIDTH_SMALL = 200;
	private static int SVG_WIDTH_LARGE = 640;
	private static int SVG_HEIGHT_SMALL = 200;
	private static int SVG_HEIGHT_LARGE = 480;
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
		boolean wantThumbnail = Boolean.valueOf(request.getParameter("want_thumbnail"));
		int width = (wantThumbnail) ? SVG_WIDTH_SMALL : SVG_WIDTH_LARGE;
		int height = (wantThumbnail) ? SVG_HEIGHT_SMALL : SVG_HEIGHT_LARGE;

		log.info("************************ NeighborhoodMapRetriever.subExecute(), id: " + id + ", want_thumbnail: " + wantThumbnail);

		try {
			// get neighbor ids
			long[] neighborIDs = getNeighborIDs(id);

			log.info("************************ NeighborhoodMapRetriever.subExecute(), id count: " + neighborIDs.length);

			// short circuit if necessary
			if (neighborIDs.length == 0) {
				writeMapToResponse(NeighborhoodMapRetriever.class.getResource("resources/no-neighbors-found.png"), width, height, response);
				return null;
			}

			// get biopax assembly
			XmlAssembly biopaxAssembly = XmlAssemblyFactory.createXmlAssembly(neighborIDs, XmlRecordType.BIO_PAX, 1,
																			  XmlAssemblyFactory.XML_FULL, true, new XDebug());

			log.info("************************ NeighborhoodMapRetriever.subExecute(), biopax assembly: " + biopaxAssembly);

			// write out png 
			writeMapToResponse(getNeighborhoodMapURL(biopaxAssembly, width, height), width, height, response);
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
	 * Gets url to neighborhood map server.
	 *
	 * @param biopaxAssembly XmlAssemby
	 * @param width int
	 * @param height int
	 * @return URL
	 * @throws Exception
	 */
	private URL getNeighborhoodMapURL(XmlAssembly biopaxAssembly, int width, int height) throws Exception {

		NameValuePair nvps[] = new NameValuePair[3];
		nvps[0] = new NameValuePair("data", biopaxAssembly.getXmlString());
		nvps[1] = new NameValuePair("width", Integer.toString(width));
		nvps[2] = new NameValuePair("height", Integer.toString(height));

        StringBuffer buf = new StringBuffer("http://toro.cbio.mskcc.org:8080/nms/retrieve-neighborhood-map.do");
        buf.append("?");
        for (NameValuePair nvp : nvps) {
            buf.append(nvp.getName() + "=" + nvp.getValue() + "&");
        }
		// zap off last "&"
		String url = buf.toString();
		url = url.substring(0, url.length()-1);

		// outta here
        return new URL(url);
	}

	/**
	 * Writes no neighbors found image to response.
	 *
	 * @param url URL
	 * @param width int
	 * @param height int
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void writeMapToResponse(URL url, int width, int height, HttpServletResponse response) throws IOException {

		// setup some vars
		final ImageIcon icon = new ImageIcon(url);

		// create buffered image
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2d = image.createGraphics();
		g2d.drawImage(icon.getImage(), 0, 0, width, height, null);
		g2d.dispose();

		// write out the image bytes
		response.setContentType("image/png");
		contentTypeSet = true;
		ImageIO.write(image, "png", response.getOutputStream());
	}
}
