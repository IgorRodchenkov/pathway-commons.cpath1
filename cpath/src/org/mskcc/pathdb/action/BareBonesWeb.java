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
import org.mskcc.pathdb.util.cache.GlobalCache;
import org.mskcc.pathdb.util.cache.EhCache;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;

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
        if (format != null && format.equalsIgnoreCase("xml_abbrev")) {
            response.setContentType("text/xml");
            ServletOutputStream stream = response.getOutputStream();
            stream.println(record.getXmlContent());
            stream.flush();
            stream.close();
        }

        if (format != null && format.equalsIgnoreCase("xml_full")) {
            response.setContentType("text/xml");
            xdebug.logMsg(this, "Getting XML Assembly");
            ServletOutputStream stream = response.getOutputStream();
            XmlAssembly assembly =
                    XmlAssemblyFactory.createXmlAssembly(Long.parseLong(id),
                            XmlRecordType.BIO_PAX, 1, xdebug);
            stream.println(assembly.getXmlString());
            stream.flush();
            stream.close();
        }
        if (format != null && format.equalsIgnoreCase("xml_debug")) {
            xdebug.logMsg(this, "Getting XML Assembly");
            XmlAssembly assembly =
                    XmlAssemblyFactory.createXmlAssembly(Long.parseLong(id),
                            XmlRecordType.BIO_PAX, 1, xdebug);
            return mapping.findForward ("record");
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
        xdebug.logMsg(this, "Checking Memory Cache:  "
                + EhCache.KEY_PATHWAY_LIST);
        CacheManager manager = CacheManager.create();
        Cache cache = manager.getCache(EhCache.GLOBAL_CACHE_NAME);
        Element element = cache.get(EhCache.KEY_PATHWAY_LIST);

        ArrayList pathwayList = new ArrayList();
        if (element != null) {
            xdebug.logMsg(this, "Successfully Retrieved from Cache");
            xdebug.logMsg(this, "Cached Element created at:  "
                    + new Date (element.getCreationTime()));
            xdebug.logMsg(this, "Time to Live:  "
                    + cache.getTimeToLiveSeconds() / 60.0 + " minutes");
            pathwayList = (ArrayList) element.getValue();
        } else {
            xdebug.logMsg(this, "Not hit in cache.  Getting all pathways.");
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
            Element newElement = new Element (EhCache.KEY_PATHWAY_LIST,
                    pathwayList);
            cache.put(newElement);
        }
        request.setAttribute("RECORDS", pathwayList);
    }
}
