package org.mskcc.pathdb.sql.query;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.util.cache.EhCache;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
        xdebug.logMsg(this, "Checking In-Memory Cache:  "
                + EhCache.KEY_PATHWAY_LIST);
        CacheManager manager = CacheManager.create();
        Cache cache = manager.getCache(EhCache.GLOBAL_CACHE_NAME);
        Element element = cache.get(EhCache.KEY_PATHWAY_LIST);

        if (element != null) {
            xdebug.logMsg(this, "Successfully Retrieved from Cache");
            xdebug.logMsg(this, "Cached Element created at:  "
                    + new Date(element.getCreationTime()));
            pathwayList = (ArrayList) element.getValue();
        } else {
            xdebug.logMsg(this, "Not hit in cache.  Getting all pathways.");
            determineTopLevelPathwayList(xdebug);
            if (pathwayList.size() > 0) {
                Element newElement = new Element(EhCache.KEY_PATHWAY_LIST,
                        pathwayList);
                cache.put(newElement);
            }
        }
    }

    /**
     * Executes Query Sub Task.
     * @return  XmlAssembly Object.
     * @throws Exception    All Errors.
     */
    protected XmlAssembly executeSub() throws Exception {
        determineTopLevelPathwayList(xdebug);
        return XmlAssemblyFactory.createXmlAssembly(pathwayList,
                XmlRecordType.BIO_PAX, pathwayList.size(),
                XmlAssemblyFactory.XML_ABBREV, xdebug);
    }

    private void determineTopLevelPathwayList(XDebug xdebug)
            throws DaoException, IOException {
        DaoCPath dao = DaoCPath.getInstance();

        //  Get all candidate pathways
        ArrayList candidateList = null;
        if (request != null && request.getOrganism() != null) {
            int taxonomyId = Integer.parseInt(request.getOrganism());
            xdebug.logMsg(this, "Getting All Candidate Pathways for Organism:  "
                + taxonomyId);
            candidateList = dao.getRecordByTaxonomyID(CPathRecordType.PATHWAY,
                    taxonomyId);
        } else {
            xdebug.logMsg(this, "Getting All Candidate Pathways");
            candidateList = dao.getAllRecords(CPathRecordType.PATHWAY);
        }
        xdebug.logMsg(this, "Total Number of Candidate Pathways Found:  "
                + candidateList.size());

        //  Determine high-level pathways
        DaoInternalLink daoInternalLink = new DaoInternalLink();
        for (int i = 0; i < candidateList.size(); i++) {
            CPathRecord pathway = (CPathRecord) candidateList.get(i);
            ArrayList sourceLinks = daoInternalLink.getSources
                    (pathway.getId());
            //  If nothing points to this pathway, it is a top level pathway.
            if (sourceLinks.size() == 0) {
                pathwayList.add(pathway);
            }
        }
        xdebug.logMsg(this, "Total Number of High Level Pathways:  "
                + pathwayList.size());
    }
}