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
package org.mskcc.pathdb.util;

import java.util.HashMap;

/**
 * Global Cache Object.
 *
 * @author Ethan Cerami
 */
public class GlobalCache {
    private static GlobalCache cache;
    private HashMap hash;

    /**
     * Gets Instance of Singleton Global Cache.
     *
     * @return Global Cache Object.
     */
    public static GlobalCache getInstance() {
        if (cache == null) {
            cache = new GlobalCache();
        }
        return cache;
    }

    /**
     * Private Constructor.
     */
    private GlobalCache() {
        hash = new HashMap();
    }

    /**
     * Resets the Global Cache.
     */
    public void resetCache() {
        hash = new HashMap();
    }

    /**
     * Gets Object by Key.
     *
     * @param key Key.
     * @return Cached Object.
     */
    public Object get(String key) {
        return hash.get(key);
    }

    /**
     * Puts New Object in Cache.
     *
     * @param key    Key.
     * @param object Object to be cached.
     */
    public void put(String key, Object object) {
        hash.put(key, object);
    }
}
