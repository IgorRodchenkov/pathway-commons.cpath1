// $Id: IconRetriever.java,v 1.1 2006-12-20 18:46:45 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Retrieves an external database icon from mysql.
 *
 * @author Benjamin Gross
 */
public class IconRetriever extends BaseAction {

    /**
     * Executes Bare Bones Web.
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

		// get external db id
        String idStr = request.getParameter("id");
		if (idStr == null) {
			return mapping.findForward(BaseAction.FORWARD_FAILURE);
		}
		Integer id = new Integer(idStr);

		// get ExternalDatabaseRecord and icon
        DaoExternalDb daoExternalDb = new DaoExternalDb();
		ExternalDatabaseRecord extDatabaseRecord = daoExternalDb.getRecordById(id);
		InputStream iconBinaryStream = daoExternalDb.getIconBinaryStream(id);

		// make sure we have valid references
		if (extDatabaseRecord == null || iconBinaryStream == null) {
			return mapping.findForward(BaseAction.FORWARD_FAILURE);
		}

		// set content type on response object
		response.setContentType("image/" + extDatabaseRecord.getIconFileExtension());

		// write out the data
		writeIconData(iconBinaryStream, response.getOutputStream());

		// outta here
		return null;
    }

	/**
	 * Writes in stream bytes to out stream.
	 *
	 * @param in InputStream
	 * @param out OutputStream
	 * @throws IOException
	 */
	private void writeIconData(InputStream in, OutputStream out) throws IOException {

		int bytesRead;
		final int BUF_SIZE = 1000;
		byte[] temp = new byte[BUF_SIZE];

		do {
			bytesRead = in.read(temp, 0, BUF_SIZE);
			out.write(temp, 0, (bytesRead > 0) ? bytesRead : 0);
		} while (bytesRead != -1);
	}
}
