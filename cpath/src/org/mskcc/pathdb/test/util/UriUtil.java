package org.mskcc.pathdb.test.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Common URI Utilities.
 *
 * @author Ethan Cerami.
 */
public class UriUtil {

    /**
     * Normalizes HashMap of String Array data to String data.
     * Given a HashMap of String arrays, this method extracts the zeroeth
     * element, and normalizes everything to a single String.
     * Important Note:  This method can result is loss of data.
     * @param paramMap Map of Name/Value Pairs (array string values).
     * @return HashMap of Name/Value Pairs (single string values).
     */
    public static HashMap normalizeParameterMap(Map paramMap) {
        HashMap newMap = new HashMap();
        Iterator names = paramMap.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            String values[] = (String[]) paramMap.get(name);
            //  Extract Only the Zeroeth Value;  Ignore all others.
            if (values[0] != null) {
                newMap.put(name, values[0]);
            }
        }
        return newMap;
    }
}
