// $Id: ShowBioPaxRecord.java,v 1.2 2006-02-21 22:51:09 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
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

import net.sf.ehcache.CacheException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.query.GetTopLevelPathwayListCommand;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Shows a BioPAX Record.
 *
 * @author Ethan Cerami
 */
public class ShowBioPaxRecord extends BaseAction {

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
        DaoCPath dao = DaoCPath.getInstance();
        String id = request.getParameter("id");
        CPathRecord record = null;
        if (id != null) {
            xdebug.logMsg(this, "Using cPath ID:  " + id);
            record = dao.getRecordById(Long.parseLong(id));
            xdebug.logMsg(this, "Got cPath Record:  " + record.getName());
            request.setAttribute("RECORD", record);
        } else {
            getTopLevelPathways(xdebug, dao, request);
        }

        String format = request.getParameter("format");
        if (format != null && format.equalsIgnoreCase("xml_abbrev")) {
            response.setContentType("text/xml");
            ServletOutputStream stream = response.getOutputStream();
            stream.println(record.getXmlContent());
            stream.flush();
            stream.close();
        }

        if (id == null) {
            return mapping.findForward("pathways");
        } else {
            return mapping.findForward("record");
        }
    }

    /**
     * Gets a List of Top-Level Pathways.
     */
    private void getTopLevelPathways(XDebug xdebug, DaoCPath dao,
            HttpServletRequest request) throws DaoException, IOException,
            CacheException {
        GetTopLevelPathwayListCommand getPathwayListCommand =
                new GetTopLevelPathwayListCommand(xdebug);
        ArrayList pathwayList = getPathwayListCommand.getTopLevelPathwayList();
        request.setAttribute("RECORDS", pathwayList);
    }
}
