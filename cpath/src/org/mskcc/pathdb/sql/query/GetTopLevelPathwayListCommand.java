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
package org.mskcc.pathdb.sql.query;

import net.sf.ehcache.CacheException;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.util.TopLevelPathwayUtil;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Gets List of Top Level Pathways.
 * <p/>
 * There are two ways in which a client may request a list of top-level
 * pathways.
 * <UL>
 * <LI>html mode:  in this mode, a web browser requests a list of top-level
 * pathways, and we need to create a simple HTML page consisting of all
 * matching pathways.  In this mode, there is no need to create an
 * intermediary XML document, and we can cache the matching records in the
 * global in-memory cache.
 * <LI>xml mode:  in this mode, a client application requests a list of
 * top-level pathways in BioPAX format.  In this mode, we (obviously) need
 * to create a complete BioPAX document, and can cache the XML assembly
 * in the MySQL XML database cache.
 * </UL>
 *
 * @author Ethan Cerami
 */
public class GetTopLevelPathwayListCommand extends Query {
    private ArrayList pathwayList;
    private ProtocolRequest request;

    /**
     * Constructor.
     *
     * @param xdebug XDebug Object.
     */
    public GetTopLevelPathwayListCommand(XDebug xdebug) {
        this.xdebug = xdebug;
        this.pathwayList = new ArrayList();
    }

    /**
     * Constructor.
     *
     * @param request ProtocolRequest Object.
     * @param xdebug  XDebug Object.
     */
    GetTopLevelPathwayListCommand(ProtocolRequest request,
            XDebug xdebug) {
        this.request = request;
        this.xdebug = xdebug;
        this.pathwayList = new ArrayList();
    }

    /**
     * Gets the Top Level Pathway List (HTML Mode).
     *
     * @return ArrayList of CPathRecord Objects.
     * @throws DaoException   Data Access Error.
     * @throws IOException    I/O Error.
     * @throws CacheException Cache Error.
     */
    public ArrayList getTopLevelPathwayList() throws DaoException,
            IOException, CacheException {
        processHtmlRequest(xdebug);
        return this.pathwayList;
    }

    /**
     * In HTML Mode, we use the Global In-Memory Cache.
     */
    private void processHtmlRequest(XDebug xdebug) throws CacheException,
            DaoException, IOException {
        determineTopLevelPathwayList(xdebug);
    }

    /**
     * Executes Query Sub Task.
     *
     * @return XmlAssembly Object.
     * @throws Exception All Errors.
     */
    protected XmlAssembly executeSub() throws Exception {
        determineTopLevelPathwayList(xdebug);
        return XmlAssemblyFactory.createXmlAssembly(pathwayList,
                XmlRecordType.BIO_PAX, pathwayList.size(),
                XmlAssemblyFactory.XML_ABBREV, xdebug);
    }

    private void determineTopLevelPathwayList(XDebug xdebug)
            throws DaoException, IOException, CacheException {
        TopLevelPathwayUtil util = new TopLevelPathwayUtil(xdebug);
        if (request != null && request.getOrganism() != null) {
            int taxonomyId = Integer.parseInt(request.getOrganism());
            pathwayList = util.getTopLevelPathwayList(taxonomyId, true);
        } else {
            pathwayList = util.getTopLevelPathwayList(true);
        }
    }
}