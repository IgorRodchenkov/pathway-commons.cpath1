// $Id: EhCache.java,v 1.10 2006-06-09 19:22:04 cerami Exp $
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
package org.mskcc.pathdb.util.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import java.io.IOException;

/**
 * Global Cache.
 *
 * @author Ethan Cerami.
 */
public class EhCache {
    /**
     * Name of In-Memory Cache.
     */
    public static final String PERSISTENT_CACHE = "persistent_cache";

    /**
     * Key for Pathway List.
     */
    public static final String KEY_PATHWAY_LIST = "KEY_PATHWAY_LIST";

    /**
     * Initializes the EhCache with ehcache.xml.
     *
     * @throws CacheException Error Initializing Cache.
     */
    public static void initCache() throws CacheException {
        //  Create a CacheManager using ehcache.xml
        CacheManager manager = CacheManager.getInstance();
    }

    /**
     * Shuts down EhCache.
     */
    public static void shutDownCache() {
        CacheManager manager = CacheManager.getInstance();
        manager.shutdown();
    }

    /**
     * Resets all EhCaches.
     *
     * @throws IOException IO Error.
     */
    public static void resetAllCaches() throws IOException {
        CacheManager manager = CacheManager.getInstance();
        Cache cache1 = manager.getCache(EhCache.PERSISTENT_CACHE);
        cache1.removeAll();
    }
}
