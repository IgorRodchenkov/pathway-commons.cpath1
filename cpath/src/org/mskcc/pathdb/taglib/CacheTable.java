package org.mskcc.pathdb.taglib;

import org.mskcc.dataservices.cache.CacheManager;
import org.mskcc.dataservices.cache.Cacheable;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Custom JSP Tag for Displaying the Cache Contents.
 *
 * @author Ethan Cerami
 */
public class CacheTable extends HtmlTable {

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    public void subDoStartTag() throws Exception {
        String headers[] = {"Key", "Expiration Time"};
        createHeader("Cache Contents");
        startTable();
        createTableHeaders(headers);
        outputRecords();
        endTable();
    }

    /**
     * Output Cached Records.
     */
    private void outputRecords() {
        CacheManager cacheManager = CacheManager.getInstance();
        HashMap map = cacheManager.getEntireCache();
        if (map != null) {
            Set set = map.keySet();
            if (set != null) {
                Iterator keys = set.iterator();
                while (keys.hasNext()) {
                    append("<TR>");
                    String key = (String) keys.next();
                    outputDataField(key);
                    Cacheable object = (Cacheable) map.get(key);
                    Date expirationTime = object.getExpirationTime();
                    outputDataField(expirationTime.toString());
                    append("</TR>");
                }
            }
        }
    }
}