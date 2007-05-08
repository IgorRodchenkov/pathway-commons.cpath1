// $Id: EntitySummaryParser.java,v 1.24 2007-05-08 20:23:35 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

// package
package org.mskcc.pathdb.schemas.biopax.summary;

// imports

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.util.cache.EhCache;

/**
 * Provides a public wrapper to EntitySummaryParserNoCache.
 * This wrapper *supports* caching of Entity Summaries.
 *
 * @author Ethan Cerami.
 */
public class EntitySummaryParser {
    private Logger log = Logger.getLogger(EntitySummaryParser.class);
    private EntitySummaryParserNoCache parser;

    /**
     * Entity Summary.
     */
    private EntitySummary entitySummary;

    /**
     * Constructor.
     *
     * @param recordID long
     * @throws DaoException             Throwable
     * @throws IllegalArgumentException Throwable
     */
    public EntitySummaryParser (long recordID) throws DaoException, IllegalArgumentException {

        //  Check cache first
        CacheManager manager = CacheManager.getInstance();
        Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
        String key = getCacheKey(recordID);
        log.info("Checking cache for key:  " + key);
        net.sf.ehcache.Element element = cache.get(key);
        if (element != null) {
            log.info("-->  Hit");
            entitySummary = (EntitySummary) element.getValue();
        } else {
            parser = new EntitySummaryParserNoCache(recordID);
        }
    }

    /**
     * Finds/returns physical interaction information.
     *
     * @return EntitySummary Object.
     * @throws EntitySummaryException Entity Summary Error.
     */
    public EntitySummary getEntitySummary ()
            throws EntitySummaryException {

        if (entitySummary != null) {
            return entitySummary;
        } else {
            entitySummary = parser.getEntitySummary();

            //  Store to cache
            CacheManager manager = CacheManager.getInstance();
            Cache cache = manager.getCache(EhCache.PERSISTENT_CACHE);
            String key = getCacheKey(entitySummary.getRecordID());
            log.info("Storing to cache with key:  " + key);
            net.sf.ehcache.Element element = new net.sf.ehcache.Element(key, entitySummary);
            cache.put(element);
            return entitySummary;
        }
    }

    private String getCacheKey (long recordID) {
        String key = EhCache.KEY_ENTITY_SUMMARIES + "_" + recordID;
        return key;
    }
}
