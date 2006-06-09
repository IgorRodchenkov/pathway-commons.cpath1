// $Id: TopLevelPathwayUtil.java,v 1.12 2006-06-09 19:22:03 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.sql.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.util.cache.EhCache;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Utility to determine top-level pathways.
 *
 * @author Ethan Cerami
 */
public class TopLevelPathwayUtil {
    private XDebug xdebug;

    /**
     * Constructor.
     *
     * @param xdebug XDebug Object.
     */
    public TopLevelPathwayUtil(XDebug xdebug) {
        this.xdebug = xdebug;
    }

    /**
     * Gets a list of all Top-Level Pathways.
     *
     * @param checkCache check the in-memory cache.
     * @return ArrayList of CPathRecord Objects.
     * @throws DaoException   Database Error.
     * @throws IOException    I/O Error.
     * @throws CacheException Cache Error.
     */
    public ArrayList getTopLevelPathwayList(boolean checkCache)
            throws DaoException, IOException, CacheException {
        return getTopLevelPathwayList(-1, checkCache);
    }

    /**
     * Gets a list of all Top-Level Pathways for the specified organism.
     *
     * @param taxonomyId Taxonomy ID.
     * @param checkCache check the in-memory cache.
     * @return ArrayList of CPathRecord Objects.
     * @throws DaoException   Database Error.
     * @throws IOException    I/O Error.
     * @throws CacheException Cache Error.
     */
    public ArrayList getTopLevelPathwayList(int taxonomyId, boolean checkCache)
            throws DaoException, IOException, CacheException {
        String key = generateKey(taxonomyId);
        ArrayList pathwayList;
        if (checkCache) {
            xdebug.logMsg(this, "Checking In-Memory Cache:  " + key);
            CacheManager manager = CacheManager.create();
            Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
            Element element = cache.get(key);

            if (element != null) {
                xdebug.logMsg(this, "Successfully Retrieved from Cache");
                xdebug.logMsg(this, "Cached Element created at:  "
                        + new Date(element.getCreationTime()));
                pathwayList = (ArrayList) element.getValue();
            } else {
                xdebug.logMsg(this, "Nothing in cache");
                pathwayList = determineTopLevelPathwayList(taxonomyId, key);
            }
        } else {
            //  Bypass the cache check
            pathwayList = determineTopLevelPathwayList(taxonomyId, key);
        }
        return pathwayList;
    }

    private ArrayList determineTopLevelPathwayList(int taxonomyId, String key)
            throws DaoException, IOException, CacheException {
        DaoCPath dao = DaoCPath.getInstance();
        ArrayList candidateList = new ArrayList();
        if (taxonomyId > 0) {
            xdebug.logMsg(this, "Getting All Candidate Pathways for Organism:  "
                    + taxonomyId);
            candidateList = dao.getRecordByTaxonomyID
                    (CPathRecordType.PATHWAY, taxonomyId);
        } else {
            xdebug.logMsg(this, "Getting All Candidate Pathways");
            candidateList = dao.getAllRecords(CPathRecordType.PATHWAY);
        }

        ArrayList topLevelPathwayList = filterCandidateList(candidateList);

        //  Store to Cache
        xdebug.logMsg(this, "Storing Pathway List to Cache");
        if (topLevelPathwayList.size() > 0) {
            CacheManager manager = CacheManager.create();
            Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
            Element newElement = new Element(key, topLevelPathwayList);
            cache.put(newElement);
        }
        return topLevelPathwayList;
    }

    //  Determine high-level pathways
    private ArrayList filterCandidateList(ArrayList candidateList)
            throws DaoException {
        xdebug.logMsg(this, "Number of Candidate Pathways:  "
                + candidateList.size());
        ArrayList topLevelPathwayList = new ArrayList();
        DaoInternalLink daoInternalLink = new DaoInternalLink();
        for (int i = 0; i < candidateList.size(); i++) {
            boolean hasParents = true;
            boolean containsPathways = false;
            CPathRecord pathway = (CPathRecord) candidateList.get(i);
            ArrayList sourceLinks = daoInternalLink.getSources
                    (pathway.getId());

            //  First Pass:  Does is have any parents?
            if (sourceLinks.size() == 0) {
                hasParents = false;
            }

            //  Second Pass:  Does it contain other pathways?
            if (!hasParents) {
                ArrayList targetLinks = daoInternalLink.getTargetsWithLookUp(pathway.getId());
                for (int j = 0; j < targetLinks.size(); j++) {
                    CPathRecord record = (CPathRecord) targetLinks.get(j);
                    if (record.getType().equals(CPathRecordType.PATHWAY)) {
                        containsPathways = true;
                        break;
                    }
                }
            }

            if (!hasParents && containsPathways) {
                topLevelPathwayList.add(pathway);
            }
        }
        xdebug.logMsg(this, "Total Number of Top Level Pathways:  "
                + topLevelPathwayList.size());

        //  In the case where we have only pathways and no sub-pathways, e.g. in the
        //  case of cellmap.org data, the above filter results in 0 pathways.  That's
        //  not good, and would result in 0 pathways on the home page.  To get around
        //  this, we need to roll back to use original candidate list.
        if (topLevelPathwayList.size() == 0) {
            xdebug.logMsg(this, "Total Number of Top Level Pathways is Zero!");
            xdebug.logMsg(this, "Would result in Zero pathways on home page.");
            xdebug.logMsg(this, "Rolling back to show all candidate pathways");
            topLevelPathwayList = candidateList;
        }
        Collections.sort(topLevelPathwayList, new RecordComparator());
        return topLevelPathwayList;
    }

    private String generateKey(int taxonomyId) {
        if (taxonomyId > 0) {
            return EhCache.KEY_PATHWAY_LIST + "." + taxonomyId;
        } else {
            return EhCache.KEY_PATHWAY_LIST;
        }
    }
}

/**
 * Compares two cPathRecord Objects, and sorts by specific type.
 *
 * @author Ethan Cerami
 */
class RecordComparator implements Comparator {

    /**
     * Compares two cPathRecord Objects.
     *
     * @param object0 CPathRecord Object 0.
     * @param object1 cPathRecord Object 1/
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(Object object0, Object object1) {
        if (object0 != null && object1 != null) {
            CPathRecord record0 = (CPathRecord) object0;
            CPathRecord record1 = (CPathRecord) object1;
            String name0 = record0.getName();
            String name1 = record1.getName();
            if (name0 != null && name1 != null) {
                return name0.toUpperCase().compareTo(name1.toUpperCase());
            }
        }
        return -1;
    }
}