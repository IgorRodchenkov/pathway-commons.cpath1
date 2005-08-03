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
package org.mskcc.pathdb.util.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

/**
 * Global Cache.
 *
 * @author Ethan Cerami.
 */
public class EhCache {

    /**
     * Name of Global Cache.
     */
    public static final String GLOBAL_CACHE_NAME = "global_cache";

    /**
     * Key for Pathway List.
     */
    public static final String KEY_PATHWAY_LIST = "KEY_PATHWAY_LIST";

    /**
     * Key for Organism List, sorted by Name.
     */
    public static final String KEY_ORGANISM_LIST_SORTED_BY_NAME
            = "KEY_ORGANISM_LIST_SORTED_BY_NAME";

    /**
     * Key for Organism List, sorted by number of interactions/pathways.
     */
    public static final String KEY_ORGANISM_LIST_SORTED_BY_NUM_ENTITIES
            = "KEY_ORGANISM_LIST_SORTED_BY_NUM_ENTITIES";

    /**
     * Initializes the EhCache.
     *
     * @throws CacheException Error Initializing Cache.
     */
    public static void initCache() throws CacheException {
        //Create a CacheManager using defaults
        CacheManager manager = CacheManager.create();

        //Create a Cache specifying its configuration.
        Cache testCache = new Cache(EhCache.GLOBAL_CACHE_NAME,
                1000, false, false, 1800, 1800,
                false, 120);
        manager.addCache(testCache);
    }
}
