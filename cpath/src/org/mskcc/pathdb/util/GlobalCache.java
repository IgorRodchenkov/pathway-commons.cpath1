package org.mskcc.pathdb.util;

import java.util.HashMap;

public class GlobalCache {
    private static GlobalCache cache;
    private HashMap hash;

    public static GlobalCache getInstance() {
        if (cache == null) {
            cache = new GlobalCache();
        }
        return cache;
    }

    private GlobalCache() {
        hash = new HashMap();
    }

    public void resetCache() {
        hash = new HashMap();
    }

    public Object get (String key) {
        return hash.get(key);
    }

    public void put (String key, Object object) {
        hash.put(key, object);
    }
}
