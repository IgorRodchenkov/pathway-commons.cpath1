/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Bare Bones cPath Web Site:  Prototype.
 *
 * @author Ethan Cerami
 */
public class BareBonesWeb extends BaseAction {

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
        if (format != null && format.equalsIgnoreCase("xml")) {
            response.setContentType("text/xml");
            ServletOutputStream stream = response.getOutputStream();
            stream.println(record.getXmlContent());
            stream.flush();
            stream.close();
        }

        if (format != null && format.equalsIgnoreCase("api")) {
            response.setContentType("text/xml");
            ServletOutputStream stream = response.getOutputStream();
            XmlAssembly assembly =
                    XmlAssemblyFactory.createXmlAssembly(Long.parseLong(id),
                            XmlRecordType.BIO_PAX, 1, xdebug);
            stream.println(assembly.getXmlString());
            stream.flush();
            stream.close();
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }

    /**
     * Gets a List of Top-Level Pathways.
     */
    private void getTopLevelPathways(XDebug xdebug, DaoCPath dao,
            HttpServletRequest request) throws DaoException, IOException {
        ArrayList pathwayList = new ArrayList();
        xdebug.logMsg(this, "Getting all pathways");
        ArrayList candidateList = dao.getAllRecords(CPathRecordType.PATHWAY);
        xdebug.logMsg(this, "Total Number of Candidate Pathways Found:  "
                + candidateList.size());
        DaoInternalLink daoInternalLink = new DaoInternalLink();
        for (int i=0; i<candidateList.size(); i++) {
            CPathRecord pathway = (CPathRecord) candidateList.get(i);
            ArrayList sourceLinks = daoInternalLink.getSources(pathway.getId());
            //  If nothing points to this pathway, it is a top level pathway.
            if (sourceLinks.size() == 0) {
                pathwayList.add(pathway);
            }
        }
        request.setAttribute("RECORDS", pathwayList);
    }
}
