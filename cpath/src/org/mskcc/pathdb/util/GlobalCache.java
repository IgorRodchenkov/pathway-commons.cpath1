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
